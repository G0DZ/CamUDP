package agent;

import com.github.sarxos.webcam.Webcam;
import handler.StreamServerListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.*;
import java.util.concurrent.*;

/**
 * Created by G0DZ on 28.04.2016.
 */
public class StreamerAgent implements Runnable{
    protected final static Logger logger = LoggerFactory.getLogger(Streamer.class);
    protected final Webcam webcam;
    //flags
    protected volatile boolean isWaiting;
    //connection variables
    protected InetSocketAddress streamAddress;
    protected InetSocketAddress userAddress;
    protected ExecutorService sendWorker;
    protected DatagramSocket socket = null;
    //stream variables
    protected volatile long frameCount = 0;
    protected int FPS = 1;
    protected ScheduledExecutorService timeWorker;
    protected ScheduledFuture<?> imageGrabTaskFuture;

    public StreamerAgent(Webcam webcam, InetSocketAddress streamAddress) {
        this.webcam = webcam;
        this.streamAddress = streamAddress;
        this.isWaiting = true; //ожидание входящих подключений
        try {
            socket = new DatagramSocket(4445);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        //сервер запущен
        //именно эта часть реагирует на получаемые сообщения
        logger.info("Server started :{}",streamAddress);
        while (true) {
            //ожидаем подключений в потоке
            try {
                //если мы дотупны для подключения
                byte[] buf = new byte[256];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                System.out.println("w8");
                socket.receive(packet);
                //ловим ответ
                String msg = new String(packet.getData());
                msg = msg.replaceAll("[^a-zA-Z]","");
                System.out.println(msg);
                userAddress = new InetSocketAddress(packet.getAddress(),packet.getPort());
                switch (msg){
                    case "connect":
                        if(isWaiting) {
                            isWaiting = false;
                            logger.info("user connected");
                            //устанавливаем соединение с клиентом
                            (new StreamServerListenerIMPL()).onClientConnectedIn();
                        } else {
                            // send the response to the client at "address" and "port"
                            InetAddress address = packet.getAddress();
                            int port = packet.getPort();
                            String respMsg = "failed";
                            byte[] response = respMsg.getBytes();
                            packet = new DatagramPacket(response, response.length, address, port);
                            socket.send(packet);
                            isWaiting = true;
                        }
                        break;
                    case "disconnect":
                        break;
                    default:
                        break;
                }
            } catch (IOException e) {
                logger.info("Failed bind to :{}",streamAddress);
                //e.printStackTrace();
            } finally {
                socket.close();
            }
        }
    }


    public class StreamServerListenerIMPL implements StreamServerListener {
        @Override
        public void onClientConnectedIn() {
            //нужно запустить стрим, когда создалось соединение
            Runnable imageGrabTask = new ImageGrabTask();
            ScheduledFuture<?> imageGrabFuture =
                    timeWorker.scheduleWithFixedDelay(imageGrabTask,
                            0,
                            1000/FPS,
                            TimeUnit.MILLISECONDS);
            imageGrabTaskFuture = imageGrabFuture;
            logger.info("connection opened");
        }

        @Override
        public void onClientDisconnected() {

        }

        @Override
        public void onException(Throwable t) {

        }
    }

    private class ImageGrabTask implements Runnable{
        @Override
        public void run() {
            logger.info("image grabed ,count :{}",frameCount++);
            //BufferedImage bufferedImage = webcam.getImage();
            String message = "send";
            sendWorker.execute(new SendTask(message));
        }
    }

    private class SendTask implements Runnable{
        String msg;
        SendTask(String msg){
            this.msg = msg;
        }

        public void run(){
            logger.info("msg sended");
            byte[] buf = msg.getBytes();
            DatagramPacket packet = new DatagramPacket(buf, buf.length,userAddress);
            try {
                socket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

package agent;

import handler.SingleVideoDisplayListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by G0DZ on 29.04.2016.
 */
public class ClientAgent extends Thread{
    protected final static Logger logger = LoggerFactory.getLogger(Client.class);
    public boolean isRunning;
    private SingleVideoDisplayListener parent;
    private InetSocketAddress address;
    protected ExecutorService receiveWorker;


    public ClientAgent(SingleVideoDisplayListener parent, InetSocketAddress address) {
        this.address = address;
        this.parent = parent;
        isRunning = true;
        receiveWorker = Executors.newSingleThreadExecutor();
    }

    @Override
    public void run() {
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket(Client.connectionPort);
            String msg = "connect";
            byte[] buf = msg.getBytes();
            DatagramPacket packet = new DatagramPacket(buf, msg.length(), address);
            socket.send(packet);
            packet = new DatagramPacket(buf, buf.length);
            socket.setSoTimeout(10000); //ожидание ответа 10 секунд
            socket.receive(packet);

            // обработка полученного ответа
            String receiveMsg = new String(packet.getData());
            receiveMsg = receiveMsg.replaceAll("[^a-zA-Z]","");
            //System.out.println(receiveMsg+": so much. wow!");
            switch (receiveMsg){ //watch : streamerAgent switch
                case "connect":
                    socket.setSoTimeout(5000); //ждем ответа всего 5 секунд
                    while(isRunning){
                        byte[] imageBuf = new byte[Integer.MAX_VALUE];
                        DatagramPacket imagePacket = new DatagramPacket(imageBuf, imageBuf.length);
                        socket.receive(imagePacket);
                        receiveWorker.execute(new ImageHolder(imageBuf));
                    }
                    break;
                case "disconnect":
                    break;
                default:
                    break;
            }
        } catch (java.net.SocketTimeoutException e) {
            logger.error("Неудачная попытка подключения, пробуем снова...");
            parent.connectFailedDialog();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        } finally {
            socket.close();
            isRunning = false;
        }
    }


    private class ImageHolder implements Runnable{
        byte[] imageBuf;

        public ImageHolder(byte[] imageBuf) {
            this.imageBuf = imageBuf;
        }

        @Override
        public void run() {
            InputStream in = new ByteArrayInputStream(imageBuf);
            BufferedImage bImageFromConvert = null;
            try {
                bImageFromConvert = ImageIO.read(in);
            } catch (IOException e) {
                e.printStackTrace();
            }
            parent.parent.updateImage(bImageFromConvert);
            //logger.info("image grabed ,count :{}",frameCount++);
            //BufferedImage bufferedImage = webcam.getImage();
            //String message = "send";
            //TODO sendWorker.execute(new SendTask(message));
        }
    }

}

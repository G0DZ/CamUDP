package agent;

import agent.ui.SingleVideoDisplayWindow;
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamResolution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Created by G0DZ on 28.04.2016.
 */
public class Client {
    public final Dimension dimension;
    public final SingleVideoDisplayWindow displayWindow;
    public final Webcam webcam;
    protected final static Logger logger = LoggerFactory.getLogger(Client.class);
    public static int streamerPort = 8876;
    public static int connectionPort = 8875;
    public Client() {
        dimension = new Dimension(1024,576);
        Webcam.setAutoOpenMode(true);
        webcam = Webcam.getDefault();
        Dimension webcamSize = WebcamResolution.VGA.getSize();//new Dimension(320, 240);
        webcam.setViewSize(webcamSize);
        displayWindow = new SingleVideoDisplayWindow("UDPStream",dimension,webcam);
    }

    public static void main(String[] args) {
        //setup statthe videoWindow
        Client c = new Client();
        c.displayWindow.setVisible(true);
        logger.info("setup dimension: {}",c.dimension);
        //запуск сервера передачи сообщений
        Streamer streamer = new Streamer(c.webcam, Integer.parseInt(args[0]));
        streamer.start();
        //
        BufferedImage img = null;
        try {
            img = ImageIO.read(new File("test.png"));
        } catch (IOException e) {
        }
        //logger.error(img.toString());
        c.displayWindow.updateImage(img);
    }
}

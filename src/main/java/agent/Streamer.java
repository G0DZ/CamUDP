package agent;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamResolution;

import java.awt.*;
import java.net.InetSocketAddress;

/**
 * Created by G0DZ on 28.04.2016.
 */
public class Streamer {
    private int port;
    private Webcam webcam;

    public Streamer(Webcam webcam, int port) {
        this.webcam = webcam;
        this.port = port;
    }

    void start(){
        (new Thread(new StreamerAgent(webcam,
                        new InetSocketAddress("localhost", port)))).start();
    }
}

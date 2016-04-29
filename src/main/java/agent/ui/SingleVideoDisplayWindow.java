package agent.ui;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by G0DZ on 28.04.2016.
 */
public class SingleVideoDisplayWindow {
    protected final VideoPanel videoPannel;
    protected final JFrame window;
    protected final WebcamPanel webcamPanel;

    public SingleVideoDisplayWindow(String name,Dimension dimension, Webcam webcam) {
        super();
        this.window = new JFrame(name);
        this.videoPannel = new VideoPanel(dimension);
        this.videoPannel.setPreferredSize(dimension);
        //получаем вэбкамеру
        //настраиваем панель
        this.webcamPanel = new WebcamPanel(webcam);
        //panel.setFPSDisplayed(true);
        //panel.setDisplayDebugInfo(true);
        //panel.setImageSizeDisplayed(true);
        webcamPanel.setMirrored(true);
        int hWebPanel = (int)(dimension.height/4.0);
        int wWebPanel = (int)(dimension.width/4.0);
        webcamPanel.setSize(wWebPanel,hWebPanel);
        webcamPanel.setBounds(0,dimension.height-hWebPanel,wWebPanel,hWebPanel);
        //отображение
        JLayeredPane lpane = new JLayeredPane();
        this.window.setLayout(new BorderLayout());
        this.window.add(lpane, BorderLayout.CENTER);
        lpane.setBounds(new Rectangle(new Point(0,0),dimension));
        //lpane.add(videoPannel,new Integer(0), 0);
        lpane.add(webcamPanel,new Integer(1), 0);
        this.window.add(videoPannel);
        this.window.pack();
        this.window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

    public void setVisible(boolean visible) {
        this.window.setVisible(visible);
    }

    public void updateImage(BufferedImage image) {
        videoPannel.updateImage(image);
    }

    public void close(){
        window.dispose();
        videoPannel.close();
    }
}


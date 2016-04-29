package agent.ui;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;
import handler.SingleVideoDisplayListener;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by G0DZ on 28.04.2016.
 */
public class SingleVideoDisplayWindow {
    protected final VideoPanel videoPanel;
    protected final JFrame window;
    protected final WebcamPanel webcamPanel;
    protected final JButton button;
    protected final JTextField ipTextField;
    protected final JTextField portTextField;

    public SingleVideoDisplayWindow(String name,Dimension dimension, Webcam webcam) {
        super();
        try { //стиль оформления
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException
                | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        window = new JFrame(name);
        window.setPreferredSize(new Dimension(dimension.width,dimension.height+50));
        window.setLayout(new BorderLayout());
        videoPanel = new VideoPanel(dimension);
        //this.videoPanel.setPreferredSize(dimension);
        videoPanel.setBounds(0,0,dimension.width,dimension.height);
        videoPanel.setOpaque(true);
        //получаем вэбкамеру
        //настраиваем панель
        webcamPanel = new WebcamPanel(webcam);
        //panel.setFPSDisplayed(true);
        //panel.setDisplayDebugInfo(true);
        //panel.setImageSizeDisplayed(true);
        webcamPanel.setMirrored(true);
        Dimension dimWebPanel = new Dimension(160,120);//WebcamResolution.QVGA.getSize();
        webcamPanel.setBounds(0,
                dimension.height-dimWebPanel.height,
                dimWebPanel.width,
                dimWebPanel.height);
        webcamPanel.setOpaque(true);
        //отображение
        JLayeredPane lpane = new JLayeredPane();
        this.window.add(lpane, BorderLayout.CENTER);
        lpane.setBounds(0,0,dimension.width,dimension.height);
        lpane.add(webcamPanel,new Integer(1), 0);
        lpane.add(videoPanel,new Integer(0), 0);

        //поле ввода IP
        ipTextField = new JTextField("127.0.0.1",10);
        //ipTextField.setSize(50,50);
        //поле ввода порта
        portTextField = new JTextField("9876",5);
        portTextField.setSize(50,50);
        //кнопка
        button = new JButton("connect");
        button.setSize(dimension.width-50,50);
        button.addActionListener(new SingleVideoDisplayListener(this));
        //объединяем
        JPanel p1 = new JPanel();
        p1.setLayout(new FlowLayout());
        p1.add(ipTextField);
        p1.add(portTextField);
        p1.add(button);

        this.window.add(p1,BorderLayout.SOUTH);
        this.window.pack();
        this.window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

    public void setVisible(boolean visible) {
        this.window.setVisible(visible);
    }

    public void updateImage(BufferedImage image) {
        videoPanel.updateImage(image);
    }

    public void close(){
        window.dispose();
        videoPanel.close();
    }

    public String getPort() {
        return portTextField.getText();
    }

    public String getIP() {
        return ipTextField.getText();
    }

    public JFrame getWindow() {
        return window;
    }
}


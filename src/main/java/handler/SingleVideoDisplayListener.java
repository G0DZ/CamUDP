package handler;

import agent.Client;
import agent.ClientAgent;
import agent.ui.SingleVideoDisplayWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;

/**
 * Created by G0DZ on 29.04.2016.
 */
public class SingleVideoDisplayListener implements ActionListener {
    protected final static Logger logger = LoggerFactory.getLogger(Client.class);
    public SingleVideoDisplayWindow parent;
    private ClientAgent clientAgent;

    public SingleVideoDisplayListener(SingleVideoDisplayWindow parent) {
        this.parent = parent;
        clientAgent = null;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        int port;
        try {
            port = Integer.parseInt(parent.getPort());
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
            return;
        }
        InetSocketAddress address = new InetSocketAddress(parent.getIP(),port);
        if(clientAgent == null || !clientAgent.isRunning){
            logger.info("trying to open connection: {}",address);
            clientAgent = new ClientAgent(this,address);
            clientAgent.start();
        }
        else{
            Object[] options = { "ОК", "Отмена" };
            JOptionPane.showOptionDialog(parent.getWindow(), "Невозможно открыть новое соединение!\n"+
                            "Закройте уже существующее", "Внимание",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
                    null, options, options[0]);
//            if(clientAgent.isRunning){
//                //TODO stop thread
//                System.out.println("running");
//                //clientAgent.interrupt();
//            }


//            JOptionPane pane = new JOptionPane(arguments);
//            pane.set.Xxxx(...); // Configure
//            JDialog dialog = pane.createDialog(parentComponent, title);
//            dialog.show();
//            Object selectedValue = pane.getValue();
//            if(selectedValue == null)
//                return CLOSED_OPTION;
//            //If there is not an array of option buttons:
//            if(options == null) {
//                if(selectedValue instanceof Integer)
//                    return ((Integer)selectedValue).intValue();
//                return CLOSED_OPTION;
//            }
//            //If there is an array of option buttons:
//            for(int counter = 0, maxCounter = options.length;
//                counter < maxCounter; counter++) {
//                if(options[counter].equals(selectedValue))
//                    return counter;
//            }
//            return CLOSED_OPTION;
        }
    }

    public void connectFailedDialog(){
        JOptionPane.showMessageDialog(parent.getWindow(),"Невозможно установить соединение!\nПопробуйте позже.");
    }
}

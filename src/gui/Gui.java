package src.gui;
import src.syncing.*;
import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.io.File;

public class Gui {
    public void application(){
        Dsync dsync = new Dsync();

        JFrame frame = new JFrame("Dsync");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200,675);

        frame.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        JPanel panel1 = new JPanel();
        JTextField path1 = new JTextField(50);
        JLabel label1 = new JLabel("Folder path :");
        panel1.add(label1);
        panel1.add(path1);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        frame.add(panel1, c);

        JPanel panel2 = new JPanel();
        JTextField path2 = new JTextField(50);
        JLabel label2 = new JLabel("Folder path :");
        panel2.add(label2);
        panel2.add(path2);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 1;
        frame.add(panel2, c);

        JPanel panel3 = new JPanel();
        JTextArea messages = new JTextArea();
        panel3.add(messages);
        c.ipady = 100;
        c.gridx = 0;
        c.gridy = 2;
        frame.add(panel3, c);

        JPanel panel4 = new JPanel();
        JButton startSync = new JButton("Start Syncing");
        JButton stopSync = new JButton("Pause Syncing");
        panel4.add(startSync);
        panel4.add(stopSync);
        c.gridx = 0;
        c.gridy = 3;
        frame.add(panel4, c);

        startSync.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Boolean valid = new File(path1.getText()).exists() && new File(path2.getText()).exists();

                if (valid) {
                    dsync.setSync(true);
                    dsync.setPath1(path1.getText());
                    dsync.setPath2(path2.getText());
                    dsync.start();
                }
                else {
                    dsync.addMessage("Invalid paths.");
                }
            }
        });

        stopSync.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dsync.setSync(false);
                dsync.addMessage("Syncing Paused.");
            }
        });


        new Timer(200, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                messages.setText("");
                for (String msg : dsync.getMessages()) {
                    messages.append(msg + "\n");
                }
                messages.updateUI();
            }
        }).start();

        // frame.getContentPane().add(BorderLayout.NORTH, panel1);
        // frame.getContentPane().add(BorderLayout.NORTH, panel2);
        // frame.getContentPane().add(BorderLayout.CENTER, panel3);
        // frame.getContentPane().add(BorderLayout.PAGE_END, panel4);
        frame.setVisible(true);
     }

     public static void main(String[] args) {
        new Gui().application();
     }
}

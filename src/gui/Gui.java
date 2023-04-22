package src.gui;
import src.syncing.*;
import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import java.awt.*;
import java.awt.event.*;
import java.io.File;

public class Gui {

    public void updatePanel(JTextPane jTextPane, StyledDocument styledDoc, Dsync dsync) {
        jTextPane.setText("");
        int pos = 0;
        for (String msg : dsync.getMessages()) {
            try {
                String str = msg + "\n";
                styledDoc.insertString(pos, str, jTextPane.getStyle("default"));
                pos += str.length();
            } 
            catch (BadLocationException e1) {}
        }
        Document doc = jTextPane.getDocument();
        int length = doc.getLength();
        if (length > 0) {
            try {
                doc.remove(length - 1, 1);
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        }
        jTextPane.updateUI();
    }

    public void application(){
        Dsync dsync = new Dsync();
        dsync.addMessage("Press a button to get started.");

        JFrame frame = new JFrame("Dsync");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200,675);

        frame.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 1.0;
        c.weighty = 1.0;

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

        JPanel panel3 = new JPanel(new GridBagLayout());
        panel3.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(20, Color.GRAY, 2),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
            ));
        
        JTextPane messages = new JTextPane();
        messages.setEditable(false);
        messages.setFont(new Font("Calibri", 10, 20));
        messages.setBackground(new Color(220, 220, 220));

        StyledDocument styledMsg = (StyledDocument)messages.getDocument(); // Used to edit the style

        // Used to center the text in the JTextPane
        SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
        messages.setParagraphAttributes(center, false);

        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.fill = GridBagConstraints.BOTH;
        panel3.add(messages, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 2;
        c.insets = new Insets(0, 50, 0, 50);
        frame.add(panel3, c);
        // messages.setPreferredSize((new Dimension((int)(frame.getWidth()-250), (int)(frame.getHeight()/3))));
        messages.updateUI();

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
                Boolean valid = new File(path1.getText()).exists() 
                    && new File(path1.getText()).isDirectory() 
                    && new File(path2.getText()).exists()
                    && new File(path2.getText()).isDirectory();

                if (valid) {
                    dsync.setSync(true);
                    dsync.setPath1(path1.getText());
                    dsync.setPath2(path2.getText());
                    if (!dsync.isAlive()) dsync.start();
                }
                else {
                    dsync.addMessage("Invalid paths.");
                }

                updatePanel(messages, styledMsg, dsync);
            }
        });

        stopSync.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dsync.setSync(false);
                dsync.addMessage("Syncing Paused.");

                updatePanel(messages, styledMsg, dsync);
            }
        });


        new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updatePanel(messages, styledMsg, dsync);
            }
        }).start();

        frame.addComponentListener(new ComponentAdapter( ) {
            public void componentResized(ComponentEvent ev) {
                // messages.setPreferredSize((new Dimension((int)(frame.getWidth()-250), (int)(frame.getHeight()/3))));
            }
          });

        updatePanel(messages, styledMsg, dsync);
        frame.setVisible(true);
     }

     public static void main(String[] args) {
        new Gui().application();
     }
}

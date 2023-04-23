package src.gui;
import src.syncing.*;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

public class Gui {
    Boolean started = false;

    Boolean browse1Removed = false;
    Boolean browse2Removed = false;

    String syncImagePath = "img/syncIconColorBorder.png";

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

    public void application() throws IOException{
        Dsync dsync = new Dsync();
        dsync.addMessage("Press a button to get started.");

        JFrame frame = new JFrame("Dsync");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200,675);

        frame.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 1.0;
        c.weighty = 1.0;

        // Path input design
        Font labelFont = new Font("Calibri", Font.BOLD, 18);
        Font pathFont = new Font("Calibri", 0, 17);
        Dimension textFieldSize = new Dimension(0, 30);
        Dimension browseButtonSize = new Dimension(100, 25);
        Border textFieldBorder = BorderFactory.createEmptyBorder();

        // Button sizes and fonts
        Font browseButtonFont = new Font("Calibri", Font.BOLD, 14);
        Font buttonFont = new Font("Calibri", Font.BOLD, 18);
        Dimension buttonSize = new Dimension(200, 80);

        //--------------------- First path input ---------------------\\

        JPanel panel1 = new JPanel(new GridBagLayout());
        
        JTextField path1 = new JTextField(0);
        path1.setPreferredSize(textFieldSize);
        path1.setFont(pathFont);
        path1.setBackground(Color.WHITE);
        path1.setBorder(textFieldBorder);

        JLabel label1 = new JLabel("First folder path :");
        label1.setFont(labelFont);

        JButton browseButton1 = new JButton("Browse...");
        browseButton1.setPreferredSize(browseButtonSize);
        browseButton1.setFont(browseButtonFont);
        browseButton1.setBackground(Color.LIGHT_GRAY);
        browseButton1.setFocusPainted(false);
        browseButton1.setOpaque(true);
        browseButton1.setBorderPainted(false);

        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        panel1.add(label1, c);

        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 3;
        panel1.add(path1, c);

        path1.setLayout(new BorderLayout());
        path1.add(browseButton1, BorderLayout.EAST);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        c.insets = new Insets(0, 30, 0, 15);
        frame.add(panel1, c);

        //### END OF SECTION ###\\

        //--------------------- Second path input ---------------------\\

        JPanel panel2 = new JPanel(new GridBagLayout());

        JTextField path2 = new JTextField(0);
        path2.setPreferredSize(textFieldSize);
        path2.setFont(pathFont);
        path2.setBackground(Color.WHITE);
        path2.setBorder(textFieldBorder);
        
        JLabel label2 = new JLabel("Second folder path :");
        label2.setFont(labelFont);

        JButton browseButton2 = new JButton("Browse...");
        browseButton2.setPreferredSize(browseButtonSize);
        browseButton2.setFont(browseButtonFont);
        browseButton2.setBackground(Color.LIGHT_GRAY);
        browseButton2.setFocusPainted(false);
        browseButton2.setOpaque(true);
        browseButton2.setBorderPainted(false);
        
        c.insets = new Insets(0, 0, 0, 0);
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 1;
        c.gridy = 0;
        c.gridwidth = 1;
        panel2.add(label2, c);

        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 3;
        panel2.add(path2, c);

        path2.setLayout(new BorderLayout());
        path2.add(browseButton2, BorderLayout.EAST);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 0;
        c.gridwidth = 1;
        c.insets = new Insets(0, 15, 0, 30);
        frame.add(panel2, c);

        // Resetting the inset value
        c.insets = new Insets(0, 0, 0, 0);

        //### END OF SECTION ###\\

        //--------------------- Log messages panel ---------------------\\

        JPanel panel3 = new JPanel(new GridBagLayout());
        panel3.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(20, Color.GRAY, 2),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        
        JTextPane messages = new JTextPane();
        messages.setEditable(false);
        messages.setFont(new Font("Calibri", 0, 20));
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
        c.gridy = 1;
        c.insets = new Insets(0, 50, 0, 50);
        c.gridwidth = 2;
        frame.add(panel3, c);
        messages.updateUI();
        //Resetting the inset
        c.insets = new Insets(0, 0, 0, 0);

        //### END OF SECTION ###\\

        //--------------------- Start/Pause button ---------------------\\

        JPanel panel4 = new JPanel();
        panel4.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(20, Color.GRAY, 2),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        panel4.setLayout(new BorderLayout());

        JButton startSync = new JButton("Start Syncing");
        startSync.setPreferredSize(buttonSize);
        startSync.setFont(buttonFont);
        startSync.setBackground(new Color(187, 252, 203));
        startSync.setFocusPainted(false);
        startSync.setOpaque(true);
        startSync.setBorderPainted(false);
        
        BackgroundRotation syncIcon = new BackgroundRotation(syncImagePath);
        syncIcon.setPreferredSize(new Dimension(75, 75));
        syncIcon.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        panel4.add(syncIcon, BorderLayout.WEST);

        c.fill = GridBagConstraints.BOTH;
        c.gridx = 1;
        c.gridy = 0;
        c.gridwidth = 1;
        panel4.add(startSync, BorderLayout.EAST);

        c.fill = GridBagConstraints.RELATIVE;
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 1;
        c.insets = new Insets(0, 0, 0, 0);
        frame.add(panel4, c);

        //### END OF SECTION ###\\

        //--------------------- Reset button ---------------------\\

        JPanel panel5 = new JPanel();
        panel5.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(20, Color.GRAY, 2),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        panel5.setLayout(new BorderLayout());

        JButton resetSync = new JButton("Reset");
        resetSync.setLayout(new GridBagLayout());
        resetSync.setPreferredSize(buttonSize);
        resetSync.setFont(buttonFont);
        resetSync.setBackground(new Color(158, 216, 247));
        resetSync.setFocusPainted(false);
        resetSync.setOpaque(true);
        resetSync.setBorderPainted(false);

        panel5.add(resetSync, BorderLayout.CENTER);

        c.fill = GridBagConstraints.RELATIVE;
        c.gridx = 1;
        c.gridy = 2;
        c.gridwidth = 1;
        c.insets = new Insets(0, 0, 0, 0);
        frame.add(panel5, c);

        //### END OF SECTION ###\\

        //--------------------- Listeners to perform actions after button presses or every second ---------------------\\

        // Hovering the buttons
        browseButton1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                // Change button appearance when mouse enters
                browseButton1.setBackground(browseButton1.getBackground().darker());
                browseButton1.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                // Reset button appearance when mouse exits
                browseButton1.setBackground(browseButton1.getBackground().brighter());
            }
        });

        browseButton2.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                // Change button appearance when mouse enters
                browseButton2.setBackground(browseButton2.getBackground().darker());
                browseButton2.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                // Reset button appearance when mouse exits
                browseButton2.setBackground(browseButton2.getBackground().brighter());
            }
        });

        startSync.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                // Change button appearance when mouse enters
                startSync.setBackground(startSync.getBackground().darker());
                startSync.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                // Reset button appearance when mouse exits
                startSync.setBackground(startSync.getBackground().brighter());
            }
        });

        resetSync.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                // Change button appearance when mouse enters
                resetSync.setBackground(resetSync.getBackground().darker());
                resetSync.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                // Reset button appearance when mouse exits
                resetSync.setBackground(resetSync.getBackground().brighter());
            }
        });

        // When the buttons are clicked
        startSync.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!started) {
                    Boolean valid = new File(path1.getText()).exists() 
                    && new File(path1.getText()).isDirectory() 
                    && new File(path2.getText()).exists()
                    && new File(path2.getText()).isDirectory();

                    if (valid) {
                        dsync.setSync(true);
                        if (startSync.getText().equals("Start Syncing")) {
                            dsync.setPath1(path1.getText().trim());
                            dsync.setPath2(path2.getText().trim());
                            path1.setEditable(false);
                            path1.setBackground(Color.LIGHT_GRAY);
                            path2.setEditable(false);
                            path2.setBackground(Color.LIGHT_GRAY);
                        }

                        if (!dsync.isAlive()) dsync.start();
                        started = true;

                        startSync.setText("Pause Syncing");
                        startSync.setBackground(new Color(250, 90, 90));

                        syncIcon.setRotate(true); // Start rotation
                    }
                    else {
                        dsync.addMessage("Invalid paths.");
                    }

                    updatePanel(messages, styledMsg, dsync);
                }
                
                else {
                    dsync.setSync(false);
                    dsync.addMessage("Syncing Paused.");

                    updatePanel(messages, styledMsg, dsync);
                    started = false;

                    startSync.setText("Resume Syncing");
                    // startSync.setBackground(new Color(190, 250, 200)); --> The greenish color makes swing bug...
                    startSync.setBackground(new Color(240, 231, 134));

                    syncIcon.setRotate(false); // Stop rotation
                }
            }
        });

        resetSync.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                path1.setText("");
                path2.setText("");
                path1.setEditable(true);
                path1.setBackground(Color.WHITE);
                path2.setEditable(true);
                path2.setBackground(Color.WHITE);

                path1.add(browseButton1, BorderLayout.EAST);
                path1.revalidate();
                path1.repaint();
                browse1Removed = false;

                path2.add(browseButton2, BorderLayout.EAST);
                path2.revalidate();
                path2.repaint();
                browse2Removed = false;

                syncIcon.setRotate(false); // Stop rotation

                dsync.resetMessages();
                dsync.setFirstSync(true);
                dsync.setSync(false);
                updatePanel(messages, styledMsg, dsync);

                startSync.setText("Start Syncing");
                startSync.setBackground(new Color(187, 252, 203));
                started = false;
            }
        });

        browseButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int option = fileChooser.showOpenDialog(frame);

                if (option == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    path1.setText(file.getPath());
                    path1.remove(browseButton1);
                    browse1Removed = true;
                }
            }
        });

        browseButton2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int option = fileChooser.showOpenDialog(frame);

                if (option == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    path2.setText(file.getPath());
                    path2.remove(browseButton2);
                    browse2Removed = true;
                }
            }
        });

        // Every second to update elements without user input
        new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updatePanel(messages, styledMsg, dsync);

                if (path1.getText().isEmpty() && browse1Removed) {
                    path1.add(browseButton1, BorderLayout.EAST);
                    path1.revalidate();
                    path1.repaint();
                    browse1Removed = false;
                }

                if (!path1.getText().isEmpty() && !browse1Removed) {
                    path1.remove(browseButton1);
                    browse1Removed = true;
                }

                if (path2.getText().isEmpty() && browse2Removed) {
                    path2.add(browseButton2, BorderLayout.EAST);
                    path2.revalidate();
                    path2.repaint();
                    browse2Removed = false;
                }

                if (!path2.getText().isEmpty() && !browse2Removed) {
                    path2.remove(browseButton2);
                    browse2Removed = true;
                }
            }

        }).start();

        //### END OF SECTION ###\\

        updatePanel(messages, styledMsg, dsync);
        frame.setVisible(true);
     }

     public static void main(String[] args) throws IOException {
        new Gui().application();
     }
}

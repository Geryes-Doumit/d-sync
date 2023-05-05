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
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Gui {
    private Boolean started = false;
    private Boolean networkMode = false;

    private Boolean browse1Removed = false;
    private Boolean browse2Removed = false;

    private String syncImagePath = "img/syncIconColorBorder.png";
    private String userDataPath = "DsyncUserData.txt";

    private List<String> data = new ArrayList<>();

    // Used to check if the ip address entered is valid (no letters, etc...)
    private static final Pattern ipPattern = Pattern.compile(
        "^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");

    public static boolean isValid(final String ip) {
        return ipPattern.matcher(ip).matches();
    }

    // Used to check if the port number entered is valid (no non-numeric characters)
    private static final Pattern portPattern = Pattern.compile(
        "-?\\d+(\\.\\d+)?");

    public boolean isNumber(String port) {
        return portPattern.matcher(port).matches();
    }

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

    
    // Reading the user data and saving it
    public void updateData() throws FileNotFoundException {
        File userDataFile = new File(userDataPath);
        data.clear();

        if (userDataFile.exists()) {
            Scanner userdata = new Scanner(userDataFile);
            while(userdata.hasNextLine()) {
                data.add(userdata.nextLine());
            }
            userdata.close();
        }
        else {
            try {
                userDataFile.createNewFile();
            } catch (IOException e1) {}
        }
    }

    public void application() throws IOException{
        System.setProperty("sun.java2d.uiScale.enabled", "false");
        System.setProperty("apple.awt.uiScale", "1.0");

        updateData(); // Making sure the data is up to date when the app opens

        Dsync dsync = new Dsync();
        dsync.addMessage("Press a button to get started.");

        JFrame window = new JFrame("Dsync");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        int width = 800; int height = 450;

        window.setSize((int) (width*1.5), (int) (height*1.5));
        window.setMinimumSize(new Dimension(width, height));
        window.setLayout(new BorderLayout());
        
        JPanel frame = new JPanel();
        frame.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 1.0;
        c.weighty = 1.0;

        Color defaultFrameBG = new Color(230, 230, 230);
        frame.setBackground(defaultFrameBG);
        Color invertedFrameBackground = new Color(255 - frame.getBackground().getRed(), 255 - frame.getBackground().getGreen(), 255 - frame.getBackground().getBlue());
        Color darkerFrameBackground = new Color(frame.getBackground().getRed() - 15, frame.getBackground().getGreen() - 15, frame.getBackground().getBlue() - 15);


        // Path input design
        Font labelFont = new Font("Calibri", Font.BOLD, 18);
        Font pathFont = new Font("Calibri", 0, 17);
        Dimension textFieldSize = new Dimension(0, 30);
        Dimension browseButtonSize = new Dimension(100, 25);
        Border textFieldBorder = BorderFactory.createEmptyBorder();

        // Button sizes and fonts
        Font browseButtonFont = new Font("Calibri", Font.BOLD, 14);
        Font buttonFont = new Font("Calibri", Font.BOLD, 17);
        Dimension buttonSize = new Dimension(200, 80);

        //--------------------- First path input ---------------------\\

        JPanel pathsPanel = new JPanel();
        pathsPanel.setLayout(new GridBagLayout());

        JPanel panel1 = new JPanel(new GridBagLayout());
        
        JTextField path1 = new JTextField(0);
        path1.setPreferredSize(textFieldSize);
        path1.setFont(pathFont);
        path1.setBackground(frame.getBackground().brighter());
        path1.setBorder(textFieldBorder);
        try {
            path1.setText(data.get(0));
        } catch(Exception e) {
            // if the data wasn't saved correctly
        }

        JLabel label1 = new JLabel("First folder path :");
        label1.setFont(labelFont);
        label1.setForeground(invertedFrameBackground);

        JButton browseButton1 = new JButton("Browse");
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
        c.gridwidth = 1;
        panel1.add(path1, c);

        path1.setLayout(new BorderLayout());
        path1.add(browseButton1, BorderLayout.EAST);

        panel1.setBackground(frame.getBackground());

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        c.insets = new Insets(0, 30, 0, 15);
        pathsPanel.add(panel1, c);

        //### END OF SECTION ###\\

        //--------------------- Second path input / network settings ---------------------\\

        JPanel panel2 = new JPanel(new GridBagLayout());

        JTextField path2 = new JTextField(0);
        path2.setPreferredSize(textFieldSize);
        path2.setFont(pathFont);
        path2.setBackground(frame.getBackground().brighter());
        path2.setBorder(textFieldBorder);
        try {
            path2.setText(data.get(1));
        } catch(Exception e) {
            // if the data wasn't saved correctly
        }
        
        JLabel label2 = new JLabel("Second folder path :");
        label2.setFont(labelFont);
        label2.setForeground(invertedFrameBackground);

        JButton browseButton2 = new JButton("Browse");
        browseButton2.setPreferredSize(browseButtonSize);
        browseButton2.setFont(browseButtonFont);
        browseButton2.setBackground(Color.LIGHT_GRAY);
        browseButton2.setFocusPainted(false);
        browseButton2.setOpaque(true);
        browseButton2.setBorderPainted(false);
        
        c.insets = new Insets(0, 0, 0, 0);
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        panel2.add(label2, c);

        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 1;
        panel2.add(path2, c);

        path2.setLayout(new BorderLayout());
        path2.add(browseButton2, BorderLayout.EAST);

        JToggleButton hostButton = new JToggleButton("Host server");
        hostButton.setPreferredSize(new Dimension(150, path2.getHeight()));
        hostButton.setFont(browseButtonFont);
        hostButton.setBackground(Color.WHITE);
        hostButton.setFocusPainted(false);
        hostButton.setOpaque(true);
        hostButton.setBorderPainted(false);

        JButton startSession = new JButton("Connect");
        startSession.setPreferredSize(new Dimension(100, path2.getHeight()));
        startSession.setFont(browseButtonFont);
        startSession.setBackground(new Color(187, 252, 203));
        startSession.setFocusPainted(false);
        startSession.setOpaque(true);
        startSession.setBorderPainted(false);

        JPanel enterNetworkInfo = new JPanel(new GridBagLayout());

        // JLabel ipAddressLabel = new JLabel("IP address :");
        JTextField ipTextField = new JTextField("IP address");
        ipTextField.setBorder(BorderFactory.createEmptyBorder());
        ipTextField.setFont(pathFont);
        ipTextField.setForeground(Color.GRAY);

        // JLabel portLabel = new JLabel("Port number :");
        JTextField portTextField = new JTextField("port");
        portTextField.setBorder(BorderFactory.createEmptyBorder());
        portTextField.setFont(pathFont);
        portTextField.setForeground(Color.GRAY);

        // c.fill = GridBagConstraints.BOTH;
        // c.gridx = 0;
        // c.gridy = 0;
        // c.gridwidth = 1;
        // c.insets = new Insets(0, 0, 0, 0);
        // enterNetworkInfo.add(ipAddressLabel, c);

        c.fill = GridBagConstraints.BOTH;
        c.gridx = 1;
        c.gridy = 0;
        c.gridwidth = 1;
        c.insets = new Insets(0, 10, 0, 0);
        enterNetworkInfo.add(ipTextField, c);

        // c.fill = GridBagConstraints.BOTH;
        // c.gridx = 2;
        // c.gridy = 0;
        // c.gridwidth = 1;
        // c.insets = new Insets(0, 0, 0, 0);
        // enterNetworkInfo.add(portLabel, c);

        c.fill = GridBagConstraints.BOTH;
        c.gridx = 3;
        c.gridy = 0;
        c.gridwidth = 1;
        c.insets = new Insets(0, 10, 0, 10);
        enterNetworkInfo.add(portTextField, c);

        panel2.setBackground(frame.getBackground());

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 0;
        c.gridwidth = 1;
        c.insets = new Insets(0, 15, 0, 30);
        pathsPanel.add(panel2, c);

        pathsPanel.setBackground(frame.getBackground());

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 4;
        c.insets = new Insets(0, 0, 0, 0);
        frame.add(pathsPanel, c);

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

        panel3.setBackground(frame.getBackground());

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 1;
        c.insets = new Insets(0, 50, 0, 50);
        c.gridwidth = 4;
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
        syncIcon.setBackground(frame.getBackground());

        panel4.add(syncIcon, BorderLayout.WEST);

        c.fill = GridBagConstraints.BOTH;
        c.gridx = 1;
        c.gridy = 0;
        c.gridwidth = 1;
        panel4.add(startSync, BorderLayout.EAST);

        panel4.setBackground(frame.getBackground());

        c.fill = GridBagConstraints.RELATIVE;
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 1;
        c.insets = new Insets(0, 0, 0, 0);
        frame.add(panel4, c);

        //### END OF SECTION ###\\

        //--------------------- Reset and connect button ---------------------\\

        JPanel panel5 = new JPanel();
        panel5.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(20, Color.GRAY, 2),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        panel5.setLayout(new BorderLayout());

        JButton resetSync = new JButton("Reset");
        resetSync.setLayout(new GridBagLayout());
        resetSync.setPreferredSize(new Dimension(89, 75));
        resetSync.setFont(buttonFont);
        resetSync.setBackground(new Color(158, 216, 247));
        resetSync.setFocusPainted(false);
        resetSync.setOpaque(true);
        resetSync.setBorderPainted(false);

        JButton networkButton = new JButton("<html><p style=\"text-align: center;\">Connect to another computer</p></html>");
        networkButton.setPreferredSize(buttonSize);
        networkButton.setFont(buttonFont);
        networkButton.setBackground(new Color(242, 199, 138));
        networkButton.setFocusPainted(false);
        networkButton.setOpaque(true);
        networkButton.setBorderPainted(false);

        panel5.add(resetSync, BorderLayout.WEST);
        panel5.add(networkButton, BorderLayout.EAST);

        panel5.setBackground(frame.getBackground());

        c.fill = GridBagConstraints.RELATIVE;
        c.gridx = 3;
        c.gridy = 2;
        c.gridwidth = 1;
        c.insets = new Insets(0, 0, 0, 0);
        frame.add(panel5, c);

        //### END OF SECTION ###\\

        //--------------------- Options Panel ---------------------\\

        // int colorNumber = 230;
        // Color optionsGray = new Color(colorNumber, colorNumber, colorNumber);

        JPanel optionsPanel = new JPanel(new BorderLayout());
        optionsPanel.setPreferredSize(new Dimension(window.getWidth(), 25));
        optionsPanel.setBackground(darkerFrameBackground);

        JButton optionsButton = new JButton("Options");
        optionsButton.setFont(browseButtonFont);
        optionsButton.setBackground(optionsPanel.getBackground());
        optionsButton.setFocusPainted(false);
        optionsButton.setOpaque(true);
        optionsButton.setBorderPainted(false);
        optionsButton.setForeground(invertedFrameBackground);

        JButton HelloButton = new JButton("Dsync, brought to you by Geryes and Marc.");
        HelloButton.setFont(browseButtonFont);
        HelloButton.setBackground(optionsPanel.getBackground());
        HelloButton.setFocusPainted(false);
        HelloButton.setOpaque(true);
        HelloButton.setBorderPainted(false);
        HelloButton.setForeground(invertedFrameBackground);

        optionsPanel.add(optionsButton, BorderLayout.WEST);
        optionsPanel.add(HelloButton, BorderLayout.EAST);

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

        networkButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                // Change button appearance when mouse enters
                if (networkButton.isEnabled()) {
                    networkButton.setBackground(networkButton.getBackground().darker());
                    networkButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
                }
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                // Reset button appearance when mouse exits
                if (networkButton.isEnabled()) {
                    networkButton.setBackground(networkButton.getBackground().brighter());
                }
            }
        });

        hostButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                // Change button appearance when mouse enters
                hostButton.setBackground(hostButton.getBackground().darker());
                hostButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                // Reset button appearance when mouse exits
                hostButton.setBackground(hostButton.getBackground().brighter());
            }
        });

        startSession.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                // Change button appearance when mouse enters
                startSession.setBackground(startSession.getBackground().darker());
                startSession.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                // Reset button appearance when mouse exits
                startSession.setBackground(startSession.getBackground().brighter());
            }
        });

        HelloButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                // Change button appearance when mouse enters
                HelloButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
                HelloButton.setForeground(HelloButton.getForeground().brighter());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                // Reset button appearance when mouse exits
                HelloButton.setForeground(invertedFrameBackground); 
            }
        });

        optionsButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                // Change button appearance when mouse enters
                optionsButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
                optionsButton.setForeground(optionsButton.getForeground().brighter());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                // Reset button appearance when mouse exits
                optionsButton.setForeground(invertedFrameBackground);
            }
        });

        ipTextField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (ipTextField.getText().equals("IP address")) {
                    ipTextField.setText("");
                    ipTextField.setForeground(Color.BLACK);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (ipTextField.getText().isEmpty()) {
                    ipTextField.setForeground(Color.GRAY);
                    ipTextField.setText("IP address");
                }
            }
        });

        portTextField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (portTextField.getText().equals("port")) {
                    portTextField.setText("");
                    portTextField.setForeground(Color.BLACK);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (portTextField.getText().isEmpty()) {
                    portTextField.setForeground(Color.GRAY);
                    portTextField.setText("port");
                }
            }
        });

        // When the buttons are clicked
        startSync.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!started) {
                    Boolean validLocal = new File(path1.getText()).exists() 
                        && new File(path1.getText()).isDirectory() 
                        && new File(path2.getText()).exists()
                        && new File(path2.getText()).isDirectory();

                    Boolean validNetwork = false; // to change

                    if (validLocal || validNetwork) {
                        if (!networkMode) {
                            dsync.setSync(true);
                            if (startSync.getText().equals("Start Syncing")) {
                                dsync.setPath1(path1.getText().trim());
                                dsync.setPath2(path2.getText().trim());
                                path1.setEditable(false);
                                path1.setBackground(Color.LIGHT_GRAY);
                                path2.setEditable(false);
                                path2.setBackground(Color.LIGHT_GRAY);

                                networkButton.setEnabled(false);
                                networkButton.setBackground(Color.LIGHT_GRAY);
                            }

                            if (!dsync.isAlive()) dsync.start();
                        }
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

                // Saving user data :
                try {
                    FileWriter userData = new FileWriter(userDataPath);

                    String data = path1.getText().trim() + "\n" 
                        + path2.getText().trim() + "\n" 
                        + ipTextField.getText().trim() + "\n" 
                        + portTextField.getText().trim() + "\n";

                    userData.write(data);
                    userData.close();
                } catch (IOException e1) {}

                // Updating the saved data :
                try {
                    updateData();
                } catch (FileNotFoundException e1) {}

            }
        });

        resetSync.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                path1.setText("");
                path2.setText("");
                path1.setEditable(true);
                path1.setBackground(Color.WHITE);

                path1.add(browseButton1, BorderLayout.EAST);
                path1.revalidate();
                path1.repaint();
                browse1Removed = false;

                if (!networkMode) {
                    path2.setEditable(true);
                    path2.setBackground(Color.WHITE);
                    path2.add(browseButton2, BorderLayout.EAST);
                    path2.revalidate();
                    path2.repaint();
                    browse2Removed = false;
                }
                else {
                    ipTextField.setText("IP address");
                    ipTextField.setForeground(Color.GRAY);
                    
                    portTextField.setText("port");
                    portTextField.setForeground(Color.GRAY);
                }

                networkButton.setEnabled(true);
                networkButton.setBackground(new Color(242, 199, 138));

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

        networkButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!networkMode) {
                    networkButton.setText("<html><p style=\"text-align: center;\">Use two<br/>local folders</p></html>");
                    label1.setText("Local folder path :");
                    networkMode = true;

                    label2.setText("Put in the IP adress and port of a server-host :");
                    path2.setEditable(false);
                    path2.setText("");
                    path2.setBackground(new Color(220, 220, 220));
                    path2.remove(browseButton2);

                    path2.add(hostButton, BorderLayout.WEST);
                    path2.add(enterNetworkInfo, BorderLayout.CENTER);
                    path2.add(startSession, BorderLayout.EAST);
                    
                    try {
                        path1.setText(data.get(0));
                        ipTextField.setText(data.get(2));
                        portTextField.setText(data.get(3));
                    } catch(Exception error) {
                        // if the data wasn't saved correctly
                    }

                    path2.revalidate();
                    path2.repaint();
                }
                else {
                    networkButton.setText("<html><p style=\"text-align: center;\">Connect to<br>another computer</p></html>");
                    label1.setText("First folder path :");
                    networkMode = false;

                    label2.setText("Second folder path :");
                    path2.setEditable(true);
                    path2.setBackground(Color.WHITE);
                    path2.add(browseButton2, BorderLayout.EAST);

                    path2.remove(hostButton);
                    path2.remove(startSession);
                    path2.remove(enterNetworkInfo);

                    try {
                        path1.setText(data.get(0));
                        path2.setText(data.get(1));
                    } catch(Exception error) {
                        // if the data wasn't saved correctly
                    }

                    path2.revalidate();
                    path2.repaint();
                }
            }
        });

        hostButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (hostButton.isSelected()) {
                    hostButton.setText("Become client"); 
                    hostButton.setBackground(Color.GRAY);

                    startSession.setText("Start");

                    label2.setText("Put in the desired port number :                    ");

                    // enterNetworkInfo.remove(ipAddressLabel);
                    enterNetworkInfo.remove(ipTextField);
                }
                else {
                    hostButton.setText("Host server");
                    hostButton.setBackground(Color.WHITE);

                    startSession.setText("Connect");

                    label2.setText("Put in the IP address and port of a server-host :");

                    c.fill = GridBagConstraints.BOTH;
                    c.gridy = 0;
                    c.gridwidth = 1;

                    c.gridx = 0;
                    c.insets = new Insets(0, 0, 0, 0);
                    // enterNetworkInfo.add(ipAddressLabel, c);

                    c.gridx = 1;
                    c.insets = new Insets(0, 10, 0, 0);
                    enterNetworkInfo.add(ipTextField, c);
                }
            }
        });

        // Every second to update elements without user input
        new Timer(300, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updatePanel(messages, styledMsg, dsync);

                if (path1.getText().isEmpty() && browse1Removed) {
                    path1.add(browseButton1, BorderLayout.EAST);
                    browseButton1.setBackground(Color.LIGHT_GRAY);
                    path1.revalidate();
                    path1.repaint();
                    browse1Removed = false;
                }

                if (!path1.getText().isEmpty() && !browse1Removed) {
                    path1.remove(browseButton1);
                    browse1Removed = true;
                }

                if (!path1.getText().isEmpty() && !started) {
                    if (new File(path1.getText()).exists() && new File(path1.getText()).isDirectory()) {
                        path1.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(187, 252, 203)));
                    }
                    else {
                        path1.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(250, 90, 90)));
                    }
                }
                else {
                    path1.setBorder(BorderFactory.createEmptyBorder());
                }

                if (!networkMode) {
                    if (path2.getText().isEmpty() && browse2Removed) {
                        path2.add(browseButton2, BorderLayout.EAST);
                        browseButton2.setBackground(Color.LIGHT_GRAY);
                        path2.revalidate();
                        path2.repaint();
                        browse2Removed = false;
                    }
    
                    if (!path2.getText().isEmpty() && !browse2Removed ) {
                        path2.remove(browseButton2);
                        browse2Removed = true;
                    }

                    if (!path2.getText().isEmpty() && !started) {
                        if (new File(path2.getText()).exists() && new File(path2.getText()).isDirectory()) {
                            path2.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(187, 252, 203)));
                        }
                        else {
                            path2.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(250, 90, 90)));
                        }
                    }
                    else {
                        path2.setBorder(BorderFactory.createEmptyBorder());
                    }
                }
                else {
                    if (!ipTextField.getText().equals("IP address")) {
                        ipTextField.setForeground(Color.BLACK);
                        if (isValid(ipTextField.getText().trim())) {
                            ipTextField.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(187, 252, 203)));
                        }
                        else {
                            ipTextField.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(250, 90, 90)));
                        }
                    }
                    else {
                        ipTextField.setBorder(BorderFactory.createEmptyBorder());
                    }

                    if (!portTextField.getText().equals("port")) {
                        portTextField.setForeground(Color.BLACK);
                        if (isNumber(portTextField.getText().trim())) {
                            portTextField.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(187, 252, 203)));
                        }
                        else {
                            portTextField.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(250, 90, 90)));
                        }
                    }
                    else {
                        portTextField.setBorder(BorderFactory.createEmptyBorder());
                    }
                }
                
            }
        }).start();

        //### END OF SECTION ###\\

        updatePanel(messages, styledMsg, dsync);
        window.add(frame, BorderLayout.CENTER);
        window.add(optionsPanel, BorderLayout.NORTH);
        window.setVisible(true);
     }

     public static void main(String[] args) throws IOException {
        new Gui().application();
     }
}

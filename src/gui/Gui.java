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

    private Color defaultFrameBG = new Color(230, 230, 230);

    private Boolean isServer = false;
    private Network network;

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

    public void buttonHover(JButton button) {
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                // Change button appearance when mouse enters
                button.setBackground(button.getBackground().darker());
                button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                // Reset button appearance when mouse exits
                button.setBackground(button.getBackground().brighter());
            }
        });
    }

    public Boolean isDark(Color c) {
        float[] hsb = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);

        float brightness = hsb[2];

        if (brightness > 0.5) {
            return false;
        } else {
            return true;
         }
    }

    public void setPathTextColorWithBG(JLabel label, JTextField path, Color frameBG) {
        if (isDark(frameBG)) {
            path.setForeground(Color.WHITE);
            label.setForeground(Color.WHITE);
        }
        else {
            path.setForeground(Color.BLACK);
            label.setForeground(Color.BLACK);
        }
    }

    public void setIPTextColorWithBG(JTextField path, Color frameBG) {
        if (isDark(frameBG)) {
            if (path.getText().equals("IP address") || path.getText().equals("port")) {
                path.setForeground(Color.GRAY);
            }
            else {
                path.setForeground(Color.WHITE);
            }
        }
        else {
            if (path.getText().equals("IP address") || path.getText().equals("port")) {
                path.setForeground(Color.GRAY);
            }
            else {
                path.setForeground(Color.BLACK);
            }
        }
    }

    public List<String> localOrNetworkMessages(Boolean networkMode, List<String> localMessages, List<String> networkMessages) {
        if (!networkMode) {
            return localMessages;
        }
        else {
            return networkMessages;
        }
    }

    public void updatePanel(JTextPane jTextPane, StyledDocument styledDoc, List<String> messages) {
        jTextPane.setText("");
        int pos = 0;
        for (String msg : messages) {
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

    public void updateTheme(JPanel frame, JTextField path1, JLabel label1, JTextField path2, JLabel label2, 
        JTextPane messages, JPanel panel1, JPanel panel2, JPanel pathsPanel, JPanel panel3, JPanel panel4,
        JPanel panel5, BackgroundRotation syncIcon, JButton optionsButton, JButton helloButton, JPanel optionsPanel,
        JTextField ipTextField, JTextField portTextField, JPanel enterNetworkInfo) {

            Color frameBG = frame.getBackground();

            path1.setBackground(frameBG.brighter());
            path2.setBackground(frameBG.brighter());

            setPathTextColorWithBG(label1, path1, frameBG);
            setPathTextColorWithBG(label2, path2, frameBG);

            messages.setBackground(frameBG.brighter());
            if (isDark(frameBG)) {
                messages.setForeground(Color.WHITE);
            }
            else {
                messages.setForeground(Color.BLACK);
            }

            panel1.setBackground(frameBG);
            panel2.setBackground(frameBG);
            panel3.setBackground(frameBG);
            panel4.setBackground(frameBG);
            panel5.setBackground(frameBG);
            pathsPanel.setBackground(frameBG);

            syncIcon.setBackground(frameBG);

            Color darkerFrameBackground = new Color(Math.abs(frameBG.getRed() - 15), Math.abs(frameBG.getGreen() - 15), Math.abs(frameBG.getBlue() - 15));
            optionsPanel.setBackground(darkerFrameBackground);
            optionsButton.setBackground(darkerFrameBackground);
            helloButton.setBackground(darkerFrameBackground);

            if (isDark(frameBG)) {
                optionsButton.setForeground(Color.LIGHT_GRAY);
                helloButton.setForeground(Color.LIGHT_GRAY);
            }
            else {
                optionsButton.setForeground(Color.DARK_GRAY);
                helloButton.setForeground(Color.DARK_GRAY);
            }

            ipTextField.setBackground(frameBG.brighter());
            portTextField.setBackground(frameBG.brighter());
            enterNetworkInfo.setBackground(frameBG);

            frame.repaint();
        }

    public void application() throws Exception{
        System.setProperty("sun.java2d.uiScale.enabled", "false");
        System.setProperty("apple.awt.uiScale", "1.0");

        updateData(); // Making sure the data is up to date when the app opens

        network = new Client(null, 0, null);
        network.addMessage("Press a button to get started.");

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

        try {
            frame.setBackground(new Color(Integer.valueOf(data.get(4))));
        } 
        catch(Exception e) {
            frame.setBackground(defaultFrameBG);
        }
        Color invertedFrameBackground = new Color(255 - frame.getBackground().getRed(), 255 - frame.getBackground().getGreen(), 255 - frame.getBackground().getBlue());
        Color darkerFrameBackground = new Color(Math.abs(frame.getBackground().getRed() - 15), Math.abs(frame.getBackground().getGreen() - 15), Math.abs(frame.getBackground().getBlue() - 15));


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

        setPathTextColorWithBG(label1, path1, frame.getBackground());

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
        } 
        catch(Exception e) {
            // if the data wasn't saved correctly
        }
        
        JLabel label2 = new JLabel("Second folder path :");
        label2.setFont(labelFont);

        setPathTextColorWithBG(label2, path2, frame.getBackground());

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

        JPanel enterNetworkInfo = new JPanel(new GridBagLayout());
        enterNetworkInfo.setBackground(frame.getBackground());

        // JLabel ipAddressLabel = new JLabel("IP address :");
        JTextField ipTextField = new JTextField("IP address");
        ipTextField.setBorder(BorderFactory.createEmptyBorder());
        ipTextField.setFont(pathFont);
        setIPTextColorWithBG(ipTextField, frame.getBackground());
        ipTextField.setBackground(frame.getBackground().brighter());

        // JLabel portLabel = new JLabel("Port number :");
        JTextField portTextField = new JTextField("port");
        portTextField.setBorder(BorderFactory.createEmptyBorder());
        portTextField.setFont(pathFont);
        setIPTextColorWithBG(portTextField, frame.getBackground());
        portTextField.setBackground(frame.getBackground().brighter());

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
        messages.setBackground(frame.getBackground().brighter());
        if (isDark(frame.getBackground())) {
            messages.setForeground(Color.WHITE);
        }
        else {
            messages.setForeground(Color.BLACK);
        }

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

        JButton optionsButton = new JButton("Change theme");
        optionsButton.setFont(browseButtonFont);
        optionsButton.setBackground(optionsPanel.getBackground());
        optionsButton.setFocusPainted(false);
        optionsButton.setOpaque(true);
        optionsButton.setBorderPainted(false);
        optionsButton.setForeground(invertedFrameBackground);

        JButton HelloButton = new JButton("Dsync, by Geryes and Marc.");
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
        buttonHover(browseButton1);

        buttonHover(browseButton2);

        buttonHover(startSync);

        buttonHover(resetSync);

        networkButton.addMouseListener(new MouseAdapter() { // Un peu spécial
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

        hostButton.addMouseListener(new MouseAdapter() { // Jtoggle button, cannot call buttonHover function
            @Override
            public void mouseEntered(MouseEvent e) {
                hostButton.setBackground(hostButton.getBackground().darker());
                hostButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                hostButton.setBackground(hostButton.getBackground().brighter());
            }
        });

        HelloButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                // Change cursor when mouse enters
                HelloButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                // No need to change the cursor
            }
        });

        optionsButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                // Change cursor when mouse enters
                optionsButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                // No need to change the cursor
            }
        });

        ipTextField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (ipTextField.getText().equals("IP address")) {
                    ipTextField.setText("");
                    setIPTextColorWithBG(ipTextField, frame.getBackground());
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (ipTextField.getText().isEmpty()) {
                    ipTextField.setText("IP address");
                    setIPTextColorWithBG(ipTextField, frame.getBackground());
                }
            }
        });

        portTextField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (portTextField.getText().equals("port")) {
                    portTextField.setText("");
                    setIPTextColorWithBG(portTextField, frame.getBackground());
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (portTextField.getText().isEmpty()) {
                    portTextField.setText("port");
                    setIPTextColorWithBG(portTextField, frame.getBackground());
                }
            }
        });

        // When the buttons are clicked
        startSync.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!started) {
                    
                    if (!networkMode) {
                        Boolean validLocal = new File(path1.getText()).exists() 
                        && new File(path1.getText()).isDirectory() 
                        && new File(path2.getText()).exists()
                        && new File(path2.getText()).isDirectory();

                        if (validLocal) {
                            dsync.setSync(true);
                            if (startSync.getText().equals("Start Syncing")) {
                                dsync.setPath1(path1.getText().trim());
                                dsync.setPath2(path2.getText().trim());
                                path1.setEditable(false);
                                path1.setBackground(frame.getBackground().darker());
                                path1.setBorder(BorderFactory.createEmptyBorder());
                                path2.setEditable(false);
                                path2.setBackground(frame.getBackground().darker());
                                path2.setBorder(BorderFactory.createEmptyBorder());
    
                                networkButton.setEnabled(false);
                                networkButton.setBackground(Color.LIGHT_GRAY);
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
                    }
                    else { // If network mode is enabled
                        Boolean validPath = new File(path1.getText()).exists() && new File(path1.getText()).isDirectory();
                        Boolean validNetwork;
                        if (isServer) {
                            if (isNumber(portTextField.getText().trim())) {
                                validNetwork = true;
                            }
                            else {
                                validNetwork = false;
                            }
                        }
                        else {
                            if (isNumber(portTextField.getText().trim()) && isValid(ipTextField.getText().trim())) {
                                validNetwork = true;
                            }
                            else {
                                validNetwork = false;
                            }
                        }

                        if (validPath && validNetwork) {

                            if (startSync.getText().equals("Start Syncing")) {
                                path1.setEditable(false);
                                path1.setBackground(frame.getBackground().darker());
                                path1.setBorder(BorderFactory.createEmptyBorder());

                                ipTextField.setEditable(false);
                                ipTextField.setBackground(frame.getBackground().darker());
                                ipTextField.setBorder(BorderFactory.createEmptyBorder());
                                portTextField.setEditable(false);
                                portTextField.setBackground(frame.getBackground().darker());
                                portTextField.setBorder(BorderFactory.createEmptyBorder());

                                networkButton.setEnabled(false);
                                networkButton.setBackground(Color.LIGHT_GRAY);
                            }
                                
                            started = true;
    
                            startSync.setText("Pause Syncing");
                            startSync.setBackground(new Color(250, 90, 90));
    
                            syncIcon.setRotate(true); // Start rotation

                            if (isServer) {
                                try {
                                    if (network.getSync() == null) {
                                        System.out.println("Test");
                                        network = new Server(Integer.valueOf(portTextField.getText().trim()), new File(path1.getText().trim()).getAbsolutePath());
                                        network.addMessage("");
                                    }
                                    network.setActive(true);
                                    System.out.println(network.getActive());
                                    network.setSync(true);

                                    if (!network.isAlive()) network.start();
                                } 
                                catch (NumberFormatException e1) {} 
                                catch (Exception e1) {}
                            }
                            else {
                                try {
                                    if (network.getSync() == null) {
                                        System.out.println("Test");
                                        network = new Client(ipTextField.getText().trim(), Integer.valueOf(portTextField.getText().trim()), new File(path1.getText().trim()).getAbsolutePath());
                                        network.addMessage("");
                                    }
                                    network.setActive(true);
                                    System.out.println(network.getActive());
                                    network.setSync(true);

                                    if (!network.isAlive()) network.start();
                                } 
                                catch (NumberFormatException e1) {} 
                                catch (Exception e1) {}
                            }
                        }
                        else {
                            network.addMessage("Invalid fields.");
                        }

                    }

                    updatePanel(messages, styledMsg, localOrNetworkMessages(networkMode, dsync.getMessages(), network.getMessages()));
                }
                
                else { // If it has already been started
                    if (!networkMode) {
                        dsync.setSync(false);
                        dsync.addMessage("Syncing Paused.");
                    }
                    else {
                        network.setSync(false);
                        network.addMessage("Syncing Paused.");
                    }


                    updatePanel(messages, styledMsg, localOrNetworkMessages(networkMode, dsync.getMessages(), network.getMessages()));
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
                        + portTextField.getText().trim() + "\n"
                        + frame.getBackground().getRGB() + "\n";

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
                path1.setBackground(frame.getBackground().brighter());
                path1.add(browseButton1, BorderLayout.EAST);
                path1.revalidate();
                path1.repaint();
                browse1Removed = false;

                if (!networkMode) {
                    path2.setEditable(true);
                    path2.setBackground(frame.getBackground().brighter());
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
                    
                    ipTextField.setEditable(true);
                    ipTextField.setBackground(frame.getBackground().brighter());
                    portTextField.setEditable(true);
                    portTextField.setBackground(frame.getBackground().brighter());
                }

                networkButton.setEnabled(true);
                networkButton.setBackground(new Color(242, 199, 138));

                syncIcon.setRotate(false); // Stop rotation

                if (!networkMode) {
                    dsync.resetMessages();
                    dsync.setFirstSync(true);
                    dsync.setSync(false);
                }
                else {
                    network.resetMessages();
                    // network.setFirstSync(true);
                    network.setSync(false);
                    network.close();
                    network.setActive(false);
                    System.out.println(network.getActive());
                }
                updatePanel(messages, styledMsg, localOrNetworkMessages(networkMode, dsync.getMessages(), network.getMessages()));

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
                    path2.setBorder(BorderFactory.createEmptyBorder());
                    path2.remove(browseButton2);

                    path2.add(hostButton, BorderLayout.WEST);
                    path2.add(enterNetworkInfo, BorderLayout.CENTER);
                    
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
                    path2.setBackground(frame.getBackground().brighter());
                    path2.add(browseButton2, BorderLayout.EAST);

                    path2.remove(hostButton);
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
                    isServer = true;
                    hostButton.setText("Become client"); 
                    hostButton.setBackground(Color.GRAY);

                    label2.setText("Put in the desired port number :                    ");

                    // enterNetworkInfo.remove(ipAddressLabel);
                    enterNetworkInfo.remove(ipTextField);
                }
                else {
                    isServer = false;
                    hostButton.setText("Host server");
                    hostButton.setBackground(Color.WHITE);

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

        optionsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Color selectedColor = JColorChooser.showDialog(frame, "Choose a Color", Color.WHITE);
                // if (selectedColor != null) {
                //     frame.setBackground(selectedColor);
                //     updateTheme(frame, path1, label1, path2, label2, messages, panel1, panel2,
                //         pathsPanel, panel3, panel4, panel5, syncIcon, optionsButton, HelloButton, optionsPanel);
                // }

                ThemeFrame newTheme = new ThemeFrame(window);
                newTheme.setBackground(frame.getBackground());
                newTheme.setChosenColor(frame.getBackground());
                newTheme.setVisible(true);
                newTheme.setModal(true);

                frame.setBackground(newTheme.chosenColor());
                updateTheme(frame, path1, label1, path2, label2, messages, panel1, panel2,
                    pathsPanel, panel3, panel4, panel5, syncIcon, optionsButton, HelloButton, optionsPanel,
                    ipTextField, portTextField, enterNetworkInfo);
            }
            
        });

        // Every second to update elements without user input
        new Timer(300, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updatePanel(messages, styledMsg, localOrNetworkMessages(networkMode, dsync.getMessages(), network.getMessages()));

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
                        setIPTextColorWithBG(ipTextField, frame.getBackground());
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
                        setIPTextColorWithBG(ipTextField, frame.getBackground());
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

        updatePanel(messages, styledMsg, dsync.getMessages());
        window.add(frame, BorderLayout.CENTER);
        window.add(optionsPanel, BorderLayout.NORTH);
        window.setVisible(true);
     }

     public static void main(String[] args) throws Exception {
        new Gui().application();
     }
}

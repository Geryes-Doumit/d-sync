package src.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class ThemeFrame extends JDialog {
    private Color chosenColor;
    
    // Different theme colors
    private Color lightThemeColor = new Color(230, 230, 230);
    private Color lightPinkThemeColor = new Color(245, 171, 231);
    private Color darkThemeColor = Color.DARK_GRAY;
    private Color darkPurpleThemeColor = new Color(40, 40, 60);

    public Color chosenColor() {
        return chosenColor;
    }

    public void setChosenColor(Color c) {
        chosenColor = c;
    }

    public JButton newThemeButton(String name, Color themeColor, Color buttonTextColor) {
        JButton button = new JButton(name);
        button.setBackground(themeColor);
        button.setForeground(buttonTextColor);

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setChosenColor(themeColor);
                dispose();
            }
        });

        return button;
    }

    public ThemeFrame(JFrame parent) {
        super(parent, "Choose theme", true);

        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        setSize(300, 500);
        setLocationRelativeTo(null);

        JButton lightTheme = newThemeButton("Default Light Theme", lightThemeColor, darkThemeColor);

        JButton lightPinkTheme = newThemeButton("Light Pink Theme", lightPinkThemeColor, darkThemeColor);

        JButton darkTheme = newThemeButton("Dark Gray Theme", darkThemeColor, lightThemeColor);

        JButton darkPurpleTheme = newThemeButton("Dark Purple Theme", darkPurpleThemeColor, lightThemeColor);

        JPanel customPanel = new JPanel(new BorderLayout());
        ColorInputPanel customTheme = new ColorInputPanel();
        JButton customButton = new JButton("Select custom theme");
        customButton.setBackground(getBackground());

        customButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setChosenColor(customTheme.chosenColor());
                dispose();
            }
            
        });

        customPanel.add(customTheme, BorderLayout.NORTH);
        customPanel.add(customButton, BorderLayout.SOUTH);

        c.gridx = 0;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(10, 10, 10, 10);

        c.gridy = 0;
        add(lightTheme, c);

        c.gridy = 1;
        add(lightPinkTheme, c);

        c.gridy = 2;
        add(darkTheme, c);

        c.gridy = 3;
        add(darkPurpleTheme, c);

        c.gridy = 4;
        add(customPanel, c);
    }
    
}

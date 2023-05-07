package src.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

/**
 * Class that creates a frame that allows the user to choose a theme.
 * <br/>
 * Author: Geryes Doumit
 */
public class ThemeFrame extends JDialog {
    private Color chosenColor;
    
    // Different theme colors
    private Color lightThemeColor = new Color(230, 230, 230);
    private Color lightPinkThemeColor = new Color(245, 171, 231);
    private Color darkThemeColor = Color.DARK_GRAY;
    private Color darkPurpleThemeColor = new Color(40, 40, 60);

    /**
     * Get the chosen color.
     * @return The chosen color.
     */
    public Color chosenColor() {
        return chosenColor;
    }

    /**
     * Set the chosen color.
     * @param c The chosen color.
     */
    public void setChosenColor(Color c) {
        chosenColor = c;
    }

    /**
     * Create a new theme button.
     * @param name The name of the button.
     * @param themeColor The color of the button.
     * @param buttonTextColor The color of the text of the button.
     * @return The new theme button.
     */
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

    /**
     * Constructor of the ThemeFrame class that generates the frame with the background color given. It needs to know the parent frame.
     * @param parent The parent frame.
     * @param bg The background color of the frame.
     */
    public ThemeFrame(JFrame parent, Color bg) {
        super(parent, "Choose theme", true);

        JPanel frame = new JPanel();
        frame.setBackground(bg);

        frame.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        setSize(800, 600);
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
        frame.add(lightTheme, c);

        c.gridy = 1;
        frame.add(lightPinkTheme, c);

        c.gridy = 2;
        frame.add(darkTheme, c);

        c.gridy = 3;
        frame.add(darkPurpleTheme, c);

        c.gridy = 4;
        frame.add(customPanel, c);

        add(frame);
    }
    
}

package src.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Class that creates a JPanel that allows the user to choose a color.
 * <br/>
 * Author: Geryes Doumit
 */
public class ColorInputPanel extends JPanel {
    private JTextField redField;
    private JTextField greenField;
    private JTextField blueField;
    private JPanel previewPanel;

    private Color chosenColor;

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
     * The constructor of the ColorInputPanel class. Creates a new color input panel.
     */
    public ColorInputPanel() {
        JLabel title = new JLabel("Custom theme:");

        // Create the RGB input fields
        redField = new JTextField("55", 3);
        greenField = new JTextField("90", 3);
        blueField = new JTextField("60", 3);

        // Create the preview panel
        previewPanel = new JPanel();
        previewPanel.setPreferredSize(new Dimension(50, 50));
        previewPanel.setBackground(new Color(0, 0, 0));

        new Timer(30, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    // Get the RGB values from the input fields
                    int red = Integer.parseInt(redField.getText());
                    int green = Integer.parseInt(greenField.getText());
                    int blue = Integer.parseInt(blueField.getText());

                    if (0 <= red && red <= 255 && 0 <= green && green <= 255 && 0 <= blue && blue <= 255) {
                        // Create a new color from the RGB values
                        Color color = new Color(red, green, blue);

                        // Update the preview panel with the new color
                        previewPanel.setBackground(color);

                        //Set the chosen color value:
                        setChosenColor(color);
                    }
                } catch (NumberFormatException ex) {
                    // If one of the input fields is not a number, do nothing
                }
            }
        }).start();

        // Create a panel for the RGB input fields
        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("Red: "));
        inputPanel.add(redField);
        inputPanel.add(new JLabel("Green: "));
        inputPanel.add(greenField);
        inputPanel.add(new JLabel("Blue: "));
        inputPanel.add(blueField);

        // Add the components to the frame
        setLayout(new BorderLayout());
        add(title, BorderLayout.NORTH);
        add(inputPanel, BorderLayout.CENTER);
        add(previewPanel, BorderLayout.SOUTH);

        // Set the size and show the frame
        setVisible(true);
    }
}


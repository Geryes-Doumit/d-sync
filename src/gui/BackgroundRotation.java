package src.gui;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.*;

/**
 * Class that creates a JPanel that rotates a background image. Used for the animation of the sync icon.
 * <br/>
 * Author: Geryes Doumit
 */
public class BackgroundRotation extends JPanel {
    private BufferedImage bgImage;
    private double angle = 0;

    private Boolean rotate = false;

    /**
     * The constructor of the BackgroundRotation class.
     * @param path The path of the background image.
     */
    public BackgroundRotation(String path) {
        try {
            bgImage = ImageIO.read(getClass().getResource(path));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // Timer that updates the rotation angle every 100 milliseconds
        Timer timer = new Timer(30, e -> {
            if (rotate) {
                angle -= 8;
            }
            repaint();
        });
        timer.start();
    }

    /**
     * Set the state of the rotation of the background image.
     * @param bool
     */
    public void setRotate(Boolean bool) {
        this.rotate = bool;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D)g.create();

        // Calculate the center point of the panel
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;

        // Create an AffineTransform that rotates the background image
        AffineTransform transform = new AffineTransform();
        transform.rotate(Math.toRadians(angle), centerX, centerY);

        // Apply the transform to the graphics object
        g2d.setTransform(transform);

        // Draw the background image
        g2d.drawImage(bgImage, 0, 0, getWidth(), getHeight(), null);

        g2d.dispose();
    }
}

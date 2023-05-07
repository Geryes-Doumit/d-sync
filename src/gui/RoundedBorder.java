package src.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import javax.swing.border.Border;

/**
 * Class that creates a rounded border.
 * <br/>
 * Author: Geryes Doumit
 */
public class RoundedBorder implements Border {
    private int radius;
    private Color color;
    private int thickness;

    /**
     * Constructor of the RoundedBorder class.
     * 
     * @param radius The radius of the border.
     * @param color The color of the border.
     * @param thickness The thickness of the border.
     */
    public RoundedBorder(int radius, Color color, int thickness) {
        this.radius = radius;
        this.color = color;
        this.thickness = thickness;
    }

    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setStroke(new BasicStroke(thickness));
        g2.setColor(color);

        RoundRectangle2D roundRectangle = new RoundRectangle2D.Float(x + thickness / 2, y + thickness / 2,
                width - thickness, height - thickness, radius, radius);
        g2.draw(roundRectangle);
    }

    /**
     * {@inheritDoc}
     */
    public Insets getBorderInsets(Component c) {
        return new Insets(thickness, thickness, thickness, thickness);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isBorderOpaque() {
        return true;
    }

    // Getters

    /**
     * Get the radius of the border.
     * @return The radius of the border.
     */
    public int getRadius() {
        return radius;
    }

    /**
     * Get the color of the border.
     * @return The color of the border.
     */
    public Color getColor() {
        return color;
    }

    /**
     * Get the thickness of the border.
     * @return The thickness of the border.
     */
    public int getThickness() {
        return thickness;
    }
}

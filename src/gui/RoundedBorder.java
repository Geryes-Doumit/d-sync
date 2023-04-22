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

public class RoundedBorder implements Border {
    private int radius;
    private Color color;
    private int thickness;

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

    public Insets getBorderInsets(Component c) {
        return new Insets(thickness, thickness, thickness, thickness);
    }

    public boolean isBorderOpaque() {
        return true;
    }

    public int getRadius() {
        return radius;
    }

    public Color getColor() {
        return color;
    }

    public int getThickness() {
        return thickness;
    }
}

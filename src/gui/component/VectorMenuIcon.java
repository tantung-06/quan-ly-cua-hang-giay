package gui.component;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;

import javax.swing.Icon;

import gui.UiTheme;

public final class VectorMenuIcon implements Icon {
    private final String name;
    private final Color defaultColor;
    private boolean selected;

    public VectorMenuIcon(String name) {
        this(name, UiTheme.MUTED);
    }

    public VectorMenuIcon(String name, Color defaultColor) {
        this.name = name;
        this.defaultColor = defaultColor;
    }

    void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public int getIconWidth() {
        return 18;
    }

    @Override
    public int getIconHeight() {
        return 18;
    }

    @Override
    public void paintIcon(Component component, Graphics graphics, int x, int y) {
        Graphics2D g = (Graphics2D) graphics.create();
        g.translate(x + 1, y + 1);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(selected ? UiTheme.PRIMARY : defaultColor);
        g.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        drawIcon(g);
        g.dispose();
    }

    private void drawIcon(Graphics2D g) {
        switch (name) {
            case "home":
                draw(g, path(2, 8, 9, 2, 16, 8, 14, 8, 14, 16, 10, 16, 10, 11, 7, 11, 7, 16, 4, 16, 4, 8, 2, 8));
                break;
            case "product":
                draw(g, path(9, 2, 16, 6, 9, 10, 2, 6, 9, 2));
                draw(g, path(2, 6, 2, 14, 9, 18, 16, 14, 16, 6));
                draw(g, path(9, 10, 9, 18));
                draw(g, path(5, 4, 12, 8));
                break;
            case "attribute":
                draw(g, path(2, 3, 10, 3, 16, 9, 9, 16, 2, 9, 2, 3));
                g.draw(new Ellipse2D.Double(5, 5, 2, 2));
                break;
            case "purchase":
                draw(g, roundRect(3, 3, 12, 14, 1.5));
                draw(g, roundRect(6, 1.5, 6, 4, 1));
                draw(g, openPath(6, 9, 12, 9));
                draw(g, openPath(6, 12, 12, 12));
                break;
            case "sale":
                draw(g, openPath(1.5, 2, 4, 2, 6.5, 12.5, 14.5, 12.5, 16.5, 6, 5, 6));
                g.draw(new Ellipse2D.Double(7, 14, 2, 2));
                g.draw(new Ellipse2D.Double(13, 14, 2, 2));
                break;
            case "supplier":
                draw(g, path(2, 4, 10, 4, 10, 8, 14, 8, 17, 11, 17, 14, 2, 14, 2, 4));
                draw(g, path(10, 8, 14, 8, 14, 11, 10, 11, 10, 8));
                g.draw(new Ellipse2D.Double(4, 13, 3, 3));
                g.draw(new Ellipse2D.Double(12, 13, 3, 3));
                break;
            case "customer":
                g.draw(new Ellipse2D.Double(6, 2, 6, 6));
                draw(g, openPath(3, 16, 3, 14, 4, 11, 9, 10, 14, 11, 15, 14, 15, 16));
                break;
            case "employee":
                g.draw(new Ellipse2D.Double(3, 3, 5, 5));
                g.draw(new Ellipse2D.Double(10, 4, 4, 4));
                draw(g, openPath(1, 16, 2, 12, 5.5, 10, 9, 12, 10, 16));
                draw(g, openPath(10, 11, 13, 10, 16, 12, 17, 16));
                break;
            case "account":
                draw(g, new Arc2D.Double(4, 1, 10, 11, 180, -180, Arc2D.OPEN));
                draw(g, roundRect(2, 8, 14, 9, 1.5));
                g.draw(new Ellipse2D.Double(8, 11, 2, 2));
                draw(g, path(9, 13, 9, 15));
                break;
            case "permission":
                draw(g, path(9, 1, 16, 4, 15, 11, 9, 17, 3, 11, 2, 4, 9, 1));
                break;
            case "promotion":
                draw(g, path(3, 15, 15, 3));
                g.draw(new Ellipse2D.Double(2, 2, 5, 5));
                g.draw(new Ellipse2D.Double(11, 11, 5, 5));
                break;
            case "statistics":
                draw(g, openPath(2, 2, 2, 16, 16, 16));
                draw(g, new Rectangle2D.Double(5, 10, 2, 5));
                draw(g, new Rectangle2D.Double(9, 6, 2, 9));
                draw(g, new Rectangle2D.Double(13, 3, 2, 12));
                break;
            case "logout":
                draw(g, path(10, 2, 3, 2, 3, 16, 10, 16));
                draw(g, openPath(8, 9, 17, 9));
                draw(g, openPath(13, 5, 17, 9, 13, 13));
                break;
            default:
                draw(g, new Ellipse2D.Double(3, 3, 12, 12));
        }
    }

    private static void draw(Graphics2D g, Shape shape) {
        g.draw(shape);
    }

    private static Shape path(double... coordinates) {
        return createPath(true, coordinates);
    }

    private static Shape openPath(double... coordinates) {
        return createPath(false, coordinates);
    }

    private static Shape createPath(boolean closed, double... coordinates) {
        Path2D path = new Path2D.Double();
        path.moveTo(coordinates[0], coordinates[1]);
        for (int i = 2; i < coordinates.length; i += 2) {
            path.lineTo(coordinates[i], coordinates[i + 1]);
        }
        if (closed) {
            path.closePath();
        }
        return path;
    }

    private static Shape roundRect(double x, double y, double width, double height, double arc) {
        return new java.awt.geom.RoundRectangle2D.Double(x, y, width, height, arc, arc);
    }
}
package gui.component;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;

import javax.swing.Icon;

import gui.UiTheme;

public final class StoreLogoIcon implements Icon {
    @Override
    public int getIconWidth() {
        return 40;
    }

    @Override
    public int getIconHeight() {
        return 40;
    }

    @Override
    public void paintIcon(Component component, Graphics graphics, int x, int y) {
        Graphics2D g = (Graphics2D) graphics.create();
        g.translate(x, y);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(UiTheme.PRIMARY);
        g.fillRoundRect(0, 0, 40, 40, 12, 12);

        Path2D shoe = new Path2D.Double();
        shoe.moveTo(6, 24);
        shoe.lineTo(8, 14);
        shoe.quadTo(12, 19, 16, 16);
        shoe.lineTo(19, 12);
        shoe.lineTo(26, 20);
        shoe.quadTo(28, 22, 32, 22);
        shoe.quadTo(35, 23, 34, 27);
        shoe.lineTo(7, 27);
        shoe.closePath();
        g.setColor(Color.WHITE);
        g.fill(shoe);
        g.setStroke(new BasicStroke(1.3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawLine(7, 30, 33, 30);
        g.setColor(UiTheme.PRIMARY);
        g.drawLine(19, 17, 22, 15);
        g.drawLine(22, 20, 25, 18);
        g.dispose();
    }
}
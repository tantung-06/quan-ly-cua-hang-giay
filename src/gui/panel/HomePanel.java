package gui.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import gui.UiTheme;

public final class HomePanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private static final String IMAGE_PATH = "src/imgs/trang-chu.png";
    private final BufferedImage image;

    public HomePanel() {
        super(new BorderLayout());
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(900, 480));
        File imageFile = new File(IMAGE_PATH);
        image = loadImage(imageFile);
        if (image == null) {
            String message = imageFile.isFile()
                    ? "<b>Không thể hiển thị ảnh</b><br><br>Kiểm tra ảnh tại:<br>"
                    : "<b>Chưa có ảnh trang chủ</b><br><br>Thêm ảnh tại:<br>";
            JLabel placeholder = new JLabel("<html><div style='text-align:center'>"
                    + message + IMAGE_PATH + "</div></html>", SwingConstants.CENTER);
            placeholder.setFont(UiTheme.regular(14));
            placeholder.setForeground(UiTheme.MUTED);
            add(placeholder, BorderLayout.CENTER);
        }
    }

    private static BufferedImage loadImage(File imageFile) {
        if (!imageFile.isFile()) {
            return null;
        }
        try {
            BufferedImage loaded = ImageIO.read(imageFile);
            if (loaded == null) {
                System.err.println("Không thể đọc ảnh trang chủ: " + IMAGE_PATH);
            }
            return loaded;
        } catch (IOException ex) {
            System.err.println("Không thể tải ảnh trang chủ: " + IMAGE_PATH);
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        if (image == null) {
            return;
        }

        Graphics2D g2 = (Graphics2D) graphics.create();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        double scale = Math.max((double) getWidth() / image.getWidth(),
                (double) getHeight() / image.getHeight());
        int width = (int) Math.round(image.getWidth() * scale);
        int height = (int) Math.round(image.getHeight() * scale);
        int x = (getWidth() - width) / 2;
        int y = (getHeight() - height) / 2;
        g2.drawImage(image, x, y, width, height, this);
        g2.dispose();
    }
}
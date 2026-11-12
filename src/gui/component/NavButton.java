package gui.component;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import gui.UiTheme;

public final class NavButton extends JPanel {
    private static final long serialVersionUID = 1L;
    private final String label;
    private final JLabel caption;
    private final JLabel iconLabel;
    private final VectorMenuIcon icon;
    private boolean selected;

    public NavButton(String label, String iconName, Runnable action) {
        this.label = label;
        setLayout(new BorderLayout(11, 0));
        setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 8));
        setPreferredSize(new Dimension(224, 42));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        icon = new VectorMenuIcon(iconName);
        iconLabel = new JLabel(icon, SwingConstants.CENTER);
        iconLabel.setPreferredSize(new Dimension(27, 27));
        caption = new JLabel(label);
        caption.setFont(UiTheme.regular(13));
        add(iconLabel, BorderLayout.WEST);
        add(caption, BorderLayout.CENTER);
        setSelected(false);
        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent event) {
                action.run();
            }

            @Override
            public void mouseEntered(java.awt.event.MouseEvent event) {
                if (!selected) {
                    setBackground(UiTheme.BACKGROUND);
                }
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent event) {
                if (!selected) {
                    setBackground(Color.WHITE);
                }
            }
        });
    }

    public String getLabel() {
        return label;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        setOpaque(true);
        setBackground(selected ? UiTheme.PRIMARY_SOFT : Color.WHITE);
        caption.setForeground(selected ? UiTheme.PRIMARY : UiTheme.MUTED);
        caption.setFont(selected ? UiTheme.bold(13) : UiTheme.regular(13));
        icon.setSelected(selected);
        iconLabel.repaint();
    }
}
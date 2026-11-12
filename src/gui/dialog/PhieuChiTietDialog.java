package gui.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import gui.UiFormats;
import gui.UiTheme;

public final class PhieuChiTietDialog {
    private PhieuChiTietDialog() {
    }

    public static void show(Component parent, String title, String[][] fields,
            String[] columns, Object[][] rows) {
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(parent), title,
                JDialog.ModalityType.APPLICATION_MODAL);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel content = new JPanel(new BorderLayout(0, 16));
        content.setBackground(Color.WHITE);
        content.setBorder(BorderFactory.createEmptyBorder(20, 22, 18, 22));

        JPanel header = new JPanel(new GridLayout(0, 2, 12, 10));
        header.setOpaque(false);
        for (String[] field : fields) {
            JLabel label = new JLabel(field[0]);
            label.setFont(UiTheme.bold(12));
            label.setForeground(UiTheme.MUTED);
            JLabel value = new JLabel(field[1]);
            value.setFont(UiTheme.regular(13));
            value.setForeground(UiTheme.TEXT);
            header.add(label);
            header.add(value);
        }
        content.add(header, BorderLayout.NORTH);

        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        for (Object[] row : rows) {
            model.addRow(row);
        }
        JTable table = new JTable(model);
        table.setFillsViewportHeight(true);
        table.setRowHeight(38);
        table.setFont(UiTheme.regular(13));
        table.setForeground(UiTheme.TEXT);
        table.setGridColor(UiTheme.BORDER);
        table.setShowVerticalLines(false);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setPreferredSize(new Dimension(0, 38));
        table.getTableHeader().setFont(UiTheme.bold(11));
        table.getTableHeader().setForeground(UiTheme.MUTED);
        table.getTableHeader().setBackground(new Color(0xF9FAFB));
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;

            @Override
            public java.awt.Component getTableCellRendererComponent(JTable source, Object value,
                    boolean selected, boolean focus, int row, int column) {
                JLabel cell = (JLabel) super.getTableCellRendererComponent(
                        source, value, selected, focus, row, column);
                cell.setFont(UiTheme.regular(13));
                cell.setForeground(UiTheme.TEXT);
                cell.setBackground(selected ? UiTheme.PRIMARY_SOFT : Color.WHITE);
                cell.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 8));
                String columnName = source.getColumnName(column);
                if (value instanceof Number && ("Đơn giá".equals(columnName)
                        || "Giá nhập".equals(columnName) || "Thành tiền".equals(columnName))) {
                    cell.setText(UiFormats.currency(((Number) value).doubleValue()));
                }
                return cell;
            }
        });
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(UiTheme.BORDER));
        scroll.getViewport().setBackground(Color.WHITE);
        content.add(scroll, BorderLayout.CENTER);

        JButton close = new JButton("Đóng");
        close.setFont(UiTheme.bold(12));
        close.setForeground(UiTheme.TEXT);
        close.setBackground(Color.WHITE);
        close.setFocusPainted(false);
        close.addActionListener(event -> dialog.dispose());
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        actions.setOpaque(false);
        actions.add(close);
        content.add(actions, BorderLayout.SOUTH);

        dialog.setContentPane(content);
        dialog.setSize(800, Math.max(520, 230 + fields.length * 28));
        dialog.setMinimumSize(new Dimension(620, 420));
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }
}
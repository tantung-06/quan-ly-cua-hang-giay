package gui.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import gui.UiTheme;
import security.AccessPolicy;
import security.PermissionAction;
import security.PermissionModule;

public final class PermissionMatrixPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private final DefaultTableModel model;

    public PermissionMatrixPanel(String role, String stored) {
        super(new BorderLayout(0, 8));
        boolean admin = AccessPolicy.ADMIN_ROLE.equals(role);
        PermissionAction[] actions = PermissionAction.values();
        String[] columns = new String[actions.length + 1];
        columns[0] = "Chức năng";
        for (int i = 0; i < actions.length; i++) columns[i + 1] = actions[i].label();
        model = new DefaultTableModel(columns, 0) {
            private static final long serialVersionUID = 1L;

            @Override public Class<?> getColumnClass(int column) {
                return column == 0 ? String.class : Boolean.class;
            }

            @Override public boolean isCellEditable(int row, int column) {
                return !admin && column > 0 && PermissionModule.values()[row].supports(actions[column - 1]);
            }

            @Override public void setValueAt(Object value, int row, int column) {
                super.setValueAt(value, row, column);
                if (column == 1 && Boolean.FALSE.equals(value)) {
                    for (int i = 2; i < getColumnCount(); i++) super.setValueAt(false, row, i);
                } else if (column > 1 && Boolean.TRUE.equals(value)) {
                    super.setValueAt(true, row, 1);
                }
            }
        };
        Set<String> permissions = AccessPolicy.effectivePermissions(role, stored);
        for (PermissionModule module : PermissionModule.values()) {
            Object[] row = new Object[columns.length];
            row[0] = module.label();
            for (int i = 0; i < actions.length; i++) row[i + 1] = permissions.contains(module.key(actions[i]));
            model.addRow(row);
        }
        JTable table = new JTable(model);
        table.setRowHeight(29);
        table.setFont(UiTheme.regular(12));
        table.getTableHeader().setFont(UiTheme.bold(11));
        table.getTableHeader().setReorderingAllowed(false);
        table.getColumnModel().getColumn(0).setPreferredWidth(170);
        table.getColumnModel().getColumn(2).setPreferredWidth(130);
        table.setDefaultRenderer(Boolean.class, new javax.swing.table.DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;
            private final javax.swing.JCheckBox check = new javax.swing.JCheckBox();

            @Override public java.awt.Component getTableCellRendererComponent(JTable source, Object value,
                    boolean selected, boolean focused, int row, int column) {
                check.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
                check.setSelected(Boolean.TRUE.equals(value));
                check.setEnabled(model.isCellEditable(row, column));
                check.setBackground(selected ? source.getSelectionBackground() : source.getBackground());
                return check;
            }
        });
        JScrollPane scroll = new JScrollPane(table);
        scroll.setPreferredSize(new Dimension(850, 350));
        add(scroll, BorderLayout.CENTER);
        JLabel note = new JLabel(admin ? "Quản trị viên luôn có toàn quyền."
                : "Chọn thao tác sẽ tự cấp quyền Xem. Quyền mới có hiệu lực khi đăng nhập lại.");
        note.setFont(UiTheme.regular(12));
        add(note, BorderLayout.SOUTH);
    }

    public String permissions() {
        Set<String> permissions = new LinkedHashSet<>();
        PermissionModule[] modules = PermissionModule.values();
        PermissionAction[] actions = PermissionAction.values();
        for (int row = 0; row < modules.length; row++) {
            for (int column = 1; column <= actions.length; column++) {
                if (Boolean.TRUE.equals(model.getValueAt(row, column))) {
                    permissions.add(modules[row].key(actions[column - 1]));
                }
            }
        }
        return AccessPolicy.encode(permissions);
    }
}

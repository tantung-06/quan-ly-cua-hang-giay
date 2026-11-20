package gui.dialog;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import gui.UiTheme;

public final class NhomQuyenDialog {
    private final String[] values;

    public NhomQuyenDialog(String[] values, boolean editing) {
        this.values = values.clone();
    }

    public String[] show(Component parent, String title) {
        JTextField code = new JTextField(values[0]);
        code.setEditable(false);
        JTextField name = new JTextField(values[1]);
        code.setFont(UiTheme.regular(13));
        name.setFont(UiTheme.regular(13));
        JPanel fields = new JPanel(new GridLayout(2, 2, 12, 10));
        fields.add(new JLabel("Mã nhóm quyền"));
        fields.add(code);
        fields.add(new JLabel("Tên nhóm quyền"));
        fields.add(name);
        PermissionMatrixPanel permissions = new PermissionMatrixPanel(values[0], values.length > 2 ? values[2] : null);
        JPanel content = new JPanel(new BorderLayout(0, 16));
        content.add(fields, BorderLayout.NORTH);
        content.add(permissions, BorderLayout.CENTER);
        while (JOptionPane.showConfirmDialog(parent, content, title, JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION) {
            if (name.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(parent, "Tên nhóm quyền không được để trống.");
                continue;
            }
            return new String[] { values[0], name.getText().trim(), permissions.permissions() };
        }
        return null;
    }
}

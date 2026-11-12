package gui.dialog;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import gui.UiTheme;

public final class ThuocTinhDialog {
    private ThuocTinhDialog() {
    }

    public static String[] show(Component parent, String group, String code, String name,
            String typeCode, String brandCode, boolean editing) {
        JComboBox<String> groupBox = new JComboBox<>(new String[] { "Loại giày", "Thương hiệu" });
        groupBox.setFont(UiTheme.regular(13));
        groupBox.setSelectedItem(group);
        groupBox.setEnabled(!editing);

        JTextField codeField = new JTextField(code);
        codeField.setFont(UiTheme.regular(13));
        codeField.setForeground(UiTheme.MUTED);
        codeField.setDisabledTextColor(UiTheme.MUTED);
        codeField.setEditable(false);
        codeField.setFocusable(false);

        JTextField nameField = new JTextField(name);
        nameField.setFont(UiTheme.regular(13));

        if (!editing) {
            groupBox.addActionListener(event -> codeField.setText(
                    "Loại giày".equals(groupBox.getSelectedItem()) ? typeCode : brandCode));
            codeField.setText("Loại giày".equals(groupBox.getSelectedItem()) ? typeCode : brandCode);
        }

        JPanel form = new JPanel(new GridLayout(3, 2, 12, 10));
        form.setBorder(BorderFactory.createEmptyBorder(12, 8, 6, 8));
        form.setPreferredSize(new Dimension(440, 150));
        addField(form, "Nhóm thuộc tính", groupBox);
        addField(form, "Mã", codeField);
        addField(form, "Tên", nameField);

        int result = JOptionPane.showConfirmDialog(parent, form,
                editing ? "Cập nhật thuộc tính" : "Thêm thuộc tính",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) {
            return null;
        }
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(parent, "Tên thuộc tính không được để trống.",
                    "Dữ liệu không hợp lệ", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return new String[] {
                String.valueOf(groupBox.getSelectedItem()),
                codeField.getText(),
                nameField.getText().trim()
        };
    }

    private static void addField(JPanel form, String name, Component input) {
        JLabel label = new JLabel(name);
        label.setFont(UiTheme.bold(12));
        label.setForeground(UiTheme.TEXT);
        form.add(label);
        form.add(input);
    }
}
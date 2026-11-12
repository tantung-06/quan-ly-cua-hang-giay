package gui.dialog;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import gui.UiTheme;

public class FormDialog {
    private final String[] labels;
    private final String[] values;
    private final String[][] options;
    private final boolean[] readOnly;

    public FormDialog(String[] labels, String[] values, String[][] options, boolean[] readOnly) {
        if (labels.length != values.length || labels.length != options.length || labels.length != readOnly.length) {
            throw new IllegalArgumentException("Mỗi trường cần giá trị, tùy chọn và trạng thái đọc riêng.");
        }
        this.labels = labels.clone();
        this.values = values.clone();
        this.options = options.clone();
        this.readOnly = readOnly.clone();
    }

    public String[] show(Component parent, String title) {
        JPanel form = new JPanel(new GridLayout(labels.length, 2, 12, 10));
        form.setBorder(BorderFactory.createEmptyBorder(12, 8, 6, 8));
        form.setPreferredSize(new Dimension(440, Math.max(180, labels.length * 42)));
        JComponent[] controls = new JComponent[labels.length];
        for (int i = 0; i < labels.length; i++) {
            JLabel label = new JLabel(labels[i]);
            label.setFont(UiTheme.bold(12));
            label.setForeground(UiTheme.TEXT);
            form.add(label);
            controls[i] = createControl(i);
            form.add(controls[i]);
        }

        int result = JOptionPane.showConfirmDialog(parent, form, title,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) {
            return null;
        }
        String[] entered = new String[controls.length];
        for (int i = 0; i < controls.length; i++) {
            entered[i] = readValue(controls[i]);
        }
        return entered;
    }

    private JComponent createControl(int index) {
        if (options[index] != null && options[index].length > 0) {
            JComboBox<String> combo = new JComboBox<>(options[index]);
            combo.setFont(UiTheme.regular(13));
            combo.setSelectedItem(values[index]);
            combo.setEnabled(!readOnly[index]);
            return combo;
        }

        String label = labels[index].trim().toLowerCase(Locale.ROOT);
        JTextField field = label.contains("mật khẩu")
                ? new JPasswordField(values[index])
                : new JTextField(values[index]);
        field.setFont(UiTheme.regular(13));
        field.setEnabled(!readOnly[index]);
        if (label.startsWith("mã") || label.equals("điểm tích lũy")) {
            field.setForeground(UiTheme.MUTED);
            field.setDisabledTextColor(UiTheme.MUTED);
        }
        return field;
    }

    private static String readValue(JComponent control) {
        if (control instanceof JComboBox<?>) {
            return String.valueOf(((JComboBox<?>) control).getSelectedItem());
        }
        if (control instanceof JPasswordField) {
            return new String(((JPasswordField) control).getPassword());
        }
        return ((JTextField) control).getText().trim();
    }
}
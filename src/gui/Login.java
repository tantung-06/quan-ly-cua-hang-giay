package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.sql.SQLException;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.border.Border;

import bus.TaiKhoanBUS;
import config.DatabaseConnection;
import dto.TaiKhoan;

public final class Login extends JFrame {
    private static final long serialVersionUID = 1L;

    private final JTextField username = new JTextField();
    private final JPasswordField password = new JPasswordField();
    private final JLabel message = new JLabel(" ");
    private final JButton loginButton = new JButton("Đăng nhập");

    public Login() {
        super("Sneaker Store | Đăng nhập");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(760, 520));
        setSize(920, 600);
        setLocationRelativeTo(null);
        setContentPane(createContent());
        getRootPane().setDefaultButton(loginButton);
    }

    private JPanel createContent() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);
        root.add(createBrandPanel(), BorderLayout.WEST);
        root.add(createFormPanel(), BorderLayout.CENTER);
        return root;
    }

    private JPanel createBrandPanel() {
        JPanel side = new JPanel(new GridBagLayout());
        side.setBackground(UiTheme.PRIMARY);
        side.setPreferredSize(new Dimension(380, 0));

        JPanel box = new JPanel();
        box.setOpaque(false);
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));

        JLabel name = label("SNEAKER STORE", 30, Color.WHITE, true);
        name.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        JLabel system = label("Hệ thống quản lý cửa hàng giày", 15, new Color(0xD1D5DB), false);

        for (JLabel text : new JLabel[] { name, system }) {
            text.setAlignmentX(Component.CENTER_ALIGNMENT);
            text.setHorizontalAlignment(JLabel.CENTER);
        }

        box.add(name);
        box.add(system);
        side.add(box);
        return side;
    }

    private JPanel createFormPanel() {
        JPanel background = new JPanel(new GridBagLayout());
        background.setBackground(Color.WHITE);

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setOpaque(false);
        form.setPreferredSize(new Dimension(340, 430));

        JLabel title = label("Chào mừng trở lại", 26, UiTheme.TEXT, true);
        JLabel subtitle = label("Đăng nhập để tiếp tục quản lý cửa hàng.", 13, UiTheme.MUTED, false);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));
        subtitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

        styleField(username);
        styleField(password);
        username.setToolTipText("Tên đăng nhập");
        password.setToolTipText("Mật khẩu");

        JCheckBox showPassword = new JCheckBox("Hiện mật khẩu");
        showPassword.setOpaque(false);
        showPassword.setForeground(UiTheme.MUTED);
        showPassword.setFont(UiTheme.regular(12));
        showPassword.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        showPassword.setAlignmentX(Component.LEFT_ALIGNMENT);
        char echo = password.getEchoChar();
        showPassword.addActionListener(e -> password.setEchoChar(showPassword.isSelected() ? (char) 0 : echo));

        message.setFont(UiTheme.regular(12));
        message.setForeground(UiTheme.DANGER);
        message.setAlignmentX(Component.LEFT_ALIGNMENT);
        message.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));

        loginButton.setFont(UiTheme.bold(14));
        loginButton.setForeground(Color.WHITE);
        loginButton.setBackground(UiTheme.BLUE);
        loginButton.setFocusPainted(false);
        loginButton.setBorderPainted(false);
        loginButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginButton.setPreferredSize(new Dimension(340, 48));
        loginButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        loginButton.addActionListener(e -> authenticate());

        form.add(title);
        form.add(subtitle);
        form.add(label("Tên đăng nhập", 13, UiTheme.TEXT, true));
        form.add(Box.createVerticalStrut(8));
        form.add(username);
        form.add(Box.createVerticalStrut(20));
        form.add(label("Mật khẩu", 13, UiTheme.TEXT, true));
        form.add(Box.createVerticalStrut(8));
        form.add(password);
        form.add(Box.createVerticalStrut(10));
        form.add(showPassword);
        form.add(Box.createVerticalStrut(8));
        form.add(message);
        form.add(loginButton);

        background.add(form);
        return background;
    }

    private void authenticate() {
        String user = username.getText().trim();
        char[] secret = password.getPassword();
        if (user.isEmpty() || secret.length == 0) {
            message.setText("Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu.");
            Arrays.fill(secret, '\0');
            return;
        }

        String pass = new String(secret);
        Arrays.fill(secret, '\0');
        loginButton.setEnabled(false);
        loginButton.setText("Đang xác thực...");
        message.setForeground(UiTheme.MUTED);
        message.setText("Đang kết nối đến hệ thống...");

        new SwingWorker<TaiKhoan, Void>() {
            @Override
            protected TaiKhoan doInBackground() {
                try (java.sql.Connection connection = DatabaseConnection.getConnection()) {
                    if (!connection.isValid(5)) {
                        throw new SQLException("Kết nối cơ sở dữ liệu không hợp lệ.");
                    }
                } catch (SQLException ex) {
                    throw new IllegalStateException("Không thể kết nối cơ sở dữ liệu.", ex);
                }
                return new TaiKhoanBUS().login(user, pass);
            }

            @Override
            protected void done() {
                loginButton.setEnabled(true);
                loginButton.setText("Đăng nhập");
                try {
                    TaiKhoan account = get();
                    if (account == null) {
                        message.setForeground(UiTheme.DANGER);
                        message.setText("Thông tin không đúng hoặc tài khoản đã ngừng hoạt động.");
                        password.setText("");
                        password.requestFocusInWindow();
                        return;
                    }
                    new MainFrame(account).setVisible(true);
                    dispose();
                } catch (Exception ex) {
                    message.setForeground(UiTheme.DANGER);
                    message.setText("Không thể kết nối cơ sở dữ liệu. Vui lòng thử lại.");
                    ex.printStackTrace();
                }
            }
        }.execute();
    }

    private void styleField(JTextField field) {
        field.setFont(UiTheme.regular(14));
        field.setForeground(UiTheme.TEXT);
        field.setBorder(fieldBorder(UiTheme.FIELD_BORDER));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setPreferredSize(new Dimension(340, 46));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                field.setBorder(fieldBorder(UiTheme.BLUE));
            }

            @Override
            public void focusLost(FocusEvent e) {
                field.setBorder(fieldBorder(UiTheme.FIELD_BORDER));
            }
        });
        field.addActionListener(e -> {
            if (field == username) {
                password.requestFocusInWindow();
            }
        });
    }

    private static Border fieldBorder(Color line) {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(line),
                BorderFactory.createEmptyBorder(11, 13, 11, 13));
    }

    private static JLabel label(String text, int size, Color color, boolean bold) {
        JLabel label = new JLabel(text);
        label.setFont(bold ? UiTheme.bold(size) : UiTheme.regular(size));
        label.setForeground(color);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    @Override
    public void addNotify() {
        super.addNotify();
        SwingUtilities.invokeLater(username::requestFocusInWindow);
    }
}
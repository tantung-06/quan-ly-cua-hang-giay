package gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import dto.TaiKhoan;
import gui.component.NavButton;
import gui.component.VectorMenuIcon;
import gui.module.ModuleDefinition;
import gui.module.ModuleRegistry;
import gui.panel.DashboardPanel;
import gui.panel.DataPage;
import gui.panel.HomePanel;

public final class MainFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    private static final String HOME = "Trang chủ";
    private static final int SIDEBAR_WIDTH = 252;
    private static final int PROFILE_HEIGHT = 68;

    private final TaiKhoan account;
    private final CardLayout pageLayout = new CardLayout();
    private final JPanel pageContainer = new JPanel(pageLayout);
    private final JPanel navigation = new JPanel();
    private final List<NavButton> navButtons = new ArrayList<>();
    private final Map<String, ModuleDefinition> definitions;
    private final Map<String, JPanel> pages = new HashMap<>();

    public MainFrame(TaiKhoan account) {
        super("Hệ thống quản lý cửa hàng giày");
        this.account = account;
        definitions = new ModuleRegistry(account, this).create();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1180, 720));
        setSize(1440, 900);
        setLocationRelativeTo(null);
        buildInterface();
    }

    private void buildInterface() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UiTheme.BACKGROUND);
        pageContainer.setBackground(UiTheme.BACKGROUND);

        JPanel workspace = new JPanel(new BorderLayout());
        workspace.setBackground(UiTheme.BACKGROUND);
        workspace.add(pageContainer, BorderLayout.CENTER);

        root.add(createSidebar(), BorderLayout.WEST);
        root.add(workspace, BorderLayout.CENTER);
        setContentPane(root);

        HomePanel home = new HomePanel();
        pages.put(HOME, home);
        pageContainer.add(home, HOME);
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(Color.WHITE);
        sidebar.setPreferredSize(new Dimension(SIDEBAR_WIDTH, 0));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, UiTheme.BORDER));
        sidebar.add(createBrand(), BorderLayout.NORTH);
        sidebar.add(createNavigation(), BorderLayout.CENTER);
        sidebar.add(createFooter(), BorderLayout.SOUTH);
        return sidebar;
    }

    private JPanel createBrand() {
        JPanel brand = new JPanel(new BorderLayout(4, 0));
        brand.setBackground(Color.WHITE);
        brand.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 16));
        brand.setPreferredSize(new Dimension(SIDEBAR_WIDTH, PROFILE_HEIGHT));

        JPanel accountLabels = new JPanel(new GridLayout(2, 1, 0, 0));
        accountLabels.setOpaque(false);
        JLabel username = new JLabel(account.getTenDangNhap());
        username.setFont(UiTheme.bold(18));
        username.setForeground(UiTheme.TEXT);
        username.setToolTipText(account.getTenDangNhap());
        JLabel role = new JLabel(account.getTenNhomQuyen());
        role.setFont(UiTheme.regular(11));
        role.setForeground(UiTheme.MUTED);
        accountLabels.add(username);
        accountLabels.add(role);
        Dimension labelSize = accountLabels.getPreferredSize();
        accountLabels.setPreferredSize(new Dimension(Math.min(170, labelSize.width), labelSize.height));

        JPanel accountInfo = new JPanel(new GridBagLayout());
        accountInfo.setOpaque(false);
        GridBagConstraints accountAlignment = new GridBagConstraints();
        accountAlignment.anchor = GridBagConstraints.WEST;
        accountAlignment.fill = GridBagConstraints.HORIZONTAL;
        accountAlignment.weightx = 1;
        accountInfo.add(accountLabels, accountAlignment);
        brand.add(new AvatarLabel(account.getTenDangNhap()), BorderLayout.WEST);
        brand.add(accountInfo, BorderLayout.CENTER);
        return brand;
    }

    private JScrollPane createNavigation() {
        navigation.setLayout(new BoxLayout(navigation, BoxLayout.Y_AXIS));
        navigation.setBackground(Color.WHITE);
        navigation.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        String[][] items = {
                { HOME, "home" },
                { "Sản phẩm", "product" },
                { "Thuộc tính", "attribute" },
                { "Phiếu nhập", "purchase" },
                { "Hóa đơn", "sale" },
                { "Nhà cung cấp", "supplier" },
                { "Khách hàng", "customer" },
                { "Nhân viên", "employee" },
                { "Tài khoản", "account" },
                { "Phân quyền", "permission" },
                { "Khuyến mãi", "promotion" },
                { "Thống kê", "statistics" }
        };

        for (String[] item : items) {
            NavButton button = new NavButton(item[0], item[1], () -> navigate(item[0]));
            navButtons.add(button);
            navigation.add(button);
            navigation.add(Box.createVerticalStrut(3));
        }
        if (!navButtons.isEmpty()) {
            navButtons.get(0).setSelected(true);
        }

        JScrollPane menuScroll = new JScrollPane(navigation);
        menuScroll.setBorder(null);
        menuScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        menuScroll.getViewport().setBackground(Color.WHITE);
        return menuScroll;
    }

    private JPanel createFooter() {
        JPanel footer = new JPanel(new BorderLayout(0, 10));
        footer.setBackground(Color.WHITE);
        footer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, UiTheme.BORDER),
                BorderFactory.createEmptyBorder(14, 16, 16, 16)));
        JButton logout = new JButton("Đăng xuất");
        logout.setHorizontalAlignment(SwingConstants.CENTER);
        logout.setIcon(new VectorMenuIcon("logout", UiTheme.DANGER));
        logout.setIconTextGap(8);
        logout.setForeground(UiTheme.DANGER);
        logout.setFont(UiTheme.regular(13));
        logout.setFocusPainted(false);
        logout.setBorder(BorderFactory.createEmptyBorder(8, 0, 2, 0));
        logout.setContentAreaFilled(false);
        logout.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logout.addActionListener(e -> logout());
        footer.add(logout, BorderLayout.SOUTH);

        return footer;
    }

    private void navigate(String name) {
        for (NavButton button : navButtons) {
            button.setSelected(button.getLabel().equals(name));
        }
        JPanel page = pages.get(name);
        if (page == null) {
            if (name.equals("Thống kê")) {
                page = new DashboardPanel(account.getTenDangNhap(), this::navigate, true);
            } else {
                ModuleDefinition definition = definitions.get(name);
                if (definition == null) {
                    return;
                }
                page = new DataPage(definition);
            }
            pages.put(name, page);
            pageContainer.add(page, name);
        }
        pageContainer.setBorder(name.equals(HOME) ? null
                : BorderFactory.createEmptyBorder(24, 30, 28, 30));
        pageLayout.show(pageContainer, name);
        if (page instanceof DataPage) {
            ((DataPage) page).refresh();
        } else if (page instanceof DashboardPanel) {
            ((DashboardPanel) page).refresh();
        }
    }

    private void logout() {
        int answer = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn đăng xuất khỏi hệ thống?",
                "Xác nhận đăng xuất", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (answer == JOptionPane.YES_OPTION) {
            dispose();
            new Login().setVisible(true);
        }
    }

    private static String initials(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "U";
        }
        String[] parts = value.trim().split("[._\\s]+");
        if (parts.length == 1) {
            return parts[0].substring(0, 1).toUpperCase(Locale.ROOT);
        }
        return (parts[0].substring(0, 1) + parts[parts.length - 1].substring(0, 1))
                .toUpperCase(Locale.ROOT);
    }

    private static final class AvatarLabel extends JLabel {
        private static final long serialVersionUID = 1L;

        AvatarLabel(String username) {
            super(initials(username), SwingConstants.CENTER);
            setForeground(UiTheme.PRIMARY);
            setFont(UiTheme.bold(13));
            setPreferredSize(new Dimension(36, 36));
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(UiTheme.PRIMARY_SOFT);
            int size = Math.min(36, Math.min(getWidth(), getHeight()));
            int x = (getWidth() - size) / 2;
            int y = (getHeight() - size) / 2;
            g.fillOval(x, y, size, size);
            g.dispose();
            super.paintComponent(graphics);
        }
    }
}
package gui.panel;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;

import bus.KhachHangBUS;
import bus.NhanVienBUS;
import bus.HoaDonBUS;
import bus.SanPhamBUS;
import dto.HoaDon;
import dto.SanPham;
import gui.UiFormats;
import gui.UiTheme;

public class DashboardPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter DATE_LABEL = DateTimeFormatter.ofPattern("dd/MM");

    private final String username;
    private final Consumer<String> navigate;
    private final boolean statistics;
    private final JLabel productsValue = new JLabel("—");
    private final JLabel customersValue = new JLabel("—");
    private final JLabel revenueValue = new JLabel("—");
    private final JLabel lowStockValue = new JLabel("—");
    private final JLabel salesSummary = new JLabel("Đang tải dữ liệu...");
    private final DefaultTableModel recentModel = new DefaultTableModel(
            new String[] { "Mã hóa đơn", "Ngày lập", "Khách hàng", "Tổng tiền", "Trạng thái" }, 0) {
        private static final long serialVersionUID = 1L;

        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final SalesChart chart = new SalesChart();
    private SwingWorker<DashboardData, Void> worker;

    public DashboardPanel(String username, Consumer<String> navigate) {
        this(username, navigate, false);
    }

    public DashboardPanel(String username, Consumer<String> navigate, boolean statistics) {
        this.username = username;
        this.navigate = navigate;
        this.statistics = statistics;
        setOpaque(false);
        setLayout(new BorderLayout(0, 20));
        buildInterface();
    }

    private void buildInterface() {
        JPanel top = new JPanel(new BorderLayout(0, 15));
        top.setOpaque(false);
        JPanel titleArea = new JPanel();
        titleArea.setOpaque(false);
        titleArea.setLayout(new javax.swing.BoxLayout(titleArea, javax.swing.BoxLayout.Y_AXIS));
        JLabel title = new JLabel(statistics ? "Thống kê bán hàng" : "Xin chào, " + username);
        title.setFont(UiTheme.bold(24));
        title.setForeground(UiTheme.TEXT);
        JLabel subtitle = new JLabel(statistics
                ? "Tổng hợp hoạt động kinh doanh trong 7 ngày gần nhất."
                : "Đây là tình hình hoạt động của cửa hàng hôm nay.");
        subtitle.setFont(UiTheme.regular(13));
        subtitle.setForeground(UiTheme.MUTED);
        subtitle.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        titleArea.add(title);
        titleArea.add(subtitle);

        JButton refresh = new JButton("Làm mới dữ liệu");
        refresh.setFont(UiTheme.bold(12));
        refresh.setForeground(UiTheme.PRIMARY);
        refresh.setBackground(Color.WHITE);
        refresh.setFocusPainted(false);
        refresh.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UiTheme.BORDER),
                BorderFactory.createEmptyBorder(9, 12, 9, 12)));
        refresh.addActionListener(event -> refresh());
        JPanel refreshWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        refreshWrap.setOpaque(false);
        refreshWrap.add(refresh);
        top.add(titleArea, BorderLayout.CENTER);
        top.add(refreshWrap, BorderLayout.EAST);

        JPanel stats = new JPanel(new GridLayout(1, 4, 14, 0));
        stats.setOpaque(false);
        stats.setPreferredSize(new Dimension(0, 120));
        stats.add(new MetricCard("Tổng sản phẩm", productsValue, "Đang có trong danh mục", UiTheme.BLUE));
        stats.add(new MetricCard("Khách hàng", customersValue, "Hồ sơ khách hàng", UiTheme.GREEN));
        stats.add(new MetricCard("Doanh thu hôm nay", revenueValue, "Từ hóa đơn đã xuất", UiTheme.PRIMARY));
        stats.add(new MetricCard("Sắp hết hàng", lowStockValue, "Sản phẩm còn tối đa 5 đôi", UiTheme.AMBER));

        JPanel chartCard = sectionCard("Doanh thu 7 ngày gần nhất", chart);
        chartCard.setPreferredSize(new Dimension(0, 300));

        JTable recentTable = new JTable(recentModel);
        recentTable.setRowHeight(38);
        recentTable.setFont(UiTheme.regular(12));
        recentTable.setForeground(UiTheme.TEXT);
        recentTable.setBackground(Color.WHITE);
        recentTable.setShowVerticalLines(false);
        recentTable.setGridColor(UiTheme.BORDER);
        recentTable.getTableHeader().setReorderingAllowed(false);
        recentTable.getTableHeader().setPreferredSize(new Dimension(0, 36));
        recentTable.getTableHeader().setFont(UiTheme.bold(10));
        recentTable.getTableHeader().setForeground(UiTheme.MUTED);
        recentTable.getTableHeader().setBackground(new Color(0xF9FAFB));
        recentTable.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;

            @Override
            public java.awt.Component getTableCellRendererComponent(JTable table, Object value,
                    boolean selected, boolean focused, int row, int column) {
                JLabel cell = (JLabel) super.getTableCellRendererComponent(
                        table, value, selected, focused, row, column);
                cell.setFont(UiTheme.regular(11));
                cell.setForeground(UiTheme.TEXT);
                cell.setBackground(selected ? UiTheme.PRIMARY_SOFT : Color.WHITE);
                cell.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 5));
                if (value instanceof Number && column == 3) {
                    cell.setText(UiFormats.currency(((Number) value).doubleValue()));
                }
                return cell;
            }
        });
        JScrollPane recentScroll = new JScrollPane(recentTable);
        recentScroll.setBorder(null);
        recentScroll.getViewport().setBackground(Color.WHITE);
        JPanel recentCard = sectionCard("Hóa đơn gần đây", recentScroll);
        recentCard.setPreferredSize(new Dimension(0, 300));

        JPanel lower = new JPanel(new GridLayout(1, 2, 14, 0));
        lower.setOpaque(false);
        lower.add(chartCard);
        lower.add(recentCard);

        JPanel body = new JPanel(new BorderLayout(0, 16));
        body.setOpaque(false);
        body.add(stats, BorderLayout.NORTH);
        body.add(lower, BorderLayout.CENTER);
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        salesSummary.setFont(UiTheme.regular(12));
        salesSummary.setForeground(UiTheme.MUTED);
        footer.add(salesSummary, BorderLayout.WEST);
        if (!statistics) {
            JButton seeSales = new JButton("Xem danh sách hóa đơn  →");
            seeSales.setFont(UiTheme.bold(12));
            seeSales.setForeground(UiTheme.PRIMARY);
            seeSales.setContentAreaFilled(false);
            seeSales.setBorderPainted(false);
            seeSales.setFocusPainted(false);
            seeSales.addActionListener(event -> navigate.accept("Hóa đơn"));
            footer.add(seeSales, BorderLayout.EAST);
        }
        body.add(footer, BorderLayout.SOUTH);

        add(top, BorderLayout.NORTH);
        add(body, BorderLayout.CENTER);
    }

    public void refresh() {
        if (worker != null && !worker.isDone()) {
            worker.cancel(true);
        }
        salesSummary.setText("Đang tải dữ liệu...");
        worker = new SwingWorker<DashboardData, Void>() {
            @Override
            protected DashboardData doInBackground() {
                List<SanPham> products = new SanPhamBUS().getAll();
                List<HoaDon> sales = new HoaDonBUS().getAll();
                int customers = new KhachHangBUS().getAll().size();
                int employees = new NhanVienBUS().getAll().size();
                return new DashboardData(products, sales, customers, employees);
            }

            @Override
            protected void done() {
                try {
                    DashboardData data = get();
                    LocalDate today = LocalDate.now();
                    double todayRevenue = 0;
                    int todayOrders = 0;
                    for (HoaDon sale : data.sales) {
                        if (today.equals(sale.getNgayLap()) && "DA_XUAT".equals(sale.getTrangThai())) {
                            todayRevenue += sale.getTongTien();
                            todayOrders++;
                        }
                    }
                    int lowStock = 0;
                    for (SanPham product : data.products) {
                        if (product.getSoLuongTon() <= 5
                                && "DANG_BAN".equals(product.getTrangThai())) {
                            lowStock++;
                        }
                    }
                    productsValue.setText(Integer.toString(data.products.size()));
                    customersValue.setText(Integer.toString(data.customers));
                    revenueValue.setText(UiFormats.currency(todayRevenue));
                    lowStockValue.setText(Integer.toString(lowStock));
                    salesSummary.setText("Hôm nay có " + todayOrders + " đơn hàng hoàn tất · "
                            + data.employees + " nhân viên trong hệ thống");
                    chart.setSales(data.sales);
                    updateRecentSales(data.sales);
                } catch (java.util.concurrent.CancellationException ignored) {
                    // A newer refresh has replaced this request.
                } catch (Exception ex) {
                    salesSummary.setForeground(UiTheme.DANGER);
                    salesSummary.setText("Không thể tải số liệu tổng quan.");
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(DashboardPanel.this,
                            "Không thể tải dữ liệu tổng quan.\n" + ex.getMessage(),
                            "Lỗi tải dữ liệu", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void updateRecentSales(List<HoaDon> sales) {
        recentModel.setRowCount(0);
        for (int index = 0; index < Math.min(6, sales.size()); index++) {
            HoaDon sale = sales.get(index);
            recentModel.addRow(new Object[] { sale.getMaHoaDon(), sale.getNgayLap(),
                    sale.getMaKhachHang() == null ? "Khách lẻ" : sale.getMaKhachHang(),
                    sale.getTongTien(), sale.getTrangThai() });
        }
    }

    private static JPanel sectionCard(String title, JComponent content) {
        JPanel card = new JPanel(new BorderLayout(0, 12));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UiTheme.BORDER),
                BorderFactory.createEmptyBorder(16, 16, 14, 16)));
        JLabel heading = new JLabel(title);
        heading.setFont(UiTheme.bold(14));
        heading.setForeground(UiTheme.TEXT);
        card.add(heading, BorderLayout.NORTH);
        card.add(content, BorderLayout.CENTER);
        return card;
    }

    private static final class MetricCard extends JPanel {
        private static final long serialVersionUID = 1L;

        MetricCard(String title, JLabel value, String detail, Color accent) {
            super(new BorderLayout(0, 8));
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(UiTheme.BORDER),
                    BorderFactory.createEmptyBorder(15, 16, 14, 14)));
            JLabel label = new JLabel(title);
            label.setFont(UiTheme.regular(12));
            label.setForeground(UiTheme.MUTED);
            value.setFont(UiTheme.bold(23));
            value.setForeground(accent);
            JLabel note = new JLabel(detail);
            note.setFont(UiTheme.regular(10));
            note.setForeground(UiTheme.MUTED);
            add(label, BorderLayout.NORTH);
            add(value, BorderLayout.CENTER);
            add(note, BorderLayout.SOUTH);
        }
    }

    private static final class DashboardData {
        final List<SanPham> products;
        final List<HoaDon> sales;
        final int customers;
        final int employees;

        DashboardData(List<SanPham> products, List<HoaDon> sales, int customers, int employees) {
            this.products = products;
            this.sales = sales;
            this.customers = customers;
            this.employees = employees;
        }
    }

    private static final class SalesChart extends JPanel {
        private static final long serialVersionUID = 1L;
        private double[] dailySales = new double[7];

        SalesChart() {
            setOpaque(false);
        }

        void setSales(List<HoaDon> sales) {
            LocalDate today = LocalDate.now();
            dailySales = new double[7];
            for (HoaDon sale : sales) {
                if (!"DA_XUAT".equals(sale.getTrangThai()) || sale.getNgayLap() == null) {
                    continue;
                }
                long daysAgo = java.time.temporal.ChronoUnit.DAYS.between(sale.getNgayLap(), today);
                if (daysAgo >= 0 && daysAgo < dailySales.length) {
                    dailySales[dailySales.length - 1 - (int) daysAgo] += sale.getTongTien();
                }
            }
            repaint();
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int left = 42;
            int right = 8;
            int top = 12;
            int bottom = 30;
            int width = Math.max(1, getWidth() - left - right);
            int height = Math.max(1, getHeight() - top - bottom);
            double max = 0;
            for (double value : dailySales) {
                max = Math.max(max, value);
            }
            if (max == 0) {
                max = 1;
            }
            g.setFont(UiTheme.regular(10));
            FontMetrics metrics = g.getFontMetrics();
            for (int line = 0; line <= 3; line++) {
                int y = top + height - line * height / 3;
                g.setColor(UiTheme.BORDER);
                g.setStroke(new BasicStroke(1));
                g.drawLine(left, y, left + width, y);
                String amount = UiFormats.number(max * line / 3);
                g.setColor(UiTheme.MUTED);
                g.drawString(amount, Math.max(0, left - metrics.stringWidth(amount) - 7), y + 4);
            }
            int slot = width / 7;
            LocalDate first = LocalDate.now().minusDays(6);
            for (int index = 0; index < dailySales.length; index++) {
                int barHeight = (int) Math.round(height * dailySales[index] / max);
                int barWidth = Math.max(8, Math.min(28, slot / 2));
                int x = left + index * slot + (slot - barWidth) / 2;
                int y = top + height - barHeight;
                g.setColor(index == dailySales.length - 1 ? UiTheme.PRIMARY : new Color(0x9CB9E8));
                g.fillRoundRect(x, y, barWidth, Math.max(3, barHeight), 7, 7);
                g.setColor(UiTheme.MUTED);
                String day = first.plusDays(index).format(DATE_LABEL);
                int labelX = x + (barWidth - metrics.stringWidth(day)) / 2;
                g.drawString(day, labelX, top + height + 20);
            }
            g.dispose();
        }
    }
}
package gui.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;

import dto.HoaDon;
import gui.UiFormats;
import gui.UiTheme;

public class DashboardPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private final String username;
    private final Consumer<String> navigate;
    private final boolean statistics;
    private final Supplier<DashboardData> dataLoader;
    private final JLabel productsValue = new JLabel("—");
    private final JLabel customersValue = new JLabel("—");
    private final JLabel revenueValue = new JLabel("—");
    private final JLabel lowStockValue = new JLabel("—");
    private final JLabel salesSummary = new JLabel("Đang tải dữ liệu...");
    private final JLabel subtitle = new JLabel();
    private final JTextField startDate = new JTextField(10);
    private final JTextField endDate = new JTextField(10);
    private final JLabel periodMessage = new JLabel(" ");
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
        this(username, navigate, statistics, DashboardData::load);
    }

    DashboardPanel(String username, Consumer<String> navigate, boolean statistics,
            Supplier<DashboardData> dataLoader) {
        this.username = username;
        this.navigate = navigate;
        this.statistics = statistics;
        this.dataLoader = java.util.Objects.requireNonNull(dataLoader);
        setOpaque(false);
        setLayout(new BorderLayout(0, 20));
        buildInterface();
    }

    private void buildInterface() {
        LocalDate today = LocalDate.now();
        SalesPeriod initialPeriod = SalesPeriod.lastSevenDays(today);
        startDate.setText(initialPeriod.start.format(SalesPeriod.DATE_FORMAT));
        endDate.setText(initialPeriod.end.format(SalesPeriod.DATE_FORMAT));
        add(createHeader(), BorderLayout.NORTH);
        add(createBody(), BorderLayout.CENTER);
    }

    private JPanel createHeader() {
        JPanel top = new JPanel(new BorderLayout(0, 15));
        top.setOpaque(false);
        JPanel titleArea = new JPanel();
        titleArea.setOpaque(false);
        titleArea.setLayout(new javax.swing.BoxLayout(titleArea, javax.swing.BoxLayout.Y_AXIS));
        JLabel title = new JLabel(statistics ? "Thống kê bán hàng" : "Xin chào, " + username);
        title.setFont(UiTheme.bold(24));
        title.setForeground(UiTheme.TEXT);
        subtitle.setText(statistics
                ? "Chọn khoảng thời gian để xem doanh thu và hóa đơn."
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
        if (statistics) {
            top.add(createPeriodFilter(), BorderLayout.SOUTH);
        }
        return top;
    }

    private JPanel createMetrics() {
        JPanel stats = new JPanel(new GridLayout(1, 4, 14, 0));
        stats.setOpaque(false);
        stats.setPreferredSize(new Dimension(0, 120));
        stats.add(new MetricCard("Tổng sản phẩm", productsValue, "Đang có trong danh mục", UiTheme.BLUE));
        stats.add(new MetricCard("Khách hàng", customersValue, "Hồ sơ khách hàng", UiTheme.GREEN));
        stats.add(new MetricCard(statistics ? "Doanh thu trong kỳ" : "Doanh thu hôm nay", revenueValue,
                "Từ hóa đơn đã xuất", UiTheme.PRIMARY));
        stats.add(new MetricCard("Sắp hết hàng", lowStockValue, "Sản phẩm còn tối đa 5 đôi", UiTheme.AMBER));
        return stats;
    }

    private JPanel createInvoiceCard() {
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
        JPanel recentCard = sectionCard(statistics ? "Hóa đơn trong kỳ" : "Hóa đơn gần đây", recentScroll);
        recentCard.setPreferredSize(new Dimension(0, 300));
        return recentCard;
    }

    private JPanel createBody() {
        JPanel chartCard = sectionCard(statistics ? "Doanh thu trong kỳ" : "Doanh thu 7 ngày gần nhất", chart);
        chartCard.setPreferredSize(new Dimension(0, 300));
        JPanel lower = new JPanel(new GridLayout(1, 2, 14, 0));
        lower.setOpaque(false);
        lower.add(chartCard);
        lower.add(createInvoiceCard());

        JPanel body = new JPanel(new BorderLayout(0, 16));
        body.setOpaque(false);
        body.add(createMetrics(), BorderLayout.NORTH);
        body.add(lower, BorderLayout.CENTER);
        body.add(createFooter(), BorderLayout.SOUTH);
        return body;
    }

    private JPanel createFooter() {
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
        return footer;
    }

    private JPanel createPeriodFilter() {
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        controls.setOpaque(false);
        JTextField[] fields = { startDate, endDate };
        String[] labels = { "Từ ngày", "Đến ngày" };
        for (int i = 0; i < fields.length; i++) {
            JLabel label = new JLabel(labels[i]);
            label.setLabelFor(fields[i]);
            label.setFont(UiTheme.bold(12));
            fields[i].setFont(UiTheme.regular(13));
            fields[i].setToolTipText("dd/MM/yyyy");
            fields[i].getAccessibleContext().setAccessibleName(labels[i] + " (dd/MM/yyyy)");
            fields[i].setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(UiTheme.FIELD_BORDER),
                    BorderFactory.createEmptyBorder(8, 10, 8, 10)));
            fields[i].addActionListener(event -> refresh());
            controls.add(label);
            controls.add(fields[i]);
        }
        JButton apply = new JButton("Áp dụng");
        apply.setFont(UiTheme.bold(12));
        apply.setBackground(UiTheme.BLUE);
        apply.setForeground(Color.WHITE);
        apply.setFocusPainted(false);
        apply.addActionListener(event -> refresh());
        controls.add(apply);
        JLabel format = new JLabel("dd/MM/yyyy");
        format.setFont(UiTheme.regular(12));
        format.setForeground(UiTheme.MUTED);
        controls.add(format);

        JPanel filter = new JPanel(new BorderLayout(0, 4));
        filter.setOpaque(false);
        filter.add(controls, BorderLayout.NORTH);
        periodMessage.setFont(UiTheme.regular(12));
        periodMessage.setForeground(UiTheme.DANGER);
        filter.add(periodMessage, BorderLayout.SOUTH);
        return filter;
    }

    public void refresh() {
        final SalesPeriod period;
        try {
            LocalDate today = LocalDate.now();
            period = statistics ? SalesPeriod.parse(startDate.getText(), endDate.getText())
                    : SalesPeriod.lastSevenDays(today);
        } catch (IllegalArgumentException ex) {
            periodMessage.setText(ex.getMessage());
            return;
        }
        periodMessage.setText(" ");
        if (worker != null && !worker.isDone()) {
            worker.cancel(true);
        }
        salesSummary.setText("Đang tải dữ liệu...");
        salesSummary.setForeground(UiTheme.MUTED);
        worker = new SwingWorker<DashboardData, Void>() {
            @Override
            protected DashboardData doInBackground() {
                return dataLoader.get();
            }

            @Override
            protected void done() {
                if (isCancelled() || worker != this) {
                    return;
                }
                try {
                    displayData(get(), period);
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

    private void displayData(DashboardData data, SalesPeriod period) {
        LocalDate today = LocalDate.now();
        SalesPeriod.Result result = period.summarize(data.sales);
        SalesPeriod.Result metrics = statistics ? result
                : new SalesPeriod(today, today).summarize(data.sales);
        productsValue.setText(Integer.toString(data.products.size()));
        customersValue.setText(Integer.toString(data.customers));
        revenueValue.setText(UiFormats.currency(metrics.revenue));
        lowStockValue.setText(Integer.toString(data.lowStockProducts));
        salesSummary.setText((statistics ? "Trong kỳ có " : "Hôm nay có ")
                + metrics.completed + " đơn hàng hoàn tất · "
                + data.employees + " nhân viên trong hệ thống");
        if (statistics) {
            subtitle.setText("Từ " + period.start.format(SalesPeriod.DATE_FORMAT) + " đến "
                    + period.end.format(SalesPeriod.DATE_FORMAT)
                    + " · Sản phẩm, khách hàng và tồn kho là số liệu hiện tại.");
        }
        chart.setSales(period, result.buckets);
        updateRecentSales(statistics ? result.sales : data.sales);
    }

    private void updateRecentSales(List<HoaDon> sales) {
        recentModel.setRowCount(0);
        int limit = statistics ? sales.size() : Math.min(6, sales.size());
        for (int index = 0; index < limit; index++) {
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

}

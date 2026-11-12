package gui.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;

import bus.KhachHangBUS;
import bus.KhuyenMaiBUS;
import bus.NhaCungCapBUS;
import bus.NhanVienBUS;
import bus.SanPhamBUS;
import dto.ChiTietPhieuNhap;
import dto.ChiTietHoaDon;
import dto.KhachHang;
import dto.KhuyenMai;
import dto.NhaCungCap;
import dto.NhanVien;
import dto.PhieuNhap;
import dto.HoaDon;
import dto.SanPham;
import gui.UiFormats;
import gui.UiTheme;

public final class PhieuLapDialog {
    private final boolean sale;
    private final String code;
    private final JComboBox<NhanVien> employeeBox;
    private final JComboBox<NhaCungCap> supplierBox;
    private final JComboBox<KhachHangChoice> customerBox;
    private final JComboBox<SanPham> productBox;
    private final JSpinner quantity = new JSpinner(new SpinnerNumberModel(1, 1, 100000, 1));
    private final JTextField unitPrice = new JTextField("0");
    private JTable detailTable;
    private final PhieuLineTableModel lines = new PhieuLineTableModel();
    private final PhieuKhuyenMaiPanel promotionPanel;
    private final JLabel totalLabel = new JLabel("Tổng tiền: 0 ₫");
    private Draft result;
    private SwingWorker<Set<String>, Void> promotionWorker;

    private PhieuLapDialog(boolean sale, String code, String employeeId) {
        this.sale = sale;
        this.code = code;
        List<SanPham> productData;
        List<NhanVien> employeeData;
        List<NhaCungCap> supplierData;
        List<KhachHang> customerData;
        productData = new SanPhamBUS().getAll();
        employeeData = new NhanVienBUS().getAll();
        supplierData = sale ? new ArrayList<>() : new NhaCungCapBUS().getHoatDong();
        customerData = sale ? new KhachHangBUS().getAll() : new ArrayList<>();
        promotionPanel = sale ? new PhieuKhuyenMaiPanel(new KhuyenMaiBUS().getHieuLuc(LocalDate.now())) : null;
        employeeBox = new JComboBox<>(employeeData.stream()
                .filter(item -> "HOAT_DONG".equals(item.getTrangThai()))
                .toArray(NhanVien[]::new));
        supplierBox = new JComboBox<>(supplierData.toArray(new NhaCungCap[0]));
        List<KhachHangChoice> customerChoices = new ArrayList<>();
        customerChoices.add(new KhachHangChoice(null));
        for (KhachHang customer : customerData) {
            if ("HOAT_DONG".equals(customer.getTrangThai())) {
                customerChoices.add(new KhachHangChoice(customer));
            }
        }
        customerBox = new JComboBox<>(customerChoices.toArray(new KhachHangChoice[0]));
        if (sale) {
            customerBox.addActionListener(event -> loadCustomerPromotions());
        }
        productBox = new JComboBox<>(productData.stream()
                .filter(item -> sale
                        ? "DANG_BAN".equals(item.getTrangThai()) && item.getSoLuongTon() > 0
                        : "DANG_BAN".equals(item.getTrangThai()))
                .toArray(SanPham[]::new));
        employeeBox.setRenderer(new DefaultListCellRenderer() {
            private static final long serialVersionUID = 1L;

            @Override
            public java.awt.Component getListCellRendererComponent(javax.swing.JList<?> list, Object value,
                    int index, boolean selected, boolean focus) {
                super.getListCellRendererComponent(list, value, index, selected, focus);
                if (value instanceof NhanVien) {
                    NhanVien employee = (NhanVien) value;
                    setText(employee.getMaNhanVien() + " - " + employee.getHo() + " " + employee.getTen());
                }
                return this;
            }
        });
        supplierBox.setRenderer(new DefaultListCellRenderer() {
            private static final long serialVersionUID = 1L;

            @Override
            public java.awt.Component getListCellRendererComponent(javax.swing.JList<?> list, Object value,
                    int index, boolean selected, boolean focus) {
                super.getListCellRendererComponent(list, value, index, selected, focus);
                if (value instanceof NhaCungCap) {
                    NhaCungCap supplier = (NhaCungCap) value;
                    setText(supplier.getMaNCC() + " - " + supplier.getTenNCC());
                }
                return this;
            }
        });
        productBox.setRenderer(new DefaultListCellRenderer() {
            private static final long serialVersionUID = 1L;

            @Override
            public java.awt.Component getListCellRendererComponent(javax.swing.JList<?> list, Object value,
                    int index, boolean selected, boolean focus) {
                super.getListCellRendererComponent(list, value, index, selected, focus);
                if (value instanceof SanPham) {
                    SanPham product = (SanPham) value;
                    setText(product.getMaSanPham() + " - " + product.getTenSanPham()
                            + (sale ? " (tồn " + product.getSoLuongTon() + ")" : ""));
                }
                return this;
            }
        });
        customerBox.setRenderer(new DefaultListCellRenderer() {
            private static final long serialVersionUID = 1L;

            @Override
            public java.awt.Component getListCellRendererComponent(javax.swing.JList<?> list, Object value,
                    int index, boolean selected, boolean focus) {
                super.getListCellRendererComponent(list, value, index, selected, focus);
                if (value instanceof KhachHangChoice) {
                    setText(value.toString());
                }
                return this;
            }
        });
        for (int i = 0; i < employeeBox.getItemCount(); i++) {
            if (employeeId != null && employeeId.equals(employeeBox.getItemAt(i).getMaNhanVien())) {
                employeeBox.setSelectedIndex(i);
                break;
            }
        }
        productBox.addActionListener(event -> updateSuggestedPrice());
        updateSuggestedPrice();
    }

    public static Draft show(java.awt.Window parent, boolean sale, String code, String employeeId) {
        PhieuLapDialog form = new PhieuLapDialog(sale, code, employeeId);
        return form.open(parent);
    }

    private void loadCustomerPromotions() {
        if (promotionWorker != null) {
            promotionWorker.cancel(true);
        }
        KhachHangChoice choice = (KhachHangChoice) customerBox.getSelectedItem();
        KhachHang customer = choice == null ? null : choice.customer;
        promotionPanel.setKhachHang(customer);
        if (customer == null) {
            return;
        }
        promotionPanel.setDangKiemTraKhuyenMai(true);
        promotionWorker = new SwingWorker<Set<String>, Void>() {
            @Override
            protected Set<String> doInBackground() {
                return new KhuyenMaiBUS().getMaDaSuDung(customer.getMaKhachHang());
            }

            @Override
            protected void done() {
                if (isCancelled() || promotionWorker != this) {
                    return;
                }
                try {
                    promotionPanel.capNhatKhuyenMaiDaDung(get());
                } catch (Exception ex) {
                    promotionPanel.setKhachHang(null);
                    ex.printStackTrace();
                    showError(customerBox, "Không thể kiểm tra khuyến mãi đã dùng. Vui lòng chọn lại khách hàng.");
                }
            }
        };
        promotionWorker.execute();
    }

    private Draft open(java.awt.Window parent) {
        JDialog dialog = new JDialog(parent, sale ? "Lập hóa đơn" : "Lập phiếu nhập",
                JDialog.ModalityType.APPLICATION_MODAL);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent event) {
                if (promotionWorker != null) {
                    promotionWorker.cancel(true);
                }
            }
        });
        JPanel content = new JPanel(new BorderLayout(0, 14));
        content.setBackground(Color.WHITE);
        content.setBorder(BorderFactory.createEmptyBorder(20, 22, 18, 22));

        JPanel header = new JPanel(new GridLayout(0, 2, 12, 10));
        header.setOpaque(false);
        addHeaderField(header, sale ? "Mã hóa đơn" : "Mã phiếu", new JLabel(code));
        addHeaderField(header, sale ? "Nhân viên bán hàng" : "Nhân viên lập phiếu", employeeBox);
        if (sale) {
            addHeaderField(header, "Khách hàng", customerBox);
        } else {
            addHeaderField(header, "Nhà cung cấp", supplierBox);
        }
        addHeaderField(header, "Ngày lập", new JLabel(LocalDate.now().toString()));

        JPanel addLine = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        addLine.setOpaque(false);
        productBox.setPreferredSize(new Dimension(230, 34));
        quantity.setPreferredSize(new Dimension(76, 34));
        unitPrice.setPreferredSize(new Dimension(125, 34));
        if (sale) {
            unitPrice.setEditable(false);
            unitPrice.setBackground(UiTheme.BACKGROUND);
        }
        JButton add = new JButton("Thêm sản phẩm");
        add.addActionListener(event -> addProduct());
        JButton remove = new JButton("Bỏ dòng");
        remove.addActionListener(event -> {
            int row = tableRow();
            if (row >= 0) {
                lines.removeRow(row);
                updateTotal();
            }
        });
        addLine.add(new JLabel("Sản phẩm"));
        addLine.add(productBox);
        addLine.add(new JLabel("SL"));
        addLine.add(quantity);
        addLine.add(new JLabel(sale ? "Đơn giá" : "Giá nhập"));
        addLine.add(unitPrice);
        addLine.add(add);
        addLine.add(remove);

        detailTable = new JTable(lines);
        detailTable.setRowHeight(34);
        detailTable.setFont(UiTheme.regular(12));
        detailTable.setFillsViewportHeight(true);
        detailTable.getTableHeader().setReorderingAllowed(false);
        detailTable.getTableHeader().setFont(UiTheme.bold(11));

        JPanel details = new JPanel(new BorderLayout(0, 10));
        details.setOpaque(false);
        details.add(addLine, BorderLayout.NORTH);
        details.add(new JScrollPane(detailTable), BorderLayout.CENTER);
        totalLabel.setFont(UiTheme.bold(15));
        totalLabel.setForeground(UiTheme.PRIMARY);
        totalLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        details.add(sale ? promotionPanel : totalLabel, BorderLayout.SOUTH);

        JButton cancel = new JButton("Đóng");
        JButton save = new JButton(sale ? "Lập hóa đơn" : "Lập phiếu nhập");
        save.setBackground(UiTheme.BLUE);
        save.setForeground(Color.WHITE);
        save.setFocusPainted(false);
        cancel.addActionListener(event -> dialog.dispose());
        save.addActionListener(event -> {
            if (employeeBox.getSelectedItem() == null) {
                showError(dialog, "Cần có nhân viên đang hoạt động để lập phiếu.");
                return;
            }
            if (!sale && supplierBox.getSelectedItem() == null) {
                showError(dialog, "Cần có nhà cung cấp đang hoạt động.");
                return;
            }
            if (productBox.getItemCount() == 0) {
                showError(dialog, sale ? "Không có sản phẩm khả dụng để bán."
                        : "Không có sản phẩm để lập phiếu nhập.");
                return;
            }
            if (lines.getRowCount() == 0) {
                showError(dialog, sale ? "Vui lòng thêm ít nhất một sản phẩm vào hóa đơn."
                        : "Vui lòng thêm ít nhất một sản phẩm vào phiếu nhập.");
                return;
            }
            result = buildDraft(dialog);
            if (result != null) {
                dialog.dispose();
            }
        });
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        footer.setOpaque(false);
        footer.add(cancel);
        footer.add(save);

        content.add(header, BorderLayout.NORTH);
        content.add(details, BorderLayout.CENTER);
        content.add(footer, BorderLayout.SOUTH);
        dialog.setContentPane(content);
        dialog.setMinimumSize(new Dimension(900, 510));
        dialog.setSize(980, sale ? 760 : 560);
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
        return result;
    }

    private int tableRow() {
        return detailTable == null ? -1 : detailTable.getSelectedRow();
    }

    private void addProduct() {
        SanPham product = (SanPham) productBox.getSelectedItem();
        if (product == null) {
            return;
        }
        try {
            quantity.commitEdit();
            int count = ((Number) quantity.getValue()).intValue();
            double price = Double.parseDouble(unitPrice.getText().trim());
            lines.addProduct(product, count, price, sale);
            updateTotal();
        } catch (java.text.ParseException ex) {
            showError(quantity, "Số lượng phải là số nguyên hợp lệ.");
        } catch (NumberFormatException ex) {
            showError(productBox, "Đơn giá phải là số không âm.");
        } catch (IllegalArgumentException ex) {
            showError(productBox, ex.getMessage());
        }
    }

    private Draft buildDraft(java.awt.Component parent) {
        NhanVien employee = (NhanVien) employeeBox.getSelectedItem();
        if (employee == null) {
            showError(parent, "Vui lòng chọn nhân viên.");
            return null;
        }
        double total = lines.getTotal();
        Draft draft = new Draft();
        if (sale) {
            try {
                total = promotionPanel.getTongThanhToan();
            } catch (IllegalArgumentException ex) {
                showError(parent, ex.getMessage());
                return null;
            }
            KhuyenMai promotion = promotionPanel.getKhuyenMai();
            KhachHangChoice customer = (KhachHangChoice) customerBox.getSelectedItem();
            draft.sale = new HoaDon(code, LocalDate.now(), employee.getMaNhanVien(),
                    customer == null ? null : customer.getMaKhachHang(),
                    promotion == null ? null : promotion.getMaKhuyenMai(),
                    "TIEN_MAT", total, "CHO_XU_LY");
            draft.sale.setDiemSuDung(promotionPanel.getDiemSuDung());
            draft.saleLines = new ArrayList<>();
            for (int i = 0; i < lines.getRowCount(); i++) {
                String productId = lines.getProductId(i);
                int count = lines.getQuantity(i);
                double price = lines.getUnitPrice(i);
                draft.saleLines.add(new ChiTietHoaDon(code, productId, count, price, count * price));
            }
        } else {
            NhaCungCap supplier = (NhaCungCap) supplierBox.getSelectedItem();
            draft.purchase = new PhieuNhap(code, LocalDate.now(), employee.getMaNhanVien(),
                    supplier.getMaNCC(), total, "CHO_XU_LY");
            draft.purchaseLines = new ArrayList<>();
            for (int i = 0; i < lines.getRowCount(); i++) {
                String productId = lines.getProductId(i);
                int count = lines.getQuantity(i);
                double price = lines.getUnitPrice(i);
                draft.purchaseLines.add(new ChiTietPhieuNhap(code, productId, count, price, count * price));
            }
        }
        return draft;
    }

    private void updateSuggestedPrice() {
        SanPham selected = (SanPham) productBox.getSelectedItem();
        unitPrice.setText(selected == null ? "0" : Double.toString(selected.getGiaBan()));
    }

    private void updateTotal() {
        if (sale) {
            promotionPanel.setTienHang(lines.getTotal());
        } else {
            totalLabel.setText("Tổng tiền: " + UiFormats.currency(lines.getTotal()));
        }
    }

    private static void addHeaderField(JPanel panel, String title, java.awt.Component value) {
        JLabel label = new JLabel(title);
        label.setFont(UiTheme.bold(12));
        label.setForeground(UiTheme.TEXT);
        panel.add(label);
        if (value instanceof JLabel) {
            ((JLabel) value).setFont(UiTheme.regular(13));
            ((JLabel) value).setForeground(UiTheme.MUTED);
        }
        panel.add(value);
    }

    private static void showError(java.awt.Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Thông tin chưa hợp lệ", JOptionPane.WARNING_MESSAGE);
    }

    public static final class Draft {
        public PhieuNhap purchase;
        public List<ChiTietPhieuNhap> purchaseLines;
        public HoaDon sale;
        public List<ChiTietHoaDon> saleLines;
    }

    private static final class KhachHangChoice {
        private final KhachHang customer;

        KhachHangChoice(KhachHang customer) {
            this.customer = customer;
        }

        String getMaKhachHang() {
            return customer == null ? null : customer.getMaKhachHang();
        }

        @Override
        public String toString() {
            return customer == null ? "Khách lẻ"
                    : customer.getMaKhachHang() + " - "
                            + customer.getHo() + " " + customer.getTen();
        }
    }
}
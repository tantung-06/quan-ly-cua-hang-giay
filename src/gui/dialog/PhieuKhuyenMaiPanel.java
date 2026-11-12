package gui.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;

import bus.DiemTichLuyBUS;
import bus.KhuyenMaiBUS;
import dto.KhachHang;
import dto.KhuyenMai;
import gui.UiFormats;
import gui.UiTheme;

public final class PhieuKhuyenMaiPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private final KhuyenMaiBUS promotions = new KhuyenMaiBUS();
    private final JComboBox<PromotionChoice> promotionBox = new JComboBox<>();
    private final List<KhuyenMai> availablePromotions;
    private final JLabel message = new JLabel(" ");
    private final JLabel subtotalLabel = amountLabel();
    private final JLabel discountLabel = amountLabel();
    private final JLabel totalLabel = amountLabel();
    private final JLabel balanceLabel = new JLabel("Điểm hiện có: 0");
    private final JSpinner points = new JSpinner(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1));
    private final JLabel pointDiscountLabel = amountLabel();
    private final JLabel earnedLabel = amountLabel();
    private KhachHang customer;
    private double subtotal;
    private boolean promotionLoading;

    public PhieuKhuyenMaiPanel(List<KhuyenMai> availablePromotions) {
        super(new BorderLayout(0, 8));
        this.availablePromotions = new ArrayList<>(availablePromotions);
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
        promotionBox.addItem(new PromotionChoice(null));
        promotionBox.setEnabled(false);
        promotionBox.setFont(UiTheme.regular(12));
        promotionBox.setBackground(Color.WHITE);
        promotionBox.setToolTipText("Chọn khách hàng trước; mỗi khách chỉ dùng một mã khuyến mãi một lần");
        promotionBox.addActionListener(event -> updateAmounts());
        JPanel selection = new JPanel(new BorderLayout(12, 0));
        selection.setOpaque(false);
        JLabel label = new JLabel("Khuyến mãi");
        label.setFont(UiTheme.bold(12));
        selection.add(label, BorderLayout.WEST);
        selection.add(promotionBox, BorderLayout.CENTER);
        add(selection, BorderLayout.NORTH);

        message.setFont(UiTheme.regular(12));
        message.setForeground(UiTheme.DANGER);
        JPanel amounts = new JPanel(new GridLayout(0, 1, 0, 4));
        amounts.setOpaque(false);
        amounts.add(subtotalLabel);
        amounts.add(discountLabel);
        amounts.add(totalLabel);
        totalLabel.setFont(UiTheme.bold(15));
        totalLabel.setForeground(UiTheme.PRIMARY);
        JPanel loyalty = new JPanel(new BorderLayout(12, 4));
        loyalty.setOpaque(false);
        balanceLabel.setFont(UiTheme.regular(12));
        loyalty.add(balanceLabel, BorderLayout.WEST);
        JPanel usePoints = new JPanel();
        usePoints.setOpaque(false);
        JLabel pointsCaption = new JLabel("Điểm sử dụng");
        pointsCaption.setLabelFor(points);
        usePoints.add(pointsCaption);
        points.setPreferredSize(new java.awt.Dimension(100, 28));
        points.setEnabled(false);
        points.setToolTipText("1 điểm = 1.000 VNĐ; không vượt quá điểm hiện có và tiền sau khuyến mãi");
        points.addChangeListener(event -> updateAmounts());
        usePoints.add(points);
        loyalty.add(usePoints, BorderLayout.EAST);
        JPanel center = new JPanel(new BorderLayout(0, 4));
        center.setOpaque(false);
        center.add(loyalty, BorderLayout.NORTH);
        center.add(message, BorderLayout.SOUTH);
        add(center, BorderLayout.CENTER);
        amounts.add(pointDiscountLabel, 2);
        amounts.add(earnedLabel);
        add(amounts, BorderLayout.SOUTH);
        updateAmounts();
    }

    public void setTienHang(double subtotal) {
        this.subtotal = subtotal;
        updateAmounts();
    }

    public KhuyenMai getKhuyenMai() {
        PromotionChoice choice = (PromotionChoice) promotionBox.getSelectedItem();
        return choice == null ? null : choice.promotion;
    }

    public double getTongThanhToan() {
        if (customer == null && getKhuyenMai() != null) {
            throw new IllegalArgumentException("Vui lòng chọn khách hàng trước khi áp dụng khuyến mãi.");
        }
        double afterPromotion = promotions.tinhTongThanhToan(getKhuyenMai(), subtotal, LocalDate.now());
        return DiemTichLuyBUS.tinhThucTra(afterPromotion, getDiemSuDung(),
                customer == null ? 0 : customer.getDiemTichLuy());
    }

    public void setKhachHang(KhachHang customer) {
        setKhachHang(customer, Collections.emptySet());
    }

    public void setKhachHang(KhachHang customer, Set<String> usedPromotions) {
        this.customer = customer;
        points.setValue(0);
        points.setEnabled(customer != null);
        capNhatKhuyenMaiDaDung(usedPromotions);
    }

    public void capNhatKhuyenMaiDaDung(Set<String> usedPromotions) {
        promotionLoading = false;
        promotionBox.removeAllItems();
        promotionBox.addItem(new PromotionChoice(null));
        if (customer != null) {
            for (KhuyenMai promotion : availablePromotions) {
                if (!usedPromotions.contains(promotion.getMaKhuyenMai())) {
                    promotionBox.addItem(new PromotionChoice(promotion));
                }
            }
        }
        promotionBox.setSelectedIndex(0);
        promotionBox.setEnabled(customer != null);
        updateAmounts();
    }

    public void setDangKiemTraKhuyenMai(boolean loading) {
        promotionLoading = loading;
        promotionBox.setEnabled(customer != null && !loading);
        updateAmounts();
    }

    public int getDiemSuDung() {
        try {
            points.commitEdit();
        } catch (java.text.ParseException ex) {
            throw new IllegalArgumentException("Điểm sử dụng phải là số nguyên không âm.");
        }
        return ((Number) points.getValue()).intValue();
    }

    private void updateAmounts() {
        double discount = 0;
        double total = subtotal;
        double pointReduction = 0;
        int earned = 0;
        try {
            KhuyenMai promotion = getKhuyenMai();
            discount = promotions.tinhTienGiam(promotion, subtotal, LocalDate.now());
            total = promotions.tinhTongThanhToan(promotion, subtotal, LocalDate.now());
            int used = ((Number) points.getValue()).intValue();
            total = DiemTichLuyBUS.tinhThucTra(total, used, customer == null ? 0 : customer.getDiemTichLuy());
            pointReduction = (double) used * DiemTichLuyBUS.VND_MOI_DIEM_DUNG;
            earned = customer == null ? 0 : DiemTichLuyBUS.tinhDiemCong(total);
            message.setText(promotionLoading ? "Đang kiểm tra mã khuyến mãi khách hàng đã dùng..." : " ");
        } catch (IllegalArgumentException ex) {
            message.setText(ex.getMessage());
        }
        subtotalLabel.setText("Tiền hàng: " + UiFormats.currency(subtotal));
        discountLabel.setText("Giảm giá: " + UiFormats.currency(discount));
        balanceLabel.setText(customer == null ? "Khách lẻ: không tích hoặc dùng điểm"
                : "Điểm hiện có: " + customer.getDiemTichLuy());
        pointDiscountLabel.setText("Giảm từ điểm: " + UiFormats.currency(pointReduction));
        totalLabel.setText("Cần thanh toán: " + UiFormats.currency(total));
        earnedLabel.setText("Điểm được cộng khi thanh toán (dự kiến): " + earned);
    }

    private static JLabel amountLabel() {
        JLabel label = new JLabel(" ", SwingConstants.RIGHT);
        label.setFont(UiTheme.regular(13));
        label.setForeground(UiTheme.TEXT);
        return label;
    }

    private static final class PromotionChoice {
        private final KhuyenMai promotion;

        private PromotionChoice(KhuyenMai promotion) {
            this.promotion = promotion;
        }

        @Override
        public String toString() {
            if (promotion == null) {
                return "Không áp dụng khuyến mãi";
            }
            String discount = "GIAM_TI_LE".equals(promotion.getLoaiKhuyenMai())
                    ? UiFormats.number(promotion.getMucGiam()) + "%"
                    : UiFormats.currency(promotion.getMucGiam());
            return promotion.getMaKhuyenMai() + " - " + promotion.getTenKhuyenMai() + " (giảm "
                    + discount + ", đơn tối thiểu " + UiFormats.currency(promotion.getDonHangToiThieu()) + ")";
        }
    }
}
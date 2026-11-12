package gui.panel;

import bus.PhieuNhapBUS;
import dto.PhieuNhap;

public class PhieuNhapPanel extends EntityListPanel {
    private static final long serialVersionUID = 1L;

    public PhieuNhapPanel() {
        super("Quản lý phiếu nhập", "Danh sách phiếu nhập hàng từ nhà cung cấp.",
                new String[] { "Mã phiếu", "Ngày nhập", "Mã nhân viên", "Mã nhà cung cấp", "Tổng tiền", "Trạng thái" },
                () -> new PhieuNhapBUS().getAll(),
                value -> {
                    PhieuNhap item = (PhieuNhap) value;
                    return new Object[] { item.getMaPhieuNhap(), item.getNgayNhap(), item.getMaNhanVien(),
                            item.getMaNCC(), item.getTongTien(), item.getTrangThai() };
                });
    }
}
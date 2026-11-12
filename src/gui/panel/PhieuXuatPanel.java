package gui.panel;

import bus.PhieuXuatBUS;
import dto.PhieuXuat;

public class PhieuXuatPanel extends EntityListPanel {
    private static final long serialVersionUID = 1L;

    public PhieuXuatPanel() {
        super("Quản lý phiếu xuất", "Theo dõi phiếu xuất và trạng thái xử lý.",
                new String[] { "Mã phiếu xuất", "Ngày lập", "Mã nhân viên", "Mã khách hàng", "Thanh toán", "Tổng tiền",
                        "Trạng thái" },
                () -> new PhieuXuatBUS().getAll(),
                value -> {
                    PhieuXuat item = (PhieuXuat) value;
                    return new Object[] { item.getMaPhieuXuat(), item.getNgayLap(), item.getMaNhanVien(),
                            item.getMaKhachHang(), item.getPhuongThucThanhToan(), item.getTongTien(),
                            item.getTrangThai() };
                });
    }
}
package gui.panel;

import bus.HoaDonBUS;
import dto.HoaDon;

public class HoaDonPanel extends EntityListPanel {
    private static final long serialVersionUID = 1L;

    public HoaDonPanel() {
        super("Quản lý hóa đơn", "Theo dõi hóa đơn và trạng thái xử lý.",
                new String[] { "Mã hóa đơn", "Ngày lập", "Mã nhân viên", "Mã khách hàng", "Thanh toán", "Tổng tiền",
                        "Trạng thái" },
                () -> new HoaDonBUS().getAll(),
                value -> {
                    HoaDon item = (HoaDon) value;
                    return new Object[] { item.getMaHoaDon(), item.getNgayLap(), item.getMaNhanVien(),
                            item.getMaKhachHang(), item.getPhuongThucThanhToan(), item.getTongTien(),
                            item.getTrangThai() };
                });
    }
}
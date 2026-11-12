package gui.panel;

import bus.KhachHangBUS;
import dto.KhachHang;

public class KhachHangPanel extends EntityListPanel {
    private static final long serialVersionUID = 1L;

    public KhachHangPanel() {
        super("Danh sách khách hàng", "Thông tin khách hàng và điểm tích lũy.",
                new String[] { "Mã khách hàng", "Họ tên", "Điện thoại", "Email", "Điểm tích lũy", "Trạng thái" },
                () -> new KhachHangBUS().getAll(),
                value -> {
                    KhachHang item = (KhachHang) value;
                    return new Object[] { item.getMaKhachHang(), item.getHo() + " " + item.getTen(),
                            item.getSoDienThoai(), item.getEmail(), item.getDiemTichLuy(), item.getTrangThai() };
                });
    }
}
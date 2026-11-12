package gui.panel;

import bus.NhanVienBUS;
import dto.NhanVien;

public class NhanVienPanel extends EntityListPanel {
    private static final long serialVersionUID = 1L;

    public NhanVienPanel() {
        super("Danh sách nhân viên", "Thông tin nhân sự đang quản lý.",
                new String[] { "Mã nhân viên", "Họ tên", "Điện thoại", "Email", "Mã tài khoản", "Trạng thái" },
                () -> new NhanVienBUS().getAll(),
                value -> {
                    NhanVien item = (NhanVien) value;
                    return new Object[] { item.getMaNhanVien(), item.getHo() + " " + item.getTen(),
                            item.getSoDienThoai(), item.getEmail(), item.getMaTaiKhoan(), item.getTrangThai() };
                });
    }
}
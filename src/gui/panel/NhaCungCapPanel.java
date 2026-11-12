package gui.panel;

import bus.NhaCungCapBUS;
import dto.NhaCungCap;

public class NhaCungCapPanel extends EntityListPanel {
    private static final long serialVersionUID = 1L;

    public NhaCungCapPanel() {
        super("Danh sách nhà cung cấp", "Thông tin liên hệ các đối tác cung ứng.",
                new String[] { "Mã nhà cung cấp", "Tên nhà cung cấp", "Điện thoại", "Email", "Địa chỉ", "Trạng thái" },
                () -> new NhaCungCapBUS().getAll(),
                value -> {
                    NhaCungCap item = (NhaCungCap) value;
                    return new Object[] { item.getMaNCC(), item.getTenNCC(), item.getSoDienThoai(),
                            item.getEmail(), item.getDiaChi(), item.getTrangThai() };
                });
    }
}
package gui.panel;

import bus.KhuyenMaiBUS;
import dto.KhuyenMai;

public class KhuyenMaiPanel extends EntityListPanel {
    private static final long serialVersionUID = 1L;

    public KhuyenMaiPanel() {
        super("Chương trình khuyến mãi", "Danh sách ưu đãi và thời gian áp dụng.",
                new String[] { "Mã", "Tên chương trình", "Loại", "Mức giảm", "Bắt đầu", "Kết thúc", "Trạng thái" },
                () -> new KhuyenMaiBUS().getAll(),
                value -> {
                    KhuyenMai item = (KhuyenMai) value;
                    return new Object[] { item.getMaKhuyenMai(), item.getTenKhuyenMai(), item.getLoaiKhuyenMai(),
                            item.getMucGiam(), item.getNgayBatDau(), item.getNgayKetThuc(), item.getTrangThai() };
                });
    }
}
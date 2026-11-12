package gui.dialog;

public final class KhuyenMaiDialog extends FormDialog {
    public KhuyenMaiDialog(String[] values, boolean editing) {
        super(new String[] { "Mã khuyến mãi", "Tên chương trình", "Đơn hàng tối thiểu",
                "Loại khuyến mãi", "Mức giảm", "Ngày bắt đầu (YYYY-MM-DD)",
                "Ngày kết thúc (YYYY-MM-DD)", "Trạng thái" },
                values,
                new String[][] { null, null, null, { "GIAM_TI_LE", "GIAM_TIEN" }, null, null, null,
                        { "AP_DUNG", "HET_HAN" } },
                new boolean[] { true, false, false, false, false, false, false, false });
    }
}
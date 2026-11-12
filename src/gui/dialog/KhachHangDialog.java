package gui.dialog;

public final class KhachHangDialog extends FormDialog {
    public KhachHangDialog(String[] values, boolean editing) {
        super(new String[] { "Mã khách hàng", "Họ", "Tên", "Điện thoại", "Địa chỉ",
                "Email", "Điểm tích lũy", "Trạng thái" },
                values,
                new String[][] { null, null, null, null, null, null, null, { "HOAT_DONG", "NGUNG" } },
                new boolean[] { true, false, false, false, false, false, true, false });
    }
}
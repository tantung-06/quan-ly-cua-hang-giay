package gui.dialog;

public final class PhieuNhapDialog extends FormDialog {
    public PhieuNhapDialog(String[] values) {
        super(new String[] { "Mã phiếu", "Ngày nhập", "Mã nhân viên", "Mã nhà cung cấp",
                "Tổng tiền", "Trạng thái" },
                values,
                new String[][] { null, null, null, null, null, { "CHO_XU_LY", "DA_NHAP", "HUY" } },
                new boolean[] { true, true, true, true, true, false });
    }
}
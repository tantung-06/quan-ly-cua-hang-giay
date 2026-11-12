package gui.dialog;

public final class HoaDonDialog extends FormDialog {
    public HoaDonDialog(String[] values) {
        super(new String[] { "Mã hóa đơn", "Ngày lập", "Mã nhân viên", "Mã khách hàng",
                "Thanh toán", "Tổng tiền", "Trạng thái" },
                values,
                new String[][] { null, null, null, null, null, null,
                        { "CHO_XU_LY", "DA_XUAT", "HUY" } },
                new boolean[] { true, true, true, true, true, true, false });
    }
}
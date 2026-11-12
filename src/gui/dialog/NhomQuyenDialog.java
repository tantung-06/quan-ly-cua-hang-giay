package gui.dialog;

public final class NhomQuyenDialog extends FormDialog {
    public NhomQuyenDialog(String[] values, boolean editing) {
        super(new String[] { "Mã nhóm quyền", "Tên nhóm quyền" },
                values, new String[][] { null, null }, new boolean[] { true, false });
    }
}
package security;

public enum PermissionAction {
    VIEW("Xem"), CREATE("Thêm / Lập phiếu"), EDIT("Sửa"), DELETE("Xóa"),
    COMPLETE("Xác nhận"), CANCEL("Hủy phiếu");

    private final String label;

    PermissionAction(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }
}

package gui.module;

import java.awt.Component;

public interface RecordEditor {
    Object show(Component parent, Object record);

    boolean save(Object value, boolean insert);

    boolean delete(Object value);

    default Object[][] loadViewRows(Object record) {
        return new Object[0][0];
    }

    default void showView(Component parent, Object record, Object[][] rows) {
    }

    default boolean cancel(Object value) {
        return false;
    }

    default boolean complete(Object value) {
        return false;
    }

    default boolean canCreate() {
        return true;
    }

    default boolean canEdit() {
        return true;
    }

    default boolean canDelete() {
        return true;
    }

    default boolean canCancel(Object value) {
        return false;
    }

    default boolean canComplete(Object value) {
        return false;
    }

    default String createLabel() {
        return "Thêm";
    }

    default String viewLabel() {
        return null;
    }

    default String cancelLabel() {
        return null;
    }

    default String cancelConfirmation(Object value) {
        return "Bạn có chắc muốn hủy phiếu đã chọn?";
    }

    default String completeConfirmation() {
        return "Xác nhận hoàn tất phiếu? Thao tác này sẽ cập nhật tồn kho.";
    }

    default String completeLabel() {
        return null;
    }
}
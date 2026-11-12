package gui.dialog;

import javax.swing.table.DefaultTableModel;

import dto.SanPham;

public final class PhieuLineTableModel extends DefaultTableModel {
    private static final long serialVersionUID = 1L;
    private static final int PRODUCT_ID = 0;
    private static final int QUANTITY = 2;
    private static final int UNIT_PRICE = 3;
    private static final int AMOUNT = 4;

    public PhieuLineTableModel() {
        super(new String[] { "Mã sản phẩm", "Tên sản phẩm", "Số lượng", "Đơn giá", "Thành tiền" }, 0);
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    public void addProduct(SanPham product, int quantity, double price, boolean sale) {
        if (product == null || quantity <= 0 || !Double.isFinite(price) || price < 0) {
            throw new IllegalArgumentException("Số lượng và đơn giá không hợp lệ.");
        }
        int existingRow = -1;
        long combined = quantity;
        for (int row = 0; row < getRowCount(); row++) {
            if (product.getMaSanPham().equals(getProductId(row))) {
                existingRow = row;
                combined += getQuantity(row);
                break;
            }
        }
        if (combined > Integer.MAX_VALUE || (sale && combined > product.getSoLuongTon())) {
            throw new IllegalArgumentException("Số lượng vượt quá giới hạn hoặc tồn kho hiện tại.");
        }
        double amount = combined * price;
        if (!Double.isFinite(amount)) {
            throw new IllegalArgumentException("Thành tiền vượt quá giới hạn.");
        }
        if (existingRow >= 0) {
            if (Double.compare(getUnitPrice(existingRow), price) != 0) {
                throw new IllegalArgumentException(
                        "Sản phẩm đã có đơn giá khác. Hãy bỏ dòng cũ trước khi đổi giá.");
            }
            setValueAt((int) combined, existingRow, QUANTITY);
            setValueAt(amount, existingRow, AMOUNT);
        } else {
            addRow(new Object[] { product.getMaSanPham(), product.getTenSanPham(), quantity, price, amount });
        }
    }

    public String getProductId(int row) {
        return (String) getValueAt(row, PRODUCT_ID);
    }

    public int getQuantity(int row) {
        return ((Number) getValueAt(row, QUANTITY)).intValue();
    }

    public double getUnitPrice(int row) {
        return ((Number) getValueAt(row, UNIT_PRICE)).doubleValue();
    }

    public double getTotal() {
        double total = 0;
        for (int row = 0; row < getRowCount(); row++) {
            total += ((Number) getValueAt(row, AMOUNT)).doubleValue();
        }
        return total;
    }
}
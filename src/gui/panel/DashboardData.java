package gui.panel;

import java.util.List;

import bus.HoaDonBUS;
import bus.KhachHangBUS;
import bus.NhanVienBUS;
import bus.SanPhamBUS;
import dto.HoaDon;
import dto.SanPham;

final class DashboardData {
    private static final int LOW_STOCK_LIMIT = 5;

    final List<SanPham> products;
    final List<HoaDon> sales;
    final int customers;
    final int employees;
    final int lowStockProducts;

    DashboardData(List<SanPham> products, List<HoaDon> sales, int customers, int employees) {
        this.products = List.copyOf(products);
        this.sales = List.copyOf(sales);
        this.customers = customers;
        this.employees = employees;
        lowStockProducts = (int) products.stream().filter(DashboardData::isLowStock).count();
    }

    static DashboardData load() {
        return new DashboardData(new SanPhamBUS().getAll(), new HoaDonBUS().getAll(),
                new KhachHangBUS().getAll().size(), new NhanVienBUS().getAll().size());
    }

    private static boolean isLowStock(SanPham product) {
        return "DANG_BAN".equals(product.getTrangThai()) && product.getSoLuongTon() <= LOW_STOCK_LIMIT;
    }
}

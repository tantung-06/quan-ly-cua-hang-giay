package gui.module;

import java.awt.Component;
import java.awt.Window;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import bus.DiemTichLuyBUS;
import bus.KhachHangBUS;
import bus.KhuyenMaiBUS;
import bus.LoaiGiayBUS;
import bus.NhaCungCapBUS;
import bus.NhanVienBUS;
import bus.NhomQuyenBUS;
import bus.PhieuNhapBUS;
import bus.PhieuXuatBUS;
import bus.SanPhamBUS;
import bus.TaiKhoanBUS;
import bus.ThuongHieuBUS;
import dto.ChiTietPhieuNhap;
import dto.ChiTietPhieuXuat;
import dto.KhachHang;
import dto.KhuyenMai;
import dto.LichSuDiem;
import dto.LoaiGiay;
import dto.NhaCungCap;
import dto.NhanVien;
import dto.NhomQuyen;
import dto.PhieuNhap;
import dto.PhieuXuat;
import dto.SanPham;
import dto.TaiKhoan;
import dto.ThuongHieu;
import gui.UiFormats;
import gui.dialog.KhachHangDialog;
import gui.dialog.KhuyenMaiDialog;
import gui.dialog.NhaCungCapDialog;
import gui.dialog.NhanVienDialog;
import gui.dialog.NhomQuyenDialog;
import gui.dialog.PhieuChiTietDialog;
import gui.dialog.PhieuLapDialog;
import gui.dialog.PhieuNhapDialog;
import gui.dialog.PhieuXuatDialog;
import gui.dialog.SanPhamDialog;
import gui.dialog.TaiKhoanDialog;
import gui.dialog.ThuocTinhDialog;

public final class ModuleRegistry {
    private final TaiKhoan account;
    private final Window owner;

    public ModuleRegistry(TaiKhoan account, Window owner) {
        this.account = account;
        this.owner = owner;
    }

    public Map<String, ModuleDefinition> create() {
        Map<String, ModuleDefinition> result = new LinkedHashMap<>();
        result.put("Sản phẩm", new ModuleDefinition("Danh mục sản phẩm", "Theo dõi thông tin và tồn kho sản phẩm.",
                new String[] { "Mã sản phẩm", "Tên sản phẩm", "Loại giày", "Thương hiệu", "Giá bán", "Tồn kho",
                        "Trạng thái" },
                () -> new SanPhamBUS().getAll(),
                value -> {
                    SanPham item = (SanPham) value;
                    return new Object[] { item.getMaSanPham(), item.getTenSanPham(), item.getMaLoaiGiay(),
                            item.getMaThuongHieu(), item.getGiaBan(), item.getSoLuongTon(), item.getTrangThai() };
                }));
        result.put("Thuộc tính", new ModuleDefinition("Loại giày & thương hiệu", "Danh mục thuộc tính sản phẩm.",
                new String[] { "Nhóm thuộc tính", "Mã", "Tên" },
                () -> {
                    List<AttributeItem> rows = new ArrayList<>();
                    for (LoaiGiay item : new LoaiGiayBUS().getAll()) {
                        rows.add(new AttributeItem("Loại giày", item.getMaLoaiGiay(), item.getTenLoaiGiay()));
                    }
                    for (ThuongHieu item : new ThuongHieuBUS().getAll()) {
                        rows.add(new AttributeItem("Thương hiệu", item.getMaThuongHieu(), item.getTenThuongHieu()));
                    }
                    return rows;
                }, value -> {
                    AttributeItem item = (AttributeItem) value;
                    return new Object[] { item.group, item.code, item.name };
                }));
        result.put("Phiếu nhập",
                new ModuleDefinition("Quản lý phiếu nhập", "Danh sách phiếu nhập hàng từ nhà cung cấp.",
                        new String[] { "Mã phiếu", "Ngày nhập", "Mã nhân viên", "Mã nhà cung cấp", "Tổng tiền",
                                "Trạng thái" },
                        () -> new PhieuNhapBUS().getAll(),
                        value -> {
                            PhieuNhap item = (PhieuNhap) value;
                            return new Object[] { item.getMaPhieuNhap(), item.getNgayNhap(), item.getMaNhanVien(),
                                    item.getMaNCC(), item.getTongTien(), item.getTrangThai() };
                        }));
        result.put("Phiếu xuất", new ModuleDefinition("Quản lý phiếu xuất",
                "Theo dõi phiếu xuất bán hàng và trạng thái xử lý.",
                new String[] { "Mã phiếu xuất", "Ngày lập", "Mã nhân viên", "Mã khách hàng", "Thanh toán", "Tổng tiền",
                        "Trạng thái" },
                () -> new PhieuXuatBUS().getAll(),
                value -> {
                    PhieuXuat item = (PhieuXuat) value;
                    return new Object[] { item.getMaPhieuXuat(), item.getNgayLap(), item.getMaNhanVien(),
                            item.getMaKhachHang(), item.getPhuongThucThanhToan(), item.getTongTien(),
                            item.getTrangThai() };
                }));
        result.put("Nhà cung cấp",
                new ModuleDefinition("Danh sách nhà cung cấp", "Thông tin liên hệ các đối tác cung ứng.",
                        new String[] { "Mã nhà cung cấp", "Tên nhà cung cấp", "Điện thoại", "Email", "Địa chỉ",
                                "Trạng thái" },
                        () -> new NhaCungCapBUS().getAll(),
                        value -> {
                            NhaCungCap item = (NhaCungCap) value;
                            return new Object[] { item.getMaNCC(), item.getTenNCC(), item.getSoDienThoai(),
                                    item.getEmail(), item.getDiaChi(), item.getTrangThai() };
                        }));
        result.put("Khách hàng", new ModuleDefinition("Danh sách khách hàng", "Thông tin khách hàng và điểm tích lũy.",
                new String[] { "Mã khách hàng", "Họ tên", "Điện thoại", "Email", "Điểm tích lũy", "Trạng thái" },
                () -> new KhachHangBUS().getAll(),
                value -> {
                    KhachHang item = (KhachHang) value;
                    return new Object[] { item.getMaKhachHang(), item.getHo() + " " + item.getTen(),
                            item.getSoDienThoai(), item.getEmail(), item.getDiemTichLuy(), item.getTrangThai() };
                }));
        result.put("Nhân viên", new ModuleDefinition("Danh sách nhân viên", "Thông tin nhân sự đang quản lý.",
                new String[] { "Mã nhân viên", "Họ tên", "Điện thoại", "Email", "Mã tài khoản", "Trạng thái" },
                () -> new NhanVienBUS().getAll(),
                value -> {
                    NhanVien item = (NhanVien) value;
                    return new Object[] { item.getMaNhanVien(), item.getHo() + " " + item.getTen(),
                            item.getSoDienThoai(), item.getEmail(), item.getMaTaiKhoan(), item.getTrangThai() };
                }));
        boolean showPasswords = account != null && "NQ001".equals(account.getMaNhomQuyen())
                && "HOAT_DONG".equals(account.getTrangThai());
        result.put("Tài khoản", new ModuleDefinition("Quản lý tài khoản", "Tài khoản đăng nhập và nhóm quyền được gán.",
                showPasswords
                        ? new String[] { "Mã tài khoản", "Tên đăng nhập", "Mật khẩu", "Mã nhóm quyền", "Trạng thái" }
                        : new String[] { "Mã tài khoản", "Tên đăng nhập", "Mã nhóm quyền", "Trạng thái" },
                () -> new TaiKhoanBUS().getAll(),
                value -> {
                    TaiKhoan item = (TaiKhoan) value;
                    if (showPasswords) {
                        return new Object[] { item.getMaTaiKhoan(), item.getTenDangNhap(), item.getMatKhau(),
                                item.getMaNhomQuyen(), item.getTrangThai() };
                    }
                    return new Object[] { item.getMaTaiKhoan(), item.getTenDangNhap(),
                            item.getMaNhomQuyen(), item.getTrangThai() };
                }));
        result.put("Phân quyền", new ModuleDefinition("Nhóm quyền", "Các nhóm quyền được cấu hình trong hệ thống.",
                new String[] { "Mã nhóm quyền", "Tên nhóm quyền" },
                () -> new NhomQuyenBUS().getAll(),
                value -> {
                    NhomQuyen item = (NhomQuyen) value;
                    return new Object[] { item.getMaNhomQuyen(), item.getTenNhomQuyen() };
                }));
        result.put("Khuyến mãi",
                new ModuleDefinition("Chương trình khuyến mãi", "Danh sách ưu đãi và thời gian áp dụng.",
                        new String[] { "Mã khuyến mãi", "Tên chương trình", "Loại", "Mức giảm", "Bắt đầu", "Kết thúc",
                                "Trạng thái" },
                        () -> new KhuyenMaiBUS().getAll(),
                        value -> {
                            KhuyenMai item = (KhuyenMai) value;
                            return new Object[] { item.getMaKhuyenMai(), item.getTenKhuyenMai(),
                                    item.getLoaiKhuyenMai(),
                                    UiFormats.number(item.getMucGiam())
                                            + ("GIAM_TI_LE".equals(item.getLoaiKhuyenMai()) ? "%" : " ₫"),
                                    item.getNgayBatDau(), item.getNgayKetThuc(), item.getTrangThai() };
                        }));
        configureCrud(result);
        return result;
    }

    private void configureCrud(Map<String, ModuleDefinition> modules) {
        modules.get("Thuộc tính").editor = new RecordEditor() {
            private final LoaiGiayBUS types = new LoaiGiayBUS();
            private final ThuongHieuBUS brands = new ThuongHieuBUS();

            @Override
            public Object show(Component parent, Object record) {
                AttributeItem item = (AttributeItem) record;
                String typeCode = null;
                String brandCode = null;
                if (item == null) {
                    typeCode = types.generateMaLoaiGiay();
                    brandCode = brands.generateMaThuongHieu();
                    if (typeCode == null || brandCode == null) {
                        showInvalid(parent, "Không thể tạo mã thuộc tính. Vui lòng thử lại.");
                        return null;
                    }
                }
                String[] input = ThuocTinhDialog.show(parent,
                        item == null ? "Loại giày" : item.group,
                        item == null ? typeCode : item.code,
                        item == null ? "" : item.name,
                        typeCode, brandCode, item != null);
                return input == null ? null : new AttributeItem(input[0], input[1], input[2]);
            }

            @Override
            public boolean save(Object value, boolean insert) {
                AttributeItem item = (AttributeItem) value;
                if ("Loại giày".equals(item.group)) {
                    LoaiGiay type = new LoaiGiay(item.code, item.name);
                    return insert ? types.insert(type) : types.update(type);
                }
                ThuongHieu brand = new ThuongHieu(item.code, item.name);
                return insert ? brands.insert(brand) : brands.update(brand);
            }

            @Override
            public boolean delete(Object value) {
                AttributeItem item = (AttributeItem) value;
                return "Loại giày".equals(item.group)
                        ? types.delete(item.code)
                        : brands.delete(item.code);
            }
        };
        modules.get("Phiếu nhập").editor = new RecordEditor() {
            private final PhieuNhapBUS bus = new PhieuNhapBUS();

            @Override
            public Object show(Component parent, Object record) {
                if (record == null) {
                    NhanVien employee;
                    String code;
                    employee = new NhanVienBUS().getByTaiKhoan(account.getMaTaiKhoan());
                    code = bus.generateMaPhieuNhap();
                    PhieuLapDialog.Draft draft = PhieuLapDialog.show(
                            owner, false, code,
                            employee == null ? null : employee.getMaNhanVien());
                    return draft;
                }
                PhieuNhap item = (PhieuNhap) record;
                String[] values = { item.getMaPhieuNhap(), item.getNgayNhap().toString(),
                        item.getMaNhanVien(), item.getMaNCC(), Double.toString(item.getTongTien()),
                        item.getTrangThai() };
                String[] input = new PhieuNhapDialog(values).show(parent, "Cập nhật trạng thái phiếu nhập");
                return input == null ? null : new Object[] { item.getMaPhieuNhap(), input[5] };
            }

            @Override
            public boolean save(Object value, boolean insert) {
                if (insert) {
                    PhieuLapDialog.Draft draft = (PhieuLapDialog.Draft) value;
                    if (!bus.insert(draft.purchase))
                        return false;
                    ArrayList<dto.ChiTietPhieuNhap> details = new ArrayList<>(draft.purchaseLines);
                    if (!bus.insertBatchChiTiet(details)) {
                        bus.delete(draft.purchase.getMaPhieuNhap());
                        return false;
                    }
                    return true;
                }
                Object[] values = (Object[]) value;
                return bus.updateTrangThai((String) values[0], (String) values[1]);
            }

            @Override
            public boolean delete(Object value) {
                return false;
            }

            @Override
            public boolean cancel(Object value) {
                return bus.huyPhieu(((PhieuNhap) value).getMaPhieuNhap());
            }

            @Override
            public boolean complete(Object value) {
                return bus.hoanTatNhap(((PhieuNhap) value).getMaPhieuNhap());
            }

            @Override
            public Object[][] loadViewRows(Object record) {
                ArrayList<ChiTietPhieuNhap> details = bus.getChiTietByPhieuNhap(
                        ((PhieuNhap) record).getMaPhieuNhap());
                Object[][] rows = new Object[details.size()][4];
                for (int i = 0; i < details.size(); i++) {
                    ChiTietPhieuNhap detail = details.get(i);
                    rows[i] = new Object[] { detail.getMaSanPham(), detail.getSoLuong(),
                            detail.getGiaNhap(), detail.getThanhTien() };
                }
                return rows;
            }

            @Override
            public void showView(Component parent, Object record, Object[][] rows) {
                PhieuNhap item = (PhieuNhap) record;
                PhieuChiTietDialog.show(parent, "Chi tiết phiếu nhập " + item.getMaPhieuNhap(),
                        new String[][] {
                                { "Mã phiếu", item.getMaPhieuNhap() },
                                { "Ngày nhập", item.getNgayNhap().toString() },
                                { "Mã nhân viên", item.getMaNhanVien() },
                                { "Mã nhà cung cấp", item.getMaNCC() },
                                { "Tổng tiền", UiFormats.currency(item.getTongTien()) },
                                { "Trạng thái", item.getTrangThai() }
                        }, new String[] { "Mã sản phẩm", "Số lượng", "Giá nhập", "Thành tiền" }, rows);
            }

            @Override
            public String createLabel() {
                return "Lập phiếu nhập";
            }

            @Override
            public String viewLabel() {
                return "Xem phiếu";
            }

            @Override
            public String cancelLabel() {
                return "Hủy phiếu";
            }

            @Override
            public String completeLabel() {
                return "Xác nhận nhập";
            }

            @Override
            public boolean canEdit() {
                return false;
            }

            @Override
            public boolean canDelete() {
                return false;
            }

            @Override
            public boolean canCancel(Object value) {
                return "CHO_XU_LY".equals(((PhieuNhap) value).getTrangThai());
            }

            @Override
            public boolean canComplete(Object value) {
                return "CHO_XU_LY".equals(((PhieuNhap) value).getTrangThai());
            }
        };
        modules.get("Phiếu xuất").editor = new RecordEditor() {
            private final PhieuXuatBUS bus = new PhieuXuatBUS();

            @Override
            public Object show(Component parent, Object record) {
                if (record == null) {
                    NhanVien employee;
                    String code;
                    employee = new NhanVienBUS().getByTaiKhoan(account.getMaTaiKhoan());
                    code = bus.generateMaPhieuXuat();
                    PhieuLapDialog.Draft draft = PhieuLapDialog.show(
                            owner, true, code,
                            employee == null ? null : employee.getMaNhanVien());
                    return draft;
                }
                PhieuXuat item = (PhieuXuat) record;
                String[] values = { item.getMaPhieuXuat(), item.getNgayLap().toString(),
                        item.getMaNhanVien(), item.getMaKhachHang() == null ? "Khách lẻ" : item.getMaKhachHang(),
                        item.getPhuongThucThanhToan(), Double.toString(item.getTongTien()), item.getTrangThai() };
                String[] input = new PhieuXuatDialog(values).show(parent, "Cập nhật trạng thái phiếu xuất");
                return input == null ? null : new Object[] { item.getMaPhieuXuat(), input[6] };
            }

            @Override
            public boolean save(Object value, boolean insert) {
                if (insert) {
                    PhieuLapDialog.Draft draft = (PhieuLapDialog.Draft) value;
                    return bus.lapPhieu(draft.sale, new ArrayList<>(draft.saleLines));
                }
                Object[] values = (Object[]) value;
                return bus.updateTrangThai((String) values[0], (String) values[1]);
            }

            @Override
            public boolean delete(Object value) {
                return false;
            }

            @Override
            public boolean cancel(Object value) {
                return bus.huyPhieu(((PhieuXuat) value).getMaPhieuXuat());
            }

            @Override
            public boolean complete(Object value) {
                return bus.hoanTatXuat(((PhieuXuat) value).getMaPhieuXuat());
            }

            @Override
            public Object[][] loadViewRows(Object record) {
                ArrayList<ChiTietPhieuXuat> details = bus.getChiTietByPhieuXuat(
                        ((PhieuXuat) record).getMaPhieuXuat());
                Object[][] rows = new Object[details.size()][4];
                for (int i = 0; i < details.size(); i++) {
                    ChiTietPhieuXuat detail = details.get(i);
                    rows[i] = new Object[] { detail.getMaSanPham(), detail.getSoLuong(),
                            detail.getDonGia(), detail.getThanhTien() };
                }
                return rows;
            }

            @Override
            public void showView(Component parent, Object record, Object[][] rows) {
                PhieuXuat item = (PhieuXuat) record;
                double subtotal = 0;
                for (Object[] row : rows) {
                    subtotal += ((Number) row[3]).doubleValue();
                }
                PhieuChiTietDialog.show(parent, "Chi tiết phiếu xuất " + item.getMaPhieuXuat(),
                        new String[][] {
                                { "Mã phiếu xuất", item.getMaPhieuXuat() },
                                { "Ngày lập", item.getNgayLap().toString() },
                                { "Mã nhân viên", item.getMaNhanVien() },
                                { "Mã khách hàng", item.getMaKhachHang() == null
                                        ? "Khách lẻ"
                                        : item.getMaKhachHang() },
                                { "Thanh toán", item.getPhuongThucThanhToan() },
                                { "Khuyến mãi", item.getMaKhuyenMai() == null
                                        ? "Không áp dụng"
                                        : item.getMaKhuyenMai() },
                                { "Tiền hàng", UiFormats.currency(subtotal) },
                                { "Giảm khuyến mãi", UiFormats.currency(Math.max(0, subtotal - item.getTongTien()
                                        - (double) item.getDiemSuDung() * DiemTichLuyBUS.VND_MOI_DIEM_DUNG)) },
                                { "Điểm sử dụng", Integer.toString(item.getDiemSuDung()) },
                                { "Giảm từ điểm",
                                        UiFormats.currency(
                                                (double) item.getDiemSuDung() * DiemTichLuyBUS.VND_MOI_DIEM_DUNG) },
                                { "Điểm đã cộng", Integer.toString(item.getDiemDuocCong()) },
                                { "Xử lý điểm", !item.isDiemDaXuLy() ? "Chưa xử lý điểm"
                                        : "HUY".equals(item.getTrangThai()) ? "Đã xử lý theo lịch sử cũ"
                                                : "Đã xử lý khi thanh toán" },
                                { "Cần thanh toán", UiFormats.currency(item.getTongTien()) },
                                { "Trạng thái", item.getTrangThai() }
                        }, new String[] { "Mã sản phẩm", "Số lượng", "Đơn giá", "Thành tiền" }, rows);
            }

            @Override
            public String createLabel() {
                return "Lập phiếu xuất";
            }

            @Override
            public String viewLabel() {
                return "Xem phiếu xuất";
            }

            @Override
            public String cancelLabel() {
                return "Hủy phiếu xuất";
            }

            @Override
            public String cancelConfirmation(Object value) {
                return "Hủy phiếu xuất đang chờ? Phiếu chưa hoàn tất nên số điểm khách hàng chưa thay đổi.";
            }

            @Override
            public String completeConfirmation() {
                return "Xác nhận đã nhận đủ tiền thanh toán? Hệ thống sẽ xác nhận xuất, trừ điểm sử dụng và cộng điểm theo số tiền thực trả.";
            }

            @Override
            public String completeLabel() {
                return "Xác nhận xuất";
            }

            @Override
            public boolean canEdit() {
                return false;
            }

            @Override
            public boolean canDelete() {
                return false;
            }

            @Override
            public boolean canCancel(Object value) {
                PhieuXuat sale = (PhieuXuat) value;
                return "CHO_XU_LY".equals(sale.getTrangThai()) && !sale.isDiemDaXuLy();
            }

            @Override
            public boolean canComplete(Object value) {
                return "CHO_XU_LY".equals(((PhieuXuat) value).getTrangThai());
            }
        };
        modules.get("Sản phẩm").editor = new RecordEditor() {
            private final SanPhamBUS bus = new SanPhamBUS();

            @Override
            public Object show(Component parent, Object record) {
                SanPham item = (SanPham) record;
                ArrayList<LoaiGiay> types;
                ArrayList<ThuongHieu> brands;
                types = new LoaiGiayBUS().getAll();
                brands = new ThuongHieuBUS().getAll();
                if (types.isEmpty() || brands.isEmpty()) {
                    showInvalid(parent, "Cần có ít nhất một loại giày và một thương hiệu trước khi tạo sản phẩm.");
                    return null;
                }
                String[] typeOptions = types.stream()
                        .map(type -> type.getMaLoaiGiay() + " - " + type.getTenLoaiGiay())
                        .toArray(String[]::new);
                String[] brandOptions = brands.stream()
                        .map(brand -> brand.getMaThuongHieu() + " - " + brand.getTenThuongHieu())
                        .toArray(String[]::new);
                String selectedType = item == null ? typeOptions[0]
                        : findOption(typeOptions, item.getMaLoaiGiay());
                String selectedBrand = item == null ? brandOptions[0]
                        : findOption(brandOptions, item.getMaThuongHieu());
                if (selectedType == null || selectedBrand == null) {
                    showInvalid(parent, "Loại giày hoặc thương hiệu của sản phẩm không còn tồn tại.");
                    return null;
                }
                String[] values = item == null
                        ? new String[] { bus.generateMaSanPham(), "", selectedType, selectedBrand,
                                "0", "0", "DANG_BAN" }
                        : new String[] { item.getMaSanPham(), item.getTenSanPham(), selectedType,
                                selectedBrand, Double.toString(item.getGiaBan()),
                                Integer.toString(item.getSoLuongTon()), item.getTrangThai() };
                String[] input = new SanPhamDialog(values, item != null, typeOptions, brandOptions)
                        .show(parent, item == null ? "Thêm sản phẩm" : "Cập nhật sản phẩm");
                if (input == null)
                    return null;
                try {
                    return new SanPham(input[0], input[1], optionCode(input[2]), optionCode(input[3]),
                            Double.parseDouble(input[4]), Integer.parseInt(input[5]), input[6]);
                } catch (NumberFormatException ex) {
                    showInvalid(parent, "Giá bán và số lượng phải là số hợp lệ.");
                    return null;
                }
            }

            @Override
            public boolean save(Object value, boolean insert) {
                return insert ? bus.insert((SanPham) value) : bus.update((SanPham) value);
            }

            @Override
            public boolean delete(Object value) {
                return bus.delete(((SanPham) value).getMaSanPham());
            }
        };
        modules.get("Khách hàng").editor = new RecordEditor() {
            private final KhachHangBUS bus = new KhachHangBUS();

            @Override
            public Object show(Component parent, Object record) {
                KhachHang item = (KhachHang) record;
                String[] values = item == null
                        ? new String[] { bus.generateMaKhachHang(), "", "", "", "", "", "0", "HOAT_DONG" }
                        : new String[] { item.getMaKhachHang(), item.getHo(), item.getTen(), item.getSoDienThoai(),
                                item.getDiaChi(), item.getEmail(), Integer.toString(item.getDiemTichLuy()),
                                item.getTrangThai() };
                String[] input = new KhachHangDialog(values, item != null)
                        .show(parent, item == null ? "Thêm khách hàng" : "Cập nhật khách hàng");
                if (input == null)
                    return null;
                try {
                    return new KhachHang(input[0], input[1], input[2], input[3], input[4], input[5],
                            Integer.parseInt(input[6]), input[7]);
                } catch (NumberFormatException ex) {
                    showInvalid(parent, "Điểm tích lũy phải là số nguyên.");
                    return null;
                }
            }

            @Override
            public boolean save(Object value, boolean insert) {
                return insert ? bus.insert((KhachHang) value) : bus.update((KhachHang) value);
            }

            @Override
            public boolean delete(Object value) {
                return bus.delete(((KhachHang) value).getMaKhachHang());
            }

            @Override
            public String viewLabel() {
                return "Lịch sử điểm";
            }

            @Override
            public Object[][] loadViewRows(Object record) {
                ArrayList<LichSuDiem> entries = new DiemTichLuyBUS().getLichSu(((KhachHang) record).getMaKhachHang());
                Object[][] rows = new Object[entries.size()][];
                for (int i = 0; i < entries.size(); i++) {
                    LichSuDiem entry = entries.get(i);
                    boolean debit = "TRU".equals(entry.getLoai()) || "THU_HOI".equals(entry.getLoai());
                    rows[i] = new Object[] { entry.getMaLichSu(), entry.getThoiGian().format(
                            java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
                            entry.getMaPhieuXuat(),
                            loyaltyTypeName(entry.getLoai()), (debit ? "-" : "+") + entry.getSoDiem(),
                            entry.getDiemSau() };
                }
                return rows;
            }

            @Override
            public void showView(Component parent, Object record, Object[][] rows) {
                KhachHang customer = (KhachHang) record;
                PhieuChiTietDialog.show(parent, "Lịch sử điểm - " + customer.getMaKhachHang(),
                        new String[][] {
                                { "Khách hàng", customer.getHo() + " " + customer.getTen() },
                                { "Điểm hiện có", Integer.toString(customer.getDiemTichLuy()) },
                                { "Quy đổi", "10.000 VNĐ thực trả = 1 điểm; 1 điểm dùng = 1.000 VNĐ" }
                        }, new String[] { "Mã", "Thời gian", "Phiếu xuất", "Loại", "Biến động", "Điểm còn" }, rows);
            }
        };
        modules.get("Nhà cung cấp").editor = new RecordEditor() {
            private final NhaCungCapBUS bus = new NhaCungCapBUS();

            @Override
            public Object show(Component parent, Object record) {
                NhaCungCap item = (NhaCungCap) record;
                String[] values = item == null
                        ? new String[] { bus.generateMaNCC(), "", "", "", "", "HOAT_DONG" }
                        : new String[] { item.getMaNCC(), item.getTenNCC(), item.getSoDienThoai(),
                                item.getDiaChi(), item.getEmail(), item.getTrangThai() };
                String[] input = new NhaCungCapDialog(values, item != null)
                        .show(parent, item == null ? "Thêm nhà cung cấp" : "Cập nhật nhà cung cấp");
                return input == null ? null
                        : new NhaCungCap(input[0], input[1], input[2], input[3], input[4], input[5]);
            }

            @Override
            public boolean save(Object value, boolean insert) {
                return insert ? bus.insert((NhaCungCap) value) : bus.update((NhaCungCap) value);
            }

            @Override
            public boolean delete(Object value) {
                return bus.delete(((NhaCungCap) value).getMaNCC());
            }
        };
        modules.get("Nhân viên").editor = new RecordEditor() {
            private final NhanVienBUS bus = new NhanVienBUS();

            @Override
            public Object show(Component parent, Object record) {
                NhanVien item = (NhanVien) record;
                String[] values = item == null
                        ? new String[] { bus.generateMaNhanVien(), "", "", "", "", "", "", "HOAT_DONG" }
                        : new String[] { item.getMaNhanVien(), item.getHo(), item.getTen(), item.getSoDienThoai(),
                                item.getDiaChi(), item.getEmail(), item.getMaTaiKhoan(), item.getTrangThai() };
                String[] input = new NhanVienDialog(values, item != null)
                        .show(parent, item == null ? "Thêm nhân viên" : "Cập nhật nhân viên");
                return input == null ? null
                        : new NhanVien(input[0], input[1], input[2], input[3],
                                input[4], input[5], input[6], input[7]);
            }

            @Override
            public boolean save(Object value, boolean insert) {
                return insert ? bus.insert((NhanVien) value) : bus.update((NhanVien) value);
            }

            @Override
            public boolean delete(Object value) {
                return bus.delete(((NhanVien) value).getMaNhanVien());
            }
        };
        modules.get("Tài khoản").editor = new RecordEditor() {
            private final TaiKhoanBUS bus = new TaiKhoanBUS();

            @Override
            public Object show(Component parent, Object record) {
                TaiKhoan item = (TaiKhoan) record;
                String[] values = item == null
                        ? new String[] { bus.generateMaTaiKhoan(), "", "", "", "HOAT_DONG" }
                        : new String[] { item.getMaTaiKhoan(), item.getTenDangNhap(), item.getMatKhau(),
                                item.getMaNhomQuyen(), item.getTrangThai() };
                String[] input = new TaiKhoanDialog(values, item != null)
                        .show(parent, item == null ? "Thêm tài khoản" : "Cập nhật tài khoản");
                return input == null ? null : new TaiKhoan(input[0], input[1], input[2], input[3], input[4]);
            }

            @Override
            public boolean save(Object value, boolean insert) {
                return insert ? bus.insert((TaiKhoan) value) : bus.update((TaiKhoan) value);
            }

            @Override
            public boolean delete(Object value) {
                return bus.delete(((TaiKhoan) value).getMaTaiKhoan());
            }
        };
        modules.get("Phân quyền").editor = new RecordEditor() {
            private final NhomQuyenBUS bus = new NhomQuyenBUS();

            @Override
            public Object show(Component parent, Object record) {
                NhomQuyen item = (NhomQuyen) record;
                String[] values = item == null ? new String[] { bus.generateMaNhomQuyen(), "" }
                        : new String[] { item.getMaNhomQuyen(), item.getTenNhomQuyen() };
                String[] input = new NhomQuyenDialog(values, item != null)
                        .show(parent, item == null ? "Thêm nhóm quyền" : "Cập nhật nhóm quyền");
                return input == null ? null : new NhomQuyen(input[0], input[1]);
            }

            @Override
            public boolean save(Object value, boolean insert) {
                return insert ? bus.insert((NhomQuyen) value) : bus.update((NhomQuyen) value);
            }

            @Override
            public boolean delete(Object value) {
                return bus.delete(((NhomQuyen) value).getMaNhomQuyen());
            }
        };
        modules.get("Khuyến mãi").editor = new RecordEditor() {
            private final KhuyenMaiBUS bus = new KhuyenMaiBUS();

            @Override
            public Object show(Component parent, Object record) {
                KhuyenMai item = (KhuyenMai) record;
                String[] values = item == null
                        ? new String[] { bus.generateMaKhuyenMai(), "", "0", "GIAM_TI_LE", "10",
                                java.time.LocalDate.now().toString(), java.time.LocalDate.now().plusDays(7).toString(),
                                "AP_DUNG" }
                        : new String[] { item.getMaKhuyenMai(), item.getTenKhuyenMai(),
                                Double.toString(item.getDonHangToiThieu()), item.getLoaiKhuyenMai(),
                                Double.toString(item.getMucGiam()), item.getNgayBatDau().toString(),
                                item.getNgayKetThuc().toString(), item.getTrangThai() };
                String[] input = new KhuyenMaiDialog(values, item != null)
                        .show(parent, item == null ? "Thêm khuyến mãi" : "Cập nhật khuyến mãi");
                if (input == null)
                    return null;
                try {
                    return new KhuyenMai(input[0], input[1], Double.parseDouble(input[2]), input[3],
                            Double.parseDouble(input[4]), java.time.LocalDate.parse(input[5]),
                            java.time.LocalDate.parse(input[6]), input[7]);
                } catch (NumberFormatException | java.time.format.DateTimeParseException ex) {
                    showInvalid(parent, "Vui lòng nhập giá trị số và ngày theo định dạng YYYY-MM-DD.");
                    return null;
                }
            }

            @Override
            public boolean save(Object value, boolean insert) {
                return insert ? bus.insert((KhuyenMai) value) : bus.update((KhuyenMai) value);
            }

            @Override
            public boolean delete(Object value) {
                return bus.delete(((KhuyenMai) value).getMaKhuyenMai());
            }
        };
    }

    private static void showInvalid(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Dữ liệu không hợp lệ", JOptionPane.WARNING_MESSAGE);
    }

    private static String loyaltyTypeName(String type) {
        switch (type) {
            case "KHOI_TAO":
                return "Số dư đầu kỳ";
            case "CONG":
                return "Cộng điểm";
            case "TRU":
                return "Sử dụng điểm";
            case "HOAN":
                return "Hoàn điểm đã dùng";
            case "THU_HOI":
                return "Thu hồi điểm thưởng";
            default:
                return type;
        }
    }

    private static String findOption(String[] options, String code) {
        for (String option : options) {
            if (code.equals(optionCode(option))) {
                return option;
            }
        }
        return null;
    }

    private static String optionCode(String option) {
        int separator = option.indexOf(" - ");
        return separator < 0 ? option : option.substring(0, separator);
    }

    private static final class AttributeItem {
        final String group;
        final String code;
        final String name;

        AttributeItem(String group, String code, String name) {
            this.group = group;
            this.code = code;
            this.name = name;
        }
    }

}
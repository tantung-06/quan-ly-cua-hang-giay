CREATE DATABASE IF NOT EXISTS QuanLyCuaHangGiay
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE QuanLyCuaHangGiay;

CREATE TABLE NhomQuyen (
    maNhomQuyen VARCHAR(20) NOT NULL,
    tenNhomQuyen VARCHAR(100) NOT NULL,
    quyenHan TEXT NULL,
    CONSTRAINT PK_NhomQuyen PRIMARY KEY (maNhomQuyen)
) ENGINE=InnoDB;

CREATE TABLE ThuongHieu (
    maThuongHieu VARCHAR(20) NOT NULL,
    tenThuongHieu VARCHAR(100) NOT NULL,
    CONSTRAINT PK_ThuongHieu PRIMARY KEY (maThuongHieu)
) ENGINE=InnoDB;

CREATE TABLE LoaiGiay (
    maLoaiGiay VARCHAR(20) NOT NULL,
    tenLoaiGiay VARCHAR(100) NOT NULL,
    CONSTRAINT PK_LoaiGiay PRIMARY KEY (maLoaiGiay)
) ENGINE=InnoDB;

CREATE TABLE KhachHang (
    maKhachHang VARCHAR(20) NOT NULL,
    ho VARCHAR(50) NOT NULL,
    ten VARCHAR(50) NOT NULL,
    soDienThoai VARCHAR(15) NOT NULL,
    diaChi VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL,
    diemTichLuy INT NOT NULL DEFAULT 0,
    trangThai VARCHAR(20) NOT NULL DEFAULT 'HOAT_DONG' CHECK (trangThai IN ('HOAT_DONG', 'NGUNG')),
    CONSTRAINT PK_KhachHang PRIMARY KEY (maKhachHang),
    CONSTRAINT CK_KhachHang_DiemTichLuy CHECK (diemTichLuy >= 0)
) ENGINE=InnoDB;

CREATE TABLE NhaCungCap (
    maNCC VARCHAR(20) NOT NULL,
    tenNCC VARCHAR(100) NOT NULL,
    soDienThoai VARCHAR(15) NOT NULL,
    diaChi VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL,
    trangThai VARCHAR(20) NOT NULL DEFAULT 'HOAT_DONG' CHECK (trangThai IN ('HOAT_DONG', 'NGUNG')),
    CONSTRAINT PK_NhaCungCap PRIMARY KEY (maNCC)
) ENGINE=InnoDB;

CREATE TABLE KhuyenMai (
    maKhuyenMai VARCHAR(20) NOT NULL,
    tenKhuyenMai VARCHAR(100) NOT NULL,
    donHangToiThieu DECIMAL(14,2) NOT NULL,
    loaiKhuyenMai VARCHAR(20) NOT NULL DEFAULT 'GIAM_TI_LE'
        CHECK (loaiKhuyenMai IN ('GIAM_TI_LE', 'GIAM_TIEN')),
    mucGiam DECIMAL(14,2) NOT NULL,
    ngayBatDau DATE NOT NULL,
    ngayKetThuc DATE NOT NULL,
    trangThai VARCHAR(20) NOT NULL CHECK (trangThai IN ('AP_DUNG', 'HET_HAN')),
    CONSTRAINT PK_KhuyenMai PRIMARY KEY (maKhuyenMai),
    CONSTRAINT CK_KhuyenMai_Ngay CHECK (ngayKetThuc >= ngayBatDau),
    CONSTRAINT CK_KhuyenMai_MucGiam CHECK (
        (loaiKhuyenMai = 'GIAM_TI_LE' AND mucGiam BETWEEN 0 AND 100)
        OR (loaiKhuyenMai = 'GIAM_TIEN' AND mucGiam >= 0)
    )
) ENGINE=InnoDB;

CREATE TABLE TaiKhoan (
    maTaiKhoan VARCHAR(20) NOT NULL,
    tenDangNhap VARCHAR(50) NOT NULL UNIQUE,
    matKhau VARCHAR(255) NOT NULL,
    maNhomQuyen VARCHAR(20) NOT NULL,
    trangThai VARCHAR(20) NOT NULL CHECK (trangThai IN ('HOAT_DONG', 'NGUNG')),
    CONSTRAINT PK_TaiKhoan PRIMARY KEY (maTaiKhoan),
    CONSTRAINT FK_TaiKhoan_NhomQuyen FOREIGN KEY (maNhomQuyen)
        REFERENCES NhomQuyen(maNhomQuyen) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB;

CREATE TABLE NhanVien (
    maNhanVien VARCHAR(20) NOT NULL,
    ho VARCHAR(50) NOT NULL,
    ten VARCHAR(50) NOT NULL,
    soDienThoai VARCHAR(15) NOT NULL,
    diaChi VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL,
    maTaiKhoan VARCHAR(20) NOT NULL UNIQUE,
    trangThai VARCHAR(20) NOT NULL DEFAULT 'HOAT_DONG' CHECK (trangThai IN ('HOAT_DONG', 'NGUNG')),
    CONSTRAINT PK_NhanVien PRIMARY KEY (maNhanVien),
    CONSTRAINT FK_NhanVien_TaiKhoan FOREIGN KEY (maTaiKhoan)
        REFERENCES TaiKhoan(maTaiKhoan) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB;

CREATE TABLE SanPham (
    maSanPham VARCHAR(20) NOT NULL,
    tenSanPham VARCHAR(150) NOT NULL,
    maLoaiGiay VARCHAR(20) NOT NULL,
    maThuongHieu VARCHAR(20) NOT NULL,
    giaBan DECIMAL(14,2) NOT NULL,
    soLuongTon INT NOT NULL DEFAULT 0,
    trangThai VARCHAR(20) NOT NULL DEFAULT 'DANG_BAN',
    CONSTRAINT PK_SanPham PRIMARY KEY (maSanPham),
    CONSTRAINT FK_SanPham_ThuongHieu FOREIGN KEY (maThuongHieu)
        REFERENCES ThuongHieu(maThuongHieu) ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT FK_SanPham_LoaiGiay FOREIGN KEY (maLoaiGiay)
        REFERENCES LoaiGiay(maLoaiGiay) ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT CK_SanPham_GiaBan CHECK (giaBan >= 0),
    CONSTRAINT CK_SanPham_SoLuongTon CHECK (soLuongTon >= 0),
    CONSTRAINT CK_SanPham_TrangThai CHECK (trangThai IN ('DANG_BAN', 'NGUNG_BAN'))
) ENGINE=InnoDB;

CREATE TABLE PhieuNhap (
    maPhieuNhap VARCHAR(20) NOT NULL,
    ngayNhap DATE NOT NULL,
    maNhanVien VARCHAR(20) NOT NULL,
    maNCC VARCHAR(20) NOT NULL,
    tongTien DECIMAL(14,2) NOT NULL DEFAULT 0,
    trangThai VARCHAR(20) NOT NULL DEFAULT 'CHO_XU_LY'
        CHECK (trangThai IN ('CHO_XU_LY', 'DA_NHAP', 'HUY')),
    CONSTRAINT PK_PhieuNhap PRIMARY KEY (maPhieuNhap),
    CONSTRAINT FK_PhieuNhap_NhanVien FOREIGN KEY (maNhanVien)
        REFERENCES NhanVien(maNhanVien) ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT FK_PhieuNhap_NhaCungCap FOREIGN KEY (maNCC)
        REFERENCES NhaCungCap(maNCC) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB;

CREATE TABLE ChiTietPhieuNhap (
    maPhieuNhap VARCHAR(20) NOT NULL,
    maSanPham VARCHAR(20) NOT NULL,
    soLuong INT NOT NULL,
    giaNhap DECIMAL(14,2) NOT NULL,
    thanhTien DECIMAL(14,2) NOT NULL,
    CONSTRAINT PK_ChiTietPhieuNhap PRIMARY KEY (maPhieuNhap, maSanPham),
    CONSTRAINT FK_CTPN_PhieuNhap FOREIGN KEY (maPhieuNhap)
        REFERENCES PhieuNhap(maPhieuNhap) ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT FK_CTPN_SanPham FOREIGN KEY (maSanPham)
        REFERENCES SanPham(maSanPham) ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT CK_CTPN_SoLuong CHECK (soLuong > 0),
    CONSTRAINT CK_CTPN_GiaNhap CHECK (giaNhap >= 0),
    CONSTRAINT CK_CTPN_ThanhTien CHECK (thanhTien >= 0)
) ENGINE=InnoDB;

CREATE TABLE HoaDon (
    maHoaDon VARCHAR(20) NOT NULL,
    ngayLap DATE NOT NULL,
    maNhanVien VARCHAR(20) NOT NULL,
    maKhachHang VARCHAR(20) NULL,
    maKhuyenMai VARCHAR(20) NULL,
    phuongThucThanhToan VARCHAR(20) NOT NULL DEFAULT 'TIEN_MAT'
        CHECK (phuongThucThanhToan IN ('TIEN_MAT', 'CHUYEN_KHOAN', 'THE')),
    tongTien DECIMAL(14,2) NOT NULL DEFAULT 0,
    diemSuDung INT NOT NULL DEFAULT 0,
    diemDuocCong INT NOT NULL DEFAULT 0,
    diemDaXuLy BOOLEAN NOT NULL DEFAULT FALSE,
    trangThai VARCHAR(20) NOT NULL DEFAULT 'CHO_XU_LY'
        CHECK (trangThai IN ('CHO_XU_LY', 'DA_XUAT', 'HUY')),
    CONSTRAINT PK_HoaDon PRIMARY KEY (maHoaDon),
    CONSTRAINT CK_HoaDon_Diem CHECK (diemSuDung >= 0 AND diemDuocCong >= 0
        AND (maKhachHang IS NOT NULL OR (diemSuDung = 0 AND diemDuocCong = 0))),
    CONSTRAINT CK_HoaDon_ThucTra CHECK (tongTien >= 0),
    CONSTRAINT FK_HoaDon_NhanVien FOREIGN KEY (maNhanVien)
        REFERENCES NhanVien(maNhanVien) ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT FK_HoaDon_KhachHang FOREIGN KEY (maKhachHang)
        REFERENCES KhachHang(maKhachHang) ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT FK_HoaDon_KhuyenMai FOREIGN KEY (maKhuyenMai)
        REFERENCES KhuyenMai(maKhuyenMai) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB;

CREATE TABLE ChiTietHoaDon (
    maHoaDon VARCHAR(20) NOT NULL,
    maSanPham VARCHAR(20) NOT NULL,
    soLuong INT NOT NULL,
    donGia DECIMAL(12,2) NOT NULL,
    thanhTien DECIMAL(14,2) NOT NULL,
    CONSTRAINT PK_ChiTietHoaDon PRIMARY KEY (maHoaDon, maSanPham),
    CONSTRAINT FK_CTHD_HoaDon FOREIGN KEY (maHoaDon)
        REFERENCES HoaDon(maHoaDon) ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT FK_CTHD_SanPham FOREIGN KEY (maSanPham)
        REFERENCES SanPham(maSanPham) ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT CK_CTHD_SoLuong CHECK (soLuong > 0),
    CONSTRAINT CK_CTHD_DonGia CHECK (donGia >= 0),
    CONSTRAINT CK_CTHD_ThanhTien CHECK (thanhTien >= 0)
) ENGINE=InnoDB;

CREATE TABLE LichSuDiem (
    maLichSu BIGINT NOT NULL AUTO_INCREMENT,
    maKhachHang VARCHAR(20) NOT NULL,
    maHoaDon VARCHAR(20) NULL,
    loai VARCHAR(20) NOT NULL,
    soDiem INT NOT NULL,
    diemSau INT NOT NULL,
    thoiGian TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    CONSTRAINT PK_LichSuDiem PRIMARY KEY (maLichSu),
    CONSTRAINT UQ_LichSuDiem_PhieuLoai UNIQUE (maHoaDon, loai),
    CONSTRAINT FK_LichSuDiem_KhachHang FOREIGN KEY (maKhachHang) REFERENCES KhachHang(maKhachHang),
    CONSTRAINT FK_LichSuDiem_HoaDon FOREIGN KEY (maHoaDon) REFERENCES HoaDon(maHoaDon),
    CONSTRAINT CK_LichSuDiem_Loai CHECK (loai IN ('KHOI_TAO','CONG','TRU','HOAN','THU_HOI')),
    CONSTRAINT CK_LichSuDiem_SoDiem CHECK (soDiem >= 0 AND diemSau >= 0),
    CONSTRAINT CK_LichSuDiem_Phieu CHECK ((loai = 'KHOI_TAO' AND maHoaDon IS NULL)
        OR (loai <> 'KHOI_TAO' AND maHoaDon IS NOT NULL)),
    INDEX IDX_LichSuDiem_KhachHang (maKhachHang, maLichSu)
) ENGINE=InnoDB;

CREATE INDEX IDX_SanPham_TrangThai ON SanPham(trangThai);
CREATE INDEX IDX_HoaDon_NgayLap ON HoaDon(ngayLap);
CREATE INDEX IDX_PhieuNhap_NgayNhap ON PhieuNhap(ngayNhap);
CREATE INDEX IDX_KhachHang_SDT ON KhachHang(soDienThoai);
CREATE INDEX IDX_HoaDon_KhachHang ON HoaDon(maKhachHang);

-- 1. NHÓM QUYỀN
INSERT INTO NhomQuyen (maNhomQuyen, tenNhomQuyen) VALUES
('NQ001', 'Quản trị viên'),
('NQ002', 'Nhân viên kho'),
('NQ003', 'Nhân viên bán hàng');

-- 2. THƯƠNG HIỆU
INSERT INTO ThuongHieu (maThuongHieu, tenThuongHieu) VALUES
('TH001', 'Nike'),
('TH002', 'Adidas'),
('TH003', 'Converse'),
('TH004', 'Vans'),
('TH005', 'Puma');

-- 3. LOẠI GIÀY
INSERT INTO LoaiGiay (maLoaiGiay, tenLoaiGiay) VALUES
('LG001', 'Giày thể thao'),
('LG002', 'Giày chạy bộ'),
('LG003', 'Giày sneaker'),
('LG004', 'Giày bóng rổ'),
('LG005', 'Giày thời trang');

-- 4. KHÁCH HÀNG
INSERT INTO KhachHang
(maKhachHang, ho, ten, soDienThoai, diaChi, email, diemTichLuy, trangThai)
VALUES
('KH001', 'Nguyễn Văn', 'An', '0901000001', 'Phường Bến Thành, TP. Hồ Chí Minh', 'an.nguyen@example.com', 120, 'HOAT_DONG'),
('KH002', 'Trần Thị', 'Bình', '0901000002', 'Phường Bàn Cờ, TP. Hồ Chí Minh', 'binh.tran@example.com', 250, 'HOAT_DONG'),
('KH003', 'Lê Minh', 'Cường', '0901000003', 'Phường Thủ Đức, TP. Hồ Chí Minh', 'cuong.le@example.com', 80, 'HOAT_DONG'),
('KH004', 'Phạm Thị', 'Dung', '0901000004', 'Phường Tân Hưng, TP. Hồ Chí Minh', 'dung.pham@example.com', 500, 'HOAT_DONG'),
('KH005', 'Hoàng Văn', 'Đức', '0901000005', 'Phường Gò Vấp, TP. Hồ Chí Minh', 'duc.hoang@example.com', 0, 'HOAT_DONG'),
('KH006', 'Võ Thanh', 'Hà', '0901000006', 'Phường Bình Thạnh, TP. Hồ Chí Minh', 'ha.vo@example.com', 60, 'HOAT_DONG'),
('KH007', 'Đỗ Quang', 'Huy', '0901000007', 'Phường Tân Bình, TP. Hồ Chí Minh', 'huy.do@example.com', 180, 'HOAT_DONG'),
('KH008', 'Bùi Ngọc', 'Lan', '0901000008', 'Phường Diên Hồng, TP. Hồ Chí Minh', 'lan.bui@example.com', 30, 'NGUNG');

-- 5. NHÀ CUNG CẤP
INSERT INTO NhaCungCap
(maNCC, tenNCC, soDienThoai, diaChi, email, trangThai)
VALUES
('NCC001', 'Công ty TNHH Phân phối Giày Việt', '0283000001', 'Phường Bến Thành, TP. Hồ Chí Minh', 'giayviet@example.com', 'HOAT_DONG'),
('NCC002', 'Công ty TNHH Thể thao Thành Công', '0283000002', 'Phường Tân Hưng, TP. Hồ Chí Minh', 'thethao@example.com', 'HOAT_DONG'),
('NCC003', 'Nhà phân phối Sneaker Sài Gòn', '0283000003', 'Phường Bình Thạnh, TP. Hồ Chí Minh', 'sneaker@example.com', 'HOAT_DONG'),
('NCC004', 'Công ty TNHH Thương mại Minh Phát', '0283000004', 'Phường Thủ Đức, TP. Hồ Chí Minh', 'minhphat@example.com', 'NGUNG');

-- 6. KHUYẾN MÃI
INSERT INTO KhuyenMai
(maKhuyenMai, tenKhuyenMai, donHangToiThieu, loaiKhuyenMai, mucGiam, ngayBatDau, ngayKetThuc, trangThai)
VALUES
('KM001', 'Giảm 10% cho đơn hàng từ 500.000đ', 500000, 'GIAM_TI_LE', 10, '2026-01-01', '2027-12-31', 'AP_DUNG'),
('KM002', 'Giảm 50.000đ cho đơn hàng từ 500.000đ', 500000, 'GIAM_TIEN', 50000, '2026-01-01', '2027-12-31', 'AP_DUNG'),
('KM003', 'Ưu đãi 15% cho đơn hàng từ 1.000.000đ', 1000000, 'GIAM_TI_LE', 15, '2026-01-01', '2026-12-31', 'AP_DUNG'),
('KM004', 'Khuyến mãi hè - Giảm 30.000đ', 300000, 'GIAM_TIEN', 30000, '2025-06-01', '2025-08-31', 'HET_HAN');

-- 7. TÀI KHOẢN
INSERT INTO TaiKhoan
(maTaiKhoan, tenDangNhap, matKhau, maNhomQuyen, trangThai)
VALUES
('TK001', 'admin', '123', 'NQ001', 'HOAT_DONG'),
('TK002', 'kho01', '123', 'NQ002', 'HOAT_DONG'),
('TK003', 'banhang01', '123', 'NQ003', 'HOAT_DONG'),
('TK004', 'banhang02', '123', 'NQ003', 'HOAT_DONG'),
('TK005', 'banhang03', '123', 'NQ003', 'NGUNG');

-- 8. NHÂN VIÊN
INSERT INTO NhanVien
(maNhanVien, ho, ten, soDienThoai, diaChi, email, maTaiKhoan, trangThai)
VALUES
('NV001', 'Nguyễn Văn', 'Nam', '0912000001', 'Phường Bến Thành, TP. Hồ Chí Minh', 'nam.nv@example.com', 'TK001', 'HOAT_DONG'),
('NV002', 'Trần Minh', 'Khoa', '0912000002', 'Phường Bàn Cờ, TP. Hồ Chí Minh', 'khoa.nv@example.com', 'TK002', 'HOAT_DONG'),
('NV003', 'Lê Thị', 'Mai', '0912000003', 'Phường Tân Hưng, TP. Hồ Chí Minh', 'mai.nv@example.com', 'TK003', 'HOAT_DONG'),
('NV004', 'Phạm Văn', 'Long', '0912000004', 'Phường Thủ Đức, TP. Hồ Chí Minh', 'long.nv@example.com', 'TK004', 'HOAT_DONG'),
('NV005', 'Võ Thị', 'Linh', '0912000005', 'Phường Gò Vấp, TP. Hồ Chí Minh', 'linh.nv@example.com', 'TK005', 'NGUNG');

-- 9. SẢN PHẨM
INSERT INTO SanPham
(maSanPham, tenSanPham, maLoaiGiay, maThuongHieu, giaBan, soLuongTon, trangThai)
VALUES
('SP001', 'Nike Air Force 1 ''07', 'LG003', 'TH001', 2500000, 30, 'DANG_BAN'),
('SP002', 'Nike Air Max 90', 'LG001', 'TH001', 3200000, 20, 'DANG_BAN'),
('SP003', 'Adidas Ultraboost 5', 'LG002', 'TH002', 3500000, 15, 'DANG_BAN'),
('SP004', 'Adidas Superstar', 'LG003', 'TH002', 2200000, 25, 'DANG_BAN'),
('SP005', 'Converse Chuck Taylor All Star', 'LG005', 'TH003', 1500000, 40, 'DANG_BAN'),
('SP006', 'Vans Old Skool', 'LG005', 'TH004', 1800000, 35, 'DANG_BAN'),
('SP007', 'Puma RS-X', 'LG001', 'TH005', 2800000, 18, 'DANG_BAN'),
('SP008', 'Nike LeBron Witness 8', 'LG004', 'TH001', 2900000, 12, 'DANG_BAN'),
('SP009', 'Adidas Runfalcon 3.0', 'LG002', 'TH002', 1200000, 0, 'NGUNG_BAN'),
('SP010', 'Vans Classic Slip-On', 'LG005', 'TH004', 1600000, 22, 'DANG_BAN');

-- 10. PHIẾU NHẬP
INSERT INTO PhieuNhap
(maPhieuNhap, ngayNhap, maNhanVien, maNCC, tongTien, trangThai)
VALUES
('PN001', '2026-10-01', 'NV002', 'NCC001', 16600000, 'DA_NHAP'),
('PN002', '2026-10-05', 'NV002', 'NCC002', 17500000, 'DA_NHAP'),
('PN003', '2026-10-10', 'NV002', 'NCC003', 9000000, 'DA_NHAP'),
('PN004', '2026-10-20', 'NV002', 'NCC001', 5000000, 'CHO_XU_LY'),
('PN005', '2026-10-21', 'NV002', 'NCC004', 3000000, 'HUY');

-- 11. CHI TIẾT PHIẾU NHẬP
INSERT INTO ChiTietPhieuNhap
(maPhieuNhap, maSanPham, soLuong, giaNhap, thanhTien)
VALUES
('PN001', 'SP001', 10, 1200000, 12000000),
('PN001', 'SP005', 4, 1150000, 4600000),
('PN002', 'SP002', 5, 2500000, 12500000),
('PN002', 'SP003', 2, 2500000, 5000000),
('PN003', 'SP006', 5, 1200000, 6000000),
('PN003', 'SP007', 2, 1500000, 3000000),
('PN004', 'SP004', 2, 1500000, 3000000),
('PN004', 'SP010', 2, 1000000, 2000000),
('PN005', 'SP008', 2, 1500000, 3000000);

-- 12. HÓA ĐƠN
INSERT INTO HoaDon
(maHoaDon, ngayLap, maNhanVien, maKhachHang, maKhuyenMai, phuongThucThanhToan, tongTien, trangThai)
VALUES
('HD001', '2026-10-12', 'NV003', 'KH001', 'KM001', 'TIEN_MAT', 2250000, 'DA_XUAT'),
('HD002', '2026-10-13', 'NV003', 'KH002', 'KM002', 'CHUYEN_KHOAN', 4650000, 'DA_XUAT'),
('HD003', '2026-10-15', 'NV004', 'KH004', 'KM003', 'THE', 2975000, 'DA_XUAT'),
('HD004', '2026-10-18', 'NV003', 'KH003', NULL, 'TIEN_MAT', 4000000, 'DA_XUAT'),
('HD005', '2026-10-20', 'NV004', NULL, NULL, 'CHUYEN_KHOAN', 1800000, 'DA_XUAT'),
('HD006', '2026-10-22', 'NV003', 'KH005', 'KM001', 'TIEN_MAT', 2520000, 'CHO_XU_LY'),
('HD007', '2026-10-22', 'NV004', 'KH006', NULL, 'TIEN_MAT', 0, 'HUY');

-- 13. CHI TIẾT HÓA ĐƠN
INSERT INTO ChiTietHoaDon
(maHoaDon, maSanPham, soLuong, donGia, thanhTien)
VALUES
('HD001', 'SP001', 1, 2500000, 2500000),
('HD002', 'SP002', 1, 3200000, 3200000),
('HD002', 'SP005', 1, 1500000, 1500000),
('HD003', 'SP003', 1, 3500000, 3500000),
('HD004', 'SP004', 1, 2200000, 2200000),
('HD004', 'SP006', 1, 1800000, 1800000),
('HD005', 'SP006', 1, 1800000, 1800000),
('HD006', 'SP007', 1, 2800000, 2800000);

INSERT INTO LichSuDiem(maKhachHang,maHoaDon,loai,soDiem,diemSau)
SELECT k.maKhachHang,NULL,'KHOI_TAO',k.diemTichLuy,k.diemTichLuy FROM KhachHang k
WHERE NOT EXISTS (SELECT 1 FROM LichSuDiem h WHERE h.maKhachHang=k.maKhachHang);
CREATE DATABASE IF NOT EXISTS QuanLyCuaHangGiay
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE QuanLyCuaHangGiay;

CREATE TABLE NhomQuyen (
    maNhomQuyen VARCHAR(20) NOT NULL,
    tenNhomQuyen VARCHAR(100) NOT NULL,
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
    CONSTRAINT PK_KhachHang PRIMARY KEY (maKhachHang)
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
    CONSTRAINT CK_CTPN_SoLuong CHECK (soLuong > 0)
    CONSTRAINT CK_CTPN_GiaNhap CHECK (giaNhap >= 0),
    CONSTRAINT CK_CTPN_ThanhTien CHECK (thanhTien >= 0)
) ENGINE=InnoDB;

CREATE TABLE PhieuXuat (
    maPhieuXuat VARCHAR(20) NOT NULL,
    ngayLap DATE NOT NULL,
    maNhanVien VARCHAR(20) NOT NULL,
    maKhachHang VARCHAR(20) NULL,
    maKhuyenMai VARCHAR(20) NULL,
    phuongThucThanhToan VARCHAR(20) NOT NULL DEFAULT 'TIEN_MAT'
        CHECK (phuongThucThanhToan IN ('TIEN_MAT', 'CHUYEN_KHOAN', 'THE')),
    tongTien DECIMAL(14,2) NOT NULL DEFAULT 0,
    trangThai VARCHAR(20) NOT NULL DEFAULT 'CHO_XU_LY'
        CHECK (trangThai IN ('CHO_XU_LY', 'DA_XUAT', 'HUY')),
    CONSTRAINT PK_PhieuXuat PRIMARY KEY (maPhieuXuat),
    CONSTRAINT FK_PhieuXuat_NhanVien FOREIGN KEY (maNhanVien)
        REFERENCES NhanVien(maNhanVien) ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT FK_PhieuXuat_KhachHang FOREIGN KEY (maKhachHang)
        REFERENCES KhachHang(maKhachHang) ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT FK_PhieuXuat_KhuyenMai FOREIGN KEY (maKhuyenMai)
        REFERENCES KhuyenMai(maKhuyenMai) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB;

CREATE TABLE ChiTietPhieuXuat (
    maPhieuXuat VARCHAR(20) NOT NULL,
    maSanPham VARCHAR(20) NOT NULL,
    soLuong INT NOT NULL,
    donGia DECIMAL(12,2) NOT NULL,
    thanhTien DECIMAL(14,2) NOT NULL,
    CONSTRAINT PK_ChiTietPhieuXuat PRIMARY KEY (maPhieuXuat, maSanPham),
    CONSTRAINT FK_CTPX_PhieuXuat FOREIGN KEY (maPhieuXuat)
        REFERENCES PhieuXuat(maPhieuXuat) ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT FK_CTPX_SanPham FOREIGN KEY (maSanPham)
        REFERENCES SanPham(maSanPham) ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT CK_CTPX_SoLuong CHECK (soLuong > 0)
    CONSTRAINT CK_CTPX_DonGia CHECK (donGia >= 0),
    CONSTRAINT CK_CTPX_ThanhTien CHECK (thanhTien >= 0)
) ENGINE=InnoDB;

CREATE INDEX IDX_SanPham_TrangThai ON SanPham(trangThai);
CREATE INDEX IDX_PhieuXuat_NgayLap ON PhieuXuat(ngayLap);
CREATE INDEX IDX_PhieuNhap_NgayNhap ON PhieuNhap(ngayNhap);
CREATE INDEX IDX_KhachHang_SDT ON KhachHang(soDienThoai);
CREATE INDEX IDX_PhieuXuat_KhachHang ON PhieuXuat(maKhachHang);
CREATE INDEX IDX_PhieuXuat_NgayLap ON PhieuXuat(ngayLap);
CREATE INDEX IDX_PhieuNhap_NgayNhap ON PhieuNhap(ngayNhap);
CREATE INDEX IDX_KhachHang_SDT ON KhachHang(soDienThoai);
CREATE INDEX IDX_PhieuXuat_KhachHang ON PhieuXuat(maKhachHang);
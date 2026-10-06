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

CREATE TABLE MauSac (
    maMau VARCHAR(20) NOT NULL,
    tenMau VARCHAR(50) NOT NULL,
    CONSTRAINT PK_MauSac PRIMARY KEY (maMau)
) ENGINE=InnoDB;

CREATE TABLE KichThuoc (
    maKichThuoc VARCHAR(20) NOT NULL,
    tenKichThuoc VARCHAR(20) NOT NULL,
    CONSTRAINT PK_KichThuoc PRIMARY KEY (maKichThuoc)
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
    donHangToiThieu DOUBLE NOT NULL,
    loaiKhuyenMai VARCHAR(20) NOT NULL DEFAULT 'GIAM_TI_LE'
        CHECK (loaiKhuyenMai IN ('GIAM_TI_LE', 'GIAM_TIEN')),
    mucGiam DOUBLE NOT NULL,
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
    maThuongHieu VARCHAR(20) NOT NULL,
    maLoaiGiay VARCHAR(20) NOT NULL,
    moTa TEXT NULL,
    trangThai VARCHAR(20) NOT NULL DEFAULT 'DANG_BAN'
        CHECK (trangThai IN ('DANG_BAN', 'NGUNG_BAN')),
    CONSTRAINT PK_SanPham PRIMARY KEY (maSanPham),
    CONSTRAINT FK_SanPham_ThuongHieu FOREIGN KEY (maThuongHieu)
        REFERENCES ThuongHieu(maThuongHieu) ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT FK_SanPham_LoaiGiay FOREIGN KEY (maLoaiGiay)
        REFERENCES LoaiGiay(maLoaiGiay) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB;

CREATE TABLE BienTheSanPham (
    maBienThe VARCHAR(20) NOT NULL,
    maSanPham VARCHAR(20) NOT NULL,
    maMau VARCHAR(20) NOT NULL,
    maKichThuoc VARCHAR(20) NOT NULL,
    giaBan DOUBLE NOT NULL,
    soLuongTon INT NOT NULL DEFAULT 0,
    hinhAnh VARCHAR(255) NULL,
    trangThai VARCHAR(20) NOT NULL DEFAULT 'CON_HANG'
        CHECK (trangThai IN ('CON_HANG', 'HET_HANG', 'NGUNG_BAN')),
    CONSTRAINT PK_BienTheSanPham PRIMARY KEY (maBienThe),
    CONSTRAINT UQ_BienThe_SP_Mau_Size UNIQUE (maSanPham, maMau, maKichThuoc),
    CONSTRAINT FK_BienThe_SanPham FOREIGN KEY (maSanPham)
        REFERENCES SanPham(maSanPham) ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT FK_BienThe_MauSac FOREIGN KEY (maMau)
        REFERENCES MauSac(maMau) ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT FK_BienThe_KichThuoc FOREIGN KEY (maKichThuoc)
        REFERENCES KichThuoc(maKichThuoc) ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT CK_BienThe_SoLuong CHECK (soLuongTon >= 0),
    CONSTRAINT CK_BienThe_GiaBan CHECK (giaBan >= 0)
) ENGINE=InnoDB;

CREATE TABLE PhieuNhap (
    maPhieuNhap VARCHAR(20) NOT NULL,
    ngayNhap DATE NOT NULL,
    maNhanVien VARCHAR(20) NOT NULL,
    maNCC VARCHAR(20) NOT NULL,
    tongTien DOUBLE NOT NULL DEFAULT 0,
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
    maBienThe VARCHAR(20) NOT NULL,
    soLuong INT NOT NULL,
    giaNhap DOUBLE NOT NULL,
    thanhTien DOUBLE NOT NULL,
    CONSTRAINT PK_ChiTietPhieuNhap PRIMARY KEY (maPhieuNhap, maBienThe),
    CONSTRAINT FK_CTPN_PhieuNhap FOREIGN KEY (maPhieuNhap)
        REFERENCES PhieuNhap(maPhieuNhap) ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT FK_CTPN_BienThe FOREIGN KEY (maBienThe)
        REFERENCES BienTheSanPham(maBienThe) ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT CK_CTPN_SoLuong CHECK (soLuong > 0)
) ENGINE=InnoDB;

CREATE TABLE PhieuXuat (
    maPhieuXuat VARCHAR(20) NOT NULL,
    ngayLap DATE NOT NULL,
    maNhanVien VARCHAR(20) NOT NULL,
    maKhachHang VARCHAR(20) NULL,
    maKhuyenMai VARCHAR(20) NULL,
    phuongThucThanhToan VARCHAR(20) NOT NULL DEFAULT 'TIEN_MAT'
        CHECK (phuongThucThanhToan IN ('TIEN_MAT', 'CHUYEN_KHOAN', 'THE')),
    tongTien DOUBLE NOT NULL DEFAULT 0,
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
    maBienThe VARCHAR(20) NOT NULL,
    soLuong INT NOT NULL,
    donGia DOUBLE NOT NULL,
    thanhTien DOUBLE NOT NULL,
    CONSTRAINT PK_ChiTietPhieuXuat PRIMARY KEY (maPhieuXuat, maBienThe),
    CONSTRAINT FK_CTPX_PhieuXuat FOREIGN KEY (maPhieuXuat)
        REFERENCES PhieuXuat(maPhieuXuat) ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT FK_CTPX_BienThe FOREIGN KEY (maBienThe)
        REFERENCES BienTheSanPham(maBienThe) ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT CK_CTPX_SoLuong CHECK (soLuong > 0)
) ENGINE=InnoDB;

CREATE TABLE PhieuTraHang (
    maPhieuTra VARCHAR(20) NOT NULL,
    maPhieuXuat VARCHAR(20) NOT NULL,
    ngayTra DATE NOT NULL,
    maNhanVien VARCHAR(20) NOT NULL,
    lyDoTra VARCHAR(255) NULL,
    hinhThucXuLy VARCHAR(20) NOT NULL DEFAULT 'HOAN_TIEN'
        CHECK (hinhThucXuLy IN ('HOAN_TIEN', 'DOI_SAN_PHAM')),
    tongTienHoan DOUBLE NOT NULL DEFAULT 0,
    trangThai VARCHAR(20) NOT NULL DEFAULT 'CHO_XU_LY'
        CHECK (trangThai IN ('CHO_XU_LY', 'DA_XU_LY', 'TU_CHOI')),
    CONSTRAINT PK_PhieuTraHang PRIMARY KEY (maPhieuTra),
    CONSTRAINT FK_PhieuTra_PhieuXuat FOREIGN KEY (maPhieuXuat)
        REFERENCES PhieuXuat(maPhieuXuat) ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT FK_PhieuTra_NhanVien FOREIGN KEY (maNhanVien)
        REFERENCES NhanVien(maNhanVien) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB;

CREATE TABLE ChiTietPhieuTraHang (
    maPhieuTra VARCHAR(20) NOT NULL,
    maBienThe VARCHAR(20) NOT NULL,
    soLuongTra INT NOT NULL,
    donGia DOUBLE NOT NULL,
    thanhTien DOUBLE NOT NULL,
    tinhTrangHang VARCHAR(20) NOT NULL DEFAULT 'CON_MOI'
        CHECK (tinhTrangHang IN ('CON_MOI', 'LOI_NHA_SX', 'HU_HONG_DO_KHACH')),
    CONSTRAINT PK_ChiTietPhieuTraHang PRIMARY KEY (maPhieuTra, maBienThe),
    CONSTRAINT FK_CTPT_PhieuTra FOREIGN KEY (maPhieuTra)
        REFERENCES PhieuTraHang(maPhieuTra) ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT FK_CTPT_BienThe FOREIGN KEY (maBienThe)
        REFERENCES BienTheSanPham(maBienThe) ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT CK_CTPT_SoLuong CHECK (soLuongTra > 0)
) ENGINE=InnoDB;

CREATE INDEX IDX_BienThe_SanPham ON BienTheSanPham(maSanPham);
CREATE INDEX IDX_BienThe_TrangThai ON BienTheSanPham(trangThai);
CREATE INDEX IDX_SanPham_TrangThai ON SanPham(trangThai);
CREATE INDEX IDX_PhieuXuat_NgayLap ON PhieuXuat(ngayLap);
CREATE INDEX IDX_PhieuNhap_NgayNhap ON PhieuNhap(ngayNhap);
CREATE INDEX IDX_KhachHang_SDT ON KhachHang(soDienThoai);
CREATE INDEX IDX_PhieuXuat_KhachHang ON PhieuXuat(maKhachHang);
CREATE INDEX IDX_PhieuTra_PhieuXuat ON PhieuTraHang(maPhieuXuat);
CREATE INDEX IDX_PhieuTra_NgayTra ON PhieuTraHang(ngayTra);
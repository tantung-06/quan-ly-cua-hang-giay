package dto;

import java.time.LocalDate;

public class PhieuTraHang {
    private String maPhieuTra;
    private String maPhieuXuat;
    private LocalDate ngayTra;
    private String maNhanVien;
    private String lyDoTra;
    private String hinhThucXuLy;
    private double tongTienHoan;
    private String trangThai;

    public PhieuTraHang() {
    }

    public PhieuTraHang(String maPhieuTra, String maPhieuXuat, LocalDate ngayTra, String maNhanVien, String lyDoTra,
            String hinhThucXuLy, double tongTienHoan, String trangThai) {
        this.maPhieuTra = maPhieuTra;
        this.maPhieuXuat = maPhieuXuat;
        this.ngayTra = ngayTra;
        this.maNhanVien = maNhanVien;
        this.lyDoTra = lyDoTra;
        this.hinhThucXuLy = hinhThucXuLy;
        this.tongTienHoan = tongTienHoan;
        this.trangThai = trangThai;
    }

    public String getMaPhieuTra() {
        return maPhieuTra;
    }

    public void setMaPhieuTra(String maPhieuTra) {
        this.maPhieuTra = maPhieuTra;
    }

    public String getMaPhieuXuat() {
        return maPhieuXuat;
    }

    public void setMaPhieuXuat(String maPhieuXuat) {
        this.maPhieuXuat = maPhieuXuat;
    }

    public LocalDate getNgayTra() {
        return ngayTra;
    }

    public void setNgayTra(LocalDate ngayTra) {
        this.ngayTra = ngayTra;
    }

    public String getMaNhanVien() {
        return maNhanVien;
    }

    public void setMaNhanVien(String maNhanVien) {
        this.maNhanVien = maNhanVien;
    }

    public String getLyDoTra() {
        return lyDoTra;
    }

    public void setLyDoTra(String lyDoTra) {
        this.lyDoTra = lyDoTra;
    }

    public String getHinhThucXuLy() {
        return hinhThucXuLy;
    }

    public void setHinhThucXuLy(String hinhThucXuLy) {
        this.hinhThucXuLy = hinhThucXuLy;
    }

    public double getTongTienHoan() {
        return tongTienHoan;
    }

    public void setTongTienHoan(double tongTienHoan) {
        this.tongTienHoan = tongTienHoan;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }
}
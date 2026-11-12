package dto;

import java.time.LocalDate;

public class PhieuXuat {
    private String maPhieuXuat;
    private LocalDate ngayLap;
    private String maNhanVien;
    private String maKhachHang;
    private String maKhuyenMai;
    private String phuongThucThanhToan;
    private double tongTien;
    private String trangThai;
    private int diemSuDung;
    private int diemDuocCong;
    private boolean diemDaXuLy;

    public PhieuXuat() {
    }

    public PhieuXuat(String maPhieuXuat, LocalDate ngayLap, String maNhanVien, String maKhachHang, String maKhuyenMai,
            String phuongThucThanhToan, double tongTien, String trangThai) {
        this.maPhieuXuat = maPhieuXuat;
        this.ngayLap = ngayLap;
        this.maNhanVien = maNhanVien;
        this.maKhachHang = maKhachHang;
        this.maKhuyenMai = maKhuyenMai;
        this.phuongThucThanhToan = phuongThucThanhToan;
        this.tongTien = tongTien;
        this.trangThai = trangThai;
    }

    public String getMaPhieuXuat() {
        return maPhieuXuat;
    }

    public void setMaPhieuXuat(String maPhieuXuat) {
        this.maPhieuXuat = maPhieuXuat;
    }

    public LocalDate getNgayLap() {
        return ngayLap;
    }

    public void setNgayLap(LocalDate ngayLap) {
        this.ngayLap = ngayLap;
    }

    public String getMaNhanVien() {
        return maNhanVien;
    }

    public void setMaNhanVien(String maNhanVien) {
        this.maNhanVien = maNhanVien;
    }

    public String getMaKhachHang() {
        return maKhachHang;
    }

    public void setMaKhachHang(String maKhachHang) {
        this.maKhachHang = maKhachHang;
    }

    public String getMaKhuyenMai() {
        return maKhuyenMai;
    }

    public void setMaKhuyenMai(String maKhuyenMai) {
        this.maKhuyenMai = maKhuyenMai;
    }

    public String getPhuongThucThanhToan() {
        return phuongThucThanhToan;
    }

    public void setPhuongThucThanhToan(String phuongThucThanhToan) {
        this.phuongThucThanhToan = phuongThucThanhToan;
    }

    public double getTongTien() {
        return tongTien;
    }

    public void setTongTien(double tongTien) {
        this.tongTien = tongTien;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public int getDiemSuDung() {
        return diemSuDung;
    }

    public void setDiemSuDung(int diemSuDung) {
        this.diemSuDung = diemSuDung;
    }

    public int getDiemDuocCong() {
        return diemDuocCong;
    }

    public void setDiemDuocCong(int diemDuocCong) {
        this.diemDuocCong = diemDuocCong;
    }

    public boolean isDiemDaXuLy() {
        return diemDaXuLy;
    }

    public void setDiemDaXuLy(boolean diemDaXuLy) {
        this.diemDaXuLy = diemDaXuLy;
    }
}
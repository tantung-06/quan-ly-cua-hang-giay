package dto;

public class ChiTietPhieuXuat {
    private String maPhieuXuat;
    private String maBienThe;
    private int soLuong;
    private double donGia;
    private double thanhTien;

    public ChiTietPhieuXuat() {
    }

    public ChiTietPhieuXuat(String maPhieuXuat, String maBienThe, int soLuong, double donGia, double thanhTien) {
        this.maPhieuXuat = maPhieuXuat;
        this.maBienThe = maBienThe;
        this.soLuong = soLuong;
        this.donGia = donGia;
        this.thanhTien = thanhTien;
    }

    public String getMaPhieuXuat() {
        return maPhieuXuat;
    }

    public void setMaPhieuXuat(String maPhieuXuat) {
        this.maPhieuXuat = maPhieuXuat;
    }

    public String getMaBienThe() {
        return maBienThe;
    }

    public void setMaBienThe(String maBienThe) {
        this.maBienThe = maBienThe;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public double getDonGia() {
        return donGia;
    }

    public void setDonGia(double donGia) {
        this.donGia = donGia;
    }

    public double getThanhTien() {
        return thanhTien;
    }

    public void setThanhTien(double thanhTien) {
        this.thanhTien = thanhTien;
    }
}
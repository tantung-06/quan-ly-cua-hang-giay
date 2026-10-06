package dto;

public class ChiTietPhieuTraHang {
    private String maPhieuTra;
    private String maBienThe;
    private int soLuongTra;
    private double donGia;
    private double thanhTien;
    private String tinhTrangHang;

    public ChiTietPhieuTraHang() {
    }

    public ChiTietPhieuTraHang(String maPhieuTra, String maBienThe, int soLuongTra, double donGia, double thanhTien,
            String tinhTrangHang) {
        this.maPhieuTra = maPhieuTra;
        this.maBienThe = maBienThe;
        this.soLuongTra = soLuongTra;
        this.donGia = donGia;
        this.thanhTien = thanhTien;
        this.tinhTrangHang = tinhTrangHang;
    }

    public String getMaPhieuTra() {
        return maPhieuTra;
    }

    public void setMaPhieuTra(String maPhieuTra) {
        this.maPhieuTra = maPhieuTra;
    }

    public String getMaBienThe() {
        return maBienThe;
    }

    public void setMaBienThe(String maBienThe) {
        this.maBienThe = maBienThe;
    }

    public int getSoLuongTra() {
        return soLuongTra;
    }

    public void setSoLuongTra(int soLuongTra) {
        this.soLuongTra = soLuongTra;
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

    public String getTinhTrangHang() {
        return tinhTrangHang;
    }

    public void setTinhTrangHang(String tinhTrangHang) {
        this.tinhTrangHang = tinhTrangHang;
    }
}
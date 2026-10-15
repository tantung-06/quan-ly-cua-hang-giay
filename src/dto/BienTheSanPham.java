package dto;

public class BienTheSanPham {
    private String maBienThe;
    private String maSanPham;
    private String maMau;
    private String maKichThuoc;
    private double giaBan;
    private int soLuongTon;
    private String hinhAnh;
    private String trangThai;

    public BienTheSanPham() {
    }

    public BienTheSanPham(String maBienThe, String maSanPham, String maMau, String maKichThuoc, double giaBan,
            int soLuongTon, String hinhAnh, String trangThai) {
        this.maBienThe = maBienThe;
        this.maSanPham = maSanPham;
        this.maMau = maMau;
        this.maKichThuoc = maKichThuoc;
        this.giaBan = giaBan;
        this.soLuongTon = soLuongTon;
        this.hinhAnh = hinhAnh;
        this.trangThai = trangThai;
    }

    public String getMaBienThe() {
        return maBienThe;
    }

    public void setMaBienThe(String maBienThe) {
        this.maBienThe = maBienThe;
    }

    public String getMaSanPham() {
        return maSanPham;
    }

    public void setMaSanPham(String maSanPham) {
        this.maSanPham = maSanPham;
    }

    public String getMaMau() {
        return maMau;
    }

    public void setMaMau(String maMau) {
        this.maMau = maMau;
    }

    public String getMaKichThuoc() {
        return maKichThuoc;
    }

    public void setMaKichThuoc(String maKichThuoc) {
        this.maKichThuoc = maKichThuoc;
    }

    public double getGiaBan() {
        return giaBan;
    }

    public void setGiaBan(double giaBan) {
        this.giaBan = giaBan;
    }

    public int getSoLuongTon() {
        return soLuongTon;
    }

    public void setSoLuongTon(int soLuongTon) {
        this.soLuongTon = soLuongTon;
    }

    public String getHinhAnh() {
        return hinhAnh;
    }

    public void setHinhAnh(String hinhAnh) {
        this.hinhAnh = hinhAnh;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }
}
package dto;

public class SanPham {
    private String maSanPham;
    private String tenSanPham;
    private String maThuongHieu;
    private String maLoaiGiay;
    private String moTa;
    private String trangThai;

    public SanPham() {
    }

    public SanPham(String maSanPham, String tenSanPham, String maThuongHieu, String maLoaiGiay, String moTa,
            String trangThai) {
        this.maSanPham = maSanPham;
        this.tenSanPham = tenSanPham;
        this.maThuongHieu = maThuongHieu;
        this.maLoaiGiay = maLoaiGiay;
        this.moTa = moTa;
        this.trangThai = trangThai;
    }

    public String getMaSanPham() {
        return maSanPham;
    }

    public void setMaSanPham(String maSanPham) {
        this.maSanPham = maSanPham;
    }

    public String getTenSanPham() {
        return tenSanPham;
    }

    public void setTenSanPham(String tenSanPham) {
        this.tenSanPham = tenSanPham;
    }

    public String getMaThuongHieu() {
        return maThuongHieu;
    }

    public void setMaThuongHieu(String maThuongHieu) {
        this.maThuongHieu = maThuongHieu;
    }

    public String getMaLoaiGiay() {
        return maLoaiGiay;
    }

    public void setMaLoaiGiay(String maLoaiGiay) {
        this.maLoaiGiay = maLoaiGiay;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }
}
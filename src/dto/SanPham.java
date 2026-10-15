package dto;

public class SanPham {
    private String maSanPham;
    private String tenSanPham;
    private String maLoaiGiay;
    private String maThuongHieu;
    private double giaBan;
    private int soLuongTon;
    private String trangThai;

	public SanPham() {
    }

    public SanPham(String maSanPham, String tenSanPham, String maLoaiGiay, String maThuongHieu, double giaBan,
			int soLuongTon, String trangThai) {
		this.maSanPham = maSanPham;
		this.tenSanPham = tenSanPham;
        this.maLoaiGiay = maLoaiGiay;
		this.maThuongHieu = maThuongHieu;
		this.giaBan = giaBan;
		this.soLuongTon = soLuongTon;
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

	public String getTrangThai() {
		return trangThai;
	}

	public void setTrangThai(String trangThai) {
		this.trangThai = trangThai;
	}
}
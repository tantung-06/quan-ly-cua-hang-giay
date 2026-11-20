package dto;

public class NhomQuyen {
    private String maNhomQuyen;
    private String tenNhomQuyen;
    private String quyenHan;

    public NhomQuyen() {
    }

    public NhomQuyen(String maNhomQuyen, String tenNhomQuyen) {
        this.maNhomQuyen = maNhomQuyen;
        this.tenNhomQuyen = tenNhomQuyen;
    }

    public NhomQuyen(String maNhomQuyen, String tenNhomQuyen, String quyenHan) {
        this(maNhomQuyen, tenNhomQuyen);
        this.quyenHan = quyenHan;
    }

    public String getQuyenHan() { return quyenHan; }

    public void setQuyenHan(String quyenHan) { this.quyenHan = quyenHan; }

    public String getMaNhomQuyen() {
        return maNhomQuyen;
    }

    public void setMaNhomQuyen(String maNhomQuyen) {
        this.maNhomQuyen = maNhomQuyen;
    }

    public String getTenNhomQuyen() {
        return tenNhomQuyen;
    }

    public void setTenNhomQuyen(String tenNhomQuyen) {
        this.tenNhomQuyen = tenNhomQuyen;
    }
}

package dto;

import java.time.LocalDateTime;

public final class LichSuDiem {
    private final long maLichSu;
    private final String maKhachHang;
    private final String maPhieuXuat;
    private final String loai;
    private final int soDiem;
    private final int diemSau;
    private final LocalDateTime thoiGian;

    public LichSuDiem(long maLichSu, String maKhachHang, String maPhieuXuat, String loai,
            int soDiem, int diemSau, LocalDateTime thoiGian) {
        this.maLichSu = maLichSu;
        this.maKhachHang = maKhachHang;
        this.maPhieuXuat = maPhieuXuat;
        this.loai = loai;
        this.soDiem = soDiem;
        this.diemSau = diemSau;
        this.thoiGian = thoiGian;
    }

    public long getMaLichSu() {
        return maLichSu;
    }

    public String getMaKhachHang() {
        return maKhachHang;
    }

    public String getMaPhieuXuat() {
        return maPhieuXuat;
    }

    public String getLoai() {
        return loai;
    }

    public int getSoDiem() {
        return soDiem;
    }

    public int getDiemSau() {
        return diemSau;
    }

    public LocalDateTime getThoiGian() {
        return thoiGian;
    }
}
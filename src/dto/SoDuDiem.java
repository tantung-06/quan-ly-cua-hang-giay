package dto;

public final class SoDuDiem {
    private final int diemHienCo;

    public SoDuDiem(int diemHienCo) {
        if (diemHienCo < 0) {
            throw new IllegalArgumentException("Số dư điểm không hợp lệ.");
        }
        this.diemHienCo = diemHienCo;
    }

    public int getDiemHienCo() {
        return diemHienCo;
    }

}
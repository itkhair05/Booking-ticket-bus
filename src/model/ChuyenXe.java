package model;

import java.util.ArrayList;
import java.util.Date;

public class ChuyenXe {
    private int id;
    private String diemKhoiHanh;
    private String diemDen;
    private Date ngayKhoiHanh;
    private String taiXe;
    private int soGheTrong;
    private double giaVe;
    private String bienSo;
    private String loaiXe;
    private ArrayList<String> gheDaDat;
    private boolean thayDoi;

    public ChuyenXe(int id, String diemKhoiHanh, String diemDen, Date ngayKhoiHanh, String taiXe, int soGheTrong, double giaVe, String bienSo, String loaiXe) {
        this.id = id;
        this.diemKhoiHanh = diemKhoiHanh;
        this.diemDen = diemDen;
        this.ngayKhoiHanh = ngayKhoiHanh;
        this.taiXe = taiXe;
        this.soGheTrong = soGheTrong > 40 ? 40 : soGheTrong; // Giới hạn 40 ghế
        this.giaVe = giaVe;
        this.bienSo = bienSo;
        this.loaiXe = loaiXe;
        this.gheDaDat = new ArrayList<>();
        this.thayDoi = false;
    }

    public int getId() { return id; }
    public String getDiemKhoiHanh() { return diemKhoiHanh; }
    public String getDiemDen() { return diemDen; }
    public Date getNgayKhoiHanh() { return ngayKhoiHanh; }
    public String getTaiXe() { return taiXe; }
    public int getSoGheTrong() { return soGheTrong; }
    public double getGiaVe() { return giaVe; }
    public String getBienSo() { return bienSo; }
    public String getLoaiXe() { return loaiXe; }
    public ArrayList<String> getGheDaDat() { return gheDaDat; }
    public boolean isThayDoi() { return thayDoi; }

    public void setTaiXe(String taiXe) { this.taiXe = taiXe; }
    public void setSoGheTrong(int soGheTrong) { this.soGheTrong = soGheTrong; }
    public void setThayDoi(boolean thayDoi) { this.thayDoi = thayDoi; }

    public boolean themGheDaDat(String ghe) {
        if (!gheDaDat.contains(ghe) && isValidSeat(ghe)) {
            gheDaDat.add(ghe);
            return true;
        }
        return false;
    }

    public void xoaGheDaDat(String ghe) {
        gheDaDat.remove(ghe);
    }

    private boolean isValidSeat(String seat) {
        if (seat.length() < 2 || seat.length() > 4) return false;
        char type = seat.charAt(0);
        if (type != 'A' && type != 'B') return false;
        try {
            int number = Integer.parseInt(seat.substring(1));
            return number >= 1 && number <= 20;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
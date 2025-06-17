package model;

public class DatVe {
    private int id;
    private String maVe;
    private String tenNguoiDung;
    private int idChuyenXe;
    private int soVe;
    private String soDienThoai;
    private String trangThai;
    private boolean dichVuDuaDon;
    private String diaChiDon;

    public DatVe(int id, String maVe, String tenNguoiDung, int idChuyenXe, int soVe, String soDienThoai, boolean dichVuDuaDon, String diaChiDon) {
        this.id = id;
        this.maVe = maVe;
        this.tenNguoiDung = tenNguoiDung;
        this.idChuyenXe = idChuyenXe;
        this.soVe = soVe;
        this.soDienThoai = soDienThoai;
        this.trangThai = "ChuaThanhToan"; // Sửa giá trị mặc định
        this.dichVuDuaDon = dichVuDuaDon;
        this.diaChiDon = diaChiDon;
    }

    public DatVe(int id, String maVe, String tenNguoiDung, int idChuyenXe, int soVe, String soDienThoai) {
        this(id, maVe, tenNguoiDung, idChuyenXe, soVe, soDienThoai, false, "");
    }

    public int getId() { return id; }
    public String getMaVe() { return maVe; }
    public String getTenNguoiDung() { return tenNguoiDung; }
    public int getIdChuyenXe() { return idChuyenXe; }
    public int getSoVe() { return soVe; }
    public String getSoDienThoai() { return soDienThoai; }
    public String getTrangThai() { return trangThai; }
    public boolean isDichVuDuaDon() { return dichVuDuaDon; }
    public String getDiaChiDon() { return diaChiDon; }

    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
    public void setDichVuDuaDon(boolean dichVuDuaDon) { this.dichVuDuaDon = dichVuDuaDon; }
    public void setDiaChiDon(String diaChiDon) { this.diaChiDon = diaChiDon; }

    public void confirmPayment() { this.trangThai = "DaThanhToan"; } // Sửa giá trị
}
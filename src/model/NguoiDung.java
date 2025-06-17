package model;

public class NguoiDung {
    private String tenDangNhap;
    private String matKhau;
    private boolean laAdmin;
    private String soDienThoai;

    public NguoiDung(String tenDangNhap, String matKhau, boolean laAdmin, String soDienThoai) {
        this.tenDangNhap = tenDangNhap;
        this.matKhau = matKhau;
        this.laAdmin = laAdmin;
        this.soDienThoai = soDienThoai;
    }

    public String getTenDangNhap() { return tenDangNhap; }
    public String getMatKhau() { return matKhau; }
    public boolean isLaAdmin() { return laAdmin; }
    public String getSoDienThoai() { return soDienThoai; }

    public void setMatKhau(String matKhau) { this.matKhau = matKhau; }
}
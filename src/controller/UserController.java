package controller;

import model.ChuyenXe;
import model.DatVe;
import model.NguoiDung;
import dao.ChuyenXeDAO;
import dao.DatVeDAO;
import dao.NguoiDungDAO;
import util.Utils;
import util.DatabaseConnector;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.sql.PreparedStatement;

public class UserController {
    private ChuyenXeDAO chuyenXeDAO;
    private DatVeDAO datVeDAO;
    private NguoiDungDAO nguoiDungDAO;
    private NguoiDung nguoiDung;
    private int diemTichLuy;

    public UserController(NguoiDung nguoiDung) {
        this.chuyenXeDAO = new ChuyenXeDAO();
        this.datVeDAO = new DatVeDAO();
        this.nguoiDungDAO = new NguoiDungDAO();
        this.nguoiDung = nguoiDung;
        loadDiemTichLuyFromDB();
    }

    public List<String> getGheCuaVe(int idVe) {
        List<String> result = datVeDAO.getGheByDatVeId(idVe);
        return (result != null) ? result : new ArrayList<>();
    }

    public List<String> getGheDaDatHienTai(int idChuyenXe) {
        return this.chuyenXeDAO.getGheDaDatByIdChuyenXe(idChuyenXe);
    }

    public NguoiDung getNguoiDung() {
        return nguoiDung;
    }

    private void loadDiemTichLuyFromDB() {
        if (this.nguoiDung != null && this.nguoiDungDAO != null) {
            this.diemTichLuy = this.nguoiDungDAO.getDiemTichLuy(this.nguoiDung.getTenDangNhap());
            System.out.println("Loaded diemTichLuy for " + this.nguoiDung.getTenDangNhap() + ": " + this.diemTichLuy);
        } else {
            System.err.println("Lỗi: Không thể load điểm tích lũy do nguoiDung hoặc nguoiDungDAO là null.");
            this.diemTichLuy = 0;
        }
    }

    public int getDiemTichLuy() {
        return diemTichLuy;
    }

    public void addDiemTichLuy(int diem) {
        if (diem == 0 || this.nguoiDung == null || this.nguoiDungDAO == null) return;

        int diemMoi = this.diemTichLuy + diem;
        diemMoi = Math.max(0, diemMoi);

        boolean updated = this.nguoiDungDAO.updateDiemTichLuy(this.nguoiDung.getTenDangNhap(), diemMoi);

        if (updated) {
            this.diemTichLuy = diemMoi;
            System.out.println("Updated diemTichLuy for " + this.nguoiDung.getTenDangNhap() + " to " + this.diemTichLuy);
        } else {
            System.err.println("Lỗi: Không thể cập nhật điểm tích lũy vào DB cho " + this.nguoiDung.getTenDangNhap());
        }
    }

    public List<ChuyenXe> getDanhSachChuyenXe() {
        List<ChuyenXe> result = chuyenXeDAO.getAllChuyenXe();
        return (result != null) ? result : new ArrayList<>();
    }

    // Thêm phương thức getChuyenXeById
    public ChuyenXe getChuyenXeById(int idChuyen) {
        return chuyenXeDAO.getChuyenXeById(idChuyen);
    }

    public List<DatVe> getDanhSachDatVeCuaNguoiDung() {
        List<DatVe> result = datVeDAO.getVeByNguoiDung(this.nguoiDung.getTenDangNhap());
        return (result != null) ? result : new ArrayList<>();
    }

    public DatVe datVe(int idChuyenXe, int soVe, List<String> gheList, String soDienThoai, boolean dichVuDuaDon, String diaChiDon)
            throws IllegalArgumentException, IllegalStateException, RuntimeException {

        Connection conn = null;
        DatVe datVeMoi = null;
        boolean transactionSuccess = false;
        int idVeMoi = -1;

        System.out.println("[CONTROLLER DEBUG] datVe - Nhận yêu cầu: idChuyenXe=" + idChuyenXe + ", soVe=" + soVe + ", gheList=" + gheList + ", sdt=" + soDienThoai + ", duaDon=" + dichVuDuaDon + ", diaChiDon='" + diaChiDon + "'");

        try {
            conn = DatabaseConnector.getConnection();
            conn.setAutoCommit(false);
            System.out.println("[CONTROLLER DEBUG] datVe - Bắt đầu transaction.");

            System.out.println("[CONTROLLER DEBUG] datVe - Gọi chuyenXeDAO.getChuyenXeById...");
            ChuyenXe chuyenXe = chuyenXeDAO.getChuyenXeById(idChuyenXe, conn);
            if (chuyenXe == null) {
                throw new IllegalStateException("Không tìm thấy chuyến xe ID: " + idChuyenXe + ".");
            }
            System.out.println("[CONTROLLER DEBUG] datVe - Lấy được ChuyenXe: " + chuyenXe.getId());

            System.out.println("[CONTROLLER DEBUG] datVe - Bắt đầu kiểm tra ghế...");
            List<String> gheDaDatHienTai = chuyenXe.getGheDaDat();
            System.out.println("[CONTROLLER DEBUG] datVe - Ghế đã đặt hiện tại: " + gheDaDatHienTai);
            int tongSoGhe = chuyenXe.getSoGheTrong() + gheDaDatHienTai.size();
            int soGheTrongThucTe = tongSoGhe - gheDaDatHienTai.size();
            if (soGheTrongThucTe < soVe) {
                throw new IllegalStateException("Không đủ ghế trống (Còn: " + soGheTrongThucTe + ", Cần: " + soVe + ")");
            }
            if (gheList == null || gheList.isEmpty() || gheList.size() != soVe) {
                throw new IllegalArgumentException("Danh sách ghế chọn không hợp lệ.");
            }
            for (String ghe : gheList) {
                String gheTrimmed = ghe.trim();
                if (!isValidSeatFormat(gheTrimmed)) {
                    throw new IllegalArgumentException("Định dạng ghế '" + gheTrimmed + "' không hợp lệ!");
                }
                if (gheDaDatHienTai.contains(gheTrimmed)) {
                    throw new IllegalArgumentException("Ghế '" + gheTrimmed + "' đã có người đặt.");
                }
            }
            System.out.println("[CONTROLLER DEBUG] datVe - Kiểm tra ghế OK.");

            String maVe = Utils.taoMaVe();
            System.out.println("[CONTROLLER DEBUG] datVe - Tạo Mã vé: " + maVe);
            datVeMoi = new DatVe(0, maVe, this.nguoiDung.getTenDangNhap(), idChuyenXe, soVe, soDienThoai, dichVuDuaDon, diaChiDon);
            datVeMoi.setTrangThai("ChuaThanhToan");
            System.out.println("[CONTROLLER DEBUG] datVe - Đã tạo đối tượng DatVe (chưa có ID), trạng thái: " + datVeMoi.getTrangThai());

            System.out.println("[CONTROLLER DEBUG] datVe - Gọi datVeDAO.addDatVeVaGhe...");
            idVeMoi = datVeDAO.addDatVeVaGhe(datVeMoi, gheList, conn);

            if (idVeMoi > 0) {
                System.out.println("[CONTROLLER DEBUG] datVe - Thêm vé và ghế vào DB thành công (ID=" + idVeMoi + "). Chuẩn bị commit...");
                conn.commit();
                transactionSuccess = true;
                System.out.println("[CONTROLLER DEBUG] datVe - Commit thành công.");
                addDiemTichLuy(soVe * 10);
            } else {
                System.err.println("[CONTROLLER ERROR] datVe - DAO addDatVeVaGhe không trả về ID hợp lệ (>0). Đang rollback...");
                conn.rollback();
            }

        } catch (SQLException e) {
            System.err.println("[CONTROLLER ERROR] datVe - Lỗi SQL, rollback... " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback();
                    System.err.println("Rollback do SQLException.");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw new RuntimeException("Lỗi cơ sở dữ liệu khi đặt vé: " + e.getMessage(), e);
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.err.println("[CONTROLLER ERROR] datVe - Lỗi nghiệp vụ: " + e.getMessage());
            throw e;
        } finally {
            System.out.println("[CONTROLLER DEBUG] datVe - Vào khối finally.");
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            DatabaseConnector.closeConnection(conn);
            System.out.println("[CONTROLLER DEBUG] datVe - Đã đóng connection.");
        }

        System.out.println("[CONTROLLER DEBUG] datVe - Kết thúc, trả về: " + (transactionSuccess ? "DatVe(ID=" + idVeMoi + ")" : "null"));
        return transactionSuccess ? datVeMoi : null;
    }

    public boolean thanhToanVe(int idVe, boolean apDungUuDai) {
        DatVe ve = datVeDAO.getDatVeById(idVe);
        if (ve != null && ve.getTenNguoiDung().equals(this.nguoiDung.getTenDangNhap())) {
            if ("ChuaThanhToan".equalsIgnoreCase(ve.getTrangThai())) {
                boolean success = datVeDAO.updateTrangThaiVe(idVe, "DaThanhToan");
                if (success && apDungUuDai) {
                    addDiemTichLuy(-100);
                }
                return success;
            } else {
                return false;
            }
        }
        return false;
    }

    public boolean huyVe(int idVe) {
        DatVe ve = datVeDAO.getDatVeById(idVe);
        if (ve != null && ve.getTenNguoiDung().equals(this.nguoiDung.getTenDangNhap())) {
            if (!"DaHuy".equalsIgnoreCase(ve.getTrangThai())) {
                Connection conn = null;
                boolean success = false;
                try {
                    conn = DatabaseConnector.getConnection();
                    conn.setAutoCommit(false);

                    success = datVeDAO.updateTrangThaiVe(idVe, "DaHuy");
                    if (success) {
                        String sqlDeleteGhe = "DELETE FROM GheDaDat WHERE idDatVe = ?";
                        try (PreparedStatement pstmt = conn.prepareStatement(sqlDeleteGhe)) {
                            pstmt.setInt(1, idVe);
                            pstmt.executeUpdate();
                        }
                        conn.commit();
                        int soVe = ve.getSoVe();
                        addDiemTichLuy(-soVe * 10);
                    } else {
                        conn.rollback();
                    }
                } catch (SQLException e) {
                    System.err.println("Lỗi khi hủy vé: " + e.getMessage());
                    if (conn != null) {
                        try {
                            conn.rollback();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                    }
                    return false;
                } finally {
                    if (conn != null) {
                        try {
                            conn.setAutoCommit(true);
                            conn.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }
                return success;
            } else {
                return false;
            }
        }
        return false;
    }

    public List<ChuyenXe> timKiemChuyenXe(String diemKhoiHanh, String diemDen) {
        List<ChuyenXe> result = chuyenXeDAO.findChuyenXe(diemKhoiHanh, diemDen);
        return (result != null) ? result : new ArrayList<>();
    }

    public List<String> getThongBaoThayDoiChuyenXe() {
        List<String> thongBaoList = new ArrayList<>();
        return thongBaoList;
    }

    private boolean isValidSeatFormat(String seat) {
        if (seat == null || seat.length() < 2 || seat.length() > 3) return false;
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
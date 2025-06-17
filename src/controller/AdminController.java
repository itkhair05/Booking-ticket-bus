package controller;

import model.ChuyenXe;
import model.DatVe;
import dao.ChuyenXeDAO;
import dao.DatVeDAO;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.HashMap;

public class AdminController {
    private ChuyenXeDAO chuyenXeDAO;
    private DatVeDAO datVeDAO;

    public AdminController() {
        this.chuyenXeDAO = new ChuyenXeDAO();
        this.datVeDAO = new DatVeDAO();
    }

    public List<String> getGheDaDatHienTai(int idChuyenXe) {
        return this.chuyenXeDAO.getGheDaDatByIdChuyenXe(idChuyenXe);
    }

    public List<ChuyenXe> getDanhSachChuyenXe() {
        List<ChuyenXe> result = chuyenXeDAO.getAllChuyenXe();
        return (result != null) ? result : new ArrayList<>();
    }

    public List<DatVe> xemTatCaDatVe() {
        List<DatVe> result = datVeDAO.getAllDatVe();
        return (result != null) ? result : new ArrayList<>();
    }

    public boolean themChuyenXe(ChuyenXe chuyenXe) {
        return chuyenXeDAO.addChuyenXe(chuyenXe);
    }

    public boolean suaChuyenXe(ChuyenXe chuyenXe) {
        return chuyenXeDAO.updateChuyenXe(chuyenXe);
    }

    public boolean xoaChuyenXe(int idChuyenXe) {
        return chuyenXeDAO.deleteChuyenXe(idChuyenXe);
    }

    public boolean phanCongTaiXe(int idChuyenXe, String taiXe) {
        return chuyenXeDAO.phanCongTaiXe(idChuyenXe, taiXe);
    }

    public boolean suaDatVeVaGhe(DatVe datVeMoi, List<String> gheMoiList) {
        Connection conn = null;
        boolean success = false;
        try {
            conn = util.DatabaseConnector.getConnection();
            conn.setAutoCommit(false);
            success = datVeDAO.updateDatVeVaGhe(datVeMoi, gheMoiList, conn);
            if (success) {
                // Cập nhật số ghế trống trong ChuyenXe
                ChuyenXe chuyenXe = chuyenXeDAO.getChuyenXeById(datVeMoi.getIdChuyenXe(), conn);
                if (chuyenXe != null) {
                    int soGheDaDat = chuyenXe.getGheDaDat().size();
                    int tongSoGhe = chuyenXe.getSoGheTrong() + soGheDaDat;
                    chuyenXe.setSoGheTrong(tongSoGhe - soGheDaDat);
                    chuyenXeDAO.updateChuyenXe(chuyenXe);
                }
                conn.commit();
            } else {
                conn.rollback();
            }
        } catch (SQLException e) {
            System.err.println("Lỗi SQL trong transaction sửa vé và ghế: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            if (e.getMessage().contains("Một hoặc nhiều ghế")) {
                throw new IllegalStateException(e.getMessage());
            } else {
                throw new RuntimeException("Lỗi cơ sở dữ liệu khi sửa vé và ghế: " + e.getMessage());
            }
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
    }

    public Map<String, Double> tinhDoanhThuTatCa() {
        Map<String, Double> ketQua = new HashMap<>();
        double daThanhToan = 0;
        double chuaThanhToan = 0;
        List<DatVe> danhSachVe = datVeDAO.getAllDatVe();
        if (danhSachVe != null) {
            for (DatVe ve : danhSachVe) {
                ChuyenXe chuyenXe = chuyenXeDAO.getChuyenXeById(ve.getIdChuyenXe());
                if (chuyenXe != null) {
                    double gia = ve.getSoVe() * chuyenXe.getGiaVe();
                    if ("DaThanhToan".equalsIgnoreCase(ve.getTrangThai())) {
                        daThanhToan += gia;
                    } else if ("ChuaThanhToan".equalsIgnoreCase(ve.getTrangThai())) {
                        chuaThanhToan += gia;
                    }
                }
            }
        }
        ketQua.put("DaThanhToan", daThanhToan);
        ketQua.put("ChuaThanhToan", chuaThanhToan);
        return ketQua;
    }

    public List<DatVe> getDanhSachDatVe() {
        return datVeDAO.getAllDatVe();
    }
}
package dao;

import model.DatVe;
import util.DatabaseConnector;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

public class DatVeDAO {

    private DatVe mapResultSetToDatVe(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String maVe = rs.getString("maVe");
        String tenNguoiDung = rs.getString("tenNguoiDung");
        int idChuyenXe = rs.getInt("idChuyenXe");
        int soVe = rs.getInt("soVe");
        String soDienThoai = rs.getString("soDienThoai");
        String trangThai = rs.getString("trangThai");
        boolean dichVuDuaDon = rs.getBoolean("dichVuDuaDon");
        String diaChiDon = rs.getString("diaChiDon");

        DatVe ve = new DatVe(id, maVe, tenNguoiDung, idChuyenXe, soVe, soDienThoai, dichVuDuaDon, diaChiDon);
        ve.setTrangThai(trangThai);
        return ve;
    }

    public int addDatVeVaGhe(DatVe ve, List<String> gheList, Connection conn) throws SQLException {
        System.out.println("[DAO DEBUG] addDatVeVaGhe - Bắt đầu cho MaVe: " + ve.getMaVe() + ", Ghế: " + gheList);
        System.out.println("[DAO DEBUG] addDatVeVaGhe - Trạng thái vé: " + ve.getTrangThai());
        int idDatVeMoi = -1;

        String sqlDatVe = "INSERT INTO DatVe (maVe, tenNguoiDung, idChuyenXe, soVe, soDienThoai, trangThai, dichVuDuaDon, diaChiDon) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement pstmtDatVe = null;
        ResultSet generatedKeys = null;
        try {
            if (conn == null || conn.isClosed()) {
                System.err.println("[DAO ERROR] addDatVeVaGhe - Connection không hợp lệ.");
                throw new SQLException("Connection không hợp lệ để thêm vé.");
            }
            pstmtDatVe = conn.prepareStatement(sqlDatVe, Statement.RETURN_GENERATED_KEYS);
            pstmtDatVe.setString(1, ve.getMaVe());
            pstmtDatVe.setString(2, ve.getTenNguoiDung());
            pstmtDatVe.setInt(3, ve.getIdChuyenXe());
            pstmtDatVe.setInt(4, ve.getSoVe());
            pstmtDatVe.setString(5, ve.getSoDienThoai());
            pstmtDatVe.setString(6, ve.getTrangThai());
            pstmtDatVe.setBoolean(7, ve.isDichVuDuaDon());
            pstmtDatVe.setString(8, ve.getDiaChiDon());

            int rowsAffected = pstmtDatVe.executeUpdate();
            System.out.println("[DAO DEBUG] addDatVeVaGhe - rowsAffected khi INSERT DatVe: " + rowsAffected);

            if (rowsAffected > 0) {
                generatedKeys = pstmtDatVe.getGeneratedKeys();
                if (generatedKeys.next()) {
                    idDatVeMoi = generatedKeys.getInt(1);
                    System.out.println("[DAO DEBUG] addDatVeVaGhe - ID vé mới tạo: " + idDatVeMoi);
                } else {
                    System.err.println("[DAO ERROR] addDatVeVaGhe - Không lấy được generated key sau khi INSERT DatVe.");
                    throw new SQLException("Thêm vé thành công nhưng không lấy được ID.");
                }
            } else {
                System.err.println("[DAO ERROR] addDatVeVaGhe - INSERT DatVe không thành công (rowsAffected=0).");
                throw new SQLException("Thêm vé vào bảng DatVe thất bại.");
            }
        } finally {
            DatabaseConnector.closeResultSet(generatedKeys);
            DatabaseConnector.closeStatement(pstmtDatVe);
        }

        if (idDatVeMoi > 0 && gheList != null && !gheList.isEmpty()) {
            String sqlGhe = "INSERT INTO GheDaDat (idDatVe, idChuyenXe, tenGhe) VALUES (?, ?, ?)";
            PreparedStatement pstmtGhe = null;
            System.out.println("[DAO DEBUG] addDatVeVaGhe - Chuẩn bị thêm " + gheList.size() + " ghế vào GheDaDat cho idDatVe: " + idDatVeMoi);
            try {
                pstmtGhe = conn.prepareStatement(sqlGhe);
                for (String tenGhe : gheList) {
                    if (tenGhe == null || tenGhe.trim().isEmpty()) continue;
                    String gheTrimmed = tenGhe.trim();
                    System.out.println("  -> DAO DEBUG: addDatVeVaGhe - Thêm ghế: " + gheTrimmed);
                    pstmtGhe.setInt(1, idDatVeMoi);
                    pstmtGhe.setInt(2, ve.getIdChuyenXe());
                    pstmtGhe.setString(3, gheTrimmed);
                    pstmtGhe.addBatch();
                }
                int[] batchResult = pstmtGhe.executeBatch();
                System.out.println("[DAO DEBUG] addDatVeVaGhe - Kết quả executeBatch GheDaDat: " + Arrays.toString(batchResult));
                boolean batchOk = true;
                for (int result : batchResult) {
                    if (result < 0 && result != Statement.SUCCESS_NO_INFO) {
                        batchOk = false;
                        System.err.println("[DAO ERROR] addDatVeVaGhe - Lỗi trong batch thêm ghế, kết quả: " + result);
                        break;
                    }
                }
                if (!batchOk) {
                    throw new SQLException("Lỗi khi thực thi batch thêm ghế vào CSDL.");
                }
                System.out.println("[DAO DEBUG] addDatVeVaGhe - Đã thêm thành công " + batchResult.length + " ghế vào GheDaDat.");

            } catch (SQLException e) {
                System.err.println("[DAO ERROR] addDatVeVaGhe - Lỗi SQL khi thêm ghế: " + e.getMessage());
                if (e.getMessage().toLowerCase().contains("unique key constraint") || e.getMessage().toLowerCase().contains("duplicate key")) {
                    System.err.println(">>> Lỗi: Ghế đã bị đặt (UNIQUE constraint violation).");
                    throw new SQLException("Một hoặc nhiều ghế bạn chọn đã bị người khác đặt trong lúc bạn thao tác. Vui lòng chọn lại.", e);
                }
                throw e;
            } finally {
                DatabaseConnector.closeStatement(pstmtGhe);
            }
        } else if (idDatVeMoi <= 0) {
            System.err.println("[DAO ERROR] addDatVeVaGhe - Không thể thêm ghế vì ID vé mới không hợp lệ.");
            throw new SQLException("Không thể thêm ghế vì không lấy được ID vé mới.");
        } else {
            System.out.println("[DAO WARN] addDatVeVaGhe - Danh sách ghế rỗng, không thêm ghế nào cho vé ID: " + idDatVeMoi);
        }

        System.out.println("[DAO DEBUG] addDatVeVaGhe - Kết thúc, trả về idDatVeMoi: " + idDatVeMoi);
        return idDatVeMoi;
    }

    public List<String> getGheByDatVeId(int idDatVe) {
        System.out.println("[DAO DEBUG] getGheByDatVeId - Bắt đầu cho idDatVe: " + idDatVe);
        List<String> gheList = new ArrayList<>();
        String sql = "SELECT tenGhe FROM GheDaDat WHERE idDatVe = ? ORDER BY tenGhe";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnector.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, idDatVe);
            rs = pstmt.executeQuery();
            System.out.println("[DAO DEBUG] getGheByDatVeId - Đã thực thi query lấy ghế cho idDatVe: " + idDatVe);
            while (rs.next()) {
                String tenGhe = rs.getString("tenGhe");
                System.out.println("  -> DAO DEBUG: getGheByDatVeId - Tìm thấy ghế: " + tenGhe);
                gheList.add(tenGhe);
            }
        } catch (SQLException e) {
            System.err.println("[DAO ERROR] getGheByDatVeId - Lỗi SQL khi lấy ghế: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseConnector.closeResource(rs, pstmt, conn);
        }
        System.out.println("[DAO DEBUG] getGheByDatVeId - Trả về danh sách ghế cho idDatVe " + idDatVe + ": " + gheList);
        return gheList;
    }

    public DatVe getDatVeById(int id) {
        DatVe ve = null;
        String sql = "SELECT * FROM DatVe WHERE id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnector.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                ve = mapResultSetToDatVe(rs);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy vé theo ID: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseConnector.closeResource(rs, pstmt, conn);
        }
        return ve;
    }

    public List<DatVe> getAllDatVe() {
        List<DatVe> danhSach = new ArrayList<>();
        String sql = "SELECT * FROM DatVe ORDER BY id DESC";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnector.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                danhSach.add(mapResultSetToDatVe(rs));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy tất cả vé: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseConnector.closeResource(rs, pstmt, conn);
        }
        return danhSach;
    }

    public List<DatVe> getVeByNguoiDung(String tenNguoiDung) {
        List<DatVe> danhSach = new ArrayList<>();
        String sql = "SELECT * FROM DatVe WHERE tenNguoiDung = ? ORDER BY id DESC";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnector.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, tenNguoiDung);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                danhSach.add(mapResultSetToDatVe(rs));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy vé theo người dùng: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseConnector.closeResource(rs, pstmt, conn);
        }
        return danhSach;
    }

    public List<DatVe> getVeByIdChuyenXe(int idChuyenXe) {
        List<DatVe> danhSach = new ArrayList<>();
        String sql = "SELECT * FROM DatVe WHERE idChuyenXe = ? ORDER BY id";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnector.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, idChuyenXe);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                danhSach.add(mapResultSetToDatVe(rs));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy vé theo ID chuyến xe: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseConnector.closeResource(rs, pstmt, conn);
        }
        return danhSach;
    }

    public boolean updateTrangThaiVe(int idVe, String trangThaiMoi) {
        String sql = "UPDATE DatVe SET trangThai = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DatabaseConnector.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, trangThaiMoi);
            pstmt.setInt(2, idVe);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật trạng thái vé: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            DatabaseConnector.closeStatement(pstmt);
            DatabaseConnector.closeConnection(conn);
        }
    }

    public boolean updateDatVeVaGhe(DatVe ve, List<String> gheMoiList, Connection conn) throws SQLException {
        String sqlUpdateDatVe = "UPDATE DatVe SET maVe = ?, tenNguoiDung = ?, soVe = ?, soDienThoai = ?, trangThai = ?, dichVuDuaDon = ?, diaChiDon = ? WHERE id = ?";
        PreparedStatement pstmtUpdateDatVe = null;
        int rowsAffectedDatVe = 0;
        try {
            if (conn == null || conn.isClosed()) {
                throw new SQLException("Connection không hợp lệ để cập nhật vé.");
            }
            pstmtUpdateDatVe = conn.prepareStatement(sqlUpdateDatVe);
            pstmtUpdateDatVe.setString(1, ve.getMaVe());
            pstmtUpdateDatVe.setString(2, ve.getTenNguoiDung());
            pstmtUpdateDatVe.setInt(3, ve.getSoVe());
            pstmtUpdateDatVe.setString(4, ve.getSoDienThoai());
            pstmtUpdateDatVe.setString(5, ve.getTrangThai());
            pstmtUpdateDatVe.setBoolean(6, ve.isDichVuDuaDon());
            pstmtUpdateDatVe.setString(7, ve.getDiaChiDon());
            pstmtUpdateDatVe.setInt(8, ve.getId());

            rowsAffectedDatVe = pstmtUpdateDatVe.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi cập nhật bảng DatVe: " + e.getMessage());
            throw e;
        } finally {
            DatabaseConnector.closeStatement(pstmtUpdateDatVe);
        }

        if (rowsAffectedDatVe > 0) {
            PreparedStatement pstmtDeleteGhe = null;
            PreparedStatement pstmtInsertGhe = null;
            try {
                String sqlDeleteGhe = "DELETE FROM GheDaDat WHERE idDatVe = ?";
                pstmtDeleteGhe = conn.prepareStatement(sqlDeleteGhe);
                pstmtDeleteGhe.setInt(1, ve.getId());
                pstmtDeleteGhe.executeUpdate();

                if (gheMoiList != null && !gheMoiList.isEmpty()) {
                    String sqlInsertGhe = "INSERT INTO GheDaDat (idDatVe, idChuyenXe, tenGhe) VALUES (?, ?, ?)";
                    pstmtInsertGhe = conn.prepareStatement(sqlInsertGhe);
                    for (String tenGhe : gheMoiList) {
                        pstmtInsertGhe.setInt(1, ve.getId());
                        pstmtInsertGhe.setInt(2, ve.getIdChuyenXe());
                        pstmtInsertGhe.setString(3, tenGhe.trim());
                        pstmtInsertGhe.addBatch();
                    }
                    pstmtInsertGhe.executeBatch();
                }
                return true;

            } catch (SQLException e) {
                System.err.println("Lỗi SQL khi cập nhật bảng GheDaDat: " + e.getMessage());
                if (e.getMessage().toLowerCase().contains("unique key constraint") || e.getMessage().toLowerCase().contains("duplicate key")) {
                    System.err.println(">>> Lỗi: Ghế mới chọn đã bị đặt (UNIQUE constraint violation).");
                    throw new SQLException("Một hoặc nhiều ghế mới bạn chọn đã bị người khác đặt. Vui lòng thử lại.", e);
                }
                throw e;
            } finally {
                DatabaseConnector.closeStatement(pstmtDeleteGhe);
                DatabaseConnector.closeStatement(pstmtInsertGhe);
            }
        } else {
            System.err.println("Không tìm thấy vé ID " + ve.getId() + " để cập nhật.");
            return false;
        }
    }

    public boolean deleteVe(int id) {
        String sql = "DELETE FROM DatVe WHERE id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DatabaseConnector.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi xóa vé: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            DatabaseConnector.closeStatement(pstmt);
            DatabaseConnector.closeConnection(conn);
        }
    }
}
package dao;

import model.ChuyenXe;
import util.DatabaseConnector;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Date; // Import thêm java.util.Date nếu chưa có

public class ChuyenXeDAO {

    // Lấy danh sách ghế đã đặt cho một chuyến xe (từ bảng DatVe)
    // Sử dụng Connection được truyền vào
    /**
     * Lấy danh sách TÊN ghế đã đặt cho một chuyến xe từ bảng GheDaDat.
     * Sử dụng Connection được truyền vào (cho transaction hoặc các mục đích khác).
     * @param idChuyenXe ID của chuyến xe.
     * @param conn Connection đang sử dụng.
     * @return List các String tên ghế đã đặt (ví dụ: ["A1", "B5"]).
     * @throws SQLException Nếu có lỗi truy vấn CSDL.
     */
    private List<String> getDanhSachGheDaDat(int idChuyenXe, Connection conn) throws SQLException {
        List<String> gheDaDatList = new ArrayList<>();
        // --- THAY ĐỔI CÂU SQL ---
        // Truy vấn thẳng vào bảng GheDaDat mới
        String sql = "SELECT tenGhe FROM GheDaDat WHERE idChuyenXe = ?";
        // --- KẾT THÚC THAY ĐỔI SQL ---

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            if (conn == null || conn.isClosed()) {
                System.err.println("Lỗi: Connection không hợp lệ trong getDanhSachGheDaDat (ChuyenXeDAO).");
                throw new SQLException("Connection không hợp lệ."); // Ném lỗi rõ ràng
            }
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, idChuyenXe);
            rs = pstmt.executeQuery();

            // --- THAY ĐỔI LOGIC ĐỌC KẾT QUẢ ---
            // Không cần split chuỗi nữa, chỉ cần lấy giá trị cột tenGhe
            while (rs.next()) {
                String tenGhe = rs.getString("tenGhe");
                if (tenGhe != null && !tenGhe.trim().isEmpty()) {
                    gheDaDatList.add(tenGhe.trim());
                }
            }
            // --- KẾT THÚC THAY ĐỔI LOGIC ---
        } finally {
            // Chỉ đóng ResultSet và PreparedStatement
            DatabaseConnector.closeResultSet(rs);
            DatabaseConnector.closeStatement(pstmt);
            // Không đóng Connection conn
        }
        return gheDaDatList;
    }

    public List<String> getGheDaDatByIdChuyenXe(int idChuyenXe) {
        List<String> gheDaDatList = new ArrayList<>();
        // Truy vấn thẳng vào bảng GheDaDat
        String sql = "SELECT tenGhe FROM GheDaDat WHERE idChuyenXe = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnector.getConnection(); // Lấy connection mới
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, idChuyenXe);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                String tenGhe = rs.getString("tenGhe");
                if (tenGhe != null && !tenGhe.trim().isEmpty()) {
                    gheDaDatList.add(tenGhe.trim());
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy ghế đã đặt mới nhất cho chuyến " + idChuyenXe + ": " + e.getMessage());
            e.printStackTrace();
            // Trả về list rỗng khi có lỗi
        } finally {
            DatabaseConnector.closeResource(rs, pstmt, conn); // Đóng tất cả tài nguyên
        }
        return gheDaDatList;
    }


    // Chuyển đổi ResultSet thành đối tượng ChuyenXe (SỬA ĐỔI để nhận Connection)
    private ChuyenXe mapResultSetToChuyenXe(ResultSet rs, Connection conn) throws SQLException {
        int id = rs.getInt("id");
        String diemKH = rs.getString("diemKhoiHanh");
        String diemDen = rs.getString("diemDen");
        Timestamp ngayKH_ts = rs.getTimestamp("ngayKhoiHanh");
        Date ngayKH = (ngayKH_ts != null) ? new Date(ngayKH_ts.getTime()) : null;
        String taiXe = rs.getString("taiXe");
        int tongSoGhe = rs.getInt("tongSoGhe"); // Lấy tổng số ghế từ DB
        double giaVe = rs.getDouble("giaVe");
        String bienSo = rs.getString("bienSo");
        String loaiXe = rs.getString("loaiXe");

        // Lấy ghế đã đặt và tính số ghế trống SỬ DỤNG connection hiện tại
        List<String> gheDaDat = getDanhSachGheDaDat(id, conn);
        int soGheTrong = tongSoGhe - gheDaDat.size();

        ChuyenXe chuyen = new ChuyenXe(id, diemKH, diemDen, ngayKH, taiXe, soGheTrong, giaVe, bienSo, loaiXe);
        // Thêm danh sách ghế đã đặt vào đối tượng ChuyenXe
        gheDaDat.forEach(chuyen::themGheDaDat); // Sử dụng lại phương thức cũ trong model
        // Có thể cần thêm trạng thái `thayDoi` nếu quản lý nó trong DB
        // chuyen.setThayDoi(rs.getBoolean("thayDoi"));
        return chuyen;
    }

    // Lấy tất cả chuyến xe (Phương thức này tự quản lý Connection)
    public List<ChuyenXe> getAllChuyenXe() {
        List<ChuyenXe> danhSach = new ArrayList<>();
        String sql = "SELECT * FROM ChuyenXe ORDER BY ngayKhoiHanh";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnector.getConnection(); // Lấy connection mới
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            // Lưu ý: mapResultSetToChuyenXe cần connection, nên truyền conn vào đây
            while (rs.next()) {
                danhSach.add(mapResultSetToChuyenXe(rs, conn)); // Truyền conn vào map
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách chuyến xe: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Đóng tất cả tài nguyên vì connection này được tạo và quản lý bởi phương thức này
            DatabaseConnector.closeResource(rs, pstmt, conn);
        }
        return danhSach;
    }


    /**
     * Lấy chuyến xe theo ID, sử dụng Connection được cung cấp (cho transaction).
     * Phương thức này KHÔNG đóng Connection.
     * @param id ID chuyến xe.
     * @param conn Connection đang được sử dụng trong transaction.
     * @return ChuyenXe hoặc null nếu không tìm thấy hoặc có lỗi.
     */
    public ChuyenXe getChuyenXeById(int id, Connection conn) { // Thêm tham số Connection
        ChuyenXe chuyen = null;
        String sql = "SELECT * FROM ChuyenXe WHERE id = ?";
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            // Kiểm tra connection trước khi dùng
            if (conn == null || conn.isClosed()) {
                System.err.println("Lỗi: Connection không hợp lệ được truyền vào getChuyenXeById.");
                return null;
            }
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                chuyen = mapResultSetToChuyenXe(rs, conn); // Truyền Connection vào map
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy chuyến xe theo ID (trong transaction): " + e.getMessage());
            e.printStackTrace();
            // Không rollback ở đây, để Controller xử lý
        } finally {
            // Chỉ đóng PreparedStatement và ResultSet, KHÔNG đóng Connection
            DatabaseConnector.closeResultSet(rs);
            DatabaseConnector.closeStatement(pstmt);
        }
        return chuyen;
    }

    // Giữ lại phương thức getChuyenXeById cũ (tự quản lý connection) nếu cần
    public ChuyenXe getChuyenXeById(int id) {
        Connection conn = null;
        try {
            conn = DatabaseConnector.getConnection();
            return getChuyenXeById(id, conn); // Gọi phiên bản nhận connection
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy connection cho getChuyenXeById(id): " + e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            DatabaseConnector.closeConnection(conn); // Đóng connection đã tạo ở đây
        }
    }


    // Thêm chuyến xe mới (Phương thức này tự quản lý Connection)
    public boolean addChuyenXe(ChuyenXe chuyen) {
        String sql = "INSERT INTO ChuyenXe (diemKhoiHanh, diemDen, ngayKhoiHanh, taiXe, tongSoGhe, giaVe, bienSo, loaiXe) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DatabaseConnector.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, chuyen.getDiemKhoiHanh());
            pstmt.setString(2, chuyen.getDiemDen());
            // Kiểm tra null cho ngày khởi hành trước khi lấy time
            if (chuyen.getNgayKhoiHanh() == null) {
                pstmt.setNull(3, Types.TIMESTAMP); // Hoặc ném lỗi nếu ngày là bắt buộc
            } else {
                pstmt.setTimestamp(3, new Timestamp(chuyen.getNgayKhoiHanh().getTime()));
            }
            pstmt.setString(4, chuyen.getTaiXe());
            // Tính lại tổng số ghế từ model ChuyenXe
            int tongSoGhe = chuyen.getSoGheTrong() + chuyen.getGheDaDat().size();
            pstmt.setInt(5, tongSoGhe);
            pstmt.setDouble(6, chuyen.getGiaVe());
            pstmt.setString(7, chuyen.getBienSo());
            pstmt.setString(8, chuyen.getLoaiXe());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi thêm chuyến xe: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            // Đóng tài nguyên
            DatabaseConnector.closeStatement(pstmt);
            DatabaseConnector.closeConnection(conn);
        }
    }

    // Cập nhật thông tin chuyến xe (Phương thức này tự quản lý Connection)
    public boolean updateChuyenXe(ChuyenXe chuyen) {
        String sql = "UPDATE ChuyenXe SET diemKhoiHanh = ?, diemDen = ?, ngayKhoiHanh = ?, taiXe = ?, tongSoGhe = ?, giaVe = ?, bienSo = ?, loaiXe = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DatabaseConnector.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, chuyen.getDiemKhoiHanh());
            pstmt.setString(2, chuyen.getDiemDen());
            if (chuyen.getNgayKhoiHanh() == null) {
                pstmt.setNull(3, Types.TIMESTAMP);
            } else {
                pstmt.setTimestamp(3, new Timestamp(chuyen.getNgayKhoiHanh().getTime()));
            }
            pstmt.setString(4, chuyen.getTaiXe());
            // Tính lại tổng ghế
            int tongSoGhe = chuyen.getSoGheTrong() + chuyen.getGheDaDat().size();
            pstmt.setInt(5, tongSoGhe);
            pstmt.setDouble(6, chuyen.getGiaVe());
            pstmt.setString(7, chuyen.getBienSo());
            pstmt.setString(8, chuyen.getLoaiXe());
            pstmt.setInt(9, chuyen.getId()); // Điều kiện WHERE

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật chuyến xe: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            DatabaseConnector.closeStatement(pstmt);
            DatabaseConnector.closeConnection(conn);
        }
    }

    // Cập nhật chỉ tài xế (Phương thức này tự quản lý Connection)
    public boolean phanCongTaiXe(int idChuyenXe, String taiXe) {
        String sql = "UPDATE ChuyenXe SET taiXe = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DatabaseConnector.getConnection();
            pstmt = conn.prepareStatement(sql);
            if (taiXe == null || taiXe.trim().isEmpty()) {
                pstmt.setNull(1, Types.NVARCHAR); // Cho phép gán null nếu muốn bỏ phân công
            } else {
                pstmt.setString(1, taiXe);
            }
            pstmt.setInt(2, idChuyenXe);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật tài xế: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            DatabaseConnector.closeStatement(pstmt);
            DatabaseConnector.closeConnection(conn);
        }
    }


    // Xóa chuyến xe (Phương thức này tự quản lý Connection)
    public boolean deleteChuyenXe(int id) {
        String sql = "DELETE FROM ChuyenXe WHERE id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DatabaseConnector.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi xóa chuyến xe: " + e.getMessage());
            // Nếu có lỗi khóa ngoại (do chưa xóa vé liên quan và không có ON DELETE CASCADE)
            if (e.getMessage().toLowerCase().contains("foreign key constraint")) {
                System.err.println(">>> Không thể xóa chuyến xe vì vẫn còn vé đã đặt liên quan.");
            }
            e.printStackTrace();
            return false;
        } finally {
            DatabaseConnector.closeStatement(pstmt);
            DatabaseConnector.closeConnection(conn);
        }
    }

    // Tìm kiếm chuyến xe (Phương thức này tự quản lý Connection)
    public List<ChuyenXe> findChuyenXe(String diemKhoiHanh, String diemDen) {
        List<ChuyenXe> danhSach = new ArrayList<>();
        // Xây dựng câu SQL linh hoạt hơn một chút
        StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM ChuyenXe WHERE 1=1"); // Bắt đầu với điều kiện luôn đúng
        List<Object> params = new ArrayList<>();

        if (diemKhoiHanh != null && !diemKhoiHanh.trim().isEmpty()) {
            sqlBuilder.append(" AND diemKhoiHanh LIKE ?");
            params.add("%" + diemKhoiHanh.trim() + "%");
        }
        if (diemDen != null && !diemDen.trim().isEmpty()) {
            sqlBuilder.append(" AND diemDen LIKE ?");
            params.add("%" + diemDen.trim() + "%");
        }
        sqlBuilder.append(" ORDER BY ngayKhoiHanh"); // Luôn sắp xếp

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnector.getConnection();
            pstmt = conn.prepareStatement(sqlBuilder.toString());

            // Gán các tham số động
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }

            rs = pstmt.executeQuery();
            while (rs.next()) {
                danhSach.add(mapResultSetToChuyenXe(rs, conn)); // Truyền conn
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tìm kiếm chuyến xe: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseConnector.closeResource(rs, pstmt, conn);
        }
        return danhSach;
    }
}
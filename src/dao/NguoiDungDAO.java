package dao; // Hoặc package bạn đã tạo

import model.NguoiDung;
import util.DatabaseConnector; // Import lớp kết nối
import util.PasswordUtil;     // Import lớp tiện ích mật khẩu

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class NguoiDungDAO {

    /**
     * Tìm kiếm người dùng dựa trên tên đăng nhập và kiểm tra mật khẩu đã mã hóa (ĐÃ CẬP NHẬT).
     *
     * @param username Tên đăng nhập
     * @param plainPassword Mật khẩu dạng plain text người dùng nhập vào.
     * @return Đối tượng NguoiDung nếu tìm thấy và mật khẩu khớp, ngược lại trả về null.
     */
    public NguoiDung findNguoiDungByCredentials(String username, String plainPassword) {
        NguoiDung user = null;
        // Chỉ cần lấy thông tin người dùng dựa vào username
        String sql = "SELECT tenDangNhap, matKhau, laAdmin, soDienThoai FROM NguoiDung WHERE tenDangNhap = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnector.getConnection(); // Lấy kết nối từ lớp tiện ích
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username); // Gán giá trị cho tham số '?'

            rs = pstmt.executeQuery();

            if (rs.next()) {
                // Lấy hash mật khẩu từ DB
                String hashedPasswordFromDB = rs.getString("matKhau");

                // Sử dụng PasswordUtil để kiểm tra mật khẩu plain text với hash từ DB
                if (PasswordUtil.checkPassword(plainPassword, hashedPasswordFromDB)) {
                    // Nếu mật khẩu khớp, tạo đối tượng NguoiDung
                    String dbUsername = rs.getString("tenDangNhap");
                    boolean isAdmin = rs.getBoolean("laAdmin");
                    String phone = rs.getString("soDienThoai");
                    // Tạo đối tượng với mật khẩu là null hoặc trống, không cần lưu lại mật khẩu plain text
                    user = new NguoiDung(dbUsername, null /* Hoặc "" */, isAdmin, phone);
                }
                // Nếu checkPassword trả về false (mật khẩu sai), user vẫn là null
            }
            // Nếu username không tồn tại, user vẫn là null

        } catch (SQLException e) {
            System.err.println("Lỗi khi tìm người dùng khi đăng nhập: " + e.getMessage());
            e.printStackTrace(); // In chi tiết lỗi ra console
        } finally {
            // Đảm bảo đóng tất cả tài nguyên trong khối finally
            DatabaseConnector.closeResource(rs, pstmt, conn);
        }

        return user; // Trả về đối tượng user tìm được hoặc null
    }

    /**
     * Thêm người dùng mới vào cơ sở dữ liệu (ĐÃ CẬP NHẬT MÃ HÓA).
     * Mật khẩu sẽ được mã hóa trước khi lưu.
     * @param user Đối tượng NguoiDung chứa thông tin cần thêm (mật khẩu dạng plain text).
     * @return true nếu thêm thành công, false nếu thất bại.
     */
    public boolean addNguoiDung(NguoiDung user) {
        // Mã hóa mật khẩu trước khi lưu
        String hashedPassword = PasswordUtil.hashPassword(user.getMatKhau());
        if (hashedPassword == null) {
            System.err.println("Lỗi: Không thể mã hóa mật khẩu trống khi thêm người dùng " + user.getTenDangNhap());
            return false;
        }

        String sql = "INSERT INTO NguoiDung (tenDangNhap, matKhau, laAdmin, soDienThoai) VALUES (?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DatabaseConnector.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, user.getTenDangNhap());
            pstmt.setString(2, hashedPassword); // Lưu mật khẩu đã mã hóa
            pstmt.setBoolean(3, user.isLaAdmin());
            pstmt.setString(4, user.getSoDienThoai());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi thêm người dùng: " + e.getMessage());
            // Kiểm tra lỗi trùng khóa chính (tên đăng nhập)
            if (e.getMessage().toLowerCase().contains("primary key constraint") || e.getMessage().toLowerCase().contains("unique constraint")) {
                System.err.println(">>> Tên đăng nhập '" + user.getTenDangNhap() + "' đã tồn tại.");
            }
            e.printStackTrace();
            return false;
        } finally {
            DatabaseConnector.closeStatement(pstmt);
            DatabaseConnector.closeConnection(conn);
        }
    }

    /**
     * Cập nhật mật khẩu mới cho người dùng (ĐÃ CẬP NHẬT MÃ HÓA).
     * Mật khẩu mới sẽ được mã hóa trước khi cập nhật.
     * @param username Tên đăng nhập của người dùng cần cập nhật.
     * @param newPlainPassword Mật khẩu mới dạng plain text.
     * @return true nếu cập nhật thành công, false nếu thất bại.
     */
    public boolean updatePassword(String username, String newPlainPassword) {
        // Mã hóa mật khẩu mới
        String hashedNewPassword = PasswordUtil.hashPassword(newPlainPassword);
        if (hashedNewPassword == null) {
            System.err.println("Lỗi: Không thể mã hóa mật khẩu trống khi cập nhật cho " + username);
            return false;
        }

        String sql = "UPDATE NguoiDung SET matKhau = ? WHERE tenDangNhap = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DatabaseConnector.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, hashedNewPassword); // Lưu hash mới
            pstmt.setString(2, username);

            int rowsAffected = pstmt.executeUpdate();
            // rowsAffected > 0 nghĩa là tìm thấy username và đã cập nhật
            // Nếu rowsAffected = 0 nghĩa là không tìm thấy username đó
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật mật khẩu: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            DatabaseConnector.closeStatement(pstmt);
            DatabaseConnector.closeConnection(conn);
        }
    }

    /**
     * Kiểm tra xem tên đăng nhập đã tồn tại hay chưa.
     * @param username Tên đăng nhập cần kiểm tra.
     * @return true nếu tồn tại, false nếu không tồn tại.
     */
    public boolean checkUsernameExists(String username) {
        String sql = "SELECT 1 FROM NguoiDung WHERE tenDangNhap = ?"; // Chỉ cần kiểm tra sự tồn tại, không cần lấy dữ liệu cụ thể
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean exists = false;
        try {
            conn = DatabaseConnector.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            rs = pstmt.executeQuery();
            exists = rs.next(); // Nếu rs.next() là true -> có ít nhất 1 dòng -> tồn tại
        } catch (SQLException e) {
            System.err.println("Lỗi khi kiểm tra tên đăng nhập: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseConnector.closeResource(rs, pstmt, conn);
        }
        return exists;
    }

    /**
     * Lấy thông tin người dùng dựa trên tên đăng nhập và số điện thoại (cho chức năng quên mật khẩu).
     * @param username Tên đăng nhập.
     * @param phone Số điện thoại.
     * @return Đối tượng NguoiDung nếu tìm thấy, null nếu không.
     */
    public NguoiDung findNguoiDungByUsernameAndPhone(String username, String phone) {
        NguoiDung user = null;
        // Lấy cả mật khẩu (dạng hash) để tạo đối tượng NguoiDung đầy đủ nếu cần
        String sql = "SELECT tenDangNhap, matKhau, laAdmin, soDienThoai FROM NguoiDung WHERE tenDangNhap = ? AND soDienThoai = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnector.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, phone); // Giả sử số điện thoại lưu dạng String
            rs = pstmt.executeQuery();
            if (rs.next()) {
                // Lấy thông tin từ DB
                String dbUsername = rs.getString("tenDangNhap");
                String hashedPasswordFromDB = rs.getString("matKhau");
                boolean isAdmin = rs.getBoolean("laAdmin");
                String dbPhone = rs.getString("soDienThoai");
                // Tạo đối tượng NguoiDung, có thể lưu hash password nếu cần dùng sau
                user = new NguoiDung(dbUsername, hashedPasswordFromDB, isAdmin, dbPhone);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tìm người dùng bằng username và phone: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseConnector.closeResource(rs, pstmt, conn);
        }
        return user;
    }
    public int getDiemTichLuy(String username) {
        String sql = "SELECT diemTichLuy FROM NguoiDung WHERE tenDangNhap = ?";
        int diem = 0;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnector.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                diem = rs.getInt("diemTichLuy");
            } else {
                System.err.println("Không tìm thấy người dùng '" + username + "' để lấy điểm tích lũy.");
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy điểm tích lũy cho user '" + username + "': " + e.getMessage());
            e.printStackTrace();
            // Trả về 0 khi có lỗi để tránh lỗi ở tầng trên
        } finally {
            DatabaseConnector.closeResource(rs, pstmt, conn);
        }
        return diem;
    }

    /**
     * Cập nhật điểm tích lũy cho người dùng.
     * @param username Tên đăng nhập.
     * @param newDiem Số điểm mới cần cập nhật.
     * @return true nếu cập nhật thành công, false nếu thất bại.
     */
    public boolean updateDiemTichLuy(String username, int newDiem) {
        // Đảm bảo điểm không âm trước khi cập nhật vào DB
        if (newDiem < 0) {
            System.err.println("Cảnh báo: Điểm tích lũy không thể âm. Đặt lại về 0 cho user '" + username + "'.");
            newDiem = 0;
        }

        String sql = "UPDATE NguoiDung SET diemTichLuy = ? WHERE tenDangNhap = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DatabaseConnector.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, newDiem);
            pstmt.setString(2, username);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected == 0) {
                System.err.println("Không tìm thấy người dùng '" + username + "' để cập nhật điểm tích lũy.");
            }
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật điểm tích lũy cho user '" + username + "': " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            DatabaseConnector.closeStatement(pstmt);
            DatabaseConnector.closeConnection(conn);
        }
    }

}
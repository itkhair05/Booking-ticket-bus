package util; // Hoặc package bạn đã tạo

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnector {

    // --- !!! THAY ĐỔI CÁC THÔNG SỐ NÀY CHO PHÙ HỢP !!! ---
    private static final String SERVER_NAME = "localhost"; // Hoặc tên server/IP của bạn
    private static final String PORT_NUMBER = "1433";      // Cổng mặc định của SQL Server
    private static final String DATABASE_NAME = "QuanLyVeXe";
    private static final String DB_USER = "sa"; // Username đăng nhập SQL Server
    private static final String DB_PASSWORD = "123"; // Password đăng nhập SQL Server
    // --- --- --- --- --- --- --- --- --- --- --- --- ---

    // Chuỗi kết nối JDBC
    // encrypt=true và trustServerCertificate=true thường cần thiết cho các phiên bản driver mới
    // Nếu kết nối thất bại, thử thay đổi các tham số này hoặc kiểm tra cấu hình SQL Server
    private static final String CONNECTION_STRING = String.format(
            "jdbc:sqlserver://%s:%s;databaseName=%s;user=%s;password=%s;encrypt=true;trustServerCertificate=true;",
            SERVER_NAME, PORT_NUMBER, DATABASE_NAME, DB_USER, DB_PASSWORD);

    public static Connection getConnection() throws SQLException {
        try {
            // Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver"); // Không cần thiết với JDBC 4.0+
            return DriverManager.getConnection(CONNECTION_STRING);
        } catch (SQLException e) {
            System.err.println("Lỗi kết nối Database: " + e.getMessage());
            // Có thể throw một exception tùy chỉnh hoặc ghi log chi tiết hơn
            throw e;
        }
    }

    // Phương thức tiện ích để đóng các tài nguyên JDBC
    public static void closeResource(ResultSet rs, Statement stmt, Connection conn) {
        closeResultSet(rs);
        closeStatement(stmt);
        closeConnection(conn);
    }

    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void closeStatement(Statement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void closeResultSet(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
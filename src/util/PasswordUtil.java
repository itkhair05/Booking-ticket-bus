package util;

import org.mindrot.jbcrypt.BCrypt; // Import thư viện jBCrypt

public class PasswordUtil {

    public static String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.isEmpty()) {
            return null;
        }

        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(12));
    }


    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null || plainPassword.isEmpty() || hashedPassword.isEmpty()) {
            return false;
        }
        try {
            // BCrypt.checkpw sẽ tự động trích xuất salt từ hashedPassword để so sánh
            return BCrypt.checkpw(plainPassword, hashedPassword);
        } catch (IllegalArgumentException e) {
            // Xảy ra nếu hashedPassword không phải là định dạng BCrypt hợp lệ
            System.err.println("Lỗi khi kiểm tra mật khẩu: định dạng hash không hợp lệ. " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("Lỗi không xác định khi kiểm tra mật khẩu: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
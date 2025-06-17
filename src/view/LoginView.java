package view;

import controller.AdminController; // Cần cho khởi tạo AdminView
import controller.UserController;   // Cần cho khởi tạo UserView
import dao.NguoiDungDAO;
import model.NguoiDung;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
// import java.util.ArrayList; // Không cần nữa

public class LoginView extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    // private ArrayList<NguoiDung> danhSachNguoiDung; // Không dùng nữa
    // private ArrayList<ChuyenXe> danhSachChuyenXe;   // Không dùng nữa
    // private ArrayList<DatVe> danhSachDatVe;     // Không dùng nữa
    private NguoiDungDAO nguoiDungDAO; // Sử dụng DAO

    // Màu sắc và Font chữ
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private final Color SECONDARY_COLOR = new Color(52, 152, 219);
    private final Color BACKGROUND_COLOR = new Color(236, 240, 241);
    private final Color TEXT_COLOR = new Color(44, 62, 80);
    private final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 20);
    private final Font NORMAL_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private final Font FOOTER_FONT = new Font("Segoe UI", Font.ITALIC, 12);

    // Constructor mới không cần tham số danh sách
    public LoginView(/* ArrayList<NguoiDung> danhSachNguoiDung, ArrayList<ChuyenXe> danhSachChuyenXe, ArrayList<DatVe> danhSachDatVe */) {
        // Xóa các dòng gán ArrayList cũ
        this.nguoiDungDAO = new NguoiDungDAO(); // Khởi tạo DAO ở đây
        initUI();
    }

    private void initUI() {
        setTitle("Đăng Nhập Hệ Thống");
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BACKGROUND_COLOR);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel formPanel = createFormPanel();
        mainPanel.add(formPanel, BorderLayout.CENTER);

        JPanel footerPanel = createFooterPanel();
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("ĐĂNG NHẬP HỆ THỐNG", JLabel.CENTER);
        titleLabel.setFont(HEADER_FONT);
        titleLabel.setForeground(Color.WHITE);

        JLabel subtitleLabel = new JLabel("CÔNG TY VẬN TẢI F4 MIỀN TÂY", JLabel.CENTER);
        subtitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        subtitleLabel.setForeground(Color.WHITE);

        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(subtitleLabel, BorderLayout.SOUTH);
        return headerPanel;
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(BACKGROUND_COLOR);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        formPanel.setAlignmentX(Component.CENTER_ALIGNMENT); // Căn giữa các component con

        // Panel Username
        JPanel usernamePanel = new JPanel(new BorderLayout(5, 5)); // Giảm khoảng cách
        usernamePanel.setBackground(BACKGROUND_COLOR);
        usernamePanel.setMaximumSize(new Dimension(300, 60)); // Giới hạn chiều rộng
        JLabel usernameLabel = new JLabel("Tên đăng nhập:");
        usernameLabel.setFont(NORMAL_FONT);
        usernameLabel.setForeground(TEXT_COLOR);
        txtUsername = new JTextField();
        txtUsername.setFont(NORMAL_FONT);
        txtUsername.setMargin(new Insets(8, 8, 8, 8)); // Giảm padding
        usernamePanel.add(usernameLabel, BorderLayout.NORTH);
        usernamePanel.add(txtUsername, BorderLayout.CENTER);

        // Panel Password
        JPanel passwordPanel = new JPanel(new BorderLayout(5, 5));
        passwordPanel.setBackground(BACKGROUND_COLOR);
        passwordPanel.setMaximumSize(new Dimension(300, 60));
        JLabel passwordLabel = new JLabel("Mật khẩu:");
        passwordLabel.setFont(NORMAL_FONT);
        passwordLabel.setForeground(TEXT_COLOR);
        txtPassword = new JPasswordField();
        txtPassword.setFont(NORMAL_FONT);
        txtPassword.setMargin(new Insets(8, 8, 8, 8));
        // Thêm action listener cho password field để nhấn Enter cũng đăng nhập
        txtPassword.addActionListener(this::login); // Gọi hàm login khi nhấn Enter
        passwordPanel.add(passwordLabel, BorderLayout.NORTH);
        passwordPanel.add(txtPassword, BorderLayout.CENTER);

        // Panel Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10)); // Dùng FlowLayout
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.setMaximumSize(new Dimension(350, 50)); // Điều chỉnh kích thước panel nút

        JButton btnLogin = createStyledButton("Đăng Nhập", SECONDARY_COLOR);
        JButton btnRegister = createStyledButton("Đăng Ký", new Color(46, 204, 113));
        JButton btnForgotPassword = createStyledButton("Quên MK", new Color(231, 76, 60));

        // Giảm kích thước nút một chút nếu cần
        Dimension buttonSize = new Dimension(100, 35);
        btnLogin.setPreferredSize(buttonSize);
        btnRegister.setPreferredSize(buttonSize);
        btnForgotPassword.setPreferredSize(buttonSize);


        btnLogin.addActionListener(this::login);
        btnRegister.addActionListener(this::register);
        btnForgotPassword.addActionListener(this::forgotPassword);

        buttonPanel.add(btnLogin);
        buttonPanel.add(btnRegister);
        buttonPanel.add(btnForgotPassword);

        // Thêm các panel vào formPanel với khoảng cách
        formPanel.add(usernamePanel);
        formPanel.add(Box.createVerticalStrut(15)); // Khoảng cách dọc
        formPanel.add(passwordPanel);
        formPanel.add(Box.createVerticalStrut(25));
        formPanel.add(buttonPanel);

        return formPanel;
    }

    // Hàm tạo nút (giống các View khác)
    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(NORMAL_FONT);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        // button.setMargin(new Insets(8, 8, 8, 8)); // Bỏ margin nếu đã set preferred size
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(color.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
            }
        });
        return button;
    }

    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(SECONDARY_COLOR);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String currentDate = dateFormat.format(new Date());

        JLabel copyrightLabel = new JLabel("© Công ty Vận Tải F4 Miền Tây - " + currentDate, JLabel.CENTER);
        copyrightLabel.setFont(FOOTER_FONT);
        copyrightLabel.setForeground(Color.WHITE);

        footerPanel.add(copyrightLabel, BorderLayout.CENTER);
        return footerPanel;
    }

    // Phương thức đăng nhập (ĐÃ SỬA ĐỔI dùng DAO)
    private void login(ActionEvent e) {
        String username = txtUsername.getText().trim(); // Trim() để loại bỏ khoảng trắng thừa
        String password = new String(txtPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ Tên đăng nhập và Mật khẩu!", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Gọi DAO để kiểm tra
        NguoiDung nguoiDung = nguoiDungDAO.findNguoiDungByCredentials(username, password);

        if (nguoiDung != null) {
            // Đăng nhập thành công
            setVisible(false); // Ẩn cửa sổ Login
            dispose(); // Giải phóng tài nguyên cửa sổ Login

            // Mở View tương ứng
            NguoiDung finalNguoiDung = nguoiDung; // Tạo biến final để dùng trong lambda
            SwingUtilities.invokeLater(() -> {
                if (finalNguoiDung.isLaAdmin()) {
                    new AdminView().setVisible(true); // Khởi tạo AdminView mới (đã sửa constructor)
                } else {
                    new UserView(finalNguoiDung).setVisible(true); // Truyền NguoiDung đã đăng nhập vào UserView
                }
            });

        } else {
            // Đăng nhập thất bại
            JOptionPane.showMessageDialog(this, "Tên đăng nhập hoặc Mật khẩu không đúng!", "Đăng nhập thất bại", JOptionPane.ERROR_MESSAGE);
            txtPassword.setText(""); // Xóa trường mật khẩu
            txtUsername.requestFocus(); // Focus lại vào trường username
        }
    }

    // Phương thức đăng ký (SỬA ĐỔI dùng DAO)
    private void register(ActionEvent e) {
        JPanel registerPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        registerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JPasswordField confirmPasswordField = new JPasswordField();
        JTextField phoneField = new JTextField();

        registerPanel.add(new JLabel("Tên đăng nhập mới:")); registerPanel.add(usernameField);
        registerPanel.add(new JLabel("Mật khẩu (>=6 ký tự):")); registerPanel.add(passwordField);
        registerPanel.add(new JLabel("Xác nhận mật khẩu:")); registerPanel.add(confirmPasswordField);
        registerPanel.add(new JLabel("Số điện thoại (10-11 số):")); registerPanel.add(phoneField);

        int option = JOptionPane.showConfirmDialog(this, registerPanel, "Đăng Ký Tài Khoản Mới", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (option == JOptionPane.OK_OPTION) {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()); // Lấy pass dạng text
            String confirmPassword = new String(confirmPasswordField.getPassword());
            String phone = phoneField.getText().trim();

            // --- THÊM/CẢI THIỆN VALIDATION ---
            // 1. Kiểm tra trống
            if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || phone.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin đăng ký!", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
                return; // Dừng lại
            }

            // 2. Kiểm tra trùng khớp mật khẩu
            if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(this, "Mật khẩu xác nhận không khớp!", "Lỗi Mật Khẩu", JOptionPane.ERROR_MESSAGE);
                confirmPasswordField.requestFocus(); // Focus vào ô xác nhận
                return;
            }

            // 3. Kiểm tra độ dài mật khẩu (ví dụ: ít nhất 6 ký tự)
            if (password.length() < 6) {
                JOptionPane.showMessageDialog(this, "Mật khẩu phải có ít nhất 6 ký tự!", "Mật khẩu yếu", JOptionPane.WARNING_MESSAGE);
                passwordField.requestFocus();
                return;
            }
            // (Có thể thêm kiểm tra độ mạnh mật khẩu phức tạp hơn nếu muốn)

            // 4. Kiểm tra định dạng số điện thoại cơ bản (ví dụ: 10 hoặc 11 số, bắt đầu bằng 0)
            // Regex: Bắt đầu bằng 0 (^0), theo sau là 9 hoặc 10 chữ số (\\d{9,10}$), kết thúc chuỗi ($)
            if (!phone.matches("^0\\d{9,10}$")) {
                JOptionPane.showMessageDialog(this, "Số điện thoại không hợp lệ.\nVui lòng nhập 10 hoặc 11 chữ số, bắt đầu bằng 0.", "Lỗi Số Điện Thoại", JOptionPane.ERROR_MESSAGE);
                phoneField.requestFocus();
                return;
            }

            // 5. Kiểm tra tên đăng nhập tồn tại (đã có từ trước, gọi DAO)
            if (nguoiDungDAO.checkUsernameExists(username)) {
                JOptionPane.showMessageDialog(this, "Tên đăng nhập '" + username + "' đã được sử dụng. Vui lòng chọn tên khác.", "Trùng tên đăng nhập", JOptionPane.WARNING_MESSAGE);
                usernameField.requestFocus();
                return;
            }
            // --- KẾT THÚC VALIDATION ---


            // Nếu mọi kiểm tra OK
            // Nhớ mã hóa mật khẩu trước khi tạo NguoiDung (đã làm trong DAO)
            NguoiDung newUser = new NguoiDung(username, password, false, phone);

            // Gọi DAO để thêm người dùng
            boolean success = nguoiDungDAO.addNguoiDung(newUser);

            if(success) {
                JOptionPane.showMessageDialog(this, "Đăng ký tài khoản thành công!\nVui lòng đăng nhập bằng tài khoản vừa tạo.", "Đăng ký thành công", JOptionPane.INFORMATION_MESSAGE);
                // ... (code điền sẵn username, password để đăng nhập) ...
                txtUsername.setText(username);
                txtPassword.setText("");
                txtPassword.requestFocus();
            } else {
                JOptionPane.showMessageDialog(this, "Đăng ký thất bại! Có lỗi xảy ra khi lưu vào CSDL hoặc tên đăng nhập đã tồn tại (kiểm tra lại).", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Phương thức quên mật khẩu (SỬA ĐỔI dùng DAO)
    private void forgotPassword(ActionEvent e) {
        JPanel forgotPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        forgotPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextField usernameField = new JTextField();
        JTextField phoneField = new JTextField();
        JPasswordField newPasswordField = new JPasswordField();
        JPasswordField confirmPasswordField = new JPasswordField();

        forgotPanel.add(new JLabel("Tên đăng nhập:")); forgotPanel.add(usernameField);
        forgotPanel.add(new JLabel("Số điện thoại đã đăng ký:")); forgotPanel.add(phoneField);
        forgotPanel.add(new JLabel("Mật khẩu mới (>=6 ký tự):")); forgotPanel.add(newPasswordField);
        forgotPanel.add(new JLabel("Xác nhận mật khẩu mới:")); forgotPanel.add(confirmPasswordField);

        int option = JOptionPane.showConfirmDialog(this, forgotPanel, "Đặt Lại Mật Khẩu", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (option == JOptionPane.OK_OPTION) {
            String username = usernameField.getText().trim();
            String phone = phoneField.getText().trim();
            String newPassword = new String(newPasswordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());

            // --- THÊM/CẢI THIỆN VALIDATION ---
            // 1. Kiểm tra trống
            if (username.isEmpty() || phone.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // 2. Kiểm tra trùng khớp mật khẩu mới
            if (!newPassword.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(this, "Mật khẩu mới xác nhận không khớp!", "Lỗi Mật Khẩu", JOptionPane.ERROR_MESSAGE);
                confirmPasswordField.requestFocus();
                return;
            }

            // 3. Kiểm tra độ dài mật khẩu mới
            if (newPassword.length() < 6) { // Nên đồng nhất với kiểm tra lúc đăng ký
                JOptionPane.showMessageDialog(this, "Mật khẩu mới phải có ít nhất 6 ký tự!", "Mật khẩu yếu", JOptionPane.WARNING_MESSAGE);
                newPasswordField.requestFocus();
                return;
            }

            // 4. Kiểm tra định dạng số điện thoại
            if (!phone.matches("^0\\d{9,10}$")) {
                JOptionPane.showMessageDialog(this, "Số điện thoại không hợp lệ.\nVui lòng nhập 10 hoặc 11 chữ số, bắt đầu bằng 0.", "Lỗi Số Điện Thoại", JOptionPane.ERROR_MESSAGE);
                phoneField.requestFocus();
                return;
            }
            // --- KẾT THÚC VALIDATION ---


            // Xác thực người dùng qua DAO bằng username và phone (đã có)
            NguoiDung nguoiDung = nguoiDungDAO.findNguoiDungByUsernameAndPhone(username, phone);

            if (nguoiDung == null) {
                JOptionPane.showMessageDialog(this, "Thông tin Tên đăng nhập hoặc Số điện thoại không chính xác!\nKhông thể đặt lại mật khẩu.", "Xác thực thất bại", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Cập nhật mật khẩu mới qua DAO (nhớ mã hóa trong DAO)
            boolean success = nguoiDungDAO.updatePassword(username, newPassword);

            if(success) {
                JOptionPane.showMessageDialog(this, "Đặt lại mật khẩu thành công!\nVui lòng đăng nhập bằng mật khẩu mới.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                txtUsername.setText(username);
                txtPassword.setText("");
                txtPassword.requestFocus();
            } else {
                JOptionPane.showMessageDialog(this, "Đặt lại mật khẩu thất bại! Có lỗi xảy ra khi cập nhật CSDL.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
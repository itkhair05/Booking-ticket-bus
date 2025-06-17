package view;

import controller.UserController;
import model.ChuyenXe;
import model.DatVe;
import model.NguoiDung;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Set;
import java.util.HashSet;
import view.ChonGheDialog;

public class UserView extends JFrame {
    private UserController userController;
    private JTable tableChuyenXe;
    private DefaultTableModel model;
    private JLabel diemTichLuyLabel;
    private boolean uuDaiDaApDung = false;

    private final Color PRIMARY_COLOR = new Color(26, 188, 156);
    private final Color SECONDARY_COLOR = new Color(22, 160, 133);
    private final Color BACKGROUND_COLOR = new Color(236, 240, 241);
    private final Color TEXT_COLOR = new Color(44, 62, 80);
    private final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 24);
    private final Font NORMAL_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private final Font FOOTER_FONT = new Font("Segoe UI", Font.ITALIC, 12);

    public UserView(NguoiDung nguoiDungDangNhap) {
        this.userController = new UserController(nguoiDungDangNhap);
        initUI();
        capNhatDanhSachChuyen();
        updateDiemTichLuy();
    }

    private void initUI() {
        setTitle("Hệ Thống Đặt Vé Xe Khách");
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BACKGROUND_COLOR);
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        JPanel centerPanel = createCenterPanel();
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        JPanel buttonPanel = createButtonPanel();
        mainPanel.add(buttonPanel, BorderLayout.EAST);
        JPanel footerPanel = createFooterPanel();
        mainPanel.add(footerPanel, BorderLayout.SOUTH);
        add(mainPanel);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        JLabel titleLabel = new JLabel("HỆ THỐNG ĐẶT VÉ XE KHÁCH", JLabel.CENTER);
        titleLabel.setFont(HEADER_FONT);
        titleLabel.setForeground(Color.WHITE);
        String userName = userController.getNguoiDung().getTenDangNhap();
        JLabel userLabel = new JLabel("Xin chào, " + userName, JLabel.RIGHT);
        userLabel.setFont(NORMAL_FONT);
        userLabel.setForeground(Color.WHITE);
        diemTichLuyLabel = new JLabel("Điểm tích lũy: ?", JLabel.LEFT);
        diemTichLuyLabel.setFont(NORMAL_FONT);
        diemTichLuyLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(userLabel, BorderLayout.EAST);
        headerPanel.add(diemTichLuyLabel, BorderLayout.WEST);
        return headerPanel;
    }

    private JPanel createCenterPanel() {
        JPanel centerPanel = new JPanel(new BorderLayout(0, 5));
        centerPanel.setBackground(BACKGROUND_COLOR);
        JPanel searchPanel = createSearchPanel();
        centerPanel.add(searchPanel, BorderLayout.NORTH);
        String[] columnNames = {"ID", "Điểm Khởi Hành", "Điểm Đến", "Ngày Giờ", "Tài Xế", "Ghế Trống", "Giá Vé", "Biển Số", "Loại Xe"};
        model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableChuyenXe = new JTable(model);
        tableChuyenXe.setRowHeight(30);
        tableChuyenXe.setFont(NORMAL_FONT);
        tableChuyenXe.setGridColor(new Color(189, 195, 199));
        tableChuyenXe.setShowVerticalLines(true);
        tableChuyenXe.setSelectionBackground(new Color(26, 188, 156, 100));
        tableChuyenXe.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JTableHeader header = tableChuyenXe.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(SECONDARY_COLOR);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(100, 40));
        header.setReorderingAllowed(false);
        JScrollPane scrollPane = new JScrollPane(tableChuyenXe);
        scrollPane.setBorder(BorderFactory.createEtchedBorder());
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        return centerPanel;
    }

    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchPanel.setBackground(BACKGROUND_COLOR);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 10, 0));
        JLabel lblDiemDi = new JLabel("Điểm đi:");
        lblDiemDi.setFont(NORMAL_FONT);
        JTextField txtDiemDi = new JTextField(15);
        txtDiemDi.setFont(NORMAL_FONT);
        JLabel lblDiemDen = new JLabel("Điểm đến:");
        lblDiemDen.setFont(NORMAL_FONT);
        JTextField txtDiemDen = new JTextField(15);
        txtDiemDen.setFont(NORMAL_FONT);
        JButton btnTimKiem = createStyledButton("Tìm kiếm", SECONDARY_COLOR);
        JButton btnTaiLai = createStyledButton("Tải lại", new Color(149, 165, 166));
        Dimension btnSize = new Dimension(100, 30);
        btnTimKiem.setPreferredSize(btnSize);
        btnTaiLai.setPreferredSize(btnSize);
        btnTimKiem.addActionListener(e -> timKiemChuyenXe(txtDiemDi.getText(), txtDiemDen.getText()));
        btnTaiLai.addActionListener(e -> {
            txtDiemDi.setText("");
            txtDiemDen.setText("");
            capNhatDanhSachChuyen();
        });
        searchPanel.add(lblDiemDi);
        searchPanel.add(txtDiemDi);
        searchPanel.add(Box.createHorizontalStrut(10));
        searchPanel.add(lblDiemDen);
        searchPanel.add(txtDiemDen);
        searchPanel.add(Box.createHorizontalStrut(10));
        searchPanel.add(btnTimKiem);
        searchPanel.add(btnTaiLai);
        return searchPanel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        Dimension buttonSize = new Dimension(160, 45);
        Font buttonFont = new Font("Segoe UI", Font.PLAIN, 14);
        JButton btnDatVe = createStyledButton("Đặt Vé", PRIMARY_COLOR);
        JButton btnXemVe = createStyledButton("Xem Vé Của Tôi", new Color(52, 152, 219));
        JButton btnHuyVe = createStyledButton("Hủy Vé Đã Đặt", new Color(231, 76, 60));
        JButton btnThanhToan = createStyledButton("Thanh Toán Vé", new Color(243, 156, 18));
        JButton btnUuDai = createStyledButton("Điểm Thưởng & Ưu Đãi", new Color(230, 126, 34));
        JButton btnDangXuat = createStyledButton("Đăng Xuất", new Color(127, 140, 141));
        List<JButton> buttons = Arrays.asList(btnDatVe, btnXemVe, btnHuyVe, btnThanhToan, btnUuDai, btnDangXuat);
        for (JButton btn : buttons) {
            btn.setPreferredSize(buttonSize);
            btn.setMaximumSize(buttonSize);
            btn.setFont(buttonFont);
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        }
        btnDatVe.addActionListener(this::datVe);
        btnXemVe.addActionListener(this::xemVeDaDat);
        btnHuyVe.addActionListener(this::huyVe);
        btnThanhToan.addActionListener(this::thanhToanVe);
        btnUuDai.addActionListener(this::xemUuDai);
        btnDangXuat.addActionListener(this::dangXuat);
        buttonPanel.add(Box.createVerticalStrut(10));
        buttonPanel.add(btnDatVe);
        buttonPanel.add(Box.createVerticalStrut(10));
        buttonPanel.add(btnXemVe);
        buttonPanel.add(Box.createVerticalStrut(10));
        buttonPanel.add(btnHuyVe);
        buttonPanel.add(Box.createVerticalStrut(10));
        buttonPanel.add(btnThanhToan);
        buttonPanel.add(Box.createVerticalStrut(10));
        buttonPanel.add(btnUuDai);
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(btnDangXuat);
        buttonPanel.add(Box.createVerticalStrut(10));
        return buttonPanel;
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
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
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String currentDate = dateFormat.format(new Date());
        JLabel copyrightLabel = new JLabel("© Công ty Vận Tải F4 Miền Tây - " + currentDate, JLabel.CENTER);
        copyrightLabel.setFont(FOOTER_FONT);
        copyrightLabel.setForeground(Color.WHITE);
        footerPanel.add(copyrightLabel, BorderLayout.CENTER);
        return footerPanel;
    }

    public void capNhatDanhSachChuyen() {
        try {
            List<ChuyenXe> danhSach = userController.getDanhSachChuyenXe();
            hienThiDanhSachChuyen(danhSach);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải danh sách chuyến xe: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void timKiemChuyenXe(String diemDi, String diemDen) {
        String diemDiTrim = diemDi.trim();
        String diemDenTrim = diemDen.trim();
        if (diemDiTrim.isEmpty() && diemDenTrim.isEmpty()) {
            capNhatDanhSachChuyen();
            return;
        }
        try {
            List<ChuyenXe> ketQua = userController.timKiemChuyenXe(diemDiTrim, diemDenTrim);
            if (ketQua.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy chuyến xe phù hợp.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }
            hienThiDanhSachChuyen(ketQua);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tìm kiếm chuyến xe: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void hienThiDanhSachChuyen(List<ChuyenXe> danhSach) {
        model.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        if (danhSach != null) {
            for (ChuyenXe chuyen : danhSach) {
                if (chuyen == null) continue;
                String ngayGioStr = (chuyen.getNgayKhoiHanh() != null) ? sdf.format(chuyen.getNgayKhoiHanh()) : "N/A";
                model.addRow(new Object[]{
                        chuyen.getId(),
                        chuyen.getDiemKhoiHanh(),
                        chuyen.getDiemDen(),
                        ngayGioStr,
                        chuyen.getTaiXe() != null ? chuyen.getTaiXe() : "Chưa phân công",
                        chuyen.getSoGheTrong(),
                        String.format("%,.0f VNĐ", chuyen.getGiaVe()),
                        chuyen.getBienSo(),
                        chuyen.getLoaiXe()
                });
            }
        }
    }

    private void showSeatMap() {
        int selectedRow = tableChuyenXe.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }
        int idChuyen = (int) model.getValueAt(selectedRow, 0);

        ChuyenXe chuyenXeInfo = userController.getDanhSachChuyenXe().stream()
                .filter(c -> c.getId() == idChuyen)
                .findFirst().orElse(null);
        String titleInfo = (chuyenXeInfo != null) ?
                " (" + chuyenXeInfo.getDiemKhoiHanh() + " -> " + chuyenXeInfo.getDiemDen() + ")" : "";
        int soGheTrongHienThi = (chuyenXeInfo != null) ? chuyenXeInfo.getSoGheTrong() : -1;

        List<String> gheDaDatMoiNhat = userController.getGheDaDatHienTai(idChuyen);

        JDialog seatMapDialog = new JDialog(this, "Sơ đồ ghế - Chuyến " + idChuyen + titleInfo, true);
        seatMapDialog.setSize(1000, 400);
        seatMapDialog.setLocationRelativeTo(this);
        seatMapDialog.setLayout(new BorderLayout(15, 15));
        seatMapDialog.getContentPane().setBackground(BACKGROUND_COLOR);

        JLabel titleLabel = new JLabel("Sơ đồ ghế chuyến xe #" + idChuyen + titleInfo + " - Ghế trống (tham khảo): " + soGheTrongHienThi, JLabel.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(SECONDARY_COLOR);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        seatMapDialog.add(titleLabel, BorderLayout.NORTH);

        JPanel seatPanel = new JPanel(new GridLayout(2, 21, 8, 8));
        seatPanel.setBackground(BACKGROUND_COLOR);
        seatPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        seatPanel.add(createSeatLabel("A"));
        for (int i = 1; i <= 20; i++) {
            String seatLabel = "A" + i;
            seatPanel.add(createSeatButtonForUserView(seatLabel, gheDaDatMoiNhat));
        }
        seatPanel.add(createSeatLabel("B"));
        for (int i = 1; i <= 20; i++) {
            String seatLabel = "B" + i;
            seatPanel.add(createSeatButtonForUserView(seatLabel, gheDaDatMoiNhat));
        }

        seatMapDialog.add(seatPanel, BorderLayout.CENTER);
        seatMapDialog.add(createLegendPanel(), BorderLayout.SOUTH);
        seatMapDialog.setVisible(true);
    }

    private JButton createSeatButtonForUserView(String seatLabel, List<String> gheDaDat) {
        JButton seatButton = new JButton(seatLabel);
        seatButton.setPreferredSize(new Dimension(50, 40));
        seatButton.setFont(new Font("Segoe UI", Font.BOLD, 11));
        seatButton.setMargin(new Insets(1, 1, 1, 1));
        seatButton.setFocusPainted(false);
        seatButton.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        if (gheDaDat.contains(seatLabel)) {
            seatButton.setBackground(new Color(231, 76, 60));
            seatButton.setForeground(Color.WHITE);
            seatButton.setEnabled(false);
            seatButton.setToolTipText("Ghế đã đặt");
        } else {
            seatButton.setBackground(new Color(46, 204, 113));
            seatButton.setForeground(Color.WHITE);
            seatButton.setEnabled(false);
            seatButton.setToolTipText("Ghế trống");
        }
        return seatButton;
    }

    private JLabel createSeatLabel(String label) {
        JLabel seatLabel = new JLabel(label, JLabel.CENTER);
        seatLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        return seatLabel;
    }

    private JPanel createLegendPanel() {
        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        legendPanel.setBackground(BACKGROUND_COLOR);
        legendPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        legendPanel.add(createLegendItem(new Color(46, 204, 113), "Ghế trống"));
        legendPanel.add(createLegendItem(new Color(231, 76, 60), "Ghế đã đặt"));
        return legendPanel;
    }

    private JPanel createLegendItem(Color color, String text) {
        JPanel itemPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        itemPanel.setOpaque(false);
        itemPanel.setBackground(BACKGROUND_COLOR);
        JPanel colorBox = new JPanel();
        colorBox.setBackground(color);
        colorBox.setPreferredSize(new Dimension(15, 15));
        colorBox.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        itemPanel.add(colorBox);
        itemPanel.add(label);
        return itemPanel;
    }

    private void datVe(ActionEvent e) {
        int selectedRow = tableChuyenXe.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một chuyến xe để đặt vé!", "Chưa chọn chuyến", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int idChuyen = (int) tableChuyenXe.getValueAt(selectedRow, 0);
        ChuyenXe chuyenXe = userController.getChuyenXeById(idChuyen); // Đã thêm phương thức này vào UserController
        if (chuyenXe == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy chuyến xe!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int soGheTrong = chuyenXe.getSoGheTrong();
        if (soGheTrong <= 0) {
            JOptionPane.showMessageDialog(this, "Chuyến xe đã hết ghế trống!", "Hết ghế", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Tạo giao diện đặt vé với GridBagLayout
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField soVeField = new JTextField("1");
        JTextField soDienThoaiField = new JTextField();
        JCheckBox dichVuDuaDonCheckBox = new JCheckBox("Dịch vụ đưa đón");

        // Số Vé
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Số Vé (*):"), gbc);
        gbc.gridx = 1;
        panel.add(soVeField, gbc);

        // Số Điện Thoại
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Số Điện Thoại (*):"), gbc);
        gbc.gridx = 1;
        panel.add(soDienThoaiField, gbc);

        // Dịch Vụ Đưa Đón
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Dịch Vụ Đưa Đón:"), gbc);
        gbc.gridx = 1;
        panel.add(dichVuDuaDonCheckBox, gbc);

        int option = JOptionPane.showConfirmDialog(this, panel, "Đặt Vé", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (option != JOptionPane.OK_OPTION) return;

        try {
            String soVeStr = soVeField.getText().trim();
            String soDienThoai = soDienThoaiField.getText().trim();
            boolean duaDon = dichVuDuaDonCheckBox.isSelected();
            String diaChiDon = null;

            if (soVeStr.isEmpty() || soDienThoai.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int soVe = Integer.parseInt(soVeStr);
            if (soVe <= 0) {
                JOptionPane.showMessageDialog(this, "Số vé phải lớn hơn 0!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                soVeField.requestFocus();
                return;
            }
            if (soVe > soGheTrong) {
                JOptionPane.showMessageDialog(this, "Số vé vượt quá số ghế trống còn lại (" + soGheTrong + ")!", "Hết ghế", JOptionPane.WARNING_MESSAGE);
                soVeField.requestFocus();
                return;
            }

            if (!soDienThoai.matches("0[0-9]{9,10}")) {
                JOptionPane.showMessageDialog(this, "Số điện thoại phải có 10-11 chữ số và bắt đầu bằng 0!", "Lỗi", JOptionPane.WARNING_MESSAGE);
                soDienThoaiField.requestFocus();
                return;
            }

            // Nếu người dùng chọn dịch vụ đưa đón, hiển thị dialog nhập địa chỉ
            if (duaDon) {
                diaChiDon = showDuaDonDialog(idChuyen);
                if (diaChiDon == null) {
                    JOptionPane.showMessageDialog(this, "Bạn chưa nhập địa chỉ đón! Đặt vé bị hủy.", "Thông báo", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }

            System.out.println("[VIEW DEBUG] datVe - Chuẩn bị gọi chonGheDialog...");
            List<String> gheDaDat = userController.getGheDaDatHienTai(idChuyen); // Sửa tên phương thức
            ChonGheDialog dialog = new ChonGheDialog(this, soVe, new HashSet<>(gheDaDat));
            List<String> gheDaChon = dialog.showDialog();
            System.out.println("[VIEW DEBUG] datVe - Kết quả chonGheDialog: " + gheDaChon);
            if (gheDaChon == null || gheDaChon.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Bạn chưa chọn ghế! Đặt vé bị hủy.", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            System.out.println("[VIEW DEBUG] datVe - Chuẩn bị gọi userController.datVe...");
            DatVe veMoi = userController.datVe(idChuyen, soVe, gheDaChon, soDienThoai, duaDon, diaChiDon);
            System.out.println("[VIEW DEBUG] datVe - Gọi userController.datVe xong.");
            if (veMoi != null) {
                System.out.println("[VIEW DEBUG] datVe - Đặt vé thành công!");
                ChuyenXe chuyenXeMoi = userController.getDanhSachChuyenXe().stream().filter(c -> c.getId() == idChuyen).findFirst().orElse(null);
                double totalCost = (chuyenXeMoi != null) ? soVe * chuyenXeMoi.getGiaVe() : 0;
                List<String> gheDaChonFromDB = userController.getGheCuaVe(veMoi.getId());
                String message = "ĐẶT VÉ THÀNH CÔNG!\n\n" +
                        "Mã vé: " + veMoi.getMaVe() + "\nChuyến xe ID: " + idChuyen + "\nSố vé: " + soVe + "\nVị trí ghế: " + String.join(", ", gheDaChonFromDB) + "\nTổng tiền: " + String.format("%,.0f VNĐ", totalCost) + "\nTrạng thái: Chưa thanh toán\n" +
                        (duaDon ? ("Dịch vụ đưa đón: Có\nĐịa chỉ đón: " + diaChiDon + "\nLưu ý: Có mặt trước 15 phút\n") : "Dịch vụ đưa đón: Không có\n") +
                        "\nBạn nhận được " + (soVe * 10) + " điểm tích lũy!\nVui lòng vào mục 'Thanh Toán' để hoàn tất.";

                JOptionPane.showMessageDialog(this, message, "Đặt Vé Thành Công", JOptionPane.INFORMATION_MESSAGE);
                capNhatDanhSachChuyen();
                updateDiemTichLuy();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Số vé phải là số!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            soVeField.requestFocus();
        } catch (IllegalStateException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi không mong muốn: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private String showDuaDonDialog(int idChuyen) {
        // Tạo panel chứa trường nhập liệu
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel label = new JLabel("Nhập địa chỉ đón:");
        JTextField diaChiField = new JTextField(20);

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(label, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(diaChiField, gbc);

        // Hiển thị dialog
        int result = JOptionPane.showConfirmDialog(this, panel, "Dịch Vụ Đưa Đón", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String diaChi = diaChiField.getText().trim();
            if (diaChi.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Địa chỉ đón không được để trống!", "Lỗi", JOptionPane.WARNING_MESSAGE);
                return null; // Trả về null nếu địa chỉ trống
            }
            return diaChi; // Trả về địa chỉ nếu hợp lệ
        }
        return null; // Trả về null nếu người dùng hủy
    }

    private void xemVeDaDat(ActionEvent e) {
        List<DatVe> veDaDat = userController.getDanhSachDatVeCuaNguoiDung();
        if (veDaDat == null || veDaDat.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Bạn chưa đặt vé nào!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        StringBuilder sb = new StringBuilder("=== DANH SÁCH VÉ CỦA BẠN ===\nTổng cộng: " + veDaDat.size() + " vé\n");
        sb.append("------------------------------------------\n");
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        List<ChuyenXe> allChuyenXe = userController.getDanhSachChuyenXe();

        for (DatVe ve : veDaDat) {
            ChuyenXe chuyen = allChuyenXe.stream().filter(c -> c.getId() == ve.getIdChuyenXe()).findFirst().orElse(null);
            List<String> gheList = userController.getGheCuaVe(ve.getId());
            String gheStr = (gheList != null && !gheList.isEmpty()) ? String.join(", ", gheList) : "[Chưa có ghế]";

            sb.append("Mã vé: ").append(ve.getMaVe()).append("\n");
            if (chuyen != null) {
                sb.append(String.format("Chuyến %d: %s -> %s\n", chuyen.getId(), chuyen.getDiemKhoiHanh(), chuyen.getDiemDen()));
                sb.append("Thời gian: ").append(chuyen.getNgayKhoiHanh() != null ? sdf.format(chuyen.getNgayKhoiHanh()) : "N/A").append("\n");
                sb.append("Tổng tiền: ").append(String.format("%,.0f VNĐ", ve.getSoVe() * chuyen.getGiaVe())).append("\n");
            } else {
                sb.append("Chuyến xe ID: ").append(ve.getIdChuyenXe()).append(" (Không tìm thấy TT chuyến)\n").append("Tổng tiền: Không xác định\n");
            }
            sb.append("Số vé: ").append(ve.getSoVe()).append("\n");
            sb.append("Vị trí ghế: ").append(gheStr).append("\n");
            sb.append("Trạng thái: ").append(mapTrangThaiToDisplay(ve.getTrangThai())).append("\n");
            sb.append("Dịch vụ đưa đón: ").append(ve.isDichVuDuaDon() ? "Có" : "Không");
            if (ve.isDichVuDuaDon()) {
                sb.append(" | Địa chỉ: ").append(ve.getDiaChiDon());
                sb.append("\n   (Lưu ý: Có mặt trước 15-30 phút)");
            }
            sb.append("\n------------------------------------------\n");
        }
        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        textArea.setCaretPosition(0);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(600, 400));
        JOptionPane.showMessageDialog(this, scrollPane, "Danh Sách Vé Đã Đặt Của Bạn", JOptionPane.INFORMATION_MESSAGE);
    }

    private String mapTrangThaiToDisplay(String trangThai) {
        switch (trangThai.toLowerCase()) {
            case "chuathanhtoan":
                return "Chưa thanh toán";
            case "dathanhtoan":
                return "Đã thanh toán";
            case "dahuy":
                return "Đã hủy";
            default:
                return trangThai;
        }
    }

    private void huyVe(ActionEvent e) {
        List<DatVe> veDaDat = userController.getDanhSachDatVeCuaNguoiDung();
        if (veDaDat == null || veDaDat.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Bạn chưa đặt vé nào!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        List<DatVe> veCoTheHuy = veDaDat.stream().filter(v -> !"DaHuy".equalsIgnoreCase(v.getTrangThai())).collect(Collectors.toList());
        if (veCoTheHuy.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Bạn không có vé nào có thể hủy!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String[] options = new String[veCoTheHuy.size()];
        List<ChuyenXe> allChuyenXe = userController.getDanhSachChuyenXe();
        for (int i = 0; i < veCoTheHuy.size(); i++) {
            DatVe ve = veCoTheHuy.get(i);
            ChuyenXe chuyen = allChuyenXe.stream().filter(c -> c.getId() == ve.getIdChuyenXe()).findFirst().orElse(null);
            String tenChuyen = (chuyen != null) ? (chuyen.getDiemKhoiHanh() + "->" + chuyen.getDiemDen()) : ("Chuyến #" + ve.getIdChuyenXe());
            List<String> gheListHuy = userController.getGheCuaVe(ve.getId());
            String gheStrHuy = (gheListHuy != null && !gheListHuy.isEmpty()) ? String.join(", ", gheListHuy) : "N/A";
            options[i] = String.format("Mã: %s | %s | Ghế: %s | [%s]", ve.getMaVe(), tenChuyen, gheStrHuy, mapTrangThaiToDisplay(ve.getTrangThai()));
        }
        String selected = (String) JOptionPane.showInputDialog(this, "Chọn vé bạn muốn hủy:", "Hủy Vé Đã Đặt", JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        if (selected == null) return;

        DatVe veCanHuy = null;
        try {
            String maVeSelected = selected.split("\\|")[0].split(":")[1].trim();
            veCanHuy = veCoTheHuy.stream().filter(v -> v.getMaVe().equals(maVeSelected)).findFirst().orElse(null);
        } catch (Exception ex) {
            System.err.println("Lỗi khi phân tích mã vé: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, "Lỗi khi chọn vé để hủy: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (veCanHuy != null) {
            int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn hủy vé " + veCanHuy.getMaVe() + "?\n(Hành động này không thể hoàn tác)", "Xác Nhận Hủy Vé", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = userController.huyVe(veCanHuy.getId());
                if (success) {
                    JOptionPane.showMessageDialog(this, "Hủy vé " + veCanHuy.getMaVe() + " thành công!\nĐiểm tích lũy đã được điều chỉnh.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    capNhatDanhSachChuyen();
                    updateDiemTichLuy(); // Cập nhật điểm tích lũy trên giao diện
                } else {
                    JOptionPane.showMessageDialog(this, "Hủy vé thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Không tìm thấy vé bạn đã chọn.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void thanhToanVe(ActionEvent e) {
        List<DatVe> veDaDat = userController.getDanhSachDatVeCuaNguoiDung();
        if (veDaDat == null || veDaDat.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Bạn chưa đặt vé nào!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        List<DatVe> veChuaThanhToan = veDaDat.stream().filter(ve -> "ChuaThanhToan".equalsIgnoreCase(ve.getTrangThai())).collect(Collectors.toList());
        if (veChuaThanhToan.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Bạn không có vé nào chưa thanh toán!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String[] options = new String[veChuaThanhToan.size()];
        List<ChuyenXe> allChuyenXe = userController.getDanhSachChuyenXe();
        for (int i = 0; i < veChuaThanhToan.size(); i++) {
            DatVe ve = veChuaThanhToan.get(i);
            ChuyenXe chuyen = allChuyenXe.stream().filter(c -> c.getId() == ve.getIdChuyenXe()).findFirst().orElse(null);
            String tenChuyen = (chuyen != null) ? (chuyen.getDiemKhoiHanh() + "->" + chuyen.getDiemDen()) : ("Chuyến #" + ve.getIdChuyenXe());
            double giaTien = (chuyen != null) ? ve.getSoVe() * chuyen.getGiaVe() : 0;
            List<String> gheListThanhToan = userController.getGheCuaVe(ve.getId());
            String gheStrThanhToan = (gheListThanhToan != null && !gheListThanhToan.isEmpty()) ? String.join(", ", gheListThanhToan) : "N/A";
            options[i] = String.format("Mã: %s | %s | Ghế: %s | %,.0f VNĐ", ve.getMaVe(), tenChuyen, gheStrThanhToan, giaTien);
        }
        String selected = (String) JOptionPane.showInputDialog(this, "Chọn vé cần thanh toán:", "Thanh Toán Vé", JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        if (selected == null) return;

        DatVe veCanThanhToan = null;
        try {
            String maVeSelected = selected.split("\\|")[0].split(":")[1].trim();
            veCanThanhToan = veChuaThanhToan.stream().filter(v -> v.getMaVe().equals(maVeSelected)).findFirst().orElse(null);
        } catch (Exception ex) {
        }
        if (veCanThanhToan != null) {
            final int idChuyenXeCanTim = veCanThanhToan.getIdChuyenXe();
            ChuyenXe chuyenXe = allChuyenXe.stream().filter(c -> c.getId() == idChuyenXeCanTim).findFirst().orElse(null);
            double totalCost = (chuyenXe != null) ? veCanThanhToan.getSoVe() * chuyenXe.getGiaVe() : 0;
            double discount = 0;
            double finalCost = totalCost;
            String discountMessage = "";
            int currentPoints = userController.getDiemTichLuy();
            boolean canApplyDiscount = uuDaiDaApDung && currentPoints >= 100;
            if (canApplyDiscount) {
                discount = totalCost * 0.10;
                finalCost = totalCost - discount;
                discountMessage = String.format("Đã áp dụng ưu đãi 100 điểm: -%,.0f VNĐ\n", discount);
            } else if (uuDaiDaApDung && currentPoints < 100) {
                discountMessage = "Đã kích hoạt ưu đãi nhưng không đủ 100 điểm!";
            }

            String paymentMethod = showPaymentDialog(finalCost, veCanThanhToan.getMaVe(), discountMessage);
            if (paymentMethod != null) {
                boolean success = userController.thanhToanVe(veCanThanhToan.getId(), canApplyDiscount);
                if (success) {
                    String message = "Thanh toán thành công...";
                    if (canApplyDiscount) {
                        message += "\nĐã sử dụng 100 điểm...";
                        uuDaiDaApDung = false;
                    }
                    JOptionPane.showMessageDialog(this, message, "Thanh Toán Thành Công", JOptionPane.INFORMATION_MESSAGE);
                    xuatHoaDon(veCanThanhToan, chuyenXe, finalCost, paymentMethod, discount);
                    updateDiemTichLuy();
                } else {
                    JOptionPane.showMessageDialog(this, "Thanh toán thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Thanh toán đã bị hủy.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Không tìm thấy vé bạn đã chọn.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String showPaymentDialog(double totalCost, String maVe, String discountInfo) {
        JPanel panel = new JPanel(new GridLayout(4, 1, 5, 5));
        panel.add(new JLabel("Mã vé: " + maVe));
        panel.add(new JLabel("Tổng tiền: " + String.format("%,.0f VNĐ", totalCost)));
        if (discountInfo != null && !discountInfo.isEmpty()) {
            panel.add(new JLabel(discountInfo));
        } else {
            panel.add(new JLabel("Không có ưu đãi áp dụng."));
        }
        String[] methods = {"Tiền mặt", "Thẻ ngân hàng", "Chuyển khoản"};
        JComboBox<String> paymentMethodCombo = new JComboBox<>(methods);
        panel.add(paymentMethodCombo);
        int result = JOptionPane.showConfirmDialog(this, panel, "Chọn phương thức thanh toán", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            return (String) paymentMethodCombo.getSelectedItem();
        }
        return null;
    }

    private void xuatHoaDon(DatVe ve, ChuyenXe chuyenXe, double finalCost, String paymentMethod, double discount) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        StringBuilder sb = new StringBuilder();
        sb.append("=== HÓA ĐƠN THANH TOÁN ===\n");
        sb.append("Mã vé: ").append(ve.getMaVe()).append("\n");
        if (chuyenXe != null) {
            sb.append("Chuyến: ").append(chuyenXe.getDiemKhoiHanh()).append(" -> ").append(chuyenXe.getDiemDen()).append("\n");
            sb.append("Thời gian: ").append(chuyenXe.getNgayKhoiHanh() != null ? sdf.format(chuyenXe.getNgayKhoiHanh()) : "N/A").append("\n");
        }
        sb.append("Số vé: ").append(ve.getSoVe()).append("\n");
        sb.append("Ghế: ").append(String.join(", ", userController.getGheCuaVe(ve.getId()))).append("\n");
        sb.append("Phương thức thanh toán: ").append(paymentMethod).append("\n");
        if (discount > 0) {
            sb.append("Ưu đãi: -").append(String.format("%,.0f VNĐ", discount)).append("\n");
        }
        sb.append("Tổng tiền: ").append(String.format("%,.0f VNĐ", finalCost)).append("\n");
        sb.append("Thời gian thanh toán: ").append(sdf.format(new Date())).append("\n");
        sb.append("Cảm ơn quý khách đã sử dụng dịch vụ!\n");

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File("HoaDon_" + ve.getMaVe() + ".txt"));
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (FileWriter writer = new FileWriter(fileChooser.getSelectedFile())) {
                writer.write(sb.toString());
                JOptionPane.showMessageDialog(this, "Hóa đơn đã được xuất tại: " + fileChooser.getSelectedFile().getAbsolutePath());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi xuất hóa đơn: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private String numberToWords(long number) {
        return String.format("%,d", number);
    }

    private void xemUuDai(ActionEvent e) {
        int diemTichLuyHienTai = userController.getDiemTichLuy();
        String message;

        if (uuDaiDaApDung) {
            message = "Bạn đã kích hoạt ưu đãi giảm 10% (sử dụng 100 điểm).\n" +
                    "Ưu đãi sẽ được áp dụng tự động khi bạn thanh toán vé tiếp theo\n" +
                    "(miễn là bạn vẫn còn đủ 100 điểm tại thời điểm thanh toán).";
            JOptionPane.showMessageDialog(this, message, "Thông Tin Ưu Đãi", JOptionPane.INFORMATION_MESSAGE);
        } else if (diemTichLuyHienTai >= 100) {
            message = "Bạn đang có " + diemTichLuyHienTai + " điểm tích lũy.\n" +
                    "Bạn đủ điều kiện nhận ưu đãi:\n\n" +
                    "  - Giảm 10% cho lần thanh toán tiếp theo (sử dụng 100 điểm).\n\n" +
                    "Nhấn OK để KÍCH HOẠT ưu đãi này.\n" +
                    "(Ưu đãi sẽ được áp dụng ở lần thanh toán vé kế tiếp)";
            int option = JOptionPane.showConfirmDialog(this, message, "Kích Hoạt Ưu Đãi", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
            if (option == JOptionPane.OK_OPTION) {
                uuDaiDaApDung = true;
                JOptionPane.showMessageDialog(this, "Ưu đãi giảm 10% đã được kích hoạt!\n" +
                                "Sẽ tự động áp dụng khi bạn vào mục 'Thanh Toán'\n" +
                                "(nếu bạn còn đủ 100 điểm lúc đó).",
                        "Kích Hoạt Thành Công", JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            message = "Bạn đang có " + diemTichLuyHienTai + " điểm tích lũy.\n" +
                    "Hãy tích lũy thêm " + (100 - diemTichLuyHienTai) + " điểm nữa (" +
                    Math.max(1, (100 - diemTichLuyHienTai + 9) / 10) + " vé nữa) để nhận ưu đãi giảm 10%!";
            JOptionPane.showMessageDialog(this, message, "Thông Tin Ưu Đãi", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void updateDiemTichLuy() {
        try {
            int diem = userController.getDiemTichLuy();
            if (diemTichLuyLabel != null) {
                diemTichLuyLabel.setText("Điểm tích lũy: " + diem);
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi cập nhật hiển thị điểm: " + e.getMessage());
            if (diemTichLuyLabel != null) {
                diemTichLuyLabel.setText("Điểm tích lũy: Lỗi");
            }
            e.printStackTrace();
        }
    }

    private void dangXuat(ActionEvent e) {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc chắn muốn đăng xuất khỏi hệ thống?",
                "Xác nhận đăng xuất",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            setVisible(false);
            dispose();

            SwingUtilities.invokeLater(() -> {
                new LoginView().setVisible(true);
            });
        }
    }
}
package view;

import controller.AdminController;
import model.ChuyenXe;
import model.DatVe;
import dao.DatVeDAO;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Set;      // Import Set
import java.util.HashSet;
import java.util.Map;

public class AdminView extends JFrame {
    private final AdminController adminController;
    private JTable tableChuyenXe;
    private DefaultTableModel model;
    private DatVeDAO datVeDAO;

    private final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private final Color SECONDARY_COLOR = new Color(52, 152, 219);
    private final Color BACKGROUND_COLOR = new Color(236, 240, 241);
    private final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 24);
    private final Font NORMAL_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private final Font FOOTER_FONT = new Font("Segoe UI", Font.ITALIC, 12);

    public AdminView() {
        this.adminController = new AdminController();
        this.datVeDAO = new DatVeDAO();
        initUI();
        capNhatDanhSachChuyen();
    }

    private void initUI() {
        setTitle("Hệ Thống Quản Lý Admin");
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
        JLabel titleLabel = new JLabel("QUẢN LÝ CHUYẾN XE", JLabel.CENTER);
        titleLabel.setFont(HEADER_FONT);
        titleLabel.setForeground(Color.WHITE);
        JLabel adminLabel = new JLabel("Xin chào, Admin", JLabel.RIGHT);
        adminLabel.setFont(NORMAL_FONT);
        adminLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(adminLabel, BorderLayout.EAST);
        return headerPanel;
    }

    private JPanel createCenterPanel() {
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(BACKGROUND_COLOR);
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
        tableChuyenXe.setSelectionBackground(new Color(52, 152, 219, 100));
        tableChuyenXe.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JTableHeader header = tableChuyenXe.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(SECONDARY_COLOR);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(100, 40));
        header.setReorderingAllowed(false);
        JScrollPane scrollPane = new JScrollPane(tableChuyenXe);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        return centerPanel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new GridLayout(10, 1, 10, 10));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        JButton btnThemChuyen = createStyledButton("Thêm Chuyến", new Color(46, 204, 113));
        JButton btnSuaChuyen = createStyledButton("Sửa Chuyến", new Color(241, 196, 15));
        JButton btnXoaChuyen = createStyledButton("Xóa Chuyến", new Color(231, 76, 60));
        JButton btnPhanCongTaiXe = createStyledButton("Phân Công Tài Xế", new Color(155, 89, 182));
        JButton btnSuaVe = createStyledButton("Sửa Vé", new Color(255, 140, 0));
        JButton btnXemDoanhThu = createStyledButton("Xem Doanh Thu", new Color(52, 73, 94));
        JButton btnQuanLyVe = createStyledButton("Quản Lý Vé", new Color(22, 160, 133));
        JButton btnDangXuat = createStyledButton("Đăng Xuất", new Color(149, 165, 166));
        btnThemChuyen.addActionListener(this::themChuyenXe);
        btnSuaChuyen.addActionListener(this::suaChuyenXe);
        btnXoaChuyen.addActionListener(this::xoaChuyenXe);
        btnPhanCongTaiXe.addActionListener(this::phanCongTaiXe);
        btnSuaVe.addActionListener(this::suaDatVe);
        btnXemDoanhThu.addActionListener(this::xemDoanhThu);
        btnQuanLyVe.addActionListener(this::quanLyVe);
        btnDangXuat.addActionListener(this::dangXuat);
        buttonPanel.add(btnThemChuyen);
        buttonPanel.add(btnSuaChuyen);
        buttonPanel.add(btnXoaChuyen);
        buttonPanel.add(btnPhanCongTaiXe);
        buttonPanel.add(btnSuaVe);
        buttonPanel.add(btnXemDoanhThu);
        buttonPanel.add(btnQuanLyVe);
        buttonPanel.add(new JLabel());
        buttonPanel.add(btnDangXuat);
        return buttonPanel;
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(NORMAL_FONT);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(150, 40));
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
        model.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        try {
            List<ChuyenXe> danhSach = adminController.getDanhSachChuyenXe();
            if (danhSach == null) {
                JOptionPane.showMessageDialog(this, "Không thể tải danh sách chuyến xe từ CSDL.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
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
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật danh sách chuyến xe: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void themChuyenXe(ActionEvent e) {
        JPanel addPanel = new JPanel(new GridLayout(8, 2, 10, 10));
        addPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JTextField diemKhoiHanhField = new JTextField();
        JTextField diemDenField = new JTextField();
        JTextField ngayKhoiHanhField = new JTextField("dd/MM/yyyy HH:mm");
        JTextField taiXeField = new JTextField();
        JTextField tongSoGheField = new JTextField("40");
        JTextField giaVeField = new JTextField();
        JTextField bienSoField = new JTextField();
        JTextField loaiXeField = new JTextField();
        addPanel.add(new JLabel("Điểm Khởi Hành (*):"));
        addPanel.add(diemKhoiHanhField);
        addPanel.add(new JLabel("Điểm Đến (*):"));
        addPanel.add(diemDenField);
        addPanel.add(new JLabel("Ngày Khởi Hành (*)(dd/MM/yyyy HH:mm):"));
        addPanel.add(ngayKhoiHanhField);
        addPanel.add(new JLabel("Tài Xế:"));
        addPanel.add(taiXeField);
        addPanel.add(new JLabel("Tổng Số Ghế (*):"));
        addPanel.add(tongSoGheField);
        addPanel.add(new JLabel("Giá Vé (*):"));
        addPanel.add(giaVeField);
        addPanel.add(new JLabel("Biển Số Xe (*):"));
        addPanel.add(bienSoField);
        addPanel.add(new JLabel("Loại Xe (*):"));
        addPanel.add(loaiXeField);

        int option = JOptionPane.showConfirmDialog(this, addPanel, "Thêm Chuyến Xe Mới", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (option == JOptionPane.OK_OPTION) {
            try {
                String diemKH = diemKhoiHanhField.getText().trim();
                String diemDen = diemDenField.getText().trim();
                String ngayKHStr = ngayKhoiHanhField.getText().trim();
                String taiXe = taiXeField.getText().trim();
                String tongSoGheStr = tongSoGheField.getText().trim();
                String giaVeStr = giaVeField.getText().trim();
                String bienSo = bienSoField.getText().trim();
                String loaiXe = loaiXeField.getText().trim();
                if (diemKH.isEmpty() || diemDen.isEmpty() || ngayKHStr.isEmpty() || tongSoGheStr.isEmpty() || giaVeStr.isEmpty() || bienSo.isEmpty() || loaiXe.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin (*).", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                int tongSoGhe = 0;
                try {
                    tongSoGhe = Integer.parseInt(tongSoGheStr);
                    if (tongSoGhe <= 0) {
                        JOptionPane.showMessageDialog(this, "Tổng số ghế phải > 0.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        tongSoGheField.requestFocus();
                        return;
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Tổng số ghế phải là số.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    tongSoGheField.requestFocus();
                    return;
                }
                double giaVe = 0;
                try {
                    giaVe = Double.parseDouble(giaVeStr);
                    if (giaVe <= 0) {
                        JOptionPane.showMessageDialog(this, "Giá vé phải > 0.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        giaVeField.requestFocus();
                        return;
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Giá vé phải là số.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    giaVeField.requestFocus();
                    return;
                }
                Date ngayKhoiHanh = null;
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                    sdf.setLenient(false);
                    ngayKhoiHanh = sdf.parse(ngayKHStr);
                } catch (ParseException ex) {
                    JOptionPane.showMessageDialog(this, "Định dạng ngày sai (dd/MM/yyyy HH:mm).", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    ngayKhoiHanhField.requestFocus();
                    return;
                }

                ChuyenXe chuyenXe = new ChuyenXe(0, diemKH, diemDen, ngayKhoiHanh, taiXe.isEmpty() ? null : taiXe, tongSoGhe, giaVe, bienSo, loaiXe);
                boolean success = adminController.themChuyenXe(chuyenXe);
                if (success) {
                    capNhatDanhSachChuyen();
                    JOptionPane.showMessageDialog(this, "Thêm chuyến xe thành công!");
                } else {
                    JOptionPane.showMessageDialog(this, "Thêm chuyến xe thất bại! Lỗi CSDL.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi không xác định: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    private void suaChuyenXe(ActionEvent e) {
        int selectedRow = tableChuyenXe.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một chuyến xe để sửa!", "Chưa chọn chuyến", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int idChuyen = (int) model.getValueAt(selectedRow, 0);

        ChuyenXe chuyenXeHienTai = adminController.getDanhSachChuyenXe().stream()
                .filter(c -> c.getId() == idChuyen)
                .findFirst().orElse(null);
        if (chuyenXeHienTai == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin chi tiết cho chuyến xe ID: " + idChuyen, "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int soGheDaDatHienTai = chuyenXeHienTai.getGheDaDat().size();

        JPanel editPanel = new JPanel(new GridLayout(8, 2, 10, 10));
        editPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JTextField diemKhoiHanhField = new JTextField(chuyenXeHienTai.getDiemKhoiHanh());
        JTextField diemDenField = new JTextField(chuyenXeHienTai.getDiemDen());
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        JTextField ngayKhoiHanhField = new JTextField(chuyenXeHienTai.getNgayKhoiHanh() != null ? sdf.format(chuyenXeHienTai.getNgayKhoiHanh()) : "");
        JTextField taiXeField = new JTextField(chuyenXeHienTai.getTaiXe() != null ? chuyenXeHienTai.getTaiXe() : "");
        int tongSoGheHienTai = chuyenXeHienTai.getSoGheTrong() + soGheDaDatHienTai;
        JTextField tongSoGheField = new JTextField(String.valueOf(tongSoGheHienTai));
        JTextField giaVeField = new JTextField(String.valueOf(chuyenXeHienTai.getGiaVe()));
        JTextField bienSoField = new JTextField(chuyenXeHienTai.getBienSo());
        JTextField loaiXeField = new JTextField(chuyenXeHienTai.getLoaiXe());
        editPanel.add(new JLabel("Điểm Khởi Hành (*):"));
        editPanel.add(diemKhoiHanhField);
        editPanel.add(new JLabel("Điểm Đến (*):"));
        editPanel.add(diemDenField);
        editPanel.add(new JLabel("Ngày Khởi Hành (*)(dd/MM/yyyy HH:mm):"));
        editPanel.add(ngayKhoiHanhField);
        editPanel.add(new JLabel("Tài Xế:"));
        editPanel.add(taiXeField);
        editPanel.add(new JLabel("Tổng Số Ghế (*):"));
        editPanel.add(tongSoGheField);
        editPanel.add(new JLabel("Giá Vé (*):"));
        editPanel.add(giaVeField);
        editPanel.add(new JLabel("Biển Số Xe (*):"));
        editPanel.add(bienSoField);
        editPanel.add(new JLabel("Loại Xe (*):"));
        editPanel.add(loaiXeField);

        int option = JOptionPane.showConfirmDialog(this, editPanel, "Sửa Thông Tin Chuyến Xe (ID: " + idChuyen + ")", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (option == JOptionPane.OK_OPTION) {
            try {
                String diemKH = diemKhoiHanhField.getText().trim();
                String diemDen = diemDenField.getText().trim();
                String ngayKHStr = ngayKhoiHanhField.getText().trim();
                String taiXe = taiXeField.getText().trim();
                String tongSoGheStr = tongSoGheField.getText().trim();
                String giaVeStr = giaVeField.getText().trim();
                String bienSo = bienSoField.getText().trim();
                String loaiXe = loaiXeField.getText().trim();

                if (diemKH.isEmpty() || diemDen.isEmpty() || ngayKHStr.isEmpty() ||
                        tongSoGheStr.isEmpty() || giaVeStr.isEmpty() || bienSo.isEmpty() || loaiXe.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin (*).", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                int tongSoGheMoi = 0;
                try {
                    tongSoGheMoi = Integer.parseInt(tongSoGheStr);
                    if (tongSoGheMoi <= 0) {
                        JOptionPane.showMessageDialog(this, "Tổng số ghế phải > 0.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        tongSoGheField.requestFocus();
                        return;
                    }
                    if (tongSoGheMoi < soGheDaDatHienTai) {
                        JOptionPane.showMessageDialog(this, "Tổng số ghế mới (" + tongSoGheMoi + ") < số ghế đã đặt (" + soGheDaDatHienTai + ")!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        tongSoGheField.requestFocus();
                        return;
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Tổng số ghế phải là số.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    tongSoGheField.requestFocus();
                    return;
                }

                double giaVeMoi = 0;
                try {
                    giaVeMoi = Double.parseDouble(giaVeStr);
                    if (giaVeMoi <= 0) {
                        JOptionPane.showMessageDialog(this, "Giá vé phải > 0.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        giaVeField.requestFocus();
                        return;
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Giá vé phải là số.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    giaVeField.requestFocus();
                    return;
                }

                Date ngayKhoiHanhMoi = null;
                try {
                    sdf.setLenient(false);
                    ngayKhoiHanhMoi = sdf.parse(ngayKHStr);
                } catch (ParseException ex) {
                    JOptionPane.showMessageDialog(this, "Định dạng ngày sai (dd/MM/yyyy HH:mm).", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    ngayKhoiHanhField.requestFocus();
                    return;
                }

                ChuyenXe chuyenXeMoi = new ChuyenXe(
                        idChuyen, diemKH, diemDen, ngayKhoiHanhMoi,
                        taiXe.isEmpty() ? null : taiXe,
                        tongSoGheMoi - soGheDaDatHienTai,
                        giaVeMoi,
                        bienSo,
                        loaiXe);
                chuyenXeHienTai.getGheDaDat().forEach(chuyenXeMoi::themGheDaDat);

                boolean success = adminController.suaChuyenXe(chuyenXeMoi);

                if (success) {
                    capNhatDanhSachChuyen();
                    JOptionPane.showMessageDialog(this, "Sửa chuyến xe thành công!");
                } else {
                    JOptionPane.showMessageDialog(this, "Sửa chuyến xe thất bại! Lỗi CSDL.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi không mong muốn khi sửa: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    private void xoaChuyenXe(ActionEvent e) {
        int selectedRow = tableChuyenXe.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một chuyến xe để xóa!", "Chưa chọn chuyến", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int idChuyen = (int) model.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa chuyến xe ID " + idChuyen + "?\n(Hành động này không thể hoàn tác)", "Xác Nhận Xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean success = adminController.xoaChuyenXe(idChuyen);
                if (success) {
                    capNhatDanhSachChuyen();
                    JOptionPane.showMessageDialog(this, "Xóa chuyến xe thành công!");
                } else {
                    JOptionPane.showMessageDialog(this, "Xóa chuyến xe thất bại! Có thể chuyến xe đang có vé đặt hoặc lỗi CSDL.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi xóa chuyến xe: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    private void phanCongTaiXe(ActionEvent e) {
        int selectedRow = tableChuyenXe.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một chuyến xe để phân công tài xế!", "Chưa chọn chuyến", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int idChuyen = (int) model.getValueAt(selectedRow, 0);
        String taiXeHienTai = (String) model.getValueAt(selectedRow, 4);
        JTextField taiXeField = new JTextField(taiXeHienTai.equals("Chưa phân công") ? "" : taiXeHienTai, 20);
        JPanel panel = new JPanel(new GridLayout(2, 1, 5, 5));
        panel.add(new JLabel("Tên Tài Xế (để trống để xóa phân công):"));
        panel.add(taiXeField);
        int option = JOptionPane.showConfirmDialog(this, panel, "Phân Công Tài Xế (Chuyến ID: " + idChuyen + ")", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (option == JOptionPane.OK_OPTION) {
            try {
                String taiXeMoi = taiXeField.getText().trim();
                boolean success = adminController.phanCongTaiXe(idChuyen, taiXeMoi.isEmpty() ? null : taiXeMoi);
                if (success) {
                    capNhatDanhSachChuyen();
                    JOptionPane.showMessageDialog(this, "Phân công tài xế thành công!");
                } else {
                    JOptionPane.showMessageDialog(this, "Phân công tài xế thất bại! Lỗi CSDL.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi phân công tài xế: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    private void showSeatMap() {
        int selectedRow = tableChuyenXe.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một chuyến xe để xem sơ đồ ghế!", "Chưa chọn chuyến", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int idChuyen = (int) model.getValueAt(selectedRow, 0);
        int soGheTrong = (int) model.getValueAt(selectedRow, 5);
        List<String> gheDaDat = adminController.getGheDaDatHienTai(idChuyen);
        ChuyenXe chuyenXeInfo = adminController.getDanhSachChuyenXe().stream()
                .filter(c -> c.getId() == idChuyen)
                .findFirst().orElse(null);
        String titleInfo = (chuyenXeInfo != null) ?
                " (" + chuyenXeInfo.getDiemKhoiHanh() + " -> " + chuyenXeInfo.getDiemDen() + ")" : "";
        JDialog seatMapDialog = new JDialog(this, "Sơ đồ ghế - Chuyến " + idChuyen + titleInfo, true);
        seatMapDialog.setSize(1000, 400);
        seatMapDialog.setLocationRelativeTo(this);
        seatMapDialog.setLayout(new BorderLayout(15, 15));
        seatMapDialog.getContentPane().setBackground(BACKGROUND_COLOR);
        JLabel titleLabel = new JLabel("Sơ đồ ghế chuyến xe #" + idChuyen + titleInfo + " - Ghế trống: " + soGheTrong, JLabel.CENTER);
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
            seatPanel.add(createSeatButton(seatLabel, gheDaDat));
        }
        seatPanel.add(createSeatLabel("B"));
        for (int i = 1; i <= 20; i++) {
            String seatLabel = "B" + i;
            seatPanel.add(createSeatButton(seatLabel, gheDaDat));
        }
        seatMapDialog.add(seatPanel, BorderLayout.CENTER);
        seatMapDialog.add(createLegendPanel(), BorderLayout.SOUTH);
        seatMapDialog.setVisible(true);
    }

    private JButton createSeatButton(String seatLabel, List<String> gheDaDat) {
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

    private void suaDatVe(ActionEvent e) {
        List<DatVe> danhSachVe = adminController.xemTatCaDatVe();
        if (danhSachVe == null || danhSachVe.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Hiện không có vé nào để sửa!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String[] options = new String[danhSachVe.size()];
        List<ChuyenXe> allChuyenXe = adminController.getDanhSachChuyenXe();
        for (int i = 0; i < danhSachVe.size(); i++) {
            DatVe ve = danhSachVe.get(i);
            ChuyenXe chuyen = allChuyenXe.stream().filter(c -> c.getId() == ve.getIdChuyenXe()).findFirst().orElse(null);
            String tenChuyen = (chuyen != null) ? (chuyen.getDiemKhoiHanh() + "->" + chuyen.getDiemDen()) : ("Chuyến #" + ve.getIdChuyenXe());
            List<String> gheList = this.datVeDAO.getGheByDatVeId(ve.getId());
            String gheStr = (gheList != null && !gheList.isEmpty()) ? String.join(", ", gheList) : "N/A";
            options[i] = String.format("Mã: %s | %s | Người đặt: %s | Ghế: %s | [%s]", ve.getMaVe(), tenChuyen, ve.getTenNguoiDung(), gheStr, mapTrangThaiToDisplay(ve.getTrangThai()));
        }
        String selected = (String) JOptionPane.showInputDialog(this, "Chọn vé cần sửa:", "Sửa Thông Tin Vé", JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        if (selected == null) return;

        DatVe veCanSua = null;
        try {
            String maVeSelected = selected.split("\\|")[0].split(":")[1].trim();
            veCanSua = danhSachVe.stream().filter(v -> v.getMaVe().equals(maVeSelected)).findFirst().orElse(null);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi chọn vé để sửa: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (veCanSua == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy vé bạn đã chọn.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if ("DaThanhToan".equalsIgnoreCase(veCanSua.getTrangThai()) || "DaHuy".equalsIgnoreCase(veCanSua.getTrangThai())) {
            JOptionPane.showMessageDialog(this, "Không thể sửa vé đã thanh toán hoặc đã hủy!", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        final DatVe veCanSuaFinal = veCanSua;
        final int idChuyenXeHienTai = veCanSua.getIdChuyenXe();
        ChuyenXe chuyenXeHienTai = allChuyenXe.stream().filter(c -> c.getId() == idChuyenXeHienTai).findFirst().orElse(null);
        final int soGheTrongHienTai = (chuyenXeHienTai != null) ? chuyenXeHienTai.getSoGheTrong() : 0;
        final List<String> gheDaChonHienTai = this.datVeDAO.getGheByDatVeId(veCanSua.getId());
        final int soGheTrongHienTaiFinal = soGheTrongHienTai + gheDaChonHienTai.size();
        final AdminController adminControllerFinal = adminController;

        JPanel editPanel = new JPanel(new GridLayout(7, 2, 10, 10));
        editPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        String thongTinChuyen = (chuyenXeHienTai != null) ?
                String.format("Chuyến: %s -> %s (%s)", chuyenXeHienTai.getDiemKhoiHanh(), chuyenXeHienTai.getDiemDen(),
                        new SimpleDateFormat("dd/MM/yyyy HH:mm").format(chuyenXeHienTai.getNgayKhoiHanh())) :
                "Chuyến: " + idChuyenXeHienTai;
        JLabel idLabel = new JLabel("ID Vé: " + veCanSua.getId() + " | " + thongTinChuyen);
        JLabel gheHienTaiLabel = new JLabel("Ghế hiện tại: " + String.join(", ", gheDaChonHienTai));
        JTextField tenNguoiDungField = new JTextField(veCanSua.getTenNguoiDung());
        JTextField soVeField = new JTextField(String.valueOf(veCanSua.getSoVe()));
        JTextField soDienThoaiField = new JTextField(veCanSua.getSoDienThoai());
        JLabel trangThaiLabel = new JLabel("Trạng Thái: " + mapTrangThaiToDisplay(veCanSua.getTrangThai()));
        JCheckBox dichVuDuaDonCheckBox = new JCheckBox("Dịch vụ đưa đón", veCanSua.isDichVuDuaDon());
        JTextField diaChiDonField = new JTextField(veCanSua.getDiaChiDon());
        editPanel.add(idLabel);
        editPanel.add(new JLabel(""));
        editPanel.add(gheHienTaiLabel);
        editPanel.add(new JLabel(""));
        editPanel.add(new JLabel("Tên Người Dùng (*):"));
        editPanel.add(tenNguoiDungField);
        editPanel.add(new JLabel("Số Vé (*):"));
        editPanel.add(soVeField);
        editPanel.add(new JLabel("Số Điện Thoại (*):"));
        editPanel.add(soDienThoaiField);
        editPanel.add(trangThaiLabel);
        editPanel.add(new JLabel(""));
        editPanel.add(new JLabel("Dịch Vụ Đưa Đón:"));
        editPanel.add(dichVuDuaDonCheckBox);
        editPanel.add(new JLabel("Địa Chỉ Đón:"));
        editPanel.add(diaChiDonField);

        int optionEdit = JOptionPane.showConfirmDialog(this, editPanel, "Sửa Thông Tin Vé (Mã: " + veCanSua.getMaVe() + ")", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (optionEdit != JOptionPane.OK_OPTION) return;

        JDialog dialog = new JDialog(this, "Đang xử lý...", true);
        dialog.setSize(200, 100);
        dialog.setLocationRelativeTo(this);
        dialog.add(new JLabel("Đang sửa thông tin vé...", SwingConstants.CENTER));
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            List<String> gheMoiList = null;

            @Override
            protected Boolean doInBackground() {
                try {
                    final String tenNguoiDung = tenNguoiDungField.getText().trim();
                    final String soVeStr = soVeField.getText().trim();
                    final String soDienThoai = soDienThoaiField.getText().trim();
                    final boolean duaDonMoi = dichVuDuaDonCheckBox.isSelected();
                    final String diaChiDonMoi = diaChiDonField.getText().trim();

                    if (tenNguoiDung.isEmpty() || soVeStr.isEmpty() || soDienThoai.isEmpty()) {
                        JOptionPane.showMessageDialog(AdminView.this, "Vui lòng nhập đầy đủ thông tin (*).", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
                        return false;
                    }

                    if (!soDienThoai.matches("0[0-9]{9,10}")) {
                        JOptionPane.showMessageDialog(AdminView.this, "Số điện thoại phải có 10-11 chữ số và bắt đầu bằng 0!", "Lỗi", JOptionPane.WARNING_MESSAGE);
                        return false;
                    }

                    int soVeMoi;
                    try {
                        soVeMoi = Integer.parseInt(soVeStr);
                        if (soVeMoi <= 0) {
                            JOptionPane.showMessageDialog(AdminView.this, "Số vé phải > 0.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                            return false;
                        }
                        if (soVeMoi > gheDaChonHienTai.size() && soVeMoi - gheDaChonHienTai.size() > soGheTrongHienTaiFinal) {
                            JOptionPane.showMessageDialog(AdminView.this, "Số vé mới (" + soVeMoi + ") vượt quá số ghế trống còn lại (" + soGheTrongHienTaiFinal + ")!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                            return false;
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(AdminView.this, "Số vé phải là số.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        return false;
                    }

                    if (duaDonMoi && (diaChiDonMoi == null || diaChiDonMoi.isEmpty())) {
                        JOptionPane.showMessageDialog(AdminView.this, "Địa chỉ đón không được để trống khi chọn dịch vụ đưa đón!", "Lỗi", JOptionPane.WARNING_MESSAGE);
                        return false;
                    }

                    if (soVeMoi != gheDaChonHienTai.size()) {
                        Set<String> gheDaDat = new HashSet<>(adminControllerFinal.getGheDaDatHienTai(idChuyenXeHienTai));
                        gheDaDat.removeAll(gheDaChonHienTai);
                        ChonGheDialog dialog = new ChonGheDialog(AdminView.this, soVeMoi, gheDaDat);
                        gheMoiList = dialog.showDialog();
                        if (gheMoiList == null || gheMoiList.isEmpty()) {
                            JOptionPane.showMessageDialog(AdminView.this, "Bạn chưa chọn ghế mới! Sửa vé bị hủy.", "Thông báo", JOptionPane.WARNING_MESSAGE);
                            return false;
                        }
                    } else {
                        gheMoiList = gheDaChonHienTai;
                    }

                    DatVe veMoi = new DatVe(
                            veCanSuaFinal.getId(),
                            veCanSuaFinal.getMaVe(),
                            tenNguoiDung,
                            veCanSuaFinal.getIdChuyenXe(),
                            soVeMoi,
                            soDienThoai,
                            duaDonMoi,
                            diaChiDonMoi
                    );
                    veMoi.setTrangThai(veCanSuaFinal.getTrangThai());

                    return adminControllerFinal.suaDatVeVaGhe(veMoi, gheMoiList);
                } catch (IllegalStateException ex) {
                    if (ex.getMessage().contains("Một hoặc nhiều ghế") && gheMoiList != null) {
                        Set<String> gheDaDat = new HashSet<>(adminControllerFinal.getGheDaDatHienTai(idChuyenXeHienTai));
                        gheDaDat.removeAll(gheDaChonHienTai);
                        List<String> gheBiXungDot = gheMoiList.stream()
                                .filter(ghe -> gheDaDat.contains(ghe))
                                .collect(Collectors.toList());
                        String chiTietLoi = gheBiXungDot.isEmpty() ? ex.getMessage() : "Các ghế sau đã bị đặt: " + String.join(", ", gheBiXungDot);
                        JOptionPane.showMessageDialog(AdminView.this, "Lỗi: " + chiTietLoi + "\nVui lòng chọn ghế khác.", "Lỗi Ghế", JOptionPane.WARNING_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(AdminView.this, "Lỗi: " + ex.getMessage(), "Lỗi Ghế", JOptionPane.WARNING_MESSAGE);
                    }
                    return false;
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(AdminView.this, "Lỗi không mong muốn khi sửa vé: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                    return false;
                }
            }

            @Override
            protected void done() {
                dialog.dispose();
                try {
                    boolean success = get();
                    if (success) {
                        JOptionPane.showMessageDialog(AdminView.this, "Sửa vé thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                        capNhatDanhSachChuyen();
                    } else {
                        JOptionPane.showMessageDialog(AdminView.this, "Sửa vé thất bại! Lỗi CSDL.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(AdminView.this, "Lỗi không mong muốn khi sửa vé: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        };

        worker.execute();
        dialog.setVisible(true);
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

    private String mapTrangThaiToStorage(String trangThai) {
        switch (trangThai.toLowerCase()) {
            case "chưa thanh toán":
                return "ChuaThanhToan";
            case "đã thanh toán":
                return "DaThanhToan";
            case "đã hủy":
                return "DaHuy";
            default:
                return trangThai;
        }
    }

    private void xemDoanhThu(ActionEvent e) {
        Map<String, Double> doanhThu = adminController.tinhDoanhThuTatCa();
        double doanhThuDaThanhToan = doanhThu.getOrDefault("DaThanhToan", 0.0);
        double doanhThuChuaThanhToan = doanhThu.getOrDefault("ChuaThanhToan", 0.0);
        double tongDoanhThu = doanhThuDaThanhToan + doanhThuChuaThanhToan;

        DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue("Đã Thanh Toán", doanhThuDaThanhToan);
        dataset.setValue("Chưa Thanh Toán", doanhThuChuaThanhToan);

        JFreeChart pieChart = ChartFactory.createPieChart(
                "Biểu Đồ Doanh Thu - Tổng: " + String.format("%,.0f VNĐ", tongDoanhThu),
                dataset,
                true,
                true,
                false
        );
        pieChart.setBackgroundPaint(BACKGROUND_COLOR);
        ChartPanel chartPanel = new ChartPanel(pieChart);
        chartPanel.setPreferredSize(new Dimension(600, 400));

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(chartPanel, BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        JButton btnExportText = new JButton("Xuất Báo Cáo (Text)");
        btnExportText.setBackground(new Color(52, 73, 94));
        btnExportText.setForeground(Color.WHITE);
        btnExportText.addActionListener(e2 -> xuatBaoCaoDoanhThuText());
        buttonPanel.add(btnExportText);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        JDialog dialog = new JDialog(this, "Biểu Đồ Doanh Thu", true);
        dialog.setSize(650, 500);
        dialog.setLocationRelativeTo(this);
        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void xuatBaoCaoDoanhThuText() {
        List<DatVe> danhSachVe = adminController.getDanhSachDatVe();
        List<ChuyenXe> allChuyenXe = adminController.getDanhSachChuyenXe();
        double doanhThuDaThanhToan = 0;
        double doanhThuChuaThanhToan = 0;

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        StringBuilder sb = new StringBuilder();
        sb.append("=== BÁO CÁO DOANH THU ===\n");
        sb.append("Ngày tạo báo cáo: ").append(sdf.format(new Date())).append("\n");
        sb.append("------------------------------------------\n");

        for (DatVe ve : danhSachVe) {
            ChuyenXe chuyen = allChuyenXe.stream().filter(c -> c.getId() == ve.getIdChuyenXe()).findFirst().orElse(null);
            double giaTienVe = (chuyen != null) ? ve.getSoVe() * chuyen.getGiaVe() : 0;
            String tenChuyen = (chuyen != null) ? (chuyen.getDiemKhoiHanh() + " -> " + chuyen.getDiemDen()) : ("Chuyến #" + ve.getIdChuyenXe());
            sb.append("Mã vé: ").append(ve.getMaVe()).append("\n");
            sb.append("Chuyến: ").append(tenChuyen).append("\n");
            sb.append("Người đặt: ").append(ve.getTenNguoiDung()).append("\n");
            sb.append("Số vé: ").append(ve.getSoVe()).append(" | Tổng tiền: ").append(String.format("%,.0f VNĐ", giaTienVe)).append("\n");
            sb.append("Trạng thái: ").append(mapTrangThaiToDisplay(ve.getTrangThai())).append("\n");
            sb.append("------------------------------------------\n");

            if ("DaThanhToan".equalsIgnoreCase(ve.getTrangThai())) {
                doanhThuDaThanhToan += giaTienVe;
            } else if ("ChuaThanhToan".equalsIgnoreCase(ve.getTrangThai())) {
                doanhThuChuaThanhToan += giaTienVe;
            }
        }

        sb.append("\n=== TỔNG KẾT ===\n");
        sb.append("Doanh thu đã thanh toán: ").append(String.format("%,.0f VNĐ", doanhThuDaThanhToan)).append("\n");
        sb.append("Doanh thu chưa thanh toán: ").append(String.format("%,.0f VNĐ", doanhThuChuaThanhToan)).append("\n");
        sb.append("Tổng doanh thu: ").append(String.format("%,.0f VNĐ", doanhThuDaThanhToan + doanhThuChuaThanhToan)).append("\n");

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn nơi lưu báo cáo doanh thu");
        fileChooser.setSelectedFile(new File("BaoCaoDoanhThu_" + System.currentTimeMillis() + ".txt"));
        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            try (FileWriter writer = new FileWriter(fileToSave)) {
                writer.write(sb.toString());
                JOptionPane.showMessageDialog(this, "Xuất báo cáo doanh thu thành công!\nĐã lưu tại: " + fileToSave.getAbsolutePath(), "Thành công", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi xuất báo cáo: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    private void quanLyVe(ActionEvent e) {
        List<DatVe> danhSachVe = adminController.xemTatCaDatVe();
        if (danhSachVe == null || danhSachVe.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Hiện không có vé nào để quản lý!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        StringBuilder sb = new StringBuilder("=== DANH SÁCH TẤT CẢ VÉ ===\nTổng cộng: " + danhSachVe.size() + " vé\n");
        sb.append("------------------------------------------\n");
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        List<ChuyenXe> allChuyenXe = adminController.getDanhSachChuyenXe();

        for (DatVe ve : danhSachVe) {
            ChuyenXe chuyen = allChuyenXe.stream().filter(c -> c.getId() == ve.getIdChuyenXe()).findFirst().orElse(null);
            List<String> gheList = this.datVeDAO.getGheByDatVeId(ve.getId());
            String gheStr = (gheList != null && !gheList.isEmpty()) ? String.join(", ", gheList) : "[Chưa có ghế]";

            sb.append("Mã vé: ").append(ve.getMaVe()).append("\n");
            sb.append("ID: ").append(ve.getId()).append("\n");
            if (chuyen != null) {
                sb.append(String.format("Chuyến %d: %s -> %s\n", chuyen.getId(), chuyen.getDiemKhoiHanh(), chuyen.getDiemDen()));
                sb.append("Thời gian: ").append(chuyen.getNgayKhoiHanh() != null ? sdf.format(chuyen.getNgayKhoiHanh()) : "N/A").append("\n");
                sb.append("Tổng tiền: ").append(String.format("%,.0f VNĐ", ve.getSoVe() * chuyen.getGiaVe())).append("\n");
            } else {
                sb.append("Chuyến xe ID: ").append(ve.getIdChuyenXe()).append(" (Không tìm thấy TT chuyến)\n").append("Tổng tiền: Không xác định\n");
            }
            sb.append("Người đặt: ").append(ve.getTenNguoiDung()).append("\n");
            sb.append("Số vé: ").append(ve.getSoVe()).append("\n");
            sb.append("Vị trí ghế: ").append(gheStr).append("\n");
            sb.append("Trạng thái: ").append(mapTrangThaiToDisplay(ve.getTrangThai())).append("\n");
            sb.append("Dịch vụ đưa đón: ").append(ve.isDichVuDuaDon() ? "Có" : "Không");
            if (ve.isDichVuDuaDon()) {
                sb.append(" | Địa chỉ: ").append(ve.getDiaChiDon());
            }
            sb.append("\n------------------------------------------\n");
        }

        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        textArea.setCaretPosition(0);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(600, 400));
        JOptionPane.showMessageDialog(this, scrollPane, "Quản Lý Vé", JOptionPane.INFORMATION_MESSAGE);
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
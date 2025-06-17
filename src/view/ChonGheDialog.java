package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;

public class ChonGheDialog extends JDialog {
    private final Map<String, JToggleButton> gheButtons = new LinkedHashMap<>(); // Thêm final
    private final List<String> gheDuocChon = new ArrayList<>(); // Thêm final
    private final JLabel labelDaChon; // Thêm final
    private final int soLuongToiDa; // Thêm final

    private static final Set<String> GHE_VIP = new HashSet<>(Arrays.asList("A1", "A2", "B1", "B2"));
    private static final Color BACKGROUND_COLOR = new Color(245, 245, 250);
    private static final Color GHE_TRONG_COLOR = new Color(220, 240, 255);
    private static final Color GHE_DANG_CHON_COLOR = new Color(70, 130, 180);
    private static final Color GHE_DA_DAT_COLOR = new Color(200, 200, 200);


    public ChonGheDialog(JFrame parent, int soLuongGhe, Set<String> gheDaDat) {
        super(parent, "Chọn ghế - Chuyến 1", true);
        this.soLuongToiDa = soLuongGhe;
        setSize(1100, 800);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(BACKGROUND_COLOR);
        setLayout(new BorderLayout(15, 15));

        // Tiêu đề
        JLabel titleLabel = new JLabel("Vui lòng chọn " + soLuongToiDa + " ghế", JLabel.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(50, 50, 50));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);

        // Panel ghế
        JPanel ghePanel = new JPanel(new GridBagLayout());
        ghePanel.setBackground(BACKGROUND_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);

        // Khu vực trên (A1-A10, B1-B10)
        for (int row = 0; row < 2; row++) {
            for (int col = 0; col < 10; col++) {
                gbc.gridx = col;
                gbc.gridy = row;
                String ghe = (row == 0 ? "A" : "B") + (col + 1);
                ghePanel.add(makeGheButton(ghe, gheDaDat), gbc);
            }
        }

        // Lối đi
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 10;
        JLabel loiDi = new JLabel("LỐI ĐI");
        loiDi.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        loiDi.setForeground(new Color(100, 100, 100));
        loiDi.setHorizontalAlignment(JLabel.CENTER);
        loiDi.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, new Color(180, 180, 180)));
        ghePanel.add(loiDi, gbc);
        gbc.gridwidth = 1;

        // Khu vực dưới (A11-A20, B11-B20)
        for (int row = 3; row < 5; row++) {
            for (int col = 0; col < 10; col++) {
                gbc.gridx = col;
                gbc.gridy = row;
                String ghe = (row == 3 ? "A" : "B") + (col + 11);
                ghePanel.add(makeGheButton(ghe, gheDaDat), gbc);
            }
        }

        // Nhãn "Đã chọn"
        labelDaChon = new JLabel("Đã chọn: (Chưa chọn ghế nào)");
        labelDaChon.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        labelDaChon.setForeground(new Color(60, 60, 60));
        labelDaChon.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Chú thích (Legend)
        JPanel legend = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        legend.setBackground(BACKGROUND_COLOR);
        legend.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(180, 180, 180)), "Chú thích", 0, 0, new Font("Segoe UI", Font.PLAIN, 12)));
        legend.add(makeLegendItem(GHE_TRONG_COLOR, "Ghế trống"));
        legend.add(makeLegendItem(GHE_DANG_CHON_COLOR, "Đang chọn"));
        legend.add(makeLegendItem(GHE_DA_DAT_COLOR, "Đã đặt"));

        // Nút bấm
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(BACKGROUND_COLOR);
        JButton btnHuy = new JButton("Hủy");
        JButton btnXacNhan = new JButton("Xác nhận");
        styleButton(btnHuy, new Color(200, 50, 50));
        styleButton(btnXacNhan, new Color(50, 150, 50));
        btnXacNhan.addActionListener(e -> dispose());
        btnHuy.addActionListener(e -> {
            gheDuocChon.clear();
            dispose();
        });
        bottomPanel.add(btnHuy);
        bottomPanel.add(btnXacNhan);

        // Panel phía dưới
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.setBackground(BACKGROUND_COLOR);
        southPanel.add(labelDaChon, BorderLayout.NORTH);
        southPanel.add(legend, BorderLayout.CENTER);
        southPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(ghePanel, BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);
    }

    private JToggleButton makeGheButton(String ghe, Set<String> gheDaDat) {
        JToggleButton btn = new JToggleButton(ghe);
        btn.setPreferredSize(new Dimension(60, 40));
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setBackground(GHE_TRONG_COLOR);
        btn.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180), 1, true));
        btn.setOpaque(true);

        if (gheDaDat.contains(ghe)) {
            btn.setEnabled(false);
            btn.setBackground(GHE_DA_DAT_COLOR);
            btn.setToolTipText("Ghế đã đặt");
        }
        // Hệu ứng hover
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (btn.isEnabled() && !btn.isSelected()) {
                    btn.setBorder(BorderFactory.createLineBorder(new Color(100, 150, 200), 2, true));
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (btn.isEnabled() && !btn.isSelected()) {
                    btn.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180), 1, true));
                }
            }
        });

        btn.addActionListener(e -> {
            if (btn.isSelected()) {
                if (gheDuocChon.size() < soLuongToiDa) {
                    gheDuocChon.add(ghe);
                    btn.setBackground(GHE_DANG_CHON_COLOR);
                    btn.setBorder(BorderFactory.createLineBorder(new Color(30, 100, 150), 2, true));
                } else {
                    btn.setSelected(false);
                    JOptionPane.showMessageDialog(this, "Bạn chỉ được chọn tối đa " + soLuongToiDa + " ghế.", "Giới hạn ghế", JOptionPane.WARNING_MESSAGE);
                }
            } else {
                gheDuocChon.remove(ghe);

                btn.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180), 1, true));
            }
            capNhatLabelDaChon();
        });

        gheButtons.put(ghe, btn);
        return btn;
    }

    private JPanel makeLegendItem(Color color_, String text) { // Sửa lỗi: color thành color_
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        panel.setBackground(BACKGROUND_COLOR);
        JPanel colorBox = new JPanel();
        colorBox.setBackground(color_); // Sửa lỗi: color thành color_
        colorBox.setPreferredSize(new Dimension(20, 20));
        colorBox.setBorder(BorderFactory.createLineBorder(new Color(150, 150, 150)));
        panel.add(colorBox);
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panel.add(label);
        return panel;
    }

    private void styleButton(JButton button, Color color) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hiệu ứng hover cho nút
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(color.brighter());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(color);
            }
        });
    }

    private void capNhatLabelDaChon() {
        if (gheDuocChon.isEmpty()) {
            labelDaChon.setText("Đã chọn: (Chưa chọn ghế nào)");
        } else {
            labelDaChon.setText("Đã chọn: " + String.join(", ", gheDuocChon));
        }
    }

    public List<String> showDialog() {
        setVisible(true);
        return gheDuocChon;
    }
}
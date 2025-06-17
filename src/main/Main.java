package main;

import view.LoginView;
import javax.swing.*;
import com.formdev.flatlaf.FlatLightLaf;

public class Main {
    public static void main(String[] args) {

        try {

            UIManager.setLookAndFeel( new FlatLightLaf() );

        } catch (Exception e) {
            System.err.println("Không thể khởi tạo giao diện FlatLaf: " + e.getMessage());

        }


        SwingUtilities.invokeLater(() -> {

            new LoginView().setVisible(true);
        });
    }


}
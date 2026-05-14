package UI.Main;

import UI.CryptoToolFrame;

import javax.swing.*;

// Điểm khởi chạy ứng dụng Crypto Tool
public class ClassicCipherToolUI {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            new CryptoToolFrame().setVisible(true);
        });
    }
}

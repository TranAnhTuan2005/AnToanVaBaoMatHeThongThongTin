package UI;

import javax.swing.*;

public class ClassicCipherToolUI {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }
            new CryptoToolFrame().setVisible(true);
        });
    }
}
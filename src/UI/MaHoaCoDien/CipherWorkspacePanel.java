package UI.MaHoaCoDien;

import UI.AlgorithmItem;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

// Panel workspace cho phần mã hóa cổ điển (Shift, Substitution, Affine,...)
public class CipherWorkspacePanel extends JPanel {
    private final JLabel titleLabel = new JLabel("Crypto Tool");
    private final JLabel subTitleLabel = new JLabel("Chọn một giải thuật từ thanh bên trái để bắt đầu");

    private final JTextField keyField = new JTextField();
    private final JTextArea inputArea = new JTextArea();
    private final JTextArea outputArea = new JTextArea();
    private final JLabel hintLabel = new JLabel();
    private final JLabel panelTitle = new JLabel("Mã hóa cổ điển");

    private final JPanel welcomePanel = new JPanel(new GridBagLayout());
    private final JPanel workPanel = new JPanel(new BorderLayout(8, 8));
    private final CardLayout cardLayout = new CardLayout();
    private CipherAdapter activeAdapter;

    public CipherWorkspacePanel() {
        setLayout(cardLayout);
        setBackground(new Color(238, 238, 238));
        buildWelcomePanel();
        buildWorkPanel();
        add(welcomePanel, "welcome");
        add(workPanel, "work");
        showWelcome("Crypto Tool", "Chọn một giải thuật từ thanh bên trái để bắt đầu");
    }

    // Dựng panel chào mừng (hiển thị khi chưa chọn thuật toán)
    private void buildWelcomePanel() {
        welcomePanel.setBackground(new Color(238, 238, 238));
        JPanel box = new JPanel();
        box.setOpaque(false);
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));

        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 40));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subTitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 34));
        subTitleLabel.setForeground(new Color(96, 109, 123));
        subTitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        box.add(titleLabel);
        box.add(Box.createVerticalStrut(8));
        box.add(subTitleLabel);
        welcomePanel.add(box);
    }

    // Dựng panel làm việc chính: ô key, nút hành động, input/output
    private void buildWorkPanel() {
        workPanel.setBackground(new Color(245, 245, 245));
        workPanel.setBorder(new EmptyBorder(18, 18, 18, 18));

        panelTitle.setFont(new Font("SansSerif", Font.BOLD, 24));
        hintLabel.setForeground(new Color(106, 115, 125));

        JPanel north = new JPanel();
        north.setOpaque(false);
        north.setLayout(new BoxLayout(north, BoxLayout.Y_AXIS));
        north.add(panelTitle);
        north.add(Box.createVerticalStrut(6));
        north.add(hintLabel);
        north.add(Box.createVerticalStrut(12));

        // hàng nhập key
        JPanel keyRow = new JPanel(new BorderLayout(8, 8));
        keyRow.setOpaque(false);
        keyRow.add(new JLabel("Khóa:"), BorderLayout.WEST);
        keyRow.add(keyField, BorderLayout.CENTER);

        // các nút chức năng
        JButton generateBtn = new JButton("Sinh key");
        JButton loadKeyBtn = new JButton("Tải key");
        JButton saveKeyBtn = new JButton("Lưu key");
        JButton encryptBtn = new JButton("Mã hóa ➜");
        JButton decryptBtn = new JButton("⇦ Giải mã");
        JButton clearBtn = new JButton("Xóa");

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        actions.setOpaque(false);
        actions.add(generateBtn);
        actions.add(loadKeyBtn);
        actions.add(saveKeyBtn);
        actions.add(encryptBtn);
        actions.add(decryptBtn);
        actions.add(clearBtn);

        north.add(keyRow);
        north.add(Box.createVerticalStrut(8));
        north.add(actions);

        inputArea.setLineWrap(true);
        inputArea.setWrapStyleWord(true);
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);

        // chia đôi input/output
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                wrapWithTitle("Input text", new JScrollPane(inputArea)),
                wrapWithTitle("Output text", new JScrollPane(outputArea)));
        split.setResizeWeight(0.5);

        workPanel.add(north, BorderLayout.NORTH);
        workPanel.add(split, BorderLayout.CENTER);

        // gắn sự kiện cho các nút
        generateBtn.addActionListener(e -> onGenerate());
        loadKeyBtn.addActionListener(e -> onLoadKey());
        saveKeyBtn.addActionListener(e -> onSaveKey());
        encryptBtn.addActionListener(e -> onEncrypt());
        decryptBtn.addActionListener(e -> onDecrypt());
        clearBtn.addActionListener(e -> {
            inputArea.setText("");
            outputArea.setText("");
        });
    }

    // Bọc component trong panel có border title
    private JPanel wrapWithTitle(String title, JComponent content) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createTitledBorder(title));
        p.add(content, BorderLayout.CENTER);
        return p;
    }

    // Hiện màn hình chào mừng
    public void showWelcome(String title, String message) {
        titleLabel.setText(title);
        subTitleLabel.setText(message);
        cardLayout.show(this, "welcome");
    }

    // Chuyển sang thuật toán được chọn, cập nhật giao diện
    public void setAlgorithm(AlgorithmItem item) {
        activeAdapter = item.adapter();
        panelTitle.setText("Mã hóa cổ điển — " + item.displayName());
        hintLabel.setText("Gợi ý key: " + activeAdapter.keyHint());
        inputArea.setText("");
        outputArea.setText("");
        keyField.setText("");
        cardLayout.show(this, "work");
    }

    // Sinh key ngẫu nhiên
    private void onGenerate() {
        if (activeAdapter == null) return;
        try {
            keyField.setText(activeAdapter.generateKey());
        } catch (Exception ex) {
            showError("Không thể sinh key", ex);
        }
    }

    // Tải key từ file
    private void onLoadKey() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Tải key từ file");
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                byte[] raw = Files.readAllBytes(fc.getSelectedFile().toPath());
                String text = new String(raw, StandardCharsets.UTF_8).trim();
                keyField.setText(text);
                JOptionPane.showMessageDialog(this,
                        "Đã tải key thành công từ:\n" + fc.getSelectedFile().getName(),
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                showError("Không thể tải key", ex);
            }
        }
    }

    // Lưu key hiện tại ra file
    private void onSaveKey() {
        String key = keyField.getText().trim();
        if (key.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Chưa có key để lưu. Vui lòng sinh hoặc nhập key trước.",
                    "Thiếu key", JOptionPane.WARNING_MESSAGE);
            return;
        }
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Lưu key ra file");
        fc.setSelectedFile(new File("key.txt"));
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                Files.write(fc.getSelectedFile().toPath(), key.getBytes(StandardCharsets.UTF_8));
                JOptionPane.showMessageDialog(this,
                        "Đã lưu key thành công tại:\n" + fc.getSelectedFile().getAbsolutePath(),
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                showError("Không thể lưu key", ex);
            }
        }
    }

    // Mã hóa input text bằng key hiện tại
    private void onEncrypt() {
        if (activeAdapter == null) return;
        try {
            String key = keyField.getText().trim();
            String plain = inputArea.getText();
            if (plain.isEmpty() || key.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập input text và key.",
                        "Thiếu dữ liệu", JOptionPane.WARNING_MESSAGE);
                return;
            }
            outputArea.setText(activeAdapter.encrypt(plain, key));
        } catch (Exception ex) {
            showError("Lỗi mã hóa", ex);
        }
    }

    // Giải mã output text bằng key hiện tại
    private void onDecrypt() {
        if (activeAdapter == null) return;
        try {
            String key = keyField.getText().trim();
            String cipher = outputArea.getText();
            if (cipher.isEmpty() || key.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập output text và key.",
                        "Thiếu dữ liệu", JOptionPane.WARNING_MESSAGE);
                return;
            }
            inputArea.setText(activeAdapter.decrypt(cipher, key));
        } catch (Exception ex) {
            showError("Lỗi giải mã", ex);
        }
    }

    // Hiện dialog lỗi
    private void showError(String title, Exception ex) {
        JOptionPane.showMessageDialog(this,
                title + "\n" + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
}

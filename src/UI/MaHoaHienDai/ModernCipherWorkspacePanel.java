package UI.MaHoaHienDai;

import UI.AlgorithmItem;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class ModernCipherWorkspacePanel extends JPanel {
    private final JLabel titleLabel = new JLabel("Crypto Tool");
    private final JLabel subTitleLabel = new JLabel("Chọn một giải thuật mã hóa hiện đại đối xứng");

    private final JPanel welcomePanel = new JPanel(new GridBagLayout());
    private final JPanel workPanel = new JPanel(new BorderLayout(8, 8));
    private final CardLayout cardLayout = new CardLayout();

    private final JComboBox<String> modeCombo = new JComboBox<>();
    private final JComboBox<String> paddingCombo = new JComboBox<>();
    private final JComboBox<String> keySizeCombo = new JComboBox<>();
    private final JTextField keyField = new JTextField();
    private final JTextArea inputArea = new JTextArea();
    private final JTextArea outputArea = new JTextArea();
    private final JTextField inputFileField = new JTextField();
    private final JTextField outputFileField = new JTextField();
    private final JLabel statusLabel = new JLabel(" ");
    private final JLabel panelTitle = new JLabel("Mã hóa hiện đại đối xứng");

    private ModernSymmetricCipher activeCipher;
    private boolean updatingCombos = false;

    public ModernCipherWorkspacePanel() {
        setLayout(cardLayout);
        setBackground(new Color(238, 238, 238));
        buildWelcomePanel();
        buildWorkPanel();
        add(welcomePanel, "welcome");
        add(workPanel, "work");
        showWelcome("Crypto Tool", "Chọn một giải thuật mã hóa hiện đại đối xứng");
    }

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

    private void buildWorkPanel() {
        workPanel.setBackground(new Color(245, 245, 245));
        workPanel.setBorder(new EmptyBorder(18, 18, 18, 18));

        JPanel north = new JPanel();
        north.setOpaque(false);
        north.setLayout(new BoxLayout(north, BoxLayout.Y_AXIS));

        panelTitle.setFont(new Font("SansSerif", Font.BOLD, 24));
        panelTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        north.add(panelTitle);
        north.add(Box.createVerticalStrut(12));

        JPanel configRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        configRow.setOpaque(false);
        configRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        configRow.add(new JLabel("Mode:"));
        modeCombo.setPreferredSize(new Dimension(120, 28));
        configRow.add(modeCombo);
        configRow.add(new JLabel("Padding:"));
        paddingCombo.setPreferredSize(new Dimension(150, 28));
        configRow.add(paddingCombo);
        configRow.add(new JLabel("Key size:"));
        keySizeCombo.setPreferredSize(new Dimension(80, 28));
        configRow.add(keySizeCombo);
        configRow.add(new JLabel("bits"));
        north.add(configRow);
        north.add(Box.createVerticalStrut(10));

        JPanel keyRow = new JPanel(new BorderLayout(8, 0));
        keyRow.setOpaque(false);
        keyRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        keyRow.add(new JLabel("Khóa (Base64): "), BorderLayout.WEST);
        keyRow.add(keyField, BorderLayout.CENTER);

        JPanel keyBtnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        keyBtnPanel.setOpaque(false);
        JButton genKeyBtn = new JButton("Sinh key");
        JButton loadKeyBtn = new JButton("Tải key");
        JButton saveKeyBtn = new JButton("Lưu key");
        keyBtnPanel.add(genKeyBtn);
        keyBtnPanel.add(loadKeyBtn);
        keyBtnPanel.add(saveKeyBtn);
        keyRow.add(keyBtnPanel, BorderLayout.EAST);
        north.add(keyRow);
        north.add(Box.createVerticalStrut(6));

        workPanel.add(north, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Văn bản", buildTextTab());
        tabbedPane.addTab("Tệp tin", buildFileTab());
        workPanel.add(tabbedPane, BorderLayout.CENTER);

        modeCombo.addActionListener(e -> {
            if (!updatingCombos) onModeChanged();
        });
        genKeyBtn.addActionListener(e -> onGenKey());
        loadKeyBtn.addActionListener(e -> onLoadKey());
        saveKeyBtn.addActionListener(e -> onSaveKey());
    }

    private JPanel buildTextTab() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(new EmptyBorder(8, 0, 0, 0));
        panel.setOpaque(false);

        inputArea.setLineWrap(true);
        inputArea.setWrapStyleWord(true);
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                titled("Input", new JScrollPane(inputArea)),
                titled("Output", new JScrollPane(outputArea)));
        split.setResizeWeight(0.5);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnPanel.setOpaque(false);
        JButton encryptBtn = new JButton("Mã hóa ➜");
        JButton decryptBtn = new JButton("⇦ Giải mã");
        JButton clearBtn = new JButton("Xóa");
        btnPanel.add(encryptBtn);
        btnPanel.add(decryptBtn);
        btnPanel.add(clearBtn);

        panel.add(split, BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);

        encryptBtn.addActionListener(e -> onEncryptText());
        decryptBtn.addActionListener(e -> onDecryptText());
        clearBtn.addActionListener(e -> {
            inputArea.setText("");
            outputArea.setText("");
        });
        return panel;
    }

    private JPanel buildFileTab() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(16, 0, 0, 0));

        JPanel inputRow = new JPanel(new BorderLayout(8, 0));
        inputRow.setOpaque(false);
        inputRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        inputRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        inputRow.add(fixedLabel("File nguồn:"), BorderLayout.WEST);
        inputRow.add(inputFileField, BorderLayout.CENTER);
        JButton browseIn = new JButton("Chọn...");
        inputRow.add(browseIn, BorderLayout.EAST);
        panel.add(inputRow);
        panel.add(Box.createVerticalStrut(10));

        JPanel outputRow = new JPanel(new BorderLayout(8, 0));
        outputRow.setOpaque(false);
        outputRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        outputRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        outputRow.add(fixedLabel("File đích:    "), BorderLayout.WEST);
        outputRow.add(outputFileField, BorderLayout.CENTER);
        JButton browseOut = new JButton("Chọn...");
        outputRow.add(browseOut, BorderLayout.EAST);
        panel.add(outputRow);
        panel.add(Box.createVerticalStrut(14));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnPanel.setOpaque(false);
        btnPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JButton encFileBtn = new JButton("Mã hóa File");
        JButton decFileBtn = new JButton("Giải mã File");
        btnPanel.add(encFileBtn);
        btnPanel.add(decFileBtn);
        panel.add(btnPanel);
        panel.add(Box.createVerticalStrut(10));

        statusLabel.setForeground(new Color(60, 130, 60));
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(statusLabel);
        panel.add(Box.createVerticalGlue());

        browseIn.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
                inputFileField.setText(fc.getSelectedFile().getAbsolutePath());
        });
        browseOut.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION)
                outputFileField.setText(fc.getSelectedFile().getAbsolutePath());
        });
        encFileBtn.addActionListener(e -> onEncryptFile());
        decFileBtn.addActionListener(e -> onDecryptFile());

        return panel;
    }

    private JLabel fixedLabel(String text) {
        JLabel label = new JLabel(text);
        label.setPreferredSize(new Dimension(90, 28));
        return label;
    }

    private JPanel titled(String title, JComponent content) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createTitledBorder(title));
        p.add(content, BorderLayout.CENTER);
        return p;
    }

    public void showWelcome(String title, String message) {
        titleLabel.setText(title);
        subTitleLabel.setText(message);
        cardLayout.show(this, "welcome");
    }

    public void setAlgorithm(AlgorithmItem item) {
        activeCipher = item.modernCipher();
        panelTitle.setText("Mã hóa hiện đại đối xứng — " + activeCipher.algorithmName());

        updatingCombos = true;
        modeCombo.removeAllItems();
        for (String m : activeCipher.supportedModes()) modeCombo.addItem(m);

        keySizeCombo.removeAllItems();
        for (int s : activeCipher.supportedKeySizes()) keySizeCombo.addItem(String.valueOf(s));
        keySizeCombo.setSelectedIndex(keySizeCombo.getItemCount() - 1);
        updatingCombos = false;
        onModeChanged();

        keyField.setText("");
        inputArea.setText("");
        outputArea.setText("");
        inputFileField.setText("");
        outputFileField.setText("");
        statusLabel.setText(" ");
        cardLayout.show(this, "work");
    }

    private void onModeChanged() {
        if (activeCipher == null) return;
        String mode = (String) modeCombo.getSelectedItem();
        if (mode == null) return;
        updatingCombos = true;
        paddingCombo.removeAllItems();
        for (String p : activeCipher.supportedPaddings(mode)) paddingCombo.addItem(p);
        updatingCombos = false;
    }

    private String getMode() {
        return (String) modeCombo.getSelectedItem();
    }

    private String getPadding() {
        return (String) paddingCombo.getSelectedItem();
    }

    private int getKeySize() {
        String s = (String) keySizeCombo.getSelectedItem();
        return s != null ? Integer.parseInt(s) : 256;
    }

    private void onGenKey() {
        if (activeCipher == null) return;
        try {
            keyField.setText(activeCipher.generateKeyBase64(getKeySize()));
        } catch (Exception ex) {
            showError("Không thể sinh key", ex);
        }
    }

    private void onLoadKey() {
        JFileChooser fc = new JFileChooser();
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                byte[] raw = Files.readAllBytes(fc.getSelectedFile().toPath());
                String text = new String(raw, StandardCharsets.UTF_8).trim();
                try {
                    java.util.Base64.getDecoder().decode(text);
                    keyField.setText(text);
                } catch (IllegalArgumentException e) {
                    keyField.setText(java.util.Base64.getEncoder().encodeToString(raw));
                }
            } catch (IOException ex) {
                showError("Không thể đọc file key", ex);
            }
        }
    }

    private void onSaveKey() {
        String key = keyField.getText().trim();
        if (key.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Chưa có key để lưu.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        JFileChooser fc = new JFileChooser();
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                Files.write(fc.getSelectedFile().toPath(), key.getBytes(StandardCharsets.UTF_8));
                JOptionPane.showMessageDialog(this, "Đã lưu key thành công.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                showError("Không thể lưu key", ex);
            }
        }
    }

    private void onEncryptText() {
        if (activeCipher == null) return;
        try {
            String key = keyField.getText().trim();
            String plain = inputArea.getText();
            if (plain.isEmpty() || key.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập text và key.", "Thiếu dữ liệu", JOptionPane.WARNING_MESSAGE);
                return;
            }
            outputArea.setText(activeCipher.encryptText(plain, key, getMode(), getPadding()));
        } catch (Exception ex) {
            showError("Lỗi mã hóa", ex);
        }
    }

    private void onDecryptText() {
        if (activeCipher == null) return;
        try {
            String key = keyField.getText().trim();
            String cipher = outputArea.getText().trim();
            if (cipher.isEmpty() || key.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập output text và key.", "Thiếu dữ liệu", JOptionPane.WARNING_MESSAGE);
                return;
            }
            inputArea.setText(activeCipher.decryptText(cipher, key, getMode(), getPadding()));
        } catch (Exception ex) {
            showError("Lỗi giải mã", ex);
        }
    }

    private void onEncryptFile() {
        if (activeCipher == null) return;
        try {
            String key = keyField.getText().trim();
            String src = inputFileField.getText().trim();
            String dest = outputFileField.getText().trim();
            if (key.isEmpty() || src.isEmpty() || dest.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ key, file nguồn và file đích.", "Thiếu dữ liệu", JOptionPane.WARNING_MESSAGE);
                return;
            }
            activeCipher.encryptFile(src, dest, key, getMode(), getPadding());
            statusLabel.setForeground(new Color(60, 130, 60));
            statusLabel.setText("Mã hóa file thành công!");
        } catch (Exception ex) {
            statusLabel.setForeground(Color.RED);
            statusLabel.setText("Lỗi: " + ex.getMessage());
        }
    }

    private void onDecryptFile() {
        if (activeCipher == null) return;
        try {
            String key = keyField.getText().trim();
            String src = inputFileField.getText().trim();
            String dest = outputFileField.getText().trim();
            if (key.isEmpty() || src.isEmpty() || dest.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ key, file nguồn và file đích.", "Thiếu dữ liệu", JOptionPane.WARNING_MESSAGE);
                return;
            }
            activeCipher.decryptFile(src, dest, key, getMode(), getPadding());
            statusLabel.setForeground(new Color(60, 130, 60));
            statusLabel.setText("Giải mã file thành công!");
        } catch (Exception ex) {
            statusLabel.setForeground(Color.RED);
            statusLabel.setText("Lỗi: " + ex.getMessage());
        }
    }

    private void showError(String title, Exception ex) {
        JOptionPane.showMessageDialog(this,
                title + "\n" + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
}
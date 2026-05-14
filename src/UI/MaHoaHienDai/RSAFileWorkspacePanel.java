package UI.MaHoaHienDai;

import MaHoaHienDai.MaHoaBatDoiXung.RSAFileEncryption;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.security.PrivateKey;
import java.security.PublicKey;

// Panel workspace cho mã hóa bất đối xứng RSA (hybrid: RSA + thuật toán đối xứng)
public class RSAFileWorkspacePanel extends JPanel {
    private final JLabel titleLabel = new JLabel("Crypto Tool");
    private final JLabel subTitleLabel = new JLabel("Chọn mã hóa bất đối xứng RSA");

    private final JPanel welcomePanel = new JPanel(new GridBagLayout());
    private final JPanel workPanel = new JPanel(new BorderLayout(8, 8));
    private final CardLayout cardLayout = new CardLayout();

    // cấu hình RSA key size và thuật toán đối xứng kèm theo
    private final JComboBox<String> rsaKeySizeCombo = new JComboBox<>(new String[]{"1024", "2048", "4096"});
    private final JComboBox<String> symAlgorithmCombo = new JComboBox<>();
    private final JComboBox<String> symKeySizeCombo = new JComboBox<>();

    // đường dẫn key đã tải
    private final JTextField publicKeyField = new JTextField();
    private final JTextField privateKeyField = new JTextField();

    private final JTextArea inputArea = new JTextArea();
    private final JTextArea outputArea = new JTextArea();
    private final JTextField inputFileField = new JTextField();
    private final JTextField outputFileField = new JTextField();
    private final JLabel statusLabel = new JLabel(" ");

    private PublicKey loadedPublicKey;
    private PrivateKey loadedPrivateKey;

    public RSAFileWorkspacePanel() {
        setLayout(cardLayout);
        setBackground(new Color(238, 238, 238));
        buildWelcomePanel();
        buildWorkPanel();
        add(welcomePanel, "welcome");
        add(workPanel, "work");
        showWelcome("Crypto Tool", "Chọn mã hóa bất đối xứng RSA");
    }

    // Dựng panel chào mừng
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

    // Dựng panel làm việc: tạo key RSA, tải key, chọn thuật toán đối xứng, 2 tab text/file
    private void buildWorkPanel() {
        workPanel.setBackground(new Color(245, 245, 245));
        workPanel.setBorder(new EmptyBorder(18, 18, 18, 18));

        JPanel north = new JPanel();
        north.setOpaque(false);
        north.setLayout(new BoxLayout(north, BoxLayout.Y_AXIS));

        JLabel panelTitle = new JLabel("Mã hóa bất đối xứng — RSA");
        panelTitle.setFont(new Font("SansSerif", Font.BOLD, 24));
        panelTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        north.add(panelTitle);
        north.add(Box.createVerticalStrut(12));

        north.add(buildRSAKeySection());
        north.add(Box.createVerticalStrut(10));
        north.add(buildKeyLoadSection());
        north.add(Box.createVerticalStrut(10));
        north.add(buildSymConfigSection());
        north.add(Box.createVerticalStrut(6));

        workPanel.add(north, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Văn bản", buildTextTab());
        tabs.addTab("Tệp tin", buildFileTab());
        workPanel.add(tabs, BorderLayout.CENTER);

        initSymAlgorithms();
    }

    // Section chọn RSA key size và nút tạo cặp khóa
    private JPanel buildRSAKeySection() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(new JLabel("RSA Key Size:"));
        rsaKeySizeCombo.setPreferredSize(new Dimension(80, 28));
        rsaKeySizeCombo.setSelectedItem("2048");
        panel.add(rsaKeySizeCombo);
        panel.add(new JLabel("bits"));
        panel.add(Box.createHorizontalStrut(12));

        JButton genKeyPairBtn = new JButton("Tạo cặp khóa RSA");
        genKeyPairBtn.addActionListener(e -> onGenerateKeyPair());
        panel.add(genKeyPairBtn);

        return panel;
    }

    // Section tải Public Key và Private Key từ file
    private JPanel buildKeyLoadSection() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // hàng Public Key
        JPanel pubRow = new JPanel(new BorderLayout(8, 0));
        pubRow.setOpaque(false);
        pubRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        pubRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        pubRow.add(fixedLabel("Public Key:  ", 110), BorderLayout.WEST);
        publicKeyField.setEditable(false);
        pubRow.add(publicKeyField, BorderLayout.CENTER);
        JButton loadPubBtn = new JButton("Tải Public Key");
        loadPubBtn.addActionListener(e -> onLoadPublicKey());
        pubRow.add(loadPubBtn, BorderLayout.EAST);
        panel.add(pubRow);
        panel.add(Box.createVerticalStrut(6));

        // hàng Private Key
        JPanel priRow = new JPanel(new BorderLayout(8, 0));
        priRow.setOpaque(false);
        priRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        priRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        priRow.add(fixedLabel("Private Key: ", 110), BorderLayout.WEST);
        privateKeyField.setEditable(false);
        priRow.add(privateKeyField, BorderLayout.CENTER);
        JButton loadPriBtn = new JButton("Tải Private Key");
        loadPriBtn.addActionListener(e -> onLoadPrivateKey());
        priRow.add(loadPriBtn, BorderLayout.EAST);
        panel.add(priRow);

        return panel;
    }

    // Section chọn thuật toán đối xứng đi kèm RSA (AES, DES,...)
    private JPanel buildSymConfigSection() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(new JLabel("Giải thuật đối xứng:"));
        symAlgorithmCombo.setPreferredSize(new Dimension(120, 28));
        panel.add(symAlgorithmCombo);

        panel.add(new JLabel("Key size:"));
        symKeySizeCombo.setPreferredSize(new Dimension(80, 28));
        panel.add(symKeySizeCombo);
        panel.add(new JLabel("bits"));

        symAlgorithmCombo.addActionListener(e -> onSymAlgorithmChanged());

        return panel;
    }

    // Nạp danh sách thuật toán đối xứng hỗ trợ
    private void initSymAlgorithms() {
        for (String[] alg : RSAFileEncryption.SUPPORTED_ALGORITHMS) {
            symAlgorithmCombo.addItem(alg[0]);
        }
        symAlgorithmCombo.setSelectedIndex(0);
        onSymAlgorithmChanged();
    }

    // Khi đổi thuật toán đối xứng thì cập nhật key size tương ứng
    private void onSymAlgorithmChanged() {
        String alg = (String) symAlgorithmCombo.getSelectedItem();
        if (alg == null) return;
        symKeySizeCombo.removeAllItems();
        for (int size : RSAFileEncryption.getKeySizes(alg)) {
            symKeySizeCombo.addItem(String.valueOf(size));
        }
        symKeySizeCombo.setSelectedIndex(symKeySizeCombo.getItemCount() - 1);
    }

    // Tab mã hóa/giải mã văn bản
    private JPanel buildTextTab() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(new EmptyBorder(8, 0, 0, 0));
        panel.setOpaque(false);

        inputArea.setLineWrap(true);
        inputArea.setWrapStyleWord(true);
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                wrapWithTitle("Input (Plaintext)", new JScrollPane(inputArea)),
                wrapWithTitle("Output (Ciphertext Base64)", new JScrollPane(outputArea)));
        split.setResizeWeight(0.5);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnPanel.setOpaque(false);
        JButton encryptBtn = new JButton("Mã hóa (Public Key) ➜");
        JButton decryptBtn = new JButton("⇦ Giải mã (Private Key)");
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

    // Tab mã hóa/giải mã tệp tin
    private JPanel buildFileTab() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(16, 0, 0, 0));

        JPanel inputRow = new JPanel(new BorderLayout(8, 0));
        inputRow.setOpaque(false);
        inputRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        inputRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        inputRow.add(fixedLabel("File nguồn:", 90), BorderLayout.WEST);
        inputRow.add(inputFileField, BorderLayout.CENTER);
        JButton browseIn = new JButton("Chọn...");
        inputRow.add(browseIn, BorderLayout.EAST);
        panel.add(inputRow);
        panel.add(Box.createVerticalStrut(10));

        JPanel outputRow = new JPanel(new BorderLayout(8, 0));
        outputRow.setOpaque(false);
        outputRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        outputRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        outputRow.add(fixedLabel("File đích:   ", 90), BorderLayout.WEST);
        outputRow.add(outputFileField, BorderLayout.CENTER);
        JButton browseOut = new JButton("Chọn...");
        outputRow.add(browseOut, BorderLayout.EAST);
        panel.add(outputRow);
        panel.add(Box.createVerticalStrut(14));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnPanel.setOpaque(false);
        btnPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JButton encFileBtn = new JButton("Mã hóa File (Public Key)");
        JButton decFileBtn = new JButton("Giải mã File (Private Key)");
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

    // Hiện màn hình chào mừng
    public void showWelcome(String title, String message) {
        titleLabel.setText(title);
        subTitleLabel.setText(message);
        cardLayout.show(this, "welcome");
    }

    // Reset trạng thái và hiện panel làm việc
    public void showWork() {
        loadedPublicKey = null;
        loadedPrivateKey = null;
        publicKeyField.setText("");
        privateKeyField.setText("");
        inputArea.setText("");
        outputArea.setText("");
        inputFileField.setText("");
        outputFileField.setText("");
        statusLabel.setText(" ");
        cardLayout.show(this, "work");
    }

    // Tạo cặp khóa RSA và lưu ra thư mục được chọn
    private void onGenerateKeyPair() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Chọn thư mục lưu cặp khóa RSA");
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

        String dir = fc.getSelectedFile().getAbsolutePath();
        int rsaKeySize = Integer.parseInt((String) rsaKeySizeCombo.getSelectedItem());

        try {
            String pubPath = dir + File.separator + "public.pub";
            String priPath = dir + File.separator + "private.key";
            RSAFileEncryption.generateKeyPair(rsaKeySize, pubPath, priPath);

            // tải luôn key vừa tạo
            loadedPublicKey = RSAFileEncryption.readPublicKey(pubPath);
            loadedPrivateKey = RSAFileEncryption.readPrivateKey(priPath);
            publicKeyField.setText(pubPath);
            privateKeyField.setText(priPath);

            JOptionPane.showMessageDialog(this,
                    "Đã tạo cặp khóa RSA " + rsaKeySize + " bits:\n" +
                            "Public Key: " + pubPath + "\n" +
                            "Private Key: " + priPath,
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            showError("Không thể tạo cặp khóa RSA", ex);
        }
    }

    // Tải Public Key từ file .pub
    private void onLoadPublicKey() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Chọn file Public Key (.pub)");
        if (fc.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;
        try {
            String path = fc.getSelectedFile().getAbsolutePath();
            loadedPublicKey = RSAFileEncryption.readPublicKey(path);
            publicKeyField.setText(path);
        } catch (Exception ex) {
            showError("Không thể đọc Public Key", ex);
        }
    }

    // Tải Private Key từ file .key
    private void onLoadPrivateKey() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Chọn file Private Key (.key)");
        if (fc.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;
        try {
            String path = fc.getSelectedFile().getAbsolutePath();
            loadedPrivateKey = RSAFileEncryption.readPrivateKey(path);
            privateKeyField.setText(path);
        } catch (Exception ex) {
            showError("Không thể đọc Private Key", ex);
        }
    }

    private String getSymAlgorithm() {
        return (String) symAlgorithmCombo.getSelectedItem();
    }

    private int getSymKeySize() {
        String s = (String) symKeySizeCombo.getSelectedItem();
        return s != null ? Integer.parseInt(s) : 256;
    }

    // Mã hóa văn bản bằng Public Key
    private void onEncryptText() {
        if (loadedPublicKey == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng tải Public Key trước.",
                    "Thiếu khóa", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String plain = inputArea.getText();
        if (plain.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập văn bản cần mã hóa.",
                    "Thiếu dữ liệu", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            String cipher = RSAFileEncryption.encryptText(loadedPublicKey, plain,
                    getSymAlgorithm(), getSymKeySize());
            outputArea.setText(cipher);
        } catch (Exception ex) {
            showError("Lỗi mã hóa văn bản", ex);
        }
    }

    // Giải mã văn bản bằng Private Key
    private void onDecryptText() {
        if (loadedPrivateKey == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng tải Private Key trước.",
                    "Thiếu khóa", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String cipher = outputArea.getText().trim();
        if (cipher.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập ciphertext (Base64) vào ô Output.",
                    "Thiếu dữ liệu", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            String plain = RSAFileEncryption.decryptText(loadedPrivateKey, cipher);
            inputArea.setText(plain);
        } catch (Exception ex) {
            showError("Lỗi giải mã", ex);
        }
    }

    // Mã hóa file bằng Public Key (hybrid RSA + symmetric)
    private void onEncryptFile() {
        if (loadedPublicKey == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng tải Public Key trước.",
                    "Thiếu khóa", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String src = inputFileField.getText().trim();
        String dest = outputFileField.getText().trim();
        if (src.isEmpty() || dest.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn file nguồn và file đích.",
                    "Thiếu dữ liệu", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            RSAFileEncryption.encryptFile(loadedPublicKey, src, dest,
                    getSymAlgorithm(), getSymKeySize());
            statusLabel.setForeground(new Color(60, 130, 60));
            statusLabel.setText("Mã hóa file thành công! (RSA + " + getSymAlgorithm() + ")");
        } catch (Exception ex) {
            statusLabel.setForeground(Color.RED);
            statusLabel.setText("Lỗi: " + ex.getMessage());
        }
    }

    // Giải mã file bằng Private Key
    private void onDecryptFile() {
        if (loadedPrivateKey == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng tải Private Key trước.",
                    "Thiếu khóa", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String src = inputFileField.getText().trim();
        String dest = outputFileField.getText().trim();
        if (src.isEmpty() || dest.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn file nguồn và file đích.",
                    "Thiếu dữ liệu", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            RSAFileEncryption.decryptFile(loadedPrivateKey, src, dest);
            statusLabel.setForeground(new Color(60, 130, 60));
            statusLabel.setText("Giải mã file thành công!");
        } catch (Exception ex) {
            statusLabel.setForeground(Color.RED);
            statusLabel.setText("Lỗi: " + ex.getMessage());
        }
    }

    // Tạo JLabel có chiều rộng cố định
    private JLabel fixedLabel(String text, int width) {
        JLabel label = new JLabel(text);
        label.setPreferredSize(new Dimension(width, 28));
        return label;
    }

    // Bọc component trong panel có border title
    private JPanel wrapWithTitle(String title, JComponent content) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createTitledBorder(title));
        p.add(content, BorderLayout.CENTER);
        return p;
    }

    // Hiện dialog lỗi
    private void showError(String title, Exception ex) {
        JOptionPane.showMessageDialog(this,
                title + "\n" + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
}

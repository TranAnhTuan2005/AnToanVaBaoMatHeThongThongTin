package UI.HamBam;

import HamBam.HashAlgorithm;
import UI.AlgorithmItem;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.datatransfer.StringSelection;

// Panel workspace cho phần hàm băm (MD5, SHA, RIPEMD-160, Whirlpool,...)
public class HashWorkspacePanel extends JPanel {
    private final JLabel titleLabel = new JLabel("Crypto Tool");
    private final JLabel subTitleLabel = new JLabel("Chọn một giải thuật hàm băm");

    private final JPanel welcomePanel = new JPanel(new GridBagLayout());
    private final JPanel workPanel = new JPanel(new BorderLayout(8, 8));
    private final CardLayout cardLayout = new CardLayout();

    private final JLabel panelTitle = new JLabel("Hàm băm");
    private final JLabel infoLabel = new JLabel(" ");
    private final JTextArea inputArea = new JTextArea();
    private final JTextArea hashOutputArea = new JTextArea(3, 0);
    private final JTextField fileField = new JTextField();
    private final JTextArea fileHashOutputArea = new JTextArea(3, 0);
    private final JTextField verifyField = new JTextField();
    private final JLabel verifyResultLabel = new JLabel(" ");

    private HashAlgorithm activeHash;

    public HashWorkspacePanel() {
        setLayout(cardLayout);
        setBackground(new Color(238, 238, 238));
        buildWelcomePanel();
        buildWorkPanel();
        add(welcomePanel, "welcome");
        add(workPanel, "work");
        showWelcome("Crypto Tool", "Chọn một giải thuật hàm băm");
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

    // Dựng panel làm việc với 2 tab: băm văn bản và băm tệp tin
    private void buildWorkPanel() {
        workPanel.setBackground(new Color(245, 245, 245));
        workPanel.setBorder(new EmptyBorder(18, 18, 18, 18));

        JPanel north = new JPanel();
        north.setOpaque(false);
        north.setLayout(new BoxLayout(north, BoxLayout.Y_AXIS));

        panelTitle.setFont(new Font("SansSerif", Font.BOLD, 24));
        panelTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        north.add(panelTitle);
        north.add(Box.createVerticalStrut(4));

        // label thông tin output bits và block size
        infoLabel.setFont(new Font("SansSerif", Font.ITALIC, 14));
        infoLabel.setForeground(new Color(100, 100, 100));
        infoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        north.add(infoLabel);
        north.add(Box.createVerticalStrut(10));

        workPanel.add(north, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Băm văn bản", buildTextTab());
        tabs.addTab("Băm tệp tin", buildFileTab());
        workPanel.add(tabs, BorderLayout.CENTER);
    }

    // Tab băm văn bản: nhập text -> băm -> hiện kết quả hex + xác minh
    private JPanel buildTextTab() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(new EmptyBorder(8, 0, 0, 0));
        panel.setOpaque(false);

        inputArea.setLineWrap(true);
        inputArea.setWrapStyleWord(true);
        inputArea.setFont(new Font("Monospaced", Font.PLAIN, 14));

        JPanel inputPanel = wrapWithTitle("Nhập văn bản (hỗ trợ tiếng Việt có dấu)", new JScrollPane(inputArea));

        JPanel south = new JPanel();
        south.setOpaque(false);
        south.setLayout(new BoxLayout(south, BoxLayout.Y_AXIS));

        // nút băm và xóa
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnRow.setOpaque(false);
        btnRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        JButton hashBtn = new JButton("Băm ▼");
        JButton clearBtn = new JButton("Xóa");
        btnRow.add(hashBtn);
        btnRow.add(clearBtn);
        south.add(btnRow);
        south.add(Box.createVerticalStrut(8));

        // ô hiển thị kết quả băm
        hashOutputArea.setLineWrap(true);
        hashOutputArea.setWrapStyleWord(true);
        hashOutputArea.setEditable(false);
        hashOutputArea.setFont(new Font("Monospaced", Font.BOLD, 14));
        hashOutputArea.setBackground(new Color(240, 248, 255));

        JPanel outputPanel = new JPanel(new BorderLayout(4, 0));
        outputPanel.setOpaque(false);
        outputPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        outputPanel.setBorder(BorderFactory.createTitledBorder("Kết quả băm (Hex)"));
        outputPanel.add(new JScrollPane(hashOutputArea), BorderLayout.CENTER);
        JButton copyBtn = new JButton("Sao chép");
        copyBtn.addActionListener(e -> copyToClipboard(hashOutputArea.getText()));
        JPanel copyPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        copyPanel.setOpaque(false);
        copyPanel.add(copyBtn);
        outputPanel.add(copyPanel, BorderLayout.SOUTH);
        outputPanel.setPreferredSize(new Dimension(0, 120));
        south.add(outputPanel);
        south.add(Box.createVerticalStrut(8));

        // phần xác minh hash
        south.add(buildVerifySection("textVerify"));

        panel.add(inputPanel, BorderLayout.CENTER);
        panel.add(south, BorderLayout.SOUTH);

        hashBtn.addActionListener(e -> onHashText());
        clearBtn.addActionListener(e -> {
            inputArea.setText("");
            hashOutputArea.setText("");
            verifyField.setText("");
            verifyResultLabel.setText(" ");
        });
        return panel;
    }

    // Tab băm tệp tin: chọn file -> băm -> hiện kết quả + xác minh
    private JPanel buildFileTab() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(16, 0, 0, 0));

        // hàng chọn file
        JPanel fileRow = new JPanel(new BorderLayout(8, 0));
        fileRow.setOpaque(false);
        fileRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        fileRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        fileRow.add(fixedLabel("Chọn tệp:", 80), BorderLayout.WEST);
        fileRow.add(fileField, BorderLayout.CENTER);
        JButton browseBtn = new JButton("Chọn...");
        fileRow.add(browseBtn, BorderLayout.EAST);
        panel.add(fileRow);
        panel.add(Box.createVerticalStrut(10));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnRow.setOpaque(false);
        btnRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        JButton hashFileBtn = new JButton("Băm tệp tin ▼");
        btnRow.add(hashFileBtn);
        panel.add(btnRow);
        panel.add(Box.createVerticalStrut(10));

        // ô hiển thị kết quả băm file
        fileHashOutputArea.setLineWrap(true);
        fileHashOutputArea.setWrapStyleWord(true);
        fileHashOutputArea.setEditable(false);
        fileHashOutputArea.setFont(new Font("Monospaced", Font.BOLD, 14));
        fileHashOutputArea.setBackground(new Color(240, 248, 255));

        JPanel outputPanel = new JPanel(new BorderLayout(4, 0));
        outputPanel.setOpaque(false);
        outputPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        outputPanel.setBorder(BorderFactory.createTitledBorder("Kết quả băm tệp (Hex)"));
        outputPanel.add(new JScrollPane(fileHashOutputArea), BorderLayout.CENTER);
        JButton copyBtn2 = new JButton("Sao chép");
        copyBtn2.addActionListener(e -> copyToClipboard(fileHashOutputArea.getText()));
        JPanel copyPanel2 = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        copyPanel2.setOpaque(false);
        copyPanel2.add(copyBtn2);
        outputPanel.add(copyPanel2, BorderLayout.SOUTH);
        outputPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));
        outputPanel.setPreferredSize(new Dimension(0, 130));
        panel.add(outputPanel);
        panel.add(Box.createVerticalStrut(10));

        // phần xác minh hash file
        panel.add(buildFileVerifySection());
        panel.add(Box.createVerticalGlue());

        browseBtn.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
                fileField.setText(fc.getSelectedFile().getAbsolutePath());
        });
        hashFileBtn.addActionListener(e -> onHashFile());

        return panel;
    }

    private final JTextField fileVerifyField = new JTextField();
    private final JLabel fileVerifyResultLabel = new JLabel(" ");

    // Dựng phần xác minh hash cho tab văn bản
    private JPanel buildVerifySection(String id) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setBorder(BorderFactory.createTitledBorder("Xác minh (so sánh mã băm)"));

        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        row.add(fixedLabel("Hash cần so:", 90), BorderLayout.WEST);
        verifyField.setFont(new Font("Monospaced", Font.PLAIN, 13));
        row.add(verifyField, BorderLayout.CENTER);
        JButton verifyBtn = new JButton("Xác minh");
        verifyBtn.addActionListener(e -> onVerifyText());
        row.add(verifyBtn, BorderLayout.EAST);
        panel.add(row);
        panel.add(Box.createVerticalStrut(4));

        verifyResultLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        verifyResultLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(verifyResultLabel);

        return panel;
    }

    // Dựng phần xác minh hash cho tab tệp tin
    private JPanel buildFileVerifySection() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setBorder(BorderFactory.createTitledBorder("Xác minh tệp (so sánh mã băm)"));

        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        row.add(fixedLabel("Hash cần so:", 90), BorderLayout.WEST);
        fileVerifyField.setFont(new Font("Monospaced", Font.PLAIN, 13));
        row.add(fileVerifyField, BorderLayout.CENTER);
        JButton verifyBtn = new JButton("Xác minh");
        verifyBtn.addActionListener(e -> onVerifyFile());
        row.add(verifyBtn, BorderLayout.EAST);
        panel.add(row);
        panel.add(Box.createVerticalStrut(4));

        fileVerifyResultLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        fileVerifyResultLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(fileVerifyResultLabel);

        return panel;
    }

    // Hiện màn hình chào mừng
    public void showWelcome(String title, String message) {
        titleLabel.setText(title);
        subTitleLabel.setText(message);
        cardLayout.show(this, "welcome");
    }

    // Chuyển sang thuật toán hash được chọn, reset các ô nhập liệu
    public void setAlgorithm(AlgorithmItem item) {
        String hashName = item.hashName();
        try {
            activeHash = new HashAlgorithm(hashName);
        } catch (Exception ex) {
            showError("Không thể khởi tạo giải thuật " + hashName, ex);
            return;
        }

        panelTitle.setText("Hàm băm — " + activeHash.getName());
        infoLabel.setText("Output: " + activeHash.getOutputBits() + " bit | Block size: " + activeHash.getBlockBits() + " bit");

        // reset tất cả ô nhập liệu
        inputArea.setText("");
        hashOutputArea.setText("");
        fileField.setText("");
        fileHashOutputArea.setText("");
        verifyField.setText("");
        verifyResultLabel.setText(" ");
        fileVerifyField.setText("");
        fileVerifyResultLabel.setText(" ");
        cardLayout.show(this, "work");
    }

    // Băm văn bản từ inputArea
    private void onHashText() {
        if (activeHash == null) return;
        String text = inputArea.getText();
        if (text.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập văn bản cần băm.",
                    "Thiếu dữ liệu", JOptionPane.WARNING_MESSAGE);
            return;
        }
        hashOutputArea.setText(activeHash.hashText(text));
    }

    // Băm file được chọn
    private void onHashFile() {
        if (activeHash == null) return;
        String path = fileField.getText().trim();
        if (path.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn tệp tin.",
                    "Thiếu dữ liệu", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            fileHashOutputArea.setText(activeHash.hashFile(path));
        } catch (Exception ex) {
            showError("Lỗi băm tệp tin", ex);
        }
    }

    // So sánh hash văn bản với hash nhập vào
    private void onVerifyText() {
        if (activeHash == null) return;
        String currentHash = hashOutputArea.getText().trim();
        String expected = verifyField.getText().trim();
        if (currentHash.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng băm văn bản trước.",
                    "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (expected.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập mã băm cần so sánh.",
                    "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (currentHash.equalsIgnoreCase(expected)) {
            verifyResultLabel.setForeground(new Color(0, 140, 0));
            verifyResultLabel.setText("KHỚP — Dữ liệu toàn vẹn.");
        } else {
            verifyResultLabel.setForeground(Color.RED);
            verifyResultLabel.setText("KHÔNG KHỚP — Dữ liệu đã bị thay đổi!");
        }
    }

    // So sánh hash file với hash nhập vào
    private void onVerifyFile() {
        if (activeHash == null) return;
        String currentHash = fileHashOutputArea.getText().trim();
        String expected = fileVerifyField.getText().trim();
        if (currentHash.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng băm tệp tin trước.",
                    "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (expected.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập mã băm cần so sánh.",
                    "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (currentHash.equalsIgnoreCase(expected)) {
            fileVerifyResultLabel.setForeground(new Color(0, 140, 0));
            fileVerifyResultLabel.setText("KHỚP — Tệp tin toàn vẹn.");
        } else {
            fileVerifyResultLabel.setForeground(Color.RED);
            fileVerifyResultLabel.setText("KHÔNG KHỚP — Tệp tin đã bị thay đổi!");
        }
    }

    // Copy text vào clipboard
    private void copyToClipboard(String text) {
        if (text == null || text.isEmpty()) return;
        Toolkit.getDefaultToolkit().getSystemClipboard()
                .setContents(new StringSelection(text), null);
        JOptionPane.showMessageDialog(this, "Đã sao chép mã băm vào clipboard.",
                "Thành công", JOptionPane.INFORMATION_MESSAGE);
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

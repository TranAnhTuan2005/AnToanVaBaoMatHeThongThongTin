package UI;

import UI.MaHoaCoDien.CipherWorkspacePanel;
import UI.MaHoaHienDai.ModernCipherWorkspacePanel;
import UI.MaHoaHienDai.RSAFileWorkspacePanel;
import UI.HamBam.HashWorkspacePanel;

import javax.swing.*;
import java.awt.*;
import java.util.List;

// Cửa sổ chính của ứng dụng, chứa sidebar bên trái và workspace bên phải
public class CryptoToolFrame extends JFrame {
    private final CipherWorkspacePanel classicPanel = new CipherWorkspacePanel();
    private final ModernCipherWorkspacePanel modernPanel = new ModernCipherWorkspacePanel();
    private final RSAFileWorkspacePanel rsaPanel = new RSAFileWorkspacePanel();
    private final HashWorkspacePanel hashPanel = new HashWorkspacePanel();
    private final JPanel workspaceContainer = new JPanel(new CardLayout());

    public CryptoToolFrame() {
        setTitle("Crypto Tool — Mã hóa & Giải mã");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(1180, 760);
        setLocationRelativeTo(null); // hiển thị giữa màn hình

        // thêm các panel workspace vào CardLayout
        workspaceContainer.add(classicPanel, "classic");
        workspaceContainer.add(modernPanel, "modern");
        workspaceContainer.add(rsaPanel, "asymmetric");
        workspaceContainer.add(hashPanel, "hash");

        // tạo sidebar từ danh mục thuật toán
        List<AlgorithmItem> items = AlgorithmCatalog.create();
        SidebarPanel sidebarPanel = new SidebarPanel(items, this::onAlgorithmSelected);

        // chia đôi: sidebar | workspace
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sidebarPanel, workspaceContainer);
        splitPane.setDividerLocation(300);
        splitPane.setEnabled(false); // không cho kéo divider

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(splitPane, BorderLayout.CENTER);
    }

    // Xử lý khi người dùng chọn 1 thuật toán trên sidebar
    private void onAlgorithmSelected(AlgorithmItem item) {
        CardLayout cl = (CardLayout) workspaceContainer.getLayout();

        if (item.isHash()) {
            hashPanel.setAlgorithm(item);
            cl.show(workspaceContainer, "hash");
        } else if (item.isAsymmetric()) {
            rsaPanel.showWork();
            cl.show(workspaceContainer, "asymmetric");
        } else if (item.isModern()) {
            modernPanel.setAlgorithm(item);
            cl.show(workspaceContainer, "modern");
        } else if (item.isImplemented()) {
            classicPanel.setAlgorithm(item);
            cl.show(workspaceContainer, "classic");
        } else {
            // thuật toán chưa implement thì hiện thông báo
            classicPanel.showWelcome("Crypto Tool",
                    "Thuật toán '" + item.displayName() + "' sẽ được bổ sung trong module mã hóa hiện đại.");
            cl.show(workspaceContainer, "classic");
        }
    }
}

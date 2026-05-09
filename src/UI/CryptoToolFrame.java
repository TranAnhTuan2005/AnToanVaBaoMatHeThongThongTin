package UI;

import UI.MaHoaHienDai.ModernCipherWorkspacePanel;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class CryptoToolFrame extends JFrame {
    private final CipherWorkspacePanel classicPanel = new CipherWorkspacePanel();
    private final ModernCipherWorkspacePanel modernPanel = new ModernCipherWorkspacePanel();
    private final JPanel workspaceContainer = new JPanel(new CardLayout());

    public CryptoToolFrame() {
        setTitle("Crypto Tool — Mã hóa & Giải mã");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(1180, 760);
        setLocationRelativeTo(null);

        workspaceContainer.add(classicPanel, "classic");
        workspaceContainer.add(modernPanel, "modern");

        List<AlgorithmItem> items = AlgorithmCatalog.create();
        SidebarPanel sidebarPanel = new SidebarPanel(items, this::onAlgorithmSelected);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sidebarPanel, workspaceContainer);
        splitPane.setDividerLocation(300);
        splitPane.setEnabled(false);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(splitPane, BorderLayout.CENTER);
    }

    private void onAlgorithmSelected(AlgorithmItem item) {
        CardLayout cl = (CardLayout) workspaceContainer.getLayout();
        if (item.isModern()) {
            modernPanel.setAlgorithm(item);
            cl.show(workspaceContainer, "modern");
        } else if (item.isImplemented()) {
            classicPanel.setAlgorithm(item);
            cl.show(workspaceContainer, "classic");
        } else {
            classicPanel.showWelcome("Crypto Tool",
                    "Thuật toán '" + item.displayName() + "' sẽ được bổ sung trong module mã hóa hiện đại.");
            cl.show(workspaceContainer, "classic");
        }
    }
}
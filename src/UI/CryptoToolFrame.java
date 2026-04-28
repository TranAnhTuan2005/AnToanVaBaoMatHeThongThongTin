package UI;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class CryptoToolFrame extends JFrame {
    private final CipherWorkspacePanel workspacePanel = new CipherWorkspacePanel();

    public CryptoToolFrame() {
        setTitle("Crypto Tool — Mã hóa & Giải mã");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(1180, 760);
        setLocationRelativeTo(null);

        List<AlgorithmItem> items = AlgorithmCatalog.create();
        SidebarPanel sidebarPanel = new SidebarPanel(items, this::onAlgorithmSelected);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sidebarPanel, workspacePanel);
        splitPane.setDividerLocation(300);
        splitPane.setEnabled(false);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(splitPane, BorderLayout.CENTER);
    }

    private void onAlgorithmSelected(AlgorithmItem item) {
        if (item.isImplemented()) {
            workspacePanel.setAlgorithm(item);
            return;
        }
        workspacePanel.showWelcome("Crypto Tool", "Thuật toán '" + item.displayName() + "' sẽ được bổ sung trong module mã hóa hiện đại.");
    }
}
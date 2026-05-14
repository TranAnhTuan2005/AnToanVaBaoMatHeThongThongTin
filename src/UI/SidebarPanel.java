package UI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

// Sidebar bên trái hiển thị danh sách thuật toán theo nhóm
public class SidebarPanel extends JPanel {
    private final List<JButton> itemButtons = new ArrayList<>();

    public SidebarPanel(List<AlgorithmItem> items, Consumer<AlgorithmItem> onSelect) {
        setLayout(new BorderLayout());
        setBackground(new Color(242, 242, 242));
        setPreferredSize(new Dimension(280, 600));

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(18, 12, 18, 12));

        // tiêu đề app
        JLabel title = new JLabel("Crypto Tool");
        title.setFont(new Font("Monospaced", Font.BOLD, 34));
        title.setForeground(new Color(30, 30, 30));
        title.setBorder(new EmptyBorder(0, 8, 16, 8));
        content.add(title);

        // duyệt danh sách thuật toán, thêm header nhóm + nút cho từng thuật toán
        String currentSection = "";
        for (AlgorithmItem item : items) {
            // nếu sang nhóm mới thì thêm đường kẻ và tiêu đề nhóm
            if (!item.section().equals(currentSection)) {
                if (!currentSection.isEmpty()) {
                    content.add(Box.createVerticalStrut(8));
                    JSeparator sep = new JSeparator();
                    sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
                    content.add(sep);
                    content.add(Box.createVerticalStrut(10));
                }
                currentSection = item.section();
                JLabel sectionLabel = new JLabel(currentSection);
                sectionLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
                sectionLabel.setForeground(new Color(145, 117, 90));
                sectionLabel.setBorder(new EmptyBorder(4, 0, 8, 0));
                sectionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                content.add(sectionLabel);
            }

            // tạo nút cho thuật toán
            JButton btn = createItemButton(item);
            btn.addActionListener(e -> {
                highlightButton(btn);
                onSelect.accept(item);
            });
            itemButtons.add(btn);
            content.add(btn);
            content.add(Box.createVerticalStrut(6));
        }

        content.add(Box.createVerticalGlue());

        // cho vào scroll pane để cuộn được khi danh sách dài
        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        scroll.getViewport().setOpaque(false);
        scroll.setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);
    }

    // Tạo nút cho 1 thuật toán, có chấm tròn màu phân biệt nhóm
    private JButton createItemButton(AlgorithmItem item) {
        JButton btn = new JButton("●  " + item.displayName());
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(8, 8, 8, 8));
        btn.setOpaque(true);
        btn.setBackground(new Color(242, 242, 242));
        btn.setForeground(new Color(60, 60, 60));
        btn.setFont(new Font("SansSerif", Font.PLAIN, 16));

        // dùng HTML để tô màu chấm tròn theo nhóm
        Color c = item.bulletColor();
        btn.setText("<html><span style='color:rgb(" + c.getRed() + "," + c.getGreen() + "," + c.getBlue() + ")'>●</span> "
                + item.displayName() + "</html>");
        return btn;
    }

    // Đổi màu nền nút được chọn, bỏ highlight các nút khác
    private void highlightButton(JButton selected) {
        for (JButton btn : itemButtons) {
            btn.setBackground(new Color(242, 242, 242));
        }
        selected.setBackground(new Color(227, 224, 248));
    }
}

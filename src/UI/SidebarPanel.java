package UI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

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

        JLabel title = new JLabel("Crypto Tool");
        title.setFont(new Font("Monospaced", Font.BOLD, 34));
        title.setForeground(new Color(30, 30, 30));
        title.setBorder(new EmptyBorder(0, 8, 16, 8));
        content.add(title);

        String currentSection = "";
        for (AlgorithmItem item : items) {
            if (!item.section().equals(currentSection)) {
                if (!currentSection.isEmpty()) {
                    content.add(Box.createVerticalStrut(8));
                    JSeparator separator = new JSeparator();
                    separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
                    content.add(separator);
                    content.add(Box.createVerticalStrut(10));
                }
                currentSection = item.section();
                JLabel section = new JLabel(currentSection);
                section.setFont(new Font("SansSerif", Font.PLAIN, 18));
                section.setForeground(new Color(145, 117, 90));
                section.setBorder(new EmptyBorder(4, 0, 8, 0));
                section.setAlignmentX(Component.LEFT_ALIGNMENT);
                content.add(section);
            }

            JButton btn = createItemButton(item);
            btn.addActionListener(e -> {
                selectButton(btn);
                onSelect.accept(item);
            });
            itemButtons.add(btn);
            content.add(btn);
            content.add(Box.createVerticalStrut(6));
        }

        content.add(Box.createVerticalGlue());

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        scroll.getViewport().setOpaque(false);
        scroll.setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);
    }

    private JButton createItemButton(AlgorithmItem item) {
        JButton btn = new JButton("●  " + item.displayName());
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(8, 8, 8, 8));
        btn.setOpaque(true);
        btn.setBackground(new Color(242, 242, 242));
        btn.setForeground(new Color(60, 60, 60));
        btn.setFont(new Font("SansSerif", Font.PLAIN, 30));

        Color bullet = item.bulletColor();
        btn.setText("<html><span style='color:rgb(" + bullet.getRed() + "," + bullet.getGreen() + "," + bullet.getBlue() + ")'>●</span> "
                + item.displayName() + "</html>");
        return btn;
    }

    private void selectButton(JButton selected) {
        for (JButton button : itemButtons) {
            button.setBackground(new Color(242, 242, 242));
        }
        selected.setBackground(new Color(227, 224, 248));
    }
}
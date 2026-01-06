package skilltrack.view;

import skilltrack.controller.SkillController;
import skilltrack.model.Skill;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class HomePanel extends JPanel {

    private final SkillController controller;
    private final MainFrame frame;

    private final JLabel totalValue = new JLabel("0");
    private final JLabel categoryValue = new JLabel("0");
    private final DefaultListModel<String> recentModel = new DefaultListModel<>();
    private final JList<String> recentList = new JList<>(recentModel);

    private int carouselIndex = 0;

    public HomePanel(SkillController controller, MainFrame frame) {
        this.controller = controller;
        this.frame = frame;

        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(16, 16, 16, 16));

        add(buildTop(), BorderLayout.NORTH);
        add(buildCenter(), BorderLayout.CENTER);

        // Carousel highlight
        Timer timer = new Timer(2000, e -> rotateCarousel());
        timer.start();
    }

    private JComponent buildTop() {
        JPanel top = new JPanel(new BorderLayout(12, 12));
        top.setOpaque(false);

        JLabel welcome = new JLabel("Welcome back 👋");
        welcome.setFont(welcome.getFont().deriveFont(Font.BOLD, 20f));

        JLabel hint = new JLabel("Manage skills, certifications, and track progress over time.");
        hint.setForeground(new Color(110, 110, 110));

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.add(welcome);
        left.add(Box.createVerticalStrut(6));
        left.add(hint);

        JButton goAdmin = new JButton("Open Admin Dashboard");
        goAdmin.setFocusPainted(false);
        goAdmin.setMargin(new Insets(10, 14, 10, 14));
        goAdmin.addActionListener(e -> frame.showAdmin());

        top.add(left, BorderLayout.WEST);
        top.add(goAdmin, BorderLayout.EAST);

        return top;
    }

    private JComponent buildCenter() {
        JPanel center = new JPanel(new BorderLayout(16, 16));
        center.setOpaque(false);
        center.setBorder(new EmptyBorder(16, 0, 0, 0));

        JPanel cards = new JPanel(new GridLayout(1, 2, 16, 16));
        cards.setOpaque(false);

        cards.add(statCard("Total Skills", totalValue));
        cards.add(statCard("Categories", categoryValue));

        JPanel recentPanel = new JPanel(new BorderLayout(10, 10));
        recentPanel.setBorder(BorderFactory.createTitledBorder("Recently Added (Last 5)"));
        recentList.setVisibleRowCount(8);
        recentList.setFixedCellHeight(28);
        recentPanel.add(new JScrollPane(recentList), BorderLayout.CENTER);

        center.add(cards, BorderLayout.NORTH);
        center.add(recentPanel, BorderLayout.CENTER);

        return center;
    }

    private JPanel statCard(String title, JLabel value) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                new EmptyBorder(14, 14, 14, 14)
        ));

        JLabel t = new JLabel(title);
        t.setForeground(new Color(110, 110, 110));

        value.setFont(value.getFont().deriveFont(Font.BOLD, 28f));

        card.add(t, BorderLayout.NORTH);
        card.add(value, BorderLayout.CENTER);
        return card;
    }

    public void refresh() {
        int total = controller.getTotalSkills();
        Map<String, Integer> cats = controller.getCategoryStats();

        totalValue.setText(String.valueOf(total));
        categoryValue.setText(String.valueOf(cats.size()));

        recentModel.clear();
        List<Skill> recents = controller.getRecentlyAdded();
        for (Skill s : recents) {
            recentModel.addElement(
                    s.getName() + "  •  " + s.getCategory() + "  •  " + s.getLevel() + "  •  " + s.getYearLearned()
            );
        }

        carouselIndex = 0;
        if (!recentModel.isEmpty()) recentList.setSelectedIndex(0);
    }

    private void rotateCarousel() {
        if (recentModel.isEmpty()) return;
        carouselIndex = (carouselIndex + 1) % recentModel.size();
        recentList.setSelectedIndex(carouselIndex);
        recentList.ensureIndexIsVisible(carouselIndex);
    }
}

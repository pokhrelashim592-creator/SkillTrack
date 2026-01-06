package skilltrack.view;

import skilltrack.controller.SkillController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MainFrame extends JFrame {

    private final CardLayout cards = new CardLayout();
    private final JPanel content = new JPanel(cards);

    private final HomePanel homePanel;
    private final AdminDashboardPanel adminPanel;

    private final JLabel statusLabel = new JLabel("Ready");

    public MainFrame(SkillController controller) {
        super("SkillTrack");

        homePanel = new HomePanel(controller, this);
        adminPanel = new AdminDashboardPanel(controller, this);

        content.add(homePanel, "HOME");
        content.add(adminPanel, "ADMIN");

        setLayout(new BorderLayout());
        add(buildHeader(), BorderLayout.NORTH);
        add(buildBody(), BorderLayout.CENTER);
        add(buildStatusBar(), BorderLayout.SOUTH);

        setSize(1100, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        showHome();
    }

    private JComponent buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(new EmptyBorder(12, 16, 12, 16));

        JLabel title = new JLabel("SkillTrack");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));

        JLabel subtitle = new JLabel("Personal Skill & Certification Management System");
        subtitle.setForeground(new Color(110, 110, 110));

        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setOpaque(false);
        left.add(title);
        left.add(subtitle);

        header.add(left, BorderLayout.WEST);
        return header;
    }

    private JComponent buildBody() {
        JPanel sidebar = buildSidebar();

        JPanel body = new JPanel(new BorderLayout());
        body.add(sidebar, BorderLayout.WEST);
        body.add(content, BorderLayout.CENTER);

        return body;
    }

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(230, 1));
        sidebar.setBorder(new EmptyBorder(12, 12, 12, 12));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        JLabel nav = new JLabel("Navigation");
        nav.setForeground(new Color(110, 110, 110));
        nav.setBorder(new EmptyBorder(0, 6, 10, 0));

        JButton homeBtn = navButton("🏠  Home", this::showHome);
        JButton adminBtn = navButton("🛠  Admin Dashboard", this::showAdmin);

        sidebar.add(nav);
        sidebar.add(homeBtn);
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(adminBtn);
        sidebar.add(Box.createVerticalGlue());

        JButton exitBtn = navButton("⏻  Exit", () -> System.exit(0));
        sidebar.add(exitBtn);

        return sidebar;
    }

    private JButton navButton(String text, Runnable action) {
        JButton b = new JButton(text);
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        b.setMargin(new Insets(10, 12, 10, 12));
        b.addActionListener(e -> action.run());
        return b;
    }

    private JComponent buildStatusBar() {
        JPanel status = new JPanel(new BorderLayout());
        status.setBorder(new EmptyBorder(6, 12, 6, 12));
        statusLabel.setForeground(new Color(90, 90, 90));
        status.add(statusLabel, BorderLayout.WEST);
        return status;
    }

    public void setStatus(String msg) {
        statusLabel.setText(msg == null ? "" : msg);
    }

    public void showHome() {
        homePanel.refresh();
        cards.show(content, "HOME");
        setStatus("Home");
    }

    public void showAdmin() {
        adminPanel.refresh();
        cards.show(content, "ADMIN");
        setStatus("Admin Dashboard");
    }
}

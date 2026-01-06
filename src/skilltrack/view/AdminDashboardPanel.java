package skilltrack.view;

import skilltrack.controller.SkillController;
import skilltrack.model.Skill;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.List;

public class AdminDashboardPanel extends JPanel {

    private final SkillController controller;
    private final MainFrame frame;

    private final SkillTableModel tableModel = new SkillTableModel();
    private final JTable table = new JTable(tableModel);

    private final JTextField partialSearchField = new JTextField(18);
    private final JTextField exactNameField = new JTextField(14);
    private final JTextField exactYearField = new JTextField(6);

    private final JComboBox<String> sortKey = new JComboBox<>(new String[]{"Year Learned", "Skill Name"});
    private final JComboBox<String> sortOrder = new JComboBox<>(new String[]{"Ascending", "Descending"});

    public AdminDashboardPanel(SkillController controller, MainFrame frame) {
        this.controller = controller;
        this.frame = frame;

        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(16, 16, 16, 16));

        add(buildTop(), BorderLayout.NORTH);
        add(buildTable(), BorderLayout.CENTER);
        add(buildBottom(), BorderLayout.SOUTH);

        refresh();
    }

    private JComponent buildTop() {
        JPanel top = new JPanel(new BorderLayout(12, 12));
        top.setOpaque(false);

        JLabel title = new JLabel("Admin Dashboard");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));

        JLabel hint = new JLabel("Create, update, delete, search, sort, and undo skill records.");
        hint.setForeground(new Color(110, 110, 110));

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.add(title);
        left.add(Box.createVerticalStrut(6));
        left.add(hint);

        JToolBar bar = new JToolBar();
        bar.setFloatable(false);

        JButton add = toolBtn("➕ Add", this::onAdd);
        JButton edit = toolBtn("✏ Edit", this::onEdit);
        JButton del = toolBtn("🗑 Delete", this::onDelete);
        JButton undo = toolBtn("↩ Undo", this::onUndo);
        JButton home = toolBtn("🏠 Home", frame::showHome);

        bar.add(add);
        bar.add(edit);
        bar.add(del);
        bar.addSeparator();
        bar.add(undo);
        bar.addSeparator();
        bar.add(home);

        top.add(left, BorderLayout.WEST);
        top.add(bar, BorderLayout.EAST);
        return top;
    }

    private JButton toolBtn(String text, Runnable action) {
        JButton b = new JButton(text);
        b.setFocusPainted(false);
        b.setMargin(new Insets(8, 10, 8, 10));
        b.addActionListener(e -> action.run());
        return b;
    }

    private JComponent buildTable() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(14, 0, 14, 0));

        table.setRowHeight(30);
        table.setShowHorizontalLines(false);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Zebra striping
        DefaultTableCellRenderer zebra = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, col);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 246, 248));
                }
                setBorder(new EmptyBorder(0, 10, 0, 10));
                return c;
            }
        };

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(zebra);
        }

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));

        panel.add(sp, BorderLayout.CENTER);
        return panel;
    }

    private JComponent buildBottom() {
        JPanel bottom = new JPanel(new GridLayout(1, 2, 16, 16));
        bottom.setOpaque(false);

        bottom.add(searchPanel());
        bottom.add(sortPanel());

        return bottom;
    }

    private JComponent searchPanel() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createTitledBorder("Search"));
        p.setOpaque(false);

        p.add(row("Partial (Linear):", partialSearchField, button("Search", this::onPartialSearch)));
        p.add(Box.createVerticalStrut(10));
        p.add(row("Exact Name (Binary):", exactNameField, button("Search", this::onExactNameSearch)));
        p.add(Box.createVerticalStrut(10));
        p.add(row("Exact Year (Binary):", exactYearField, button("Search", this::onExactYearSearch)));
        p.add(Box.createVerticalStrut(10));

        JButton reset = new JButton("Reset Table");
        reset.setFocusPainted(false);
        reset.addActionListener(e -> refresh());
        p.add(reset);

        return p;
    }

    private JComponent sortPanel() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createTitledBorder("Sort (MergeSort)"));
        p.setOpaque(false);

        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row1.add(new JLabel("Sort by:"));
        row1.add(sortKey);
        row1.add(new JLabel("Order:"));
        row1.add(sortOrder);

        JButton apply = button("Apply Sort", this::onSort);

        p.add(row1);
        p.add(Box.createVerticalStrut(10));
        p.add(apply);

        return p;
    }

    private JPanel row(String label, JComponent field, JButton btn) {
        JPanel r = new JPanel(new FlowLayout(FlowLayout.LEFT));
        r.add(new JLabel(label));
        r.add(field);
        r.add(btn);
        return r;
    }

    private JButton button(String text, Runnable action) {
        JButton b = new JButton(text);
        b.setFocusPainted(false);
        b.setMargin(new Insets(8, 10, 8, 10));
        b.addActionListener(e -> action.run());
        return b;
    }

    public void refresh() {
        tableModel.setData(controller.getAllSkills());
        frame.setStatus("Loaded " + controller.getAllSkills().size() + " skills");
    }

    private Skill selectedSkill() {
        int row = table.getSelectedRow();
        if (row < 0) return null;
        return tableModel.getAt(row);
    }

    private void onAdd() {
        SkillFormDialog dlg = new SkillFormDialog(SwingUtilities.getWindowAncestor(this), "Add Skill", null);
        dlg.setVisible(true);
        Skill s = dlg.getResult();
        if (s == null) return;

        try {
            controller.addSkill(s);
            refresh();
            frame.setStatus("Added: " + s.getName());
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onEdit() {
        Skill sel = selectedSkill();
        if (sel == null) {
            JOptionPane.showMessageDialog(this, "Select a skill first.");
            return;
        }

        SkillFormDialog dlg = new SkillFormDialog(SwingUtilities.getWindowAncestor(this), "Edit Skill", sel);
        dlg.setVisible(true);
        Skill edited = dlg.getResult();
        if (edited == null) return;

        try {
            controller.updateSkill(sel.getId(), edited);
            refresh();
            frame.setStatus("Updated: " + edited.getName());
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onDelete() {
        Skill sel = selectedSkill();
        if (sel == null) {
            JOptionPane.showMessageDialog(this, "Select a skill first.");
            return;
        }

        int ok = JOptionPane.showConfirmDialog(this,
                "Delete selected skill?\n\n" + sel.getName() + " (" + sel.getCategory() + ")",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (ok != JOptionPane.YES_OPTION) return;

        try {
            controller.deleteSkill(sel.getId());
            refresh();
            frame.setStatus("Deleted: " + sel.getName());
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onUndo() {
        if (!controller.canUndo()) {
            JOptionPane.showMessageDialog(this, "Nothing to undo.");
            return;
        }
        controller.undo();
        refresh();
        frame.setStatus("Undo completed");
    }

    private void onPartialSearch() {
        String q = partialSearchField.getText();
        if (q == null || q.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter partial text.");
            return;
        }
        List<Skill> results = controller.searchPartial(q);
        tableModel.setData(results);
        frame.setStatus("Linear search results: " + results.size());
    }

    private void onExactNameSearch() {
        String name = exactNameField.getText();
        if (name == null || name.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter an exact name.");
            return;
        }

        Skill found = controller.searchExactByNameBinary(name);
        if (found == null) {
            JOptionPane.showMessageDialog(this, "No exact match found.");
            frame.setStatus("Binary exact name: no match");
            return;
        }

        tableModel.setData(List.of(found));
        frame.setStatus("Binary exact name: found " + found.getName());
    }

    private void onExactYearSearch() {
        try {
            int year = Integer.parseInt(exactYearField.getText().trim());
            Skill found = controller.searchExactByYearBinary(year);

            if (found == null) {
                JOptionPane.showMessageDialog(this, "No exact match found.");
                frame.setStatus("Binary exact year: no match");
                return;
            }

            tableModel.setData(List.of(found));
            frame.setStatus("Binary exact year: found " + found.getName());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Year must be a valid number.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onSort() {
        boolean asc = sortOrder.getSelectedItem().toString().equalsIgnoreCase("Ascending");
        String key = sortKey.getSelectedItem().toString();

        List<Skill> sorted = key.equals("Skill Name")
                ? controller.sortByName(asc)
                : controller.sortByYear(asc);

        tableModel.setData(sorted);
        frame.setStatus("Sorted (" + key + ", " + (asc ? "ASC" : "DESC") + ")");
    }
}

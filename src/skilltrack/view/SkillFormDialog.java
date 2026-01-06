package skilltrack.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Window;
import javax.swing.*;
import skilltrack.model.ProficiencyLevel;
import skilltrack.model.Skill;

public class SkillFormDialog extends JDialog {

    private final JTextField nameField = new JTextField(20);
    private final JTextField categoryField = new JTextField(20);
    private final JComboBox<ProficiencyLevel> levelBox = new JComboBox<>(ProficiencyLevel.values());
    private final JTextField yearField = new JTextField(8);
    private final JTextField certField = new JTextField(20);

    private Skill result = null;

    public SkillFormDialog(Window owner, String title, Skill existing) {
        super(owner, title, ModalityType.APPLICATION_MODAL);

        setLayout(new BorderLayout(10, 10));
        setSize(450, 260);
        setLocationRelativeTo(owner);

        JPanel form = new JPanel(new GridLayout(5, 2, 8, 8));
        form.add(new JLabel("Skill Name:"));
        form.add(nameField);

        form.add(new JLabel("Category:"));
        form.add(categoryField);

        form.add(new JLabel("Proficiency:"));
        form.add(levelBox);

        form.add(new JLabel("Year Learned:"));
        form.add(yearField);

        form.add(new JLabel("Certification (optional):"));
        form.add(certField);

        add(form, BorderLayout.CENTER);

        JButton cancelBtn = new JButton("Cancel");
        JButton saveBtn = new JButton("Save");

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(cancelBtn);
        bottom.add(saveBtn);
        add(bottom, BorderLayout.SOUTH);

        // Prefill if edit
        if (existing != null) {
            nameField.setText(existing.getName());
            categoryField.setText(existing.getCategory());
            levelBox.setSelectedItem(existing.getLevel());
            yearField.setText(String.valueOf(existing.getYearLearned()));
            certField.setText(existing.getCertification() == null ? "" : existing.getCertification());
        }

        cancelBtn.addActionListener(e -> dispose());

        saveBtn.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                String category = categoryField.getText().trim();
                ProficiencyLevel level = (ProficiencyLevel) levelBox.getSelectedItem();
                int year = Integer.parseInt(yearField.getText().trim()); // NumberFormatException possible
                String cert = certField.getText().trim();
                if (cert.isEmpty()) cert = null;

                if (existing == null) {
                    result = new Skill(name, category, level, year, cert);
                } else {
                    result = new Skill(existing.getId(), name, category, level, year, cert);
                }
                dispose();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Year must be a valid number.",
                        "Invalid Input",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    public Skill getResult() {
        return result;
    }
}

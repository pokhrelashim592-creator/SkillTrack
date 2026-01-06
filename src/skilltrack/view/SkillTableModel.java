package skilltrack.view;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import skilltrack.model.Skill;

public class SkillTableModel extends AbstractTableModel {

    private final String[] cols = {"ID", "Name", "Category", "Level", "Year Learned", "Certification"};
    private List<Skill> data = new ArrayList<>();

    public void setData(List<Skill> skills) {
        this.data = new ArrayList<>(skills);
        fireTableDataChanged();
    }

    public Skill getAt(int row) {
        return data.get(row);
    }

    @Override public int getRowCount() { return data.size(); }
    @Override public int getColumnCount() { return cols.length; }
    @Override public String getColumnName(int c) { return cols[c]; }

    @Override
    public Object getValueAt(int r, int c) {
        Skill s = data.get(r);
        switch (c) {
            case 0: return s.getId();
            case 1: return s.getName();
            case 2: return s.getCategory();
            case 3: return s.getLevel();
            case 4: return s.getYearLearned();
            case 5: return s.getCertification();
            default: return "";
        }
    }
}

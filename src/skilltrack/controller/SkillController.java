package skilltrack.controller;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import skilltrack.model.Skill;
import skilltrack.model.SkillStore;
import skilltrack.util.MergeSort;
import skilltrack.util.SearchUtils;

public class SkillController {

    private final SkillStore store;

    public SkillController(SkillStore store) {
        this.store = store;
    }

    // ---------- Basic info ----------
    public List<Skill> getAllSkills() {
        return store.getAllSkills();
    }

    public int getTotalSkills() {
        return store.totalSkills();
    }

    public Map<String, Integer> getCategoryStats() {
        return store.categoryStats();
    }

    public List<Skill> getRecentlyAdded() {
        return store.getRecentlyAdded();
    }

    // ---------- CRUD ----------
    public void addSkill(Skill s) {
        store.addSkill(s);
    }

    public void updateSkill(String id, Skill newData) {
        store.updateSkill(id, newData);
    }

    public void deleteSkill(String id) {
        store.deleteSkill(id);
    }

    // ---------- Undo ----------
    public boolean canUndo() {
        return store.canUndo();
    }

    public void undo() {
        store.undo();
    }

    // ---------- Sorting (Merge Sort) ----------
    public List<Skill> sortByName(boolean ascending) {
        Comparator<Skill> cmp = Comparator.comparing(s -> s.getName().toLowerCase());
        if (!ascending) cmp = cmp.reversed();
        return MergeSort.sort(store.getAllSkills(), cmp);
    }

    public List<Skill> sortByYear(boolean ascending) {
        Comparator<Skill> cmp = Comparator.comparingInt(Skill::getYearLearned);
        if (!ascending) cmp = cmp.reversed();
        return MergeSort.sort(store.getAllSkills(), cmp);
    }

    // ---------- Searches ----------
    // Linear search for partial matches (name/category/certification)
    public List<Skill> searchPartial(String query) {
        return SearchUtils.partialSkillSearchLinear(store.getAllSkills(), query);
    }

    // Binary exact search by Name (requires sorted list)
    public Skill searchExactByNameBinary(String exactName) {
        List<Skill> sorted = sortByName(true);
        return SearchUtils.exactByNameBinary(sorted, exactName);
    }

    // Binary exact search by Year (requires sorted list)
    public Skill searchExactByYearBinary(int year) {
        List<Skill> sorted = sortByYear(true);
        return SearchUtils.exactByYearBinary(sorted, year);
    }
}

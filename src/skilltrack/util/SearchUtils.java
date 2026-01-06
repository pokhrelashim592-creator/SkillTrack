package skilltrack.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import skilltrack.model.Skill;

public class SearchUtils {

    // ---------- Binary Search (Exact match) ----------
    // List MUST be sorted using the same comparator
    public static <T> int binarySearch(List<T> sorted, T target, Comparator<T> cmp) {
        int lo = 0;
        int hi = sorted.size() - 1;

        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            int c = cmp.compare(sorted.get(mid), target);

            if (c == 0) return mid;
            if (c < 0) lo = mid + 1;
            else hi = mid - 1;
        }
        return -1;
    }

    // Exact search by NAME using Binary Search
    public static Skill exactByNameBinary(List<Skill> sortedByName, String name) {
        if (name == null) return null;

        Skill target = new Skill("TEMP", name.trim(), "x",
                skilltrack.model.ProficiencyLevel.BEGINNER, 2000, null);

        Comparator<Skill> cmp = Comparator.comparing(s -> s.getName().toLowerCase());

        int idx = binarySearch(sortedByName, target, cmp);
        return (idx >= 0) ? sortedByName.get(idx) : null;
    }

    // Exact search by YEAR using Binary Search
    public static Skill exactByYearBinary(List<Skill> sortedByYear, int year) {
        Skill target = new Skill("TEMP", "x", "x",
                skilltrack.model.ProficiencyLevel.BEGINNER, year, null);

        Comparator<Skill> cmp = Comparator.comparingInt(Skill::getYearLearned);

        int idx = binarySearch(sortedByYear, target, cmp);
        return (idx >= 0) ? sortedByYear.get(idx) : null;
    }

    // ---------- Linear Search (Partial match) ----------
    // partial match across name/category/certification
    public static List<Skill> partialSkillSearchLinear(List<Skill> allSkills, String query) {
        ArrayList<Skill> out = new ArrayList<>();
        if (query == null) return out;

        String q = query.trim().toLowerCase();

        for (Skill s : allSkills) {
            if (s.getName() != null && s.getName().toLowerCase().contains(q)) {
                out.add(s);
                continue;
            }
            if (s.getCategory() != null && s.getCategory().toLowerCase().contains(q)) {
                out.add(s);
                continue;
            }
            if (s.getCertification() != null && s.getCertification().toLowerCase().contains(q)) {
                out.add(s);
            }
        }
        return out;
    }
}

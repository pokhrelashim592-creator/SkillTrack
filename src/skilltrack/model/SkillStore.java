package skilltrack.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class SkillStore {

    // 1) List (main storage)
    private final ArrayList<Skill> skills = new ArrayList<>();

    // 2) Queue (recently added, max 5)
    private final ArrayDeque<Skill> recentQueue = new ArrayDeque<>();

    // 3) Stack (undo)
    private final ArrayDeque<UndoAction> undoStack = new ArrayDeque<>();

    // 4) HashMap (category -> skills)
    private final HashMap<String, ArrayList<Skill>> byCategory = new HashMap<>();

    // CSV file location (in user home folder)
    private final Path dataPath = Paths.get(System.getProperty("user.home"), "skilltrack_skills.csv");

    // ---------- Getters ----------
    public List<Skill> getAllSkills() {
        return Collections.unmodifiableList(skills);
    }

    public List<Skill> getRecentlyAdded() {
        return new ArrayList<>(recentQueue);
    }

    public int totalSkills() {
        return skills.size();
    }

    public Map<String, Integer> categoryStats() {
        Map<String, Integer> out = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        for (String cat : byCategory.keySet()) {
            out.put(cat, byCategory.get(cat).size());
        }
        return out;
    }

    // ---------- CRUD ----------
    public void addSkill(Skill s) {
        if (s == null) throw new IllegalArgumentException("Skill cannot be null.");

        // Prevent duplicates (same name + category)
        for (Skill existing : skills) {
            if (existing.sameIdentityAs(s)) {
                throw new IllegalArgumentException("Duplicate skill: same Name + Category already exists.");
            }
        }

        skills.add(s);
        indexSkill(s);
        pushRecent(s);
        undoStack.push(UndoAction.add(s));

        saveToDisk(); // includes IOException handling
    }

    public void deleteSkill(String id) {
        Skill s = findById(id);
        if (s == null) throw new IllegalArgumentException("Skill not found.");

        skills.remove(s);
        deindexSkill(s);
        recentQueue.removeIf(x -> x.getId().equals(id));
        undoStack.push(UndoAction.delete(s));

        saveToDisk();
    }

    public void updateSkill(String id, Skill newData) {
        Skill existing = findById(id);
        if (existing == null) throw new IllegalArgumentException("Skill not found.");

        // Duplicate prevention (ignore same ID)
        for (Skill s : skills) {
            if (s.getId().equals(id)) continue;
            if (s.getName().equalsIgnoreCase(newData.getName())
                    && s.getCategory().equalsIgnoreCase(newData.getCategory())) {
                throw new IllegalArgumentException("Duplicate skill: same Name + Category already exists.");
            }
        }

        // Save "before" for undo
        Skill before = new Skill(existing.getId(), existing.getName(), existing.getCategory(),
                existing.getLevel(), existing.getYearLearned(), existing.getCertification());

        // Update (re-index because category might change)
        deindexSkill(existing);
        existing.setName(newData.getName());
        existing.setCategory(newData.getCategory());
        existing.setLevel(newData.getLevel());
        existing.setYearLearned(newData.getYearLearned());
        existing.setCertification(newData.getCertification());
        indexSkill(existing);

        undoStack.push(UndoAction.update(before, existing));
        saveToDisk();
    }

    // ---------- Undo ----------
    public boolean canUndo() {
        return !undoStack.isEmpty();
    }

    public void undo() {
        if (undoStack.isEmpty()) return;

        UndoAction action = undoStack.pop();

        // IMPORTANT: We do not push new undo actions while undoing
        switch (action.type) {
            case ADD:
                // undo ADD => remove the added skill
                removeByIdNoUndo(action.after.getId());
                break;
            case DELETE:
                // undo DELETE => re-add deleted
                addNoUndo(action.before);
                break;
            case UPDATE:
                // undo UPDATE => restore "before"
                updateNoUndo(action.before.getId(), action.before);
                break;
        }

        saveToDisk();
    }

    private void addNoUndo(Skill s) {
        skills.add(s);
        indexSkill(s);
        pushRecent(s);
    }

    private void removeByIdNoUndo(String id) {
        Skill s = findById(id);
        if (s == null) return;
        skills.remove(s);
        deindexSkill(s);
        recentQueue.removeIf(x -> x.getId().equals(id));
    }

    private void updateNoUndo(String id, Skill data) {
        Skill existing = findById(id);
        if (existing == null) return;

        deindexSkill(existing);
        existing.setName(data.getName());
        existing.setCategory(data.getCategory());
        existing.setLevel(data.getLevel());
        existing.setYearLearned(data.getYearLearned());
        existing.setCertification(data.getCertification());
        indexSkill(existing);
    }

    // ---------- CSV Persistence ----------
    // Format: id,name,category,level,year,certification
    public void loadFromDisk() {
        if (!Files.exists(dataPath)) return;

        try (BufferedReader br = Files.newBufferedReader(dataPath)) {
            skills.clear();
            recentQueue.clear();
            undoStack.clear();
            byCategory.clear();

            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = splitCsvLine(line);
                if (parts.length < 6) continue;

                Skill s = new Skill(
                        parts[0],
                        parts[1],
                        parts[2],
                        ProficiencyLevel.fromString(parts[3]),
                        Integer.parseInt(parts[4]),
                        parts[5].isEmpty() ? null : parts[5]
                );

                skills.add(s);
                indexSkill(s);
            }

            // recently added = last 5 items
            int start = Math.max(0, skills.size() - 5);
            for (int i = start; i < skills.size(); i++) {
                pushRecent(skills.get(i));
            }

        } catch (IOException e) {
            // Robust: do not crash
            System.err.println("Load failed (IOException): " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Load failed (invalid year): " + e.getMessage());
        }
    }

    public void saveToDisk() {
        try (BufferedWriter bw = Files.newBufferedWriter(dataPath)) {
            for (Skill s : skills) {
                bw.write(toCsvLine(s));
                bw.newLine();
            }
        } catch (IOException e) {
            // Robust: do not crash
            System.err.println("Save failed (IOException): " + e.getMessage());
        }
    }

    private String toCsvLine(Skill s) {
        return csv(s.getId()) + "," +
               csv(s.getName()) + "," +
               csv(s.getCategory()) + "," +
               csv(s.getLevel().name()) + "," +
               s.getYearLearned() + "," +
               csv(s.getCertification() == null ? "" : s.getCertification());
    }

    private String csv(String x) {
        String v = (x == null) ? "" : x;
        v = v.replace("\"", "\"\"");
        return "\"" + v + "\"";
    }

    // Minimal CSV split for quoted fields
    private String[] splitCsvLine(String line) {
        ArrayList<String> out = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    cur.append('"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                out.add(cur.toString());
                cur.setLength(0);
            } else {
                cur.append(c);
            }
        }
        out.add(cur.toString());
        return out.toArray(new String[0]);
    }

    // ---------- Helpers ----------
    private Skill findById(String id) {
        for (Skill s : skills) {
            if (s.getId().equals(id)) return s;
        }
        return null;
    }

    private void pushRecent(Skill s) {
        // remove old copy of same ID then add
        recentQueue.removeIf(x -> x.getId().equals(s.getId()));
        recentQueue.addLast(s);
        while (recentQueue.size() > 5) recentQueue.removeFirst();
    }

    private void indexSkill(Skill s) {
        String cat = s.getCategory() == null ? "Uncategorized" : s.getCategory().trim();
        byCategory.putIfAbsent(cat, new ArrayList<>());
        byCategory.get(cat).add(s);
    }

    private void deindexSkill(Skill s) {
        String cat = s.getCategory() == null ? "Uncategorized" : s.getCategory().trim();
        if (!byCategory.containsKey(cat)) return;
        byCategory.get(cat).removeIf(x -> x.getId().equals(s.getId()));
        if (byCategory.get(cat).isEmpty()) byCategory.remove(cat);
    }
}

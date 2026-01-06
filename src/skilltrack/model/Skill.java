package skilltrack.model;

import java.util.UUID;

public class Skill {
    private final String id;
    private String name;
    private String category;
    private ProficiencyLevel level;
    private int yearLearned;
    private String certification; // optional

    // New Skill constructor (auto ID)
    public Skill(String name, String category, ProficiencyLevel level, int yearLearned, String certification) {
        this(UUID.randomUUID().toString(), name, category, level, yearLearned, certification);
    }

    // Constructor used when loading from CSV (keeps same ID)
    public Skill(String id, String name, String category, ProficiencyLevel level, int yearLearned, String certification) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.level = level;
        this.yearLearned = yearLearned;
        this.certification = certification;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public ProficiencyLevel getLevel() { return level; }
    public int getYearLearned() { return yearLearned; }
    public String getCertification() { return certification; }

    public void setName(String name) { this.name = name; }
    public void setCategory(String category) { this.category = category; }
    public void setLevel(ProficiencyLevel level) { this.level = level; }
    public void setYearLearned(int yearLearned) { this.yearLearned = yearLearned; }
    public void setCertification(String certification) { this.certification = certification; }

    // Duplicate definition: same name + category (case-insensitive)
    public boolean sameIdentityAs(Skill other) {
        if (other == null) return false;
        return this.name != null && this.category != null
                && this.name.equalsIgnoreCase(other.name)
                && this.category.equalsIgnoreCase(other.category);
    }
}

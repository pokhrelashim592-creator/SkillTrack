package skilltrack.model;

public enum ProficiencyLevel {
    BEGINNER,
    INTERMEDIATE,
    ADVANCED,
    EXPERT;

    public static ProficiencyLevel fromString(String s) {
        if (s == null) return BEGINNER;
        return ProficiencyLevel.valueOf(s.trim().toUpperCase());
    }
}

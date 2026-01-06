package skilltrack.model;

public class UndoAction {
    public enum Type { ADD, DELETE, UPDATE }

    public final Type type;
    public final Skill before; // for UPDATE/DELETE
    public final Skill after;  // for UPDATE/ADD

    private UndoAction(Type type, Skill before, Skill after) {
        this.type = type;
        this.before = before;
        this.after = after;
    }

    public static UndoAction add(Skill added) {
        return new UndoAction(Type.ADD, null, cloneSkill(added));
    }

    public static UndoAction delete(Skill deleted) {
        return new UndoAction(Type.DELETE, cloneSkill(deleted), null);
    }

    public static UndoAction update(Skill before, Skill after) {
        return new UndoAction(Type.UPDATE, cloneSkill(before), cloneSkill(after));
    }

    private static Skill cloneSkill(Skill s) {
        if (s == null) return null;
        return new Skill(
                s.getId(),
                s.getName(),
                s.getCategory(),
                s.getLevel(),
                s.getYearLearned(),
                s.getCertification()
        );
    }
}

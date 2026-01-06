package skilltrack;

import javax.swing.SwingUtilities;
import skilltrack.controller.SkillController;
import skilltrack.model.SkillStore;
import skilltrack.view.MainFrame;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SkillStore store = new SkillStore();
            store.loadFromDisk();

            SkillController controller = new SkillController(store);

            MainFrame frame = new MainFrame(controller);
            frame.setVisible(true);
        });
    }
}


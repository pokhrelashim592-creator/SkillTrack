package skilltrack.util;

import javax.swing.*;
import java.awt.*;

public class UITheme {

    public static void apply() {
        // Try FlatLaf if available (best visuals)
        boolean flatInstalled = tryInstallFlatLaf();

        if (!flatInstalled) {
            installNimbus();
        }

        // Global font (works with both)
        setGlobalFont(new Font("Segoe UI", Font.PLAIN, 13));

        // Small global tweaks (FlatLaf will use more of these)
        UIManager.put("Component.arc", 12);
        UIManager.put("Button.arc", 12);
        UIManager.put("TextComponent.arc", 10);
        UIManager.put("ScrollBar.thumbArc", 999);
        UIManager.put("ScrollBar.thumbInsets", new Insets(2, 2, 2, 2));
        UIManager.put("TabbedPane.arc", 12);
    }

    private static boolean tryInstallFlatLaf() {
        try {
            Class<?> flat = Class.forName("com.formdev.flatlaf.FlatLightLaf");
            flat.getMethod("setup").invoke(null);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    private static void installNimbus() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) { }
    }

    private static void setGlobalFont(Font font) {
        var keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof Font) {
                UIManager.put(key, font);
            }
        }
    }
}

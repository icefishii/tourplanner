package dev.icefish.tourplanner.client.utils;

import javafx.scene.Scene;
import java.util.ArrayList;
import java.util.List;

public class ThemeManager {

    private static final List<Scene> registeredScenes = new ArrayList<>();
    private static boolean darkMode = false;

    public static void registerScene(Scene scene) {
        if (!registeredScenes.contains(scene)) {
            registeredScenes.add(scene);
            applyCurrentTheme(scene);  // direkt beim Registrieren anwenden
        }
    }

    public static void setDarkMode(boolean isDark) {
        darkMode = isDark;
        for (Scene scene : registeredScenes) {
            applyCurrentTheme(scene);
        }
    }

    public static void applyCurrentTheme(Scene scene) {
        scene.getStylesheets().clear();
        String stylesheet = darkMode ? "/darkstylesheet.css" : "/stylesheet.css";
        scene.getStylesheets().add(ThemeManager.class.getResource(stylesheet).toExternalForm());
    }

    public static boolean isDarkMode() {
        return darkMode;
    }
}


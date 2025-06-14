package dev.icefish.tourplanner.client.utils;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;

import java.util.Map;

public class ShortcutUtils {

    /**
     * Fügt der Scene Tastenkombinationen hinzu, die bestimmte Aktionen auslösen.
     *
     * @param scene die Scene, auf der die Shortcuts registriert werden
     * @param shortcuts Map von Tastenkombinationen zu Runnable-Handlern (z.B. Button::fire)
     */
    public static void addShortcuts(Scene scene, Map<KeyCombination, Runnable> shortcuts) {
        if (scene == null) return;
        shortcuts.forEach(scene.getAccelerators()::put);
    }

    // Hilfsmethode für Standard Shortcut-Definitionen
    public static KeyCodeCombination ctrl(KeyCode code) {
        return new KeyCodeCombination(code, KeyCombination.CONTROL_DOWN);
    }

    public static KeyCodeCombination ctrlShift(KeyCode code) {
        return new KeyCodeCombination(code, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN);
    }

    public static KeyCodeCombination shift(KeyCode code) {
        return new KeyCodeCombination(code, KeyCombination.SHIFT_DOWN);
    }

    public static KeyCodeCombination altCtrl(KeyCode code) {
        return new KeyCodeCombination(code, KeyCombination.ALT_DOWN, KeyCombination.CONTROL_DOWN);
    }

    public static KeyCodeCombination esc() {
        return new KeyCodeCombination(KeyCode.ESCAPE);
    }
}

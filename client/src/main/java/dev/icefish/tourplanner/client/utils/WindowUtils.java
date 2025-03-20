package dev.icefish.tourplanner.client.utils;

import javafx.stage.Stage;
import javafx.scene.Node;

//schließt Fenster
public class WindowUtils {
    public static void close(Node node) {
        if (node != null) {
            Stage stage = (Stage) node.getScene().getWindow();
            if (stage != null) {
                stage.close();
            }
        }
    }
}

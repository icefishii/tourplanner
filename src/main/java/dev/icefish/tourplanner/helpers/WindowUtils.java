package dev.icefish.tourplanner.helpers;

import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.control.Control;

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

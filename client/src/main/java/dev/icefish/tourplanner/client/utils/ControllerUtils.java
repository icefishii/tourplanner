package dev.icefish.tourplanner.client.utils;

import dev.icefish.tourplanner.models.Tour;
import javafx.scene.control.*;
import java.util.Map;

public class ControllerUtils {

    public static void setupTourComboBox(ComboBox<Tour> comboBox) {
        comboBox.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Tour item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : item.getName());
            }
        });
        comboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Tour item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : item.getName());
            }
        });
    }

    public static void resetFieldStyles(Control... controls) {
        for (Control c : controls) {
            if (c != null) c.setStyle(null);
        }
    }

    public static void highlightErrorFields(Map<String, String> errors, Map<String, Control> fieldMap) {
        errors.forEach((key, msg) -> {
            Control c = fieldMap.get(key);
            if (c != null) c.setStyle("-fx-border-color: red;");
        });
    }

    public static void showErrorAlert(Map<String, String> errors) {
        StringBuilder sb = new StringBuilder();
        errors.forEach((k, v) -> sb.append(k).append(": ").append(v).append("\n"));
        showErrorAlert(sb.toString());
    }

    public static void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void showInfoAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
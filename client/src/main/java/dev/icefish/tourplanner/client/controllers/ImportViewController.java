package dev.icefish.tourplanner.client.controllers;

import dev.icefish.tourplanner.client.services.ImportService;
import dev.icefish.tourplanner.client.utils.ShortcutUtils;
import dev.icefish.tourplanner.client.utils.WindowUtils;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.Map;

public class ImportViewController {

    public TextField filePathField;
    public Button browseButton;
    public Button importButton;
    public Button cancelButton;

    private final ImportService importService;

    private MainViewController mainViewController;

    public void setMainViewController(MainViewController mainViewController) {
        this.mainViewController = mainViewController;
    }

    public ImportViewController() {
        this.importService = new ImportService();
    }

    @FXML
    private void initialize() {
        browseButton.setOnAction(this::onBrowse);
        importButton.setOnAction(this::onImport);
        cancelButton.setOnAction(this::onCancel);

        Platform.runLater(() -> {
            Scene scene = browseButton.getScene();
            if (scene != null) {
                ShortcutUtils.addShortcuts(scene, Map.of(
                        ShortcutUtils.ctrl(KeyCode.B), browseButton::fire,
                        ShortcutUtils.ctrl(KeyCode.I), importButton::fire,
                        ShortcutUtils.esc(), cancelButton::fire
                ));
            }
        });
    }


    public void onBrowse(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose file to import");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JSON Files", "*.json"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );

        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            filePathField.setText(selectedFile.getAbsolutePath());
        }
    }

    public void onImport(ActionEvent actionEvent) {
        File file = new File(filePathField.getText());
        if (!file.exists()) {
            showError("File not found!");
            return;
        }

        String result = importService.importToursAndLogs(file);
        if (result.startsWith("Error")) {
            showError(result);
        } else {
            showSuccess(result);
            mainViewController.refreshUI();
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Import Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Import Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void onCancel(ActionEvent actionEvent) {
        WindowUtils.close((Node) actionEvent.getSource());
    }
}
package dev.icefish.tourplanner.client.controllers;

import dev.icefish.tourplanner.client.services.ImportService;
import dev.icefish.tourplanner.client.utils.ControllerUtils;
import dev.icefish.tourplanner.client.utils.ShortcutUtils;
import dev.icefish.tourplanner.client.utils.WindowUtils;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.File;
import java.util.Map;

public class ImportViewController {
    public final static Logger logger = LogManager.getLogger(ImportViewController.class);
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
            logger.info("Selected file for import: {}", selectedFile.getAbsolutePath());
        } else {
            ControllerUtils.showErrorAlert("No file selected.");
            logger.warn("File selection was cancelled.");
        }
    }

    public void onImport(ActionEvent actionEvent) {
        String filePath = filePathField.getText();
        if (filePath == null || filePath.isBlank()) {
            ControllerUtils.showErrorAlert("Please select a file to import.");
            return;
        }
        String result = importService.importToursAndLogs(new File(filePath));
        if (result.startsWith("Error")) {
            ControllerUtils.showErrorAlert(result);
        } else {
            ControllerUtils.showInfoAlert(result);
            if (mainViewController != null) {
                mainViewController.refreshUI();
            }
            WindowUtils.close(filePathField);
        }
    }

    public void onCancel(ActionEvent actionEvent) {
        WindowUtils.close(filePathField);
    }
}
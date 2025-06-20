package dev.icefish.tourplanner.client.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.icefish.tourplanner.client.services.ImportService;
import dev.icefish.tourplanner.client.utils.ControllerUtils;
import dev.icefish.tourplanner.client.utils.ShortcutUtils;
import dev.icefish.tourplanner.client.utils.WindowUtils;
import dev.icefish.tourplanner.client.viewmodel.TourLogViewModel;
import dev.icefish.tourplanner.client.viewmodel.TourViewModel;
import dev.icefish.tourplanner.models.Tour;
import dev.icefish.tourplanner.models.TourLog;
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
import java.util.List;
import java.util.Map;

public class ImportViewController {
    public final static Logger logger = LogManager.getLogger(ImportViewController.class);
    public TextField filePathField;
    public Button browseButton;
    public Button importButton;
    public Button cancelButton;

    private ImportService importService;
    private TourViewModel tourViewModel;
    private TourLogViewModel tourLogViewModel;
    private MainViewController mainViewController;

    public void setMainViewController(MainViewController mainViewController) {
        this.mainViewController = mainViewController;
    }

    public ImportViewController() {
        this.importService = new ImportService();
    }

    public ImportViewController(TourViewModel tourViewModel, TourLogViewModel tourLogViewModel) {
        this.importService = new ImportService();
        this.tourViewModel = tourViewModel;
        this.tourLogViewModel = tourLogViewModel;
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

    // Öffnet einen Dateiauswahldialog zum Auswählen einer JSON-Datei
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

    // Führt den Import der ausgewählten JSON-Datei durch
    public void onImport(ActionEvent actionEvent) {
        String filePath = filePathField.getText();
        if (filePath == null || filePath.isBlank()) {
            ControllerUtils.showErrorAlert("Please select a file to import.");
            return;
        }

        try {
            // Lädt die JSON-Daten aus der Datei
            Map<String, Object> data = importService.loadFromFile(new File(filePath));

            // Konvertiert
            ObjectMapper objectMapper = new ObjectMapper();
            List<Tour> tours = objectMapper.convertValue(data.get("tours"),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, Tour.class));
            List<TourLog> tourLogs = objectMapper.convertValue(data.get("tourLogs"),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, TourLog.class));

            //Fügt die importierten Daten über die ViewModels hinzu
            for (Tour tour : tours) {
                tourViewModel.createNewTour(tour);
            }

            for (TourLog tourLog : tourLogs) {
                tourLogViewModel.createNewTourLog(tourLog);
            }

            ControllerUtils.showInfoAlert("Tours and tour logs imported successfully!");
            WindowUtils.close(filePathField);
        } catch (Exception e) {
            logger.error("Error during import: {}", e.getMessage());
            ControllerUtils.showErrorAlert("Error importing data: " + e.getMessage());
        }
    }

    public void onCancel(ActionEvent actionEvent) {
        WindowUtils.close(filePathField);
    }

    public void setImportService(ImportService importService) {
        this.importService = importService;
    }

    public void setTourViewModel(TourViewModel tourViewModel) {
        this.tourViewModel = tourViewModel;
    }

    public void setTourLogViewModel(TourLogViewModel tourLogViewModel) {
        this.tourLogViewModel = tourLogViewModel;
    }
}
package dev.icefish.tourplanner.client.controllers;

import dev.icefish.tourplanner.client.utils.ControllerFactory;
import dev.icefish.tourplanner.client.utils.ControllerUtils;
import dev.icefish.tourplanner.client.utils.ShortcutUtils;
import dev.icefish.tourplanner.client.utils.ThemeManager;
import dev.icefish.tourplanner.client.viewmodel.MapViewModel;
import dev.icefish.tourplanner.client.services.ExportService;
import dev.icefish.tourplanner.models.Tour;
import dev.icefish.tourplanner.models.TourLog;
import dev.icefish.tourplanner.client.viewmodel.TourLogViewModel;
import dev.icefish.tourplanner.client.viewmodel.TourViewModel;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;

public class MainViewController {

    public final static Logger logger = LogManager.getLogger(MainViewController.class);
    public MenuItem generateTourReportItem;
    public MenuItem generateSummaryReportItem;
    public MenuItem exportMenuItem;
    public MenuItem importMenuItem;

    @FXML
    private Button deleteTourButton, editTourButton, newTourButton, newTourLogButton, deleteTourLogButton, editTourLogButton;

    @FXML
    private ListView<Tour> tourListView;

    @FXML
    private TableView<TourLog> tourLogTableView;

    @FXML
    private TableColumn<TourLog, Timestamp> tourLogDateView;

    @FXML
    private TableColumn<TourLog, String> tourLogDurationView;

    @FXML
    private TableColumn<TourLog, Double> tourLogDistanceView;

    @FXML
    private TableColumn<TourLog, Integer> tourLogDifficultyView;

    @FXML
    private TableColumn<TourLog, Integer> tourLogRatingView;

    @FXML
    private ImageView mapImageView;

    @FXML
    private WebView mapView;

    //MenuBar

    @FXML
    private CheckBox childFriendlyCheckBox;

    @FXML
    private Spinner<Integer> ratingSpinner;

    //Search
    @FXML
    private TextField tourSearchField, tourLogSearchField;

    @FXML
    private Button tourSearchButton, tourClearButton, tourLogClearButton, tourLogSearchButton;

    @FXML
    private CheckMenuItem darkModeToggle;

    @FXML
    private BorderPane rootPane;


    private Stage tourLogCreateStage;
    private Stage tourCreateStage;
    private Stage tourEditStage;
    private Stage tourLogEditStage;

    private final Map<TourLog, Stage> tourLogDetailStages = new HashMap<>();
    private final Map<Tour, Stage> tourDetailStages = new HashMap<>();

    private final TourViewModel tourViewModel;
    private final TourLogViewModel tourLogViewModel;
    private final MapViewModel mapViewModel;
    private final ExportService exportService;

    public MainViewController(TourViewModel tourViewModel, TourLogViewModel tourLogViewModel, MapViewModel mapViewModel) {
        this.tourViewModel = tourViewModel;
        this.tourLogViewModel = tourLogViewModel;
        this.mapViewModel = mapViewModel;
        this.exportService = new ExportService();
    }

    @FXML
    public void initialize() {
        setTourListView();
        setTourLogTableView();
        tourViewModel.addDataChangeListener(this::refreshUI);
        tourLogViewModel.addDataChangeListener(this::refreshUI);
        deleteTourButton.disableProperty().bind(tourViewModel.deleteTourButtonDisabledProperty());
        editTourButton.disableProperty().bind(tourViewModel.editTourButtonDisabledProperty());
        generateTourReportItem.disableProperty().bind(tourViewModel.tourReportButtonDisabledProperty());

        tourListView.getSelectionModel().getSelectedItems().addListener((ListChangeListener<Tour>) c -> {
            tourViewModel.updateTourButtonStates(tourListView.getSelectionModel().getSelectedItems());
        });

        deleteTourLogButton.disableProperty().bind(tourLogViewModel.deleteTourLogButtonDisabledProperty());
        editTourLogButton.disableProperty().bind(tourLogViewModel.editTourLogButtonDisabledProperty());

        tourLogTableView.getSelectionModel().getSelectedItems().addListener(
                (ListChangeListener<TourLog>) c -> {
                    tourLogViewModel.updateTourLogButtonStates(tourLogTableView.getSelectionModel().getSelectedItems());
                }
        );
        tourListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        setTourCellFactory();

        // Add listener to update tour logs when a tour is selected
        tourListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                UUID selectedTourId = newValue.getId();
                ObservableList<TourLog> tourLogs = tourLogViewModel.getTourLogsByTourId(selectedTourId);
                tourLogTableView.setItems(tourLogs);

                mapViewModel.setMapImageForTour(selectedTourId);
            } else {
                tourLogTableView.setItems(FXCollections.observableArrayList());
                mapViewModel.setMapImageForTour(null);
            }
        });

        tourListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) { // Double-click
                Tour selectedTour = tourListView.getSelectionModel().getSelectedItem();// Existing logic for double-click on a tour
                if (selectedTour != null) {
                    openTourDetailsWindow(selectedTour);
                }
            }
        });

        tourLogTableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) { // Double-click
                TourLog selectedTourLog = tourLogTableView.getSelectionModel().getSelectedItem();
                if (selectedTourLog != null) {
                    openTourLogDetailWindow(selectedTourLog);
                }
            }
        });

        mapImageView.imageProperty().bind(mapViewModel.currentMapImageProperty());

        ratingSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 5, 0)); // min=1, max=5, initial=3

        ratingSpinner.valueProperty().addListener((observable, oldValue, newValue) -> onSearchTours());
        childFriendlyCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> onSearchTours());

        Platform.runLater(() -> {
            Scene scene = deleteTourButton.getScene();
            if (scene != null) {

                ThemeManager.registerScene(scene);


                ShortcutUtils.addShortcuts(scene, Map.of(
                        // Tour Shortcuts (Ctrl + Key)
                        ShortcutUtils.ctrl(KeyCode.N), this::onCreateTour,
                        ShortcutUtils.ctrl(KeyCode.E), this::onEditTour,
                        ShortcutUtils.ctrl(KeyCode.D), this::onDeleteTour,
                        ShortcutUtils.ctrl(KeyCode.R), () -> onGenerateTourReport(null),
                        ShortcutUtils.ctrl(KeyCode.I), this::onImport,
                        ShortcutUtils.ctrl(KeyCode.M), this::onToggleDarkMode,

                        // TourLog Shortcuts (Ctrl + Shift + Key)
                        ShortcutUtils.ctrlShift(KeyCode.N), this::onCreateTourLog,
                        ShortcutUtils.ctrlShift(KeyCode.E), this::onEditTourLog,
                        ShortcutUtils.ctrlShift(KeyCode.D), this::onDeleteTourLog

                ));
            }
        });
    }

    public void onCreateTour() {
        try {
            if (tourCreateStage != null && tourCreateStage.isShowing()) {
                tourCreateStage.toFront();
                return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/TourCreateWindow.fxml"));
            loader.setController(new TourCreateViewController());
            Parent root = loader.load();

            TourCreateViewController controller = loader.getController();
            controller.setTourCreatedListener(tourViewModel::createNewTour);

            tourCreateStage = new Stage();
            tourCreateStage.setTitle("Create Tour");

            Scene scene = new Scene(root, 800, 600);
            ThemeManager.applyCurrentTheme(scene);
            tourCreateStage.setMinWidth(800);
            tourCreateStage.setMinHeight(600);
            tourCreateStage.setScene(scene);
            tourCreateStage.setResizable(true);
            tourCreateStage.show();

        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    public void onEditTour() {
        Tour selectedTour = tourListView.getSelectionModel().getSelectedItem();
        if (selectedTour == null) {
            System.out.println("No Tour selected");
            return;
        }

        if (tourEditStage != null && tourEditStage.isShowing()) {
            tourEditStage.toFront();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/TourCreateWindow.fxml"));
            loader.setController(new TourEditViewController());
            Parent root = loader.load();

            TourEditViewController controller = loader.getController();
            controller.setTour(selectedTour);
            controller.setTourUpdatedListener(tourViewModel::updateTour);

            tourEditStage = new Stage();
            Scene scene = new Scene(root, 800, 600);
            ThemeManager.applyCurrentTheme(scene);
            tourEditStage.setScene(scene);
            tourEditStage.setTitle("Edit Tour");
            tourEditStage.setMinWidth(800);
            tourEditStage.setMinHeight(600);
            tourEditStage.setOnCloseRequest(e -> tourEditStage = null);
            tourEditStage.show();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    public void onDeleteTour() {
        List<Tour> selectedTours = new ArrayList<>(tourListView.getSelectionModel().getSelectedItems());

        if (selectedTours.isEmpty()) {
            System.out.println("No Tour selected");
            return;
        }

        ButtonType yesButton = new ButtonType("Yes");
        ButtonType noButton = new ButtonType("No");

        StringBuilder message = new StringBuilder("Are you sure you want to delete the following tours?\n");
        for (Tour tour : selectedTours) {
            message.append("- ").append(tour.getName()).append("\n");
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message.toString(), yesButton, noButton);
        alert.setTitle("Delete Tours");
        alert.setHeaderText(null);
        alert.showAndWait().ifPresent(response -> {
            if (response == yesButton) {
                for (Tour tour : selectedTours) {
                    ObservableList<TourLog> tourLogs = tourLogViewModel.getTourLogsByTourId(tour.getId());
                    for (TourLog tourLog : tourLogs) {
                        tourLogViewModel.deleteTourLog(tourLog);
                    }
                    tourViewModel.deleteTour(tour);
                }
            }
        });
    }

    private void setTourListView() {
        tourListView.setItems(tourViewModel.getAllTours());
    }

    private void setTourLogTableView() {
        tourLogDateView.setCellValueFactory(new PropertyValueFactory<>("date"));
        tourLogDurationView.setCellValueFactory(new PropertyValueFactory<>("durationText"));
        tourLogDistanceView.setCellValueFactory(new PropertyValueFactory<>("distance"));
        tourLogDifficultyView.setCellValueFactory(new PropertyValueFactory<>("difficulty"));

        // Sterne anzeigen statt Zahl bei Rating
        tourLogRatingView.setCellValueFactory(new PropertyValueFactory<>("rating"));
        tourLogRatingView.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Integer rating, boolean empty) {
                super.updateItem(rating, empty);
                if (empty || rating == null) {
                    setText(null);
                } else {
                    String stars = "★".repeat(rating) + "☆".repeat(5 - rating);
                    setText(stars);
                    setStyle("-fx-alignment: CENTER;");
                }
            }
        });

        tourLogTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    private void setTourCellFactory() {
        tourListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Tour item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || item.getName() == null) {
                    setText(null);
                } else {
                    setText(item.getName());
                }
            }
        });
    }

    private void openTourDetailsWindow(Tour tour) {
        if (tourDetailStages.containsKey(tour) && tourDetailStages.get(tour).isShowing()) {
            tourDetailStages.get(tour).toFront();
            return;
        }
        try {
            ControllerFactory controllerFactory = new ControllerFactory(tourViewModel, tourLogViewModel, mapViewModel);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/TourDetailWindow.fxml"));
            loader.setControllerFactory(controllerFactory);
            Parent root = loader.load();

            TourDetailViewController controller = loader.getController();
            controller.setTourDetails(tour, tourLogViewModel.getTourLogsByTourId(tour.getId()));

            Stage stage = new Stage();
            stage.setTitle("Tour Details");
            Scene scene = new Scene(root);
            ThemeManager.applyCurrentTheme(scene);
            stage.setScene(scene);
            stage.setMinWidth(305);
            stage.setMinHeight(450);
            tourDetailStages.put(tour, stage);

            stage.setOnCloseRequest(e -> tourDetailStages.remove(tour));

            stage.show();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private void openTourLogDetailWindow(TourLog tourLog) {
        if (tourLogDetailStages.containsKey(tourLog) && tourLogDetailStages.get(tourLog).isShowing()) {
            tourLogDetailStages.get(tourLog).toFront();
            return;
        }

        try {
            ControllerFactory controllerFactory = new ControllerFactory(tourViewModel, tourLogViewModel, mapViewModel);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/TourLogDetailWindow.fxml"));
            loader.setControllerFactory(controllerFactory);
            Parent root = loader.load();

            TourLogDetailViewController controller = loader.getController();
            controller.setTourLog(tourLog, tourViewModel);

            Stage stage = new Stage();
            stage.setTitle("Tour Log Details");
            Scene scene = new Scene(root);
            ThemeManager.applyCurrentTheme(scene);
            stage.setScene(scene);
            stage.setMinWidth(305);
            stage.setMinHeight(450);

            tourLogDetailStages.put(tourLog, stage);
            stage.setOnCloseRequest(e -> tourLogDetailStages.remove(tourLog));

            stage.show();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    public void refreshUI() {
        tourViewModel.fetchToursFromServer();
        tourLogViewModel.fetchTourLogsFromServer();

        tourListView.setItems(tourViewModel.getAllTours());
        Tour selectedTour = tourListView.getSelectionModel().getSelectedItem();

        if (selectedTour != null) {
            ObservableList<TourLog> tourLogs = tourLogViewModel.getTourLogsByTourId(selectedTour.getId());
            tourLogTableView.setItems(tourLogs);
        } else {
            tourLogTableView.setItems(FXCollections.observableArrayList());
        }
    }

    @FXML
    public void onCreateTourLog() {
        try {
            if (tourLogCreateStage != null && tourLogCreateStage.isShowing()) {
                tourLogCreateStage.toFront();
                return;
            }
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/TourLogCreateWindow.fxml"));
            loader.setController(new TourLogCreateViewController(tourViewModel, tourLogViewModel));
            Parent root = loader.load();

            TourLogCreateViewController controller = loader.getController();
            controller.setTourLogCreatedListener(tourLogViewModel::createNewTourLog);

            tourLogCreateStage = new Stage();
            Scene scene = new Scene(root);
            ThemeManager.applyCurrentTheme(scene);
            tourLogCreateStage.setScene(scene);
            tourLogCreateStage.setTitle("Create Tour Log");
            tourLogCreateStage.setMinWidth(450);
            tourLogCreateStage.setMinHeight(600);
            tourLogCreateStage.setOnCloseRequest(e -> tourLogCreateStage = null);
            tourLogCreateStage.show();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    @FXML
    public void onDeleteTourLog() {
        List<TourLog> selectedTourLogs = new ArrayList<>(tourLogTableView.getSelectionModel().getSelectedItems());
        if (selectedTourLogs.isEmpty()) {
            System.out.println("No Tour Log selected");
            return;
        }

        ButtonType yesButton = new ButtonType("Yes");
        ButtonType noButton = new ButtonType("No");

        StringBuilder message = new StringBuilder("Are you sure you want to delete the following tour logs?\n");
        for (TourLog tourLog : selectedTourLogs) {
            message.append("- ").append(tourLog.getComment()).append("\n");
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message.toString(), yesButton, noButton);
        alert.setTitle("Delete Tour Logs");
        alert.setHeaderText(null);
        alert.showAndWait().ifPresent(response -> {
            if (response == yesButton) {
                for (TourLog tourLog : selectedTourLogs) {
                    tourLogViewModel.deleteTourLog(tourLog);
                }
            }
        });
    }

    @FXML
    public void onEditTourLog() {
        TourLog selectedTourLog = tourLogTableView.getSelectionModel().getSelectedItem();
        if (selectedTourLog == null) {
            System.out.println("No Tour Log selected");
            return;
        }

        if (tourLogEditStage != null && tourLogEditStage.isShowing()) {
            tourLogEditStage.toFront();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/TourLogCreateWindow.fxml"));
            loader.setController(new TourLogEditViewController(tourViewModel));
            Parent root = loader.load();

            TourLogEditViewController controller = loader.getController();
            controller.setTourLog(selectedTourLog);
            controller.setTourLogUpdatedListener(tourLogViewModel::updateTourLog);

            tourLogEditStage = new Stage();
            Scene scene = new Scene(root);
            ThemeManager.applyCurrentTheme(scene);
            tourLogEditStage.setScene(scene);
            tourLogEditStage.setTitle("Edit Tour Log");
            tourLogEditStage.setMinWidth(450);
            tourLogEditStage.setMinHeight(600);
            tourLogEditStage.setOnCloseRequest(e -> tourLogEditStage = null);
            tourLogEditStage.show();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    //----Menu Bar-----
    public void onImport() {
        try {
            ControllerFactory controllerFactory = new ControllerFactory(tourViewModel, tourLogViewModel, mapViewModel);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ImportWindow.fxml"));
            loader.setControllerFactory(controllerFactory);
            Parent root = loader.load();

            ImportViewController controller = loader.getController();
            controller.setMainViewController(this);

            Stage stage = new Stage();
            Scene scene = new Scene(root);
            ThemeManager.applyCurrentTheme(scene);
            stage.setScene(scene);
            stage.setMinWidth(400);
            stage.setMinHeight(200);
            stage.setTitle("Import Data");
            stage.show();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    @FXML
    public void onExport() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));

        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            String result = exportService.exportToursAndLogs(file);
            if (result.startsWith("Error")) {
                showError(result);
            } else {
                showSuccess(result);
            }
        }
    }

    private void showError(String message) {
        ControllerUtils.showErrorAlert(message);
    }

    private void showSuccess(String message) {
        ControllerUtils.showInfoAlert(message);
    }

    //----Report-----

    public void onGenerateTourReport(ActionEvent actionEvent) {
        Tour selectedTour = tourListView.getSelectionModel().getSelectedItem();

        if (selectedTour == null) {
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Tour Report");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF", "*.pdf"));
        File file = fileChooser.showSaveDialog(((MenuItem) actionEvent.getSource()).getParentPopup().getOwnerWindow());

        if (file != null) {
            tourViewModel.generateTourReport(selectedTour, file.toPath());
        }
    }

    public void onGenerateSummaryReport(ActionEvent actionEvent) {
        if (tourListView.getItems().isEmpty()) {
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Summary Report");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF", "*.pdf"));
        File file = fileChooser.showSaveDialog(((MenuItem) actionEvent.getSource()).getParentPopup().getOwnerWindow());

        if (file != null) {
            try {
                tourViewModel.generateSummaryReport(file.toPath());
            } catch (IOException e) {
                logger.error("Error generating summary report: {}", e.getMessage());
                showError("Failed to generate summary report: " + e.getMessage());
            }
        }
    }

    public void onToggleDarkMode() {
        ThemeManager.setDarkMode(darkModeToggle.isSelected());
    }

    //----Search Bars-----

    public void onClearTourSearch() {
        tourSearchField.clear();
        tourListView.setItems(tourViewModel.getAllTours());
    }

    public void onClearTourLogSearch() {
        tourLogSearchField.clear();
        Tour selectedTour = tourListView.getSelectionModel().getSelectedItem();
        if (selectedTour != null) {
            ObservableList<TourLog> tourLogs = tourLogViewModel.getTourLogsByTourId(selectedTour.getId());
            tourLogTableView.setItems(tourLogs);
        } else {
            tourLogTableView.setItems(FXCollections.observableArrayList());
        }
    }

    @FXML
    public void onSearchTours() {
        String searchText = tourSearchField.getText().trim();
        Integer selectedRating = ratingSpinner.getValue();
        boolean isChildFriendly = childFriendlyCheckBox.isSelected();

        ObservableList<Tour> filteredTours = tourViewModel.searchTours(searchText, selectedRating, isChildFriendly);
        tourListView.setItems(filteredTours);
    }

    @FXML
    public void onSearchTourLogs() {
        String searchText = tourLogSearchField.getText().trim().toLowerCase();
        Tour selectedTour = tourListView.getSelectionModel().getSelectedItem();
        if (selectedTour != null) {
            ObservableList<TourLog> currentTourLogs = tourLogViewModel.getTourLogsByTourId(selectedTour.getId());
            tourLogTableView.setItems(tourLogViewModel.searchTourLogs(searchText, currentTourLogs));
        }
    }

    public TourViewModel getTourViewModel() {
        return tourViewModel;
    }
}
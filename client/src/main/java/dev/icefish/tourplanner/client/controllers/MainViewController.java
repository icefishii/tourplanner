package dev.icefish.tourplanner.client.controllers;

import dev.icefish.tourplanner.client.viewmodel.MapViewModel;
import dev.icefish.tourplanner.models.Tour;
import dev.icefish.tourplanner.models.TourLog;
import dev.icefish.tourplanner.client.utils.TourButtonHandler;
import dev.icefish.tourplanner.client.utils.TourLogButtonHandler;
import dev.icefish.tourplanner.client.viewmodel.TourLogViewModel;
import dev.icefish.tourplanner.client.viewmodel.TourViewModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
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


    private Stage tourLogCreateStage;
    private Stage tourCreateStage;
    private Stage tourEditStage;
    private Stage tourLogEditStage;

    private final Map<TourLog, Stage> tourLogDetailStages = new HashMap<>();
    private final Map<Tour, Stage> tourDetailStages = new HashMap<>();

    private final TourViewModel tourViewModel;
    private final TourLogViewModel tourLogViewModel;
    private final MapViewModel mapViewModel;

    public MainViewController(TourViewModel tourViewModel, TourLogViewModel tourLogViewModel, MapViewModel mapViewModel) {
        this.tourViewModel = tourViewModel;
        this.tourLogViewModel = tourLogViewModel;
        this.mapViewModel = mapViewModel;
    }

    @FXML
    public void initialize() {
        setTourListView();
        setTourLogTableView();
        TourButtonHandler tourButtonHandler = new TourButtonHandler(deleteTourButton, editTourButton, newTourButton, tourListView);
        TourLogButtonHandler tourLogButtonHandler = new TourLogButtonHandler(tourListView, newTourLogButton, deleteTourLogButton, editTourLogButton, tourLogTableView);
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

        // Temporary gets own Handler
        //TODO Implement above (handlers)
        ratingSpinner.valueProperty().addListener((observable, oldValue, newValue) -> onSearchTours(null));
        childFriendlyCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> onSearchTours(null));
    }

    public void onCreateTour(ActionEvent actionEvent) {
        try {
            if (tourCreateStage != null && tourCreateStage.isShowing()) {
                tourCreateStage.toFront();
                return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/TourCreateWindow.fxml"));
            TourCreateViewController controller = new TourCreateViewController(tourViewModel);
            loader.setController(controller);
            Parent root = loader.load();

            controller.setTourCreatedListener(tour -> {
                tourViewModel.createNewTour(tour); // Add the new tour to the ViewModel
                refreshUI(); // Refresh the UI after creating a new tour
            });

            tourCreateStage = new Stage();
            tourCreateStage.setTitle("Create Tour");

            // Set the size of the window here (z.B. 800x600)
            Scene scene = new Scene(root, 800, 600); // Höhe und Breite nach Wunsch anpassen
            tourCreateStage.setScene(scene);

            // Optional: Wenn du willst, dass die Fenstergröße dynamisch angepasst wird:
            tourCreateStage.setResizable(true); // Um das Fenster vergrößern/verkleinern zu können.

            tourCreateStage.show();

        } catch (IOException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
    }

    public void onEditTour(ActionEvent actionEvent) {
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
            controller.setTourUpdatedListener(tour -> {
                tourViewModel.updateTour(tour);
                refreshUI(); // Refresh UI after editing a tour
            });

            tourEditStage = new Stage();
            tourEditStage.setScene(new Scene(root));
            tourEditStage.setTitle("Edit Tour");
            tourEditStage.setMinWidth(380);
            tourEditStage.setMinHeight(450);
            tourEditStage.setOnCloseRequest(e -> tourEditStage = null);
            tourEditStage.show();
        } catch (IOException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
    }

    public void onDeleteTour(ActionEvent actionEvent) {
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
                refreshUI(); // Refresh UI after deleting tours

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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/TourDetailWindow.fxml"));
            Parent root = loader.load();

            TourDetailViewController controller = loader.getController();
            controller.setTourDetails(tour, tourLogViewModel.getTourLogsByTourId(tour.getId()));

            Stage stage = new Stage();
            stage.setTitle("Tour Details");
            stage.setScene(new Scene(root));

            tourDetailStages.put(tour, stage);

            stage.setOnCloseRequest(e -> tourDetailStages.remove(tour));

            stage.show();
        } catch (IOException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
    }

    private void openTourLogDetailWindow(TourLog tourLog) {
        if (tourLogDetailStages.containsKey(tourLog) && tourLogDetailStages.get(tourLog).isShowing()) {
            tourLogDetailStages.get(tourLog).toFront();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/TourLogDetailWindow.fxml"));
            Parent root = loader.load();

            TourLogDetailViewController controller = loader.getController();
            controller.setTourLog(tourLog, tourViewModel);

            Stage stage = new Stage();
            stage.setTitle("Tour Log Details");
            stage.setScene(new Scene(root));

            tourLogDetailStages.put(tourLog, stage);
            stage.setOnCloseRequest(e -> tourLogDetailStages.remove(tourLog));

            stage.show();
        } catch (IOException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
    }

    private void refreshUI() {
        System.out.println("Refreshing UI...");
        Tour selectedTour = tourListView.getSelectionModel().getSelectedItem();

        if (selectedTour != null) {
            System.out.println("Selected Tour: " + selectedTour.getName());
            ObservableList<TourLog> tourLogs = tourLogViewModel.getTourLogsByTourId(selectedTour.getId());
            System.out.println("Tour Logs for selected tour: " + tourLogs);
            tourLogTableView.setItems(tourLogs);
        } else {
            System.out.println("No tour selected.");
            tourLogTableView.setItems(FXCollections.observableArrayList());
        }
    }

    @FXML
    public void onCreateTourLog(ActionEvent actionEvent) {
        try {
            if (tourLogCreateStage != null && tourLogCreateStage.isShowing()) {
                tourLogCreateStage.toFront();
                return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/TourLogCreateWindow.fxml"));
            TourLogCreateViewController controller = new TourLogCreateViewController(tourViewModel, tourLogViewModel);
            loader.setController(controller);
            Parent root = loader.load();
            controller.setTourLogCreatedListener(tourLog -> {
                tourLogViewModel.createNewTourLog(tourLog);
                refreshUI();
            });

            tourLogCreateStage = new Stage();
            tourLogCreateStage.setScene(new Scene(root));
            tourLogCreateStage.setTitle("Create Tour Log");
            tourLogCreateStage.setMinWidth(450);
            tourLogCreateStage.setMinHeight(600);
            tourLogCreateStage.setOnCloseRequest(e -> tourLogCreateStage = null);
            tourLogCreateStage.show();
        } catch (IOException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void onDeleteTourLog(ActionEvent actionEvent) {
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
                refreshUI();
            }
        });
    }

    @FXML
    public void onEditTourLog(ActionEvent actionEvent) {
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
            TourLogEditViewController controller = new TourLogEditViewController(tourViewModel);
            loader.setController(controller);
            Parent root = loader.load();

            controller.setTourLog(selectedTourLog);
            controller.setTourLogUpdatedListener(tourLog -> {
                tourLogViewModel.updateTourLog(tourLog);
                refreshUI();
            });

            tourLogEditStage = new Stage();
            tourLogEditStage.setScene(new Scene(root));
            tourLogEditStage.setTitle("Edit Tour Log");
            tourLogEditStage.setMinWidth(450);
            tourLogEditStage.setMinHeight(600);
            tourLogEditStage.setOnCloseRequest(e -> tourLogEditStage = null);
            tourLogEditStage.show();
        } catch (IOException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
    }

    //----Menu Bar-----
    public void onImport(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ImportWindow.fxml"));
            Parent root = loader.load();

            Stage importStage = new Stage();
            importStage.setTitle("Import");

            Scene scene = new Scene(root);
            importStage.setScene(scene);
            importStage.setResizable(false);
            importStage.show();

        } catch (IOException e) {
            logger.error("Fehler beim Öffnen des Import-Dialogs: " + e.getMessage(), e);
        }

    }

    public void onExport(ActionEvent actionEvent) {

    }

    public void onExit(ActionEvent actionEvent) {
    }

    //----Report-----

    public void onGenerateTourReport(ActionEvent actionEvent) {
        Tour selectedTour = tourListView.getSelectionModel().getSelectedItem();

        if (selectedTour == null) {
            //showAlert("No Tour Selected", "Please select a tour to generate the report.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Tour Report");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF", "*.pdf")); // oder .txt, .json
        File file = fileChooser.showSaveDialog(((MenuItem) actionEvent.getSource()).getParentPopup().getOwnerWindow());

        if (file != null) {
            tourViewModel.generateTourReport(selectedTour, file.toPath());
        }

    }

    public void onGenerateSummaryReport(ActionEvent actionEvent) {
        if (tourListView.getItems().isEmpty()) {
            // Optional: Alert anzeigen
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
                e.printStackTrace();
                // Optional: Fehlerdialog anzeigen
            }
        }


    }

    public void onToggleDarkMode(ActionEvent actionEvent) {
    }

    public void onAbout(ActionEvent actionEvent) {
        //ToDo Description
    }

    //----Search Bars-----

    public void onClearTourSearch(ActionEvent actionEvent) {
        tourSearchField.clear();
        tourListView.setItems(tourViewModel.getAllTours());
    }

    public void onClearTourLogSearch(ActionEvent actionEvent) {
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
    public void onSearchTours(ActionEvent actionEvent) {
        String searchText = tourSearchField.getText().trim();
        Integer selectedRating = ratingSpinner.getValue();
        boolean isChildFriendly = childFriendlyCheckBox.isSelected();

        ObservableList<Tour> filteredTours = tourViewModel.searchTours(searchText, selectedRating, isChildFriendly);
        tourListView.setItems(filteredTours);
    }

    @FXML
    public void onSearchTourLogs(ActionEvent actionEvent) {
        String searchText = tourLogSearchField.getText().trim().toLowerCase();
        Tour selectedTour = tourListView.getSelectionModel().getSelectedItem();
        if (selectedTour != null) {
            ObservableList<TourLog> currentTourLogs = tourLogViewModel.getTourLogsByTourId(selectedTour.getId());
            tourLogTableView.setItems(tourLogViewModel.searchTourLogs(searchText, currentTourLogs));
        }
    }
}



//ToDo Close window when application terminated

//ToDo Keyboard-Shortcuts

//ToDo Button Controller/Mediator

//ToDo import and export of tour data (file format of your choice)

//ToDo Rewrite Tests

//ToDo Mandatory Feature (Language, ???)

//ToDo das mit den , . in der Eingabe



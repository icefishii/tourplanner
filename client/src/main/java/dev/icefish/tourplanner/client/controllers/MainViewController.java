package dev.icefish.tourplanner.client.controllers;

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
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;

public class MainViewController {
    @FXML
    private Button deleteTourButton, editTourButton, newTourButton, newTourLogButton, deleteTourLogButton, editTourLogButton;

    @FXML
    private ListView<Tour> tourListView;

    @FXML
    private TableView<TourLog> tourLogTableView;

    private Stage tourLogCreateStage;

    private Stage tourCreateStage;

    private Stage tourEditStage;

    private Stage tourLogEditStage;

    private final Map<TourLog, Stage> tourLogDetailStages = new HashMap<>();
    private final Map<Tour, Stage> tourDetailStages = new HashMap<>();

    private final TourViewModel tourViewModel = new TourViewModel();
    private final TourLogViewModel tourLogViewModel = new TourLogViewModel();
    private TourButtonHandler tourButtonHandler;
    private TourLogButtonHandler tourLogButtonHandler;

    @FXML
    public void initialize() {
        setTourListView();
        setTourLogTableView();
        tourButtonHandler = new TourButtonHandler(deleteTourButton, editTourButton, newTourButton, tourListView);
        tourLogButtonHandler = new TourLogButtonHandler(tourListView, newTourLogButton, deleteTourLogButton, editTourLogButton, tourLogTableView);
        tourListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        setTourCellFactory();

        // Add listener to update tour logs when a tour is selected
        tourListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                UUID selectedTourId = newValue.getId();
                ObservableList<TourLog> tourLogs = tourLogViewModel.getTourLogsByTourId(selectedTourId);
                tourLogTableView.setItems(tourLogs);
            } else {
                tourLogTableView.setItems(FXCollections.observableArrayList()); // Clear table if no tour is selected
            }
        });

        tourListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) { // Double-click
                Tour selectedTour = tourListView.getSelectionModel().getSelectedItem();
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
    }

    public TourViewModel getTourViewModel() {
        return tourViewModel;
    }

    public void onCreateTour(ActionEvent actionEvent) {
        try {
            if (tourCreateStage != null && tourCreateStage.isShowing()) {
                tourCreateStage.toFront();
                return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/TourCreateWindow.fxml"));
            loader.setController(new TourCreateViewController());
            Parent root = loader.load();
            TourCreateViewController controller = loader.getController();
            controller.setTourCreatedListener(tour -> {
                if (!tourViewModel.getAllTours().contains(tour)) {
                    tourViewModel.createNewTour(tour.getName(), tour.getDescription(), tour.getFromLocation(), tour.getToLocation(), tour.getTransportType());
                    refreshUI(); // Refresh UI after creating a tour
                }
            });
            tourCreateStage = new Stage();
            tourCreateStage.setScene(new Scene(root));
            tourCreateStage.setTitle("Create Tour");
            tourCreateStage.setMinWidth(380);
            tourCreateStage.setMinHeight(450);
            tourCreateStage.show();

            tourCreateStage.setOnCloseRequest(e -> tourCreateStage = null);

        } catch (IOException e) {
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

    public void onCreateTourLog(ActionEvent actionEvent) {
        try {
            if (tourLogCreateStage != null && tourLogCreateStage.isShowing()) {
                tourLogCreateStage.toFront();
                return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/TourLogCreateWindow.fxml"));
            TourLogCreateViewController controller = new TourLogCreateViewController(tourViewModel);
            loader.setController(controller);
            Parent root = loader.load();
            controller.setTourLogCreatedListener(tourLog -> {
                tourLogViewModel.createNewTourLog(tourLog);
                refreshUI(); // Refresh UI after creating a tour log
            });

            tourLogCreateStage = new Stage();
            tourLogCreateStage.setScene(new Scene(root));
            tourLogCreateStage.setTitle("Create Tour Log");
            tourLogCreateStage.setMinWidth(450);
            tourLogCreateStage.setMinHeight(600);

            tourLogCreateStage.setOnCloseRequest(e -> tourLogCreateStage = null);

            tourLogCreateStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
            loader.setController(new TourLogEditViewController(tourViewModel));
            Parent root = loader.load();
            TourLogEditViewController controller = loader.getController();
            controller.setTourLog(selectedTourLog);
            controller.setTourLogUpdatedListener(tourLog -> {
                tourLogViewModel.updateTourLog(tourLog);
                refreshUI(); // Refresh UI after editing a tour log
            });

            tourLogEditStage = new Stage();
            tourLogEditStage.setScene(new Scene(root));
            tourLogEditStage.setTitle("Edit Tour Log");
            tourLogEditStage.setMinWidth(450);
            tourLogEditStage.setMinHeight(600);
            tourLogEditStage.setOnCloseRequest(e -> tourLogEditStage = null);
            tourLogEditStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
            message.append("- ").append(tourLog.getId()).append("\n");
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message.toString(), yesButton, noButton);
        alert.setTitle("Delete Tour Logs");
        alert.setHeaderText(null);
        alert.showAndWait().ifPresent(response -> {
            if (response == yesButton) {
                for (TourLog tourLog : selectedTourLogs) {
                    tourLogViewModel.deleteTourLog(tourLog);
                }
                refreshUI(); // Refresh UI after deleting tour logs
            }
        });
    }

    private void setTourListView() {
        tourListView.setItems(tourViewModel.getAllTours());
    }

    @FXML
    private TableColumn<TourLog, Timestamp> tourLogDateView;

    @FXML
    private TableColumn<TourLog, String> tourLogDurationView;

    @FXML
    private TableColumn<TourLog, Double> tourLogDistanceView;

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
            controller.setTourDetails(tour);

            Stage stage = new Stage();
            stage.setTitle("Tour Details");
            stage.setScene(new Scene(root));

            tourDetailStages.put(tour, stage);

            stage.setOnCloseRequest(e -> tourDetailStages.remove(tour));

            stage.show();
        } catch (IOException e) {
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
            e.printStackTrace();
        }
    }

    public TourLogViewModel getTourLogViewModel() {
        return tourLogViewModel;
    }

    private void refreshUI() {
        // Refresh the tourListView
        tourListView.setItems(FXCollections.observableArrayList(tourViewModel.getAllTours()));

        // Refresh the tourLogTableView for the selected tour
        Tour selectedTour = tourListView.getSelectionModel().getSelectedItem();
        if (selectedTour != null) {
            ObservableList<TourLog> tourLogs = tourLogViewModel.getTourLogsByTourId(selectedTour.getId());
            tourLogTableView.setItems(tourLogs);
        } else {
            tourLogTableView.setItems(FXCollections.observableArrayList());
        }
    }
}
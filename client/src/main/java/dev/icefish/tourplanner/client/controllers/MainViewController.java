package dev.icefish.tourplanner.client.controllers;

import dev.icefish.tourplanner.client.model.Tour;
import dev.icefish.tourplanner.client.model.TourLog;
import dev.icefish.tourplanner.client.utils.TourButtonHandler;
import dev.icefish.tourplanner.client.utils.TourLogButtonHandler;
import dev.icefish.tourplanner.client.viewmodel.TourLogViewModel;
import dev.icefish.tourplanner.client.viewmodel.TourViewModel;
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
import java.util.ArrayList;
import java.util.List;

public class MainViewController {
    @FXML
    private Button deleteTourButton, editTourButton, newTourButton, newTourLogButton, deleteTourLogButton, editTourLogButton;

    @FXML
    private ListView<Tour> tourListView;

    @FXML
    private TableView<TourLog> tourLogTableView;

    private final TourViewModel tourViewModel = new TourViewModel();
    private final TourLogViewModel tourLogViewModel = new TourLogViewModel();
    private TourButtonHandler tourButtonHandler;
    private TourLogButtonHandler tourLogButtonHandler;

    @FXML
    public void initialize() {
        setTourListView();
        setTourLogTableView();
        tourButtonHandler = new TourButtonHandler(deleteTourButton, editTourButton, newTourButton, tourListView);
        tourLogButtonHandler = new TourLogButtonHandler(deleteTourLogButton, editTourLogButton, tourLogTableView);
        tourListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        setTourCellFactory();

        //Könnte man möglicherweise auslagern
        tourListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) { // Doppelklick
                Tour selectedTour = tourListView.getSelectionModel().getSelectedItem();
                if (selectedTour != null) {
                    openTourDetailsWindow(selectedTour);
                }
            }
        });


        tourLogTableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) { // Doppelklick
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

    //Bei ButtonClick "+"
    public void onCreateTour(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/TourCreateWindow.fxml"));
            loader.setController(new TourCreateViewController());
            Parent root = loader.load();
            TourCreateViewController controller = loader.getController();
            controller.setTourCreatedListener(tour -> {
                if (!tourViewModel.getAllTours().contains(tour)) {
                    tourViewModel.createNewTour(tour.getName(), tour.getDescription(), tour.getFromLocation(), tour.getToLocation(), tour.getTransportType());
                }
            });
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Create Tour");
            stage.setMinWidth(380);
            stage.setMinHeight(450);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Bei ButtonClick "..."
    public void onEditTour(ActionEvent actionEvent) {
        Tour selectedTour = tourListView.getSelectionModel().getSelectedItem();
        if (selectedTour == null) {
            System.out.println("No Tour selected");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/TourCreateWindow.fxml"));
            // Set the controller explicitly before loading
            loader.setController(new TourEditViewController());
            Parent root = loader.load();

            TourEditViewController controller = loader.getController();
            controller.setTour(selectedTour);
            controller.setTourUpdatedListener(tour -> tourViewModel.updateTour(tour));
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Edit Tour");
            stage.setMinWidth(380);
            stage.setMinHeight(450);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Bei ButtonClick "-"
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
            }
        });
    }

    //Bei ButtonClick "+" Logs
    public void onCreateTourLog(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/TourLogCreateWindow.fxml"));
            TourLogCreateViewController controller = new TourLogCreateViewController(tourViewModel);
            loader.setController(controller);
            Parent root = loader.load();
            controller.setTourLogCreatedListener(tourLog -> tourLogViewModel.createNewTourLog(tourLog));
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Create Tour Log");
            stage.setMinWidth(450);
            stage.setMinHeight(600);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Bei ButtonClick "..." Logs
    public void onEditTourLog(ActionEvent actionEvent) {
        TourLog selectedTourLog = tourLogTableView.getSelectionModel().getSelectedItem();
        if (selectedTourLog == null) {
            System.out.println("No Tour Log selected");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/TourLogCreateWindow.fxml"));
            loader.setController(new TourLogEditViewController(tourViewModel));
            Parent root = loader.load();
            TourLogEditViewController controller = loader.getController();
            controller.setTourLog(selectedTourLog);
            controller.setTourLogUpdatedListener(tourLog -> tourLogViewModel.updateTourLog(tourLog));
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Edit Tour Log");
            stage.setMinWidth(450);
            stage.setMinHeight(600);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Bei ButtonClick "-"
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
            }
        });
    }

    private void setTourListView() {
        tourListView.setItems(tourViewModel.getAllTours());
    }

    // Add these column declarations to match your FXML
    @FXML
    private TableColumn<TourLog, Timestamp> tourLogDateView;

    @FXML
    private TableColumn<TourLog, String> tourLogDurationView;

    @FXML
    private TableColumn<TourLog, Double> tourLogDistanceView;

    // Then update your setTourLogTableView method
    private void setTourLogTableView() {
        tourLogTableView.setItems(tourLogViewModel.getAllTourLogs());

        // Set up the cell value factories for each column
        tourLogDateView.setCellValueFactory(new PropertyValueFactory<>("date"));
        tourLogDurationView.setCellValueFactory(new PropertyValueFactory<>("durationText"));
        tourLogDistanceView.setCellValueFactory(new PropertyValueFactory<>("distance"));

        // Set up row factory for selection
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

    //Bei Doppelklick auf eine Tour
    private void openTourDetailsWindow(Tour tour) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/TourDetailWindow.fxml"));
            Parent root = loader.load();

            // Controller wird automatisch vom FXMLLoader instanziiert
            TourDetailViewController controller = loader.getController();
            controller.setTourDetails(tour); // Tour-Daten setzen

            Stage stage = new Stage();
            stage.setTitle("Tour Details");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Bei Doppelklick auf eine TourLog
    private void openTourLogDetailWindow(TourLog tourLog) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/TourLogDetailWindow.fxml"));
            Parent root = loader.load();

            // Holen des Controllers und setzen des TourLog
            TourLogDetailViewController controller = loader.getController();
            controller.setTourLog(tourLog,tourViewModel);

            Stage stage = new Stage();
            stage.setTitle("Tour Log Details");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public TourLogViewModel getTourLogViewModel() {
        return tourLogViewModel;
    }
    //ToDo the distance, and the time should be retrieved by a REST request using the OpenRouteservice.org API

    //ToDo better Detail-Output for Tours and TourLogs

    //ToDo Keyboard-Shortcuts

    //ToDo Mandatory Feature (Language, ???)
}
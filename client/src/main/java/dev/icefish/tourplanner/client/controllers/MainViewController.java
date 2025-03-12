package dev.icefish.tourplanner.client.controllers;

import dev.icefish.tourplanner.client.model.Tour;
import dev.icefish.tourplanner.client.utils.TourButtonHandler;
import dev.icefish.tourplanner.client.viewmodel.TourViewModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainViewController {
    @FXML
    private Button deleteTourButton, editTourButton, newTourButton;

    @FXML
    private ListView<Tour> tourListView;

    private final TourViewModel tourViewModel = new TourViewModel();
    private TourButtonHandler buttonHandler;

    @FXML
    public void initialize() {
        setTourListView();
        buttonHandler = new TourButtonHandler(deleteTourButton, editTourButton, newTourButton, tourListView);  // Initialisieren des Handlers
        tourListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        setTourCellFactory();
    }

    public void onCreateTour(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/TourCreateWindow.fxml"));
            Parent root = loader.load();

            TourCreateViewController controller = loader.getController();
            controller.setTourCreatedListener(this::addTourToViewModel);

            Stage stage = new Stage();
            stage.setTitle("Create New Tour");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            System.err.println("Error loading the tour creation window:  " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void onDeleteTour(ActionEvent actionEvent) {
        List<Tour> selectedTours = new ArrayList<>(tourListView.getSelectionModel().getSelectedItems());

        if (selectedTours == null) {
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
                tourViewModel.getAllTours().removeAll(selectedTours);
                tourListView.refresh();
                System.out.println("Tours deleted: ");
                for (Tour tour : selectedTours) {
                    System.out.println(tour.getName());
                }
            }
        });
    }

    public void onEditTour(ActionEvent actionEvent) {
        Tour selectedTour = tourListView.getSelectionModel().getSelectedItem();
        if (selectedTour == null) {
            System.out.println("No Tour selected");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/TourCreateWindow.fxml"));
            loader.setControllerFactory(param -> new TourEditViewController());

            Parent root = loader.load();

            TourEditViewController controller = loader.getController();
            controller.setTour(selectedTour);
            controller.setTourUpdatedListener(this::updateTourInViewModel);

            Stage stage = new Stage();
            stage.setTitle("Edit Tour");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            System.err.println("Error loading the tour edit window: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateTourInViewModel(Tour tour) {
        tourViewModel.updateTour(tour);
        tourListView.refresh();
    }

    private void setTourListView() {
        tourListView.setItems(tourViewModel.getAllTours());
    }

    private void setTourCellFactory() {
        tourListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Tour tour, boolean empty) {
                super.updateItem(tour, empty);
                setText(empty || tour == null ? null : tour.getName());
            }
        });
    }

    public void addTourToViewModel(Tour tour) {
        tourViewModel.getAllTours().add(tour);
    }
}

//ToDo TourLogs (create, Delete, Edit)
//ToDo Strg Aktionen
//ToDo TourDetail & TourLogDetail
//ToDo Testing

package dev.icefish.tourplanner.client.controllers;

import dev.icefish.tourplanner.client.model.Tour;
import dev.icefish.tourplanner.client.viewmodel.TourViewModel;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;


public class MainViewController {
    @FXML
    private Button deleteTourButton, editTourButton, newTourButton;

    @FXML
    private ListView<Tour> tourListView;

    private final TourViewModel TourViewModel = new TourViewModel();

    @FXML
    public void initialize() {
        setTourListView();
        tourListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);//mehrere Auswählen
        setTourCellFactory();
    }

    public void onCreateTour(ActionEvent actionEvent) { //Beim Drücken von New
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/dev/icefish/tourplanner/client/TourCreateWindow.fxml"));
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

    public void onDeleteTour(ActionEvent actionEvent) { //Beim Drücken von Delete
        Tour selectedTour = tourListView.getSelectionModel().getSelectedItem();

        //ToDo Button deaktivieren (Mediator??)
        if (selectedTour == null) {
            System.out.println("No Tour selected");
            return;
        }

        //das Problem bei ButtonType.Yes = wird es dann deutsch
        ButtonType yesButton = new ButtonType("Yes");
        ButtonType noButton = new ButtonType("No");

        // Bestätigungsdialog mit eigenen Buttons
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete " + selectedTour.getName() + "?", yesButton, noButton);

        alert.setTitle("Delete Tour");
        alert.setHeaderText(null);

        // Falls der Nutzer bestätigt
        alert.showAndWait().ifPresent(response -> {

            if (response == yesButton) {
                TourViewModel.getAllTours().removeAll(selectedTour);  // Entferne die Tour aus der Liste
                tourListView.refresh();  // Aktualisiere die Tabelle
                System.out.println("Tour deleted: " + selectedTour.getName());
            }
        });

    }

    public void onEditTour(ActionEvent actionEvent) { //Beim Drücken von Edit
        //ToDo edit
    }

    private void setTourListView() {
        tourListView.setItems(TourViewModel.getAllTours());
    }

    private void setTourCellFactory() {
        tourListView.setCellFactory(param -> {
            return new ListCell<>() {
                @Override
                protected void updateItem(Tour tour, boolean empty) {
                    super.updateItem(tour, empty);
                    setText(empty || tour == null ? null : tour.getName());
                }
            };
        });
    }

    private void addTourToViewModel(Tour tour) {
        TourViewModel.getAllTours().add(tour);
    }

}
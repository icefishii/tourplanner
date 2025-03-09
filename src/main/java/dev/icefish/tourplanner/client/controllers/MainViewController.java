package dev.icefish.tourplanner.client.controllers;

import dev.icefish.tourplanner.client.model.Tour;
import dev.icefish.tourplanner.client.viewmodel.TourViewModel;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
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
            System.err.println("Fehler beim Laden des Tour-Erstellungsfensters: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void onDeleteTour(ActionEvent actionEvent) { //Beim Drücken von Delete
        //ToDo delete
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
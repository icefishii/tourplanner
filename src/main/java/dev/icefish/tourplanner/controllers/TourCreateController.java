package dev.icefish.tourplanner.controllers;

import dev.icefish.tourplanner.models.Tour;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.function.Consumer;

public class TourCreateController {
    @FXML
    private TextField tourNameField, tourDescriptionField, fromLocationField, toLocationField;

    @FXML
    private Label nameLabel, descriptionLabel, fromLabel, toLabel, transportationModeLabel;

    @FXML
    private ComboBox<String> transportTypeBox;

    private Consumer<Tour> tourCreatedListener;

    @FXML
    private void initialize() {
        transportTypeBox.getItems().addAll("Walk", "Car", "Bike"); //kann man auch im fxml machen
    }

    public void setTourCreatedListener(Consumer<Tour> listener) {
        this.tourCreatedListener = listener;
    }


    public void onCreateButtonClick(ActionEvent actionEvent) {
        String name = tourNameField.getText();
        String description = tourDescriptionField.getText();
        String fromLocation = fromLocationField.getText();
        String toLocation = toLocationField.getText();
        String transportType = transportTypeBox.getValue();

        //ToDo Daten checken eigene File

        //ToDo API distance & time

        Tour newTour = new Tour(name, description, fromLocation, toLocation, transportType);

        if (tourCreatedListener != null) { //Tour zu Liste hinzugefügt
            tourCreatedListener.accept(newTour);
        }

        System.out.println("Tour erstellt: " + newTour.getString());

        close();
    }

    public void onCancelButtonClick(ActionEvent actionEvent) {
        close(); //ToDo close auslagern (Helper)
    }

    private void close() { //könnte auch ein eigener Helper sein (JA)
        Stage stage = (Stage) tourNameField.getScene().getWindow();
        if(stage != null) {
            stage.close();
        }
    }

}

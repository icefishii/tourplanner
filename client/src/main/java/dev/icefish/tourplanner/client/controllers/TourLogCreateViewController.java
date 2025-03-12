package dev.icefish.tourplanner.client.controllers;

import dev.icefish.tourplanner.client.model.Tour;
import dev.icefish.tourplanner.client.model.TourLog;
import dev.icefish.tourplanner.client.utils.TourLogChecker;
import dev.icefish.tourplanner.client.utils.WindowUtils;
import dev.icefish.tourplanner.client.viewmodel.TourLogViewModel;
import dev.icefish.tourplanner.client.viewmodel.TourViewModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.function.Consumer;

public class TourLogCreateViewController {
    @FXML
    private Button createButton, cancelButton;

    @FXML
    private TextField dateField, difficultyField, distanceField, durationField, ratingField;

    @FXML
    private TextArea commentField;

    @FXML
    private ComboBox<Tour> tourComboBox;

    private Consumer<TourLog> tourLogCreatedListener;
    private TourLogViewModel tourLogViewModel;
    private TourViewModel tourViewModel;

    public TourLogCreateViewController(TourViewModel tourViewModel) {
        this.tourViewModel = tourViewModel;
    }

    @FXML
    private void initialize() {
        tourLogViewModel = new TourLogViewModel(tourViewModel);
        tourComboBox.setItems(tourViewModel.getAllTours());
        createButton.setOnAction(this::onCreateButtonClick);
        cancelButton.setOnAction(this::onCancelButtonClick);
    }

    public void setTourLogCreatedListener(Consumer<TourLog> listener) {
        this.tourLogCreatedListener = listener;
    }

    public void onCreateButtonClick(ActionEvent actionEvent) {
        try {
            Tour selectedTour = tourComboBox.getValue();
            Timestamp date = Timestamp.valueOf(dateField.getText());
            String comment = commentField.getText();
            int difficulty = Integer.parseInt(difficultyField.getText());
            double distance = Double.parseDouble(distanceField.getText());
            String durationText = durationField.getText();
            int rating = Integer.parseInt(ratingField.getText());
            if (selectedTour == null) {
                showErrorAlert("No tour selected");
                return;
            }
            resetFieldStyles();

            Map<String, String> errors = TourLogChecker.validateTourLogWithStringDuration(
                    selectedTour, date, comment, difficulty, distance, durationText, rating);

            if (!errors.isEmpty()) {
                highlightErrors(errors);
                return;
            }

            // Create TourLog with the selectedTour's ID and the durationText
            TourLog newTourLog = new TourLog(
                    selectedTour.getId(), // Use as the ID for the TourLog
                    date,
                    comment,
                    difficulty,
                    distance,
                    durationText, // Pass the duration text string
                    rating
            );

            // Set the tourId explicitly to ensure it's properly associated
            newTourLog.setTourId(selectedTour.getId());

            if (tourLogCreatedListener != null) {
                tourLogCreatedListener.accept(newTourLog);
            }

            System.out.println("TourLog created: " + newTourLog);
            WindowUtils.close(dateField);
        } catch (Exception e) {
            showErrorAlert("Invalid input: " + e.getMessage());
        }
    }

    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void onCancelButtonClick(ActionEvent actionEvent) {
        WindowUtils.close(dateField);
    }

    private void resetFieldStyles() {
        dateField.setStyle("");
        commentField.setStyle("");
        difficultyField.setStyle("");
        distanceField.setStyle("");
        durationField.setStyle("");
        ratingField.setStyle("");
        tourComboBox.setStyle("");
    }

    private void highlightErrors(Map<String, String> errors) {
        if (errors.containsKey("date")) {
            dateField.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
        } else {
            dateField.setStyle("");
        }

        if (errors.containsKey("comment")) {
            commentField.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
        } else {
            commentField.setStyle("");
        }

        if (errors.containsKey("difficulty")) {
            difficultyField.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
        } else {
            difficultyField.setStyle("");
        }

        if (errors.containsKey("distance")) {
            distanceField.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
        } else {
            distanceField.setStyle("");
        }

        if (errors.containsKey("duration")) {
            durationField.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
        } else {
            durationField.setStyle("");
        }

        if (errors.containsKey("rating")) {
            ratingField.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
        } else {
            ratingField.setStyle("");
        }

        if (errors.containsKey("tour")) {
            tourComboBox.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
        } else {
            tourComboBox.setStyle("");
        }
    }
}
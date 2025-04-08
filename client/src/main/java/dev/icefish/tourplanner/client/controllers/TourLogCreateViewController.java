package dev.icefish.tourplanner.client.controllers;

import dev.icefish.tourplanner.models.Tour;
import dev.icefish.tourplanner.models.TourLog;
import dev.icefish.tourplanner.client.utils.TourLogChecker;
import dev.icefish.tourplanner.client.utils.WindowUtils;
import dev.icefish.tourplanner.client.viewmodel.TourLogViewModel;
import dev.icefish.tourplanner.client.viewmodel.TourViewModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.function.Consumer;

public class TourLogCreateViewController {
    @FXML
    private Button createButton, cancelButton;

    @FXML
    private TextField timeField, difficultyField, distanceField, durationField, ratingField;

    @FXML
    private TextArea commentField;

    @FXML
    private ComboBox<Tour> tourComboBox;

    @FXML
    private DatePicker datePicker;

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

        // Set the cell factory to display the tour name
        tourComboBox.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Tour item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName());
                }
            }
        });

        // Set the button cell to display the selected tour name
        tourComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Tour item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName());
                }
            }
        });

        createButton.setOnAction(this::onCreateButtonClick);
        cancelButton.setOnAction(this::onCancelButtonClick);
    }

    public void setTourLogCreatedListener(Consumer<TourLog> listener) {
        this.tourLogCreatedListener = listener;
    }

    //Erstellen der Logs
    public void onCreateButtonClick(ActionEvent actionEvent) {
        try {
            Tour selectedTour = tourComboBox.getValue();
            LocalDate selectedDate = datePicker.getValue();
            String timeText = timeField.getText();
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

            Timestamp date;
            try {
                LocalTime selectedTime = LocalTime.parse(timeText);
                LocalDateTime dateTime = LocalDateTime.of(selectedDate, selectedTime);
                date = Timestamp.valueOf(dateTime);
            } catch (DateTimeParseException e) {
                showErrorAlert("Invalid time format. Use HH:MM:SS.");
                return;
            }

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
            WindowUtils.close(commentField);
        } catch (Exception e) {
            showErrorAlert("Invalid input: " + e.getMessage());
        }
    }

    //Fehlermeldung
    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    //Fenster schließen
    public void onCancelButtonClick(ActionEvent actionEvent) {
        WindowUtils.close(commentField);
    }

    //Felder leeren
    private void resetFieldStyles() {
        timeField.setStyle("");
        commentField.setStyle("");
        difficultyField.setStyle("");
        distanceField.setStyle("");
        durationField.setStyle("");
        ratingField.setStyle("");
        tourComboBox.setStyle("");
    }

    //Leere Stellen markieren
    private void highlightErrors(Map<String, String> errors) {
        if (errors.containsKey("date")) {
            timeField.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
        } else {
            timeField.setStyle("");
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
package dev.icefish.tourplanner.client.controllers;

import dev.icefish.tourplanner.client.utils.UUIDv7Generator;
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
    private final TourLogViewModel tourLogViewModel;
    private final TourViewModel tourViewModel;

    public TourLogCreateViewController(TourViewModel tourViewModel, TourLogViewModel tourLogViewModel) {
        this.tourViewModel = tourViewModel;
        this.tourLogViewModel = tourLogViewModel;
    }

    public void setTourLogCreatedListener(Consumer<TourLog> listener) {
        this.tourLogCreatedListener = listener;
    }

    // Call the listener when a new TourLog is created
    private void onTourLogCreated(TourLog tourLog) {
        if (tourLogCreatedListener != null) {
            tourLogCreatedListener.accept(tourLog);
        }
    }

    @FXML
    private void initialize() {
        // Ensure the ComboBox is populated and displays the tour names
        tourComboBox.setItems(tourViewModel.getAllTours());
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

        // Rebind the create and cancel button actions
        createButton.setOnAction(this::onCreateButtonClick);
        cancelButton.setOnAction(this::onCancelButtonClick);
    }

    private void onCreateButtonClick(ActionEvent actionEvent) {
        resetFieldStyles();
        try {

            Tour selectedTour = tourComboBox.getValue();
            LocalDate date = datePicker.getValue();
            String timeText = timeField.getText();
            String comment = commentField.getText();
            String difficultyText = difficultyField.getText();
            String distanceText = distanceField.getText();
            String durationText = durationField.getText();
            String ratingText = ratingField.getText();

            Map<String, String> errors = TourLogChecker.validateTourLogRaw(
                    selectedTour, date, timeText, comment, difficultyText, distanceText, durationText, ratingText);

            if (!errors.isEmpty()) {
                highlightErrorFields(errors);
                showErrorAlert(errors);
                return;
            }

            LocalTime time = LocalTime.parse(timeText);
            Timestamp timestamp = Timestamp.valueOf(LocalDateTime.of(date, time));
            int difficulty = Integer.parseInt(difficultyText);
            double distance = Double.parseDouble(distanceText);
            int rating = Integer.parseInt(ratingText);

            TourLog newTourLog = new TourLog(
                    UUIDv7Generator.generateUUIDv7(),
                    selectedTour.getId(),
                    Timestamp.valueOf(LocalDateTime.of(date, time)),
                    comment,
                    difficulty,
                    distance,
                    durationText,
                    rating
            );



            if (tourLogCreatedListener != null) {
                tourLogCreatedListener.accept(newTourLog);
            }

            WindowUtils.close(commentField);
        } catch (Exception e) {
            showErrorAlert(Map.of("error", "Unexpected Error: " + e.getMessage()));
        }
    }

    private void onCancelButtonClick(ActionEvent actionEvent) {
        WindowUtils.close(commentField);
    }

    private void resetFieldStyles() {
        timeField.setStyle(null);
        difficultyField.setStyle(null);
        distanceField.setStyle(null);
        durationField.setStyle(null);
        ratingField.setStyle(null);
        commentField.setStyle(null);
        tourComboBox.setStyle(null);
        datePicker.setStyle(null);
    }

    private void highlightErrorFields(Map<String, String> errors) {
        if (errors.containsKey("tour")) {
            tourComboBox.setStyle("-fx-border-color: red;");
        }
        if (errors.containsKey("date")) {
            datePicker.setStyle("-fx-border-color: red;");
        }
        if (errors.containsKey("comment")) {
            commentField.setStyle("-fx-border-color: red;");
        }
        if (errors.containsKey("difficulty")) {
            difficultyField.setStyle("-fx-border-color: red;");
        }
        if (errors.containsKey("distance")) {
            distanceField.setStyle("-fx-border-color: red;");
        }
        if (errors.containsKey("duration")) {
            durationField.setStyle("-fx-border-color: red;");
        }
        if (errors.containsKey("rating")) {
            ratingField.setStyle("-fx-border-color: red;");
        }
        if (errors.containsKey("time")) {
            timeField.setStyle("-fx-border-color: red;");
        }
    }

    private void showErrorAlert(Map<String, String> errors) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(String.join("\n", errors.values()));
        alert.showAndWait();
    }
}
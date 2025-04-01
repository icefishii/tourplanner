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
import java.time.LocalDateTime;
import java.util.Map;
import java.util.function.Consumer;

public class TourLogEditViewController {
    @FXML
    private Button createButton, cancelButton;

    @FXML
    private TextField timeField, difficultyField, distanceField, durationField, ratingField;

    @FXML
    private DatePicker datePicker;

    @FXML
    private TextArea commentField;

    @FXML
    private ComboBox<Tour> tourComboBox;

    @FXML
    private Label createTourLogLabel;

    private Consumer<TourLog> tourLogUpdatedListener;
    private TourLog tourLog;
    private TourLogViewModel tourLogViewModel;
    private TourViewModel tourViewModel;

    public TourLogEditViewController(TourViewModel tourViewModel) {
        this.tourViewModel = tourViewModel;
    }

    public void setTourViewModel(TourViewModel tourViewModel) {
        this.tourViewModel = tourViewModel;
    }

    @FXML
    private void initialize() {
        createTourLogLabel.setText("Edit Tour Log");
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

        tourComboBox.setDisable(true);
        createButton.setText("Save");
        createButton.setOnAction(this::onSaveButtonClick);
        cancelButton.setOnAction(this::onCancelButtonClick);
    }

    public void setTourLog(TourLog tourLog) {
        this.tourLog = tourLog;
        tourComboBox.setValue(tourViewModel.getAllTours().stream()
                .filter(tour -> tour.getId().equals(tourLog.getTourId()))
                .findFirst()
                .orElse(null));

        LocalDateTime dateTime = tourLog.getDate().toLocalDateTime();
        datePicker.setValue(dateTime.toLocalDate());
        timeField.setText(dateTime.toLocalTime().toString());

        commentField.setText(tourLog.getComment());
        difficultyField.setText(String.valueOf(tourLog.getDifficulty()));
        distanceField.setText(String.valueOf(tourLog.getDistance()));
        durationField.setText(tourLog.getDurationText());
        ratingField.setText(String.valueOf(tourLog.getRating()));
    }

    public void setTourLogUpdatedListener(Consumer<TourLog> listener) {
        this.tourLogUpdatedListener = listener;
    }

    public void onSaveButtonClick(ActionEvent actionEvent) {
        try {
            Tour selectedTour = tourComboBox.getValue();

            LocalDateTime dateTime = LocalDateTime.of(datePicker.getValue(),
                    java.time.LocalTime.parse(timeField.getText()));
            Timestamp date = Timestamp.valueOf(dateTime);

            String comment = commentField.getText();
            int difficulty = Integer.parseInt(difficultyField.getText());
            double distance = Double.parseDouble(distanceField.getText());
            String durationText = durationField.getText();
            int rating = Integer.parseInt(ratingField.getText());

            resetFieldStyles();

            Map<String, String> errors = TourLogChecker.validateTourLogWithStringDuration(
                    selectedTour, date, comment, difficulty, distance, durationText, rating);

            if (!errors.isEmpty()) {
                highlightErrors(errors);
                return;
            }

            tourLog.setTourId(selectedTour.getId());
            tourLog.setDate(date);
            tourLog.setComment(comment);
            tourLog.setDifficulty(difficulty);
            tourLog.setDistance(distance);
            tourLog.setDurationText(durationText);
            tourLog.setRating(rating);

            if (tourLogUpdatedListener != null) {
                tourLogUpdatedListener.accept(tourLog);
            }

            System.out.println("TourLog updated: " + tourLog);
            WindowUtils.close(datePicker);
        } catch (Exception e) {
            showErrorAlert("Invalid input: " + e.getMessage());
        }
    }

    public void onCancelButtonClick(ActionEvent actionEvent) {
        WindowUtils.close(datePicker);
    }

    //Fehlermeldung
    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    //Felder leeren
    private void resetFieldStyles() {
        datePicker.setStyle("");
        timeField.setStyle("");
        commentField.setStyle("");
        difficultyField.setStyle("");
        distanceField.setStyle("");
        durationField.setStyle("");
        ratingField.setStyle("");
        tourComboBox.setStyle("");
    }

    //Fehler markieren
    private void highlightErrors(Map<String, String> errors) {
        if (errors.containsKey("date")) {
            datePicker.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
        } else {
            datePicker.setStyle("");
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
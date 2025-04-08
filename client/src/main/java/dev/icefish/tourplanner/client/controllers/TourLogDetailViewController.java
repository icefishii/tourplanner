package dev.icefish.tourplanner.client.controllers;

import dev.icefish.tourplanner.models.Tour;
import dev.icefish.tourplanner.models.TourLog;
import dev.icefish.tourplanner.client.utils.WindowUtils;
import dev.icefish.tourplanner.client.viewmodel.TourViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class TourLogDetailViewController {

    @FXML
    private Label nameLabel;

    @FXML
    private Label dateLabel;

    @FXML
    private Label commentLabel;

    @FXML
    private Label difficultyLabel;

    @FXML
    private Label distanceLabel;

    @FXML
    private Label durationLabel;

    @FXML
    private Label ratingLabel;

    private TourLog tourLog;
    private TourViewModel tourViewModel;


    // Setter für das TourLog, das angezeigt werden soll
    public void setTourLog(TourLog tourLog, TourViewModel tourViewModel) {
        this.tourLog = tourLog;
        this.tourViewModel = tourViewModel;

        Tour tour = tourViewModel.getAllTours().stream()
                .filter(t -> t.getId().equals(tourLog.getTourId()))
                .findFirst()
                .orElse(null);

        if (tour != null) {
            nameLabel.setText(tour.getName());
        } else {
            nameLabel.setText("Unknown Tour");
        }

        dateLabel.setText(tourLog.getDate().toString());
        commentLabel.setText(tourLog.getComment());
        difficultyLabel.setText(String.valueOf(tourLog.getDifficulty()));
        distanceLabel.setText(String.format("%.2f km", tourLog.getDistance()));
        durationLabel.setText(String.format("%s min",tourLog.getDurationText()));
        setRatingStars(tourLog.getRating());
    }

    //Stern ausgeben
    private void setRatingStars(int rating) {
        StringBuilder stars = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            if (i < rating) {
                stars.append("★");  // Voller Stern
            } else {
                stars.append("☆");  // Leerer Stern
            }
        }
        ratingLabel.setText(stars.toString());
    }

    @FXML
    private void handleClose() {
        // Schließe das Fenster, wenn der OK-Button geklickt wird
        WindowUtils.close(dateLabel);
    }
}

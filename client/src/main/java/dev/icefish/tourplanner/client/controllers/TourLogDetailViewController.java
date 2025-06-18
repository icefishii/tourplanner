package dev.icefish.tourplanner.client.controllers;

import dev.icefish.tourplanner.models.Tour;
import dev.icefish.tourplanner.models.TourLog;
import dev.icefish.tourplanner.client.utils.WindowUtils;
import dev.icefish.tourplanner.client.viewmodel.TourViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TourLogDetailViewController {

    private static final Logger logger = LogManager.getLogger(TourLogDetailViewController.class);

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

    public void setTourLog(TourLog tourLog, TourViewModel tourViewModel) {
        this.tourLog = tourLog;
        this.tourViewModel = tourViewModel;

        Tour tour = tourViewModel.getAllTours().stream()
                .filter(t -> t.getId().equals(tourLog.getTour().getId()))
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

        logger.info("TourLog details set: {}", tourLog);
    }

    private void setRatingStars(int rating) {
        StringBuilder stars = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            if (i < rating) {
                stars.append("★");
            } else {
                stars.append("☆");
            }
        }
        ratingLabel.setText(stars.toString());
    }

    @FXML
    private void handleClose() {
        WindowUtils.close(dateLabel);
        logger.info("Detail window closed.");
    }
}
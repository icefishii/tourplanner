package dev.icefish.tourplanner.client.test;

import dev.icefish.tourplanner.client.controllers.TourLogEditViewController;
import dev.icefish.tourplanner.client.services.TourLogService;
import dev.icefish.tourplanner.client.services.TourService;
import dev.icefish.tourplanner.client.viewmodel.TourLogViewModel;
import dev.icefish.tourplanner.client.viewmodel.TourViewModel;
import dev.icefish.tourplanner.models.Tour;
import dev.icefish.tourplanner.models.TourLog;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

@ExtendWith(ApplicationExtension.class)
public class TourLogEditViewTest extends TestFXBase {

    private TourLogEditViewController controller;
    private TourLog updatedTourLog;
    private CountDownLatch latch;

    private TourViewModel tourViewModel;
    private TourLogViewModel tourLogViewModel;

    private Tour sampleTour;
    private TourLog sampleTourLog;

    @Start
    public void start(Stage stage) throws Exception {
        // Services and ViewModels
        TourService tourService = new TourService(); // or mock
        TourLogService tourLogService = new TourLogService(); // or mock
        tourViewModel = new TourViewModel(tourService, tourLogService, null);
        tourLogViewModel = new TourLogViewModel(tourLogService);

        // Create sample Tour and add to TourViewModel
        sampleTour = new Tour();
        sampleTour.setId(UUID.randomUUID());
        sampleTour.setName("Sample Tour");
        tourViewModel.getAllTours().add(sampleTour);

        // Create sample TourLog to edit
        sampleTourLog = new TourLog();
        sampleTourLog.setId(UUID.randomUUID());
        sampleTourLog.setTour(sampleTour);
        sampleTourLog.setDate(Timestamp.valueOf(LocalDateTime.of(LocalDate.now(), LocalTime.of(10, 0))));
        sampleTourLog.setComment("Original comment");
        sampleTourLog.setDifficulty(2);
        sampleTourLog.setDistance(5.0);
        sampleTourLog.setDurationText("01:00");
        sampleTourLog.setRating(3);

        // Load FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/TourLogCreateWindow.fxml"));
        controller = new TourLogEditViewController(tourViewModel);
        loader.setController(controller);
        Parent root = loader.load();

        // Set the TourLog to edit
        controller.setTourLog(sampleTourLog);

        // Setup latch and listener to capture updated TourLog
        latch = new CountDownLatch(1);
        controller.setTourLogUpdatedListener(tourLog -> {
            updatedTourLog = tourLog;
            latch.countDown();
        });

        stage.setScene(new Scene(root, 600, 400));
        stage.show();
    }

    @Test
    public void testEditTourLog() {
        try {
            // Change some fields
            clickOn("#timeField").eraseText(5).write("12:30");
            clickOn("#commentField").eraseText(17).write("Updated comment");
            clickOn("#difficultyField").eraseText(1).write("4");
            clickOn("#distanceField").eraseText(3).write("10.5");
            clickOn("#durationField").eraseText(5).write("02:15");
            clickOn("#ratingField").eraseText(1).write("5");

            // Click save button
            clickOn("#createButton");

            // Wait for listener to be called (max 5 seconds)
            boolean success = latch.await(5, TimeUnit.SECONDS);
            assertThat(success).isTrue();

            // Assertions on updated TourLog
            assertThat(updatedTourLog).isNotNull();
            assertThat(updatedTourLog.getTour().getName()).isEqualTo("Sample Tour");
            assertThat(updatedTourLog.getComment()).isEqualTo("Updated comment");
            assertThat(updatedTourLog.getDifficulty()).isEqualTo(4);
            assertThat(updatedTourLog.getDistance()).isEqualTo(10.5);
            assertThat(updatedTourLog.getDurationText()).isEqualTo("02:15");
            assertThat(updatedTourLog.getRating()).isEqualTo(5);
            assertThat(updatedTourLog.getDate().toLocalDateTime().toLocalTime()).isEqualTo(LocalTime.of(12, 30));

        } catch (Exception e) {
            fail("Exception during test: " + e.getMessage(), e);
        }
    }
}

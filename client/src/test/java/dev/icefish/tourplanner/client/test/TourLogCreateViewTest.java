package dev.icefish.tourplanner.client.test;

import dev.icefish.tourplanner.client.controllers.TourLogCreateViewController;
import dev.icefish.tourplanner.client.services.ReportService;
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

import java.time.LocalDate;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

@ExtendWith(ApplicationExtension.class)
public class TourLogCreateViewTest extends TestFXBase {

    private TourLogCreateViewController controller;
    private TourLog createdTourLog;
    private CountDownLatch latch;

    private TourViewModel tourViewModel;
    private TourLogViewModel tourLogViewModel;

    @Start
    public void start(Stage stage) throws Exception {
        // Mock or stub view models (replace with your preferred mocking)
        TourService tourService = new TourService(); // or mock
        TourLogService tourLogService = new TourLogService(); // or mock
        ReportService reportService = new ReportService(); // or mock
        tourViewModel = new TourViewModel(tourService,tourLogService,reportService); // or mock
        tourLogViewModel = new TourLogViewModel(tourLogService); // or mock

        // Add a sample Tour to tourViewModel to select from in ComboBox
        Tour sampleTour = new Tour();
        sampleTour.setId(UUID.randomUUID());
        sampleTour.setName("Sample Tour");
        tourViewModel.getAllTours().add(sampleTour);

        // Load FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/TourLogCreateWindow.fxml"));
        controller = new TourLogCreateViewController(tourViewModel, tourLogViewModel);
        loader.setController(controller);
        Parent root = loader.load();

        // Setup latch and listener to capture created TourLog
        latch = new CountDownLatch(1);
        controller.setTourLogCreatedListener(tourLog -> {
            createdTourLog = tourLog;
            latch.countDown();
        });

        stage.setScene(new Scene(root, 600, 400));
        stage.show();
    }

    @Test
    public void testCreateTourLog() {
        try {
            // Select the first tour in the ComboBox (the "Sample Tour")
            clickOn("#tourComboBox").clickOn("Sample Tour");

            // Set date picker to today
            LocalDate today = LocalDate.now();
            interact(() -> controller.datePicker.setValue(today));

            // Fill in the fields with valid values
            clickOn("#timeField").write("12:30");
            clickOn("#commentField").write("Nice tour log");
            clickOn("#difficultyField").write("3");
            clickOn("#distanceField").write("10.5");
            clickOn("#durationField").write("01:30");
            clickOn("#ratingField").write("4");

            // Click the create button
            clickOn("#createButton");

            // Wait for the listener to be called (max 5 seconds)
            boolean success = latch.await(5, TimeUnit.SECONDS);
            assertThat(success).isTrue();

            // Assertions on created TourLog
            assertThat(createdTourLog).isNotNull();
            assertThat(createdTourLog.getTour().getName()).isEqualTo("Sample Tour");
            assertThat(createdTourLog.getComment()).isEqualTo("Nice tour log");
            assertThat(createdTourLog.getDifficulty()).isEqualTo(3);
            assertThat(createdTourLog.getDistance()).isEqualTo(10.5);
            assertThat(createdTourLog.getDurationText()).isEqualTo("01:30");
            assertThat(createdTourLog.getRating()).isEqualTo(4);

        } catch (Exception e) {
            fail("Exception during test: " + e.getMessage(), e);
        }
    }
}

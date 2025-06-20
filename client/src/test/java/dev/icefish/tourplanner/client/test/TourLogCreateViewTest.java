package dev.icefish.tourplanner.client.test;

import dev.icefish.tourplanner.client.controllers.TourLogCreateViewController;
import dev.icefish.tourplanner.client.services.ReportService;
import dev.icefish.tourplanner.client.services.TourLogService;
import dev.icefish.tourplanner.client.services.TourService;
import dev.icefish.tourplanner.client.viewmodel.TourLogViewModel;
import dev.icefish.tourplanner.client.viewmodel.TourViewModel;
import dev.icefish.tourplanner.models.Tour;
import dev.icefish.tourplanner.models.TourLog;
import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.time.LocalDate;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

@ExtendWith(ApplicationExtension.class)
public class TourLogCreateViewTest extends TestFXBase {

    private TourLogCreateViewController controller;
    private TourLog createdTourLog;
    private CountDownLatch latch;

    private Tour sampleTour;
    private TourViewModel tourViewModel;
    private TourLogViewModel tourLogViewModel;

    private TourService mockTourService;
    private TourLogService mockTourLogService;
    private ReportService mockReportService;

    @Start
    public void start(Stage stage) throws Exception {
        // Create mocks
        mockTourService = mock(TourService.class);
        mockTourLogService = mock(TourLogService.class);
        mockReportService = mock(ReportService.class);

        // Sample tour
        sampleTour = new Tour();
        sampleTour.setId(UUID.randomUUID());
        sampleTour.setName("Sample Tour");

        // Configure mock service behavior
        when(mockTourService.getAllTours()).thenReturn(FXCollections.observableArrayList(sampleTour));
        when(mockTourLogService.getAllTourLogs()).thenReturn(FXCollections.observableArrayList());
        when(mockTourLogService.getTourLogsfromTour(any())).thenReturn(FXCollections.observableArrayList());

        // ViewModels using mocks
        tourViewModel = new TourViewModel(mockTourService, mockTourLogService, mockReportService);
        tourLogViewModel = new TourLogViewModel(mockTourLogService);

        // Load FXML and inject controller
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/TourLogCreateWindow.fxml"));
        controller = new TourLogCreateViewController(tourViewModel, tourLogViewModel);
        loader.setController(controller);
        Parent root = loader.load();

        // Hook for created log
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
            clickOn("#tourComboBox").clickOn("Sample Tour");

            LocalDate today = LocalDate.now();
            interact(() -> controller.datePicker.setValue(today));

            clickOn("#timeField").write("12:30");
            clickOn("#commentField").write("Nice tour log");
            clickOn("#difficultyField").write("3");
            clickOn("#distanceField").write("10.5");
            clickOn("#durationField").write("01:30");
            clickOn("#ratingField").write("4");

            clickOn("#createButton");

            boolean success = latch.await(5, TimeUnit.SECONDS);
            assertThat(success).isTrue();

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

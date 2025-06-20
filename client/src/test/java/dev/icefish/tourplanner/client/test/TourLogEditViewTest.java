package dev.icefish.tourplanner.client.test;

import dev.icefish.tourplanner.client.controllers.TourLogEditViewController;
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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(ApplicationExtension.class)
public class TourLogEditViewTest extends TestFXBase {

    private TourLogEditViewController controller;
    private TourLog updatedTourLog;
    private CountDownLatch latch;

    private TourViewModel tourViewModel;
    private TourLogViewModel tourLogViewModel;

    private Tour sampleTour;
    private TourLog sampleTourLog;

    // Renamed fields for clarity, as we are now using spies.
    private TourService tourServiceSpy;
    private TourLogService tourLogServiceSpy;

    @Start
    public void start(Stage stage) throws Exception {
        // 1. Create REAL service objects using the special constructor to prevent startup fetching.
        TourService realTourService = new TourService(null, false);
        TourLogService realTourLogService = new TourLogService(null, false);

        // 2. Create "spies" of the real objects and ASSIGN THEM TO THE CLASS FIELDS.
        this.tourServiceSpy = Mockito.spy(realTourService);
        this.tourLogServiceSpy = Mockito.spy(realTourLogService);

        // 3. Stub the methods on the spies that would make network calls.
        // Using `doReturn(...).when(...)` is the safest syntax for spies.
        doReturn(FXCollections.observableArrayList()).when(this.tourServiceSpy).getAllTours();
        doReturn(FXCollections.observableArrayList()).when(this.tourLogServiceSpy).getAllTourLogs();
        doReturn(FXCollections.observableArrayList()).when(this.tourLogServiceSpy).getTourLogsfromTour(any(UUID.class));

        // 4. Create REAL ViewModels and inject the SPIED services into them.
        tourViewModel = new TourViewModel(this.tourServiceSpy, this.tourLogServiceSpy, null);
        tourLogViewModel = new TourLogViewModel(this.tourLogServiceSpy);

        // 5. Prepare sample data
        sampleTour = new Tour();
        sampleTour.setId(UUID.randomUUID());
        sampleTour.setName("Sample Tour");
        tourViewModel.getAllTours().add(sampleTour);

        sampleTourLog = new TourLog();
        sampleTourLog.setId(UUID.randomUUID());
        sampleTourLog.setTour(sampleTour);
        sampleTourLog.setDate(Timestamp.valueOf(LocalDateTime.of(LocalDate.now(), LocalTime.of(10, 0))));
        sampleTourLog.setComment("Sample comment");
        sampleTourLog.setDifficulty(3);
        sampleTourLog.setDistance(5.0);
        sampleTourLog.setDurationText("01:30");
        sampleTourLog.setRating(4);

        // 6. Stub the updateTourLog method on our spy that we want to test the call of.
        doAnswer(invocation -> {
            updatedTourLog = invocation.getArgument(0);
            latch.countDown();
            return null;
        }).when(this.tourLogServiceSpy).updateTourLog(any(TourLog.class));

        // 7. Load the FXML and provide the REAL controller that contains the ViewModels with our SPIED services.
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/TourLogCreateWindow.fxml"));
        controller = new TourLogEditViewController(tourViewModel, tourLogViewModel);
        loader.setController(controller);
        Parent root = loader.load();

        // 8. Finalize test setup
        controller.setTourLog(sampleTourLog);
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
            // Interact with UI controls to update the TourLog fields
            clickOn("#timeField").eraseText(5).write("12:30");
            clickOn("#commentField").eraseText(14).write("Updated comment"); // Adjusted for "Sample comment"
            clickOn("#difficultyField").eraseText(1).write("4");
            clickOn("#distanceField").eraseText(3).write("10.5");
            clickOn("#durationField").eraseText(5).write("02:15");
            clickOn("#ratingField").eraseText(1).write("5");

            // Click the save/create button
            clickOn("#createButton");

            // Wait for the latch to be counted down from the mocked service call or listener
            boolean success = latch.await(5, TimeUnit.SECONDS);
            assertThat(success).isTrue();

            // Assertions to check the updated TourLog content
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
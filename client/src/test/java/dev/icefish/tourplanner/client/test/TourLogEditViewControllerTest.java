package dev.icefish.tourplanner.client.test;

import dev.icefish.tourplanner.client.controllers.TourLogEditViewController;
import dev.icefish.tourplanner.client.utils.UUIDv7Generator;
import dev.icefish.tourplanner.models.Tour;
import dev.icefish.tourplanner.models.TourLog;
import dev.icefish.tourplanner.client.viewmodel.TourViewModel;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.util.WaitForAsyncUtils;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(ApplicationExtension.class)
public class TourLogEditViewControllerTest extends TestFXBase {

    private TourLogEditViewController controller;
    private TourViewModel tourViewModel;

    @Override
    public void start(Stage stage) throws Exception {
        tourViewModel = new TourViewModel();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/TourLogCreateWindow.fxml"));
        loader.setController(controller = new TourLogEditViewController(tourViewModel));
        Parent root = loader.load();
        controller = loader.getController();

        stage.setScene(new Scene(root));
        stage.show();
        stage.toFront();
    }

    @Test
    public void testEditTourLog() {
        assertNotNull(controller, "Controller should not be null");

        // Create a tour first
        Tour testTour = new Tour(UUIDv7Generator.generateUUIDv7(),"Test Tour", "Description", "Vienna", "Salzburg", "Car");
        tourViewModel.createNewTour(testTour);

        // Create a tour log
        TourLog testTourLog = new TourLog(testTour.getId(), Timestamp.valueOf("2023-01-01 10:00:00"), "Test Comment", 3, 10, "60", 4);

        Platform.runLater(() -> {
            controller.setTourLog(testTourLog);
            controller.setTourLogUpdatedListener(tourLog -> {
                assertNotNull(tourLog, "Updated tour log should not be null");
            });

            // Simulate user input and actions
            clickOn("#commentField").write("Updated Comment");
            clickOn("#createButton");

            // Wait for the UI to update
            WaitForAsyncUtils.waitForFxEvents();
        });
    }
}
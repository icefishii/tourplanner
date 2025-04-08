package dev.icefish.tourplanner.client.test;

import dev.icefish.tourplanner.client.controllers.TourLogCreateViewController;
import dev.icefish.tourplanner.client.utils.UUIDv7Generator;
import dev.icefish.tourplanner.models.Tour;
import dev.icefish.tourplanner.models.TourLog;
import dev.icefish.tourplanner.client.viewmodel.TourViewModel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.util.WaitForAsyncUtils;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(ApplicationExtension.class)
public class TourLogCreateViewControllerTest extends TestFXBase {

    private TourLogCreateViewController controller;
    private TourViewModel tourViewModel;

    @Override
    public void start(Stage stage) throws Exception {
        tourViewModel = new TourViewModel();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/TourLogCreateWindow.fxml"));
        loader.setController(controller = new TourLogCreateViewController(tourViewModel));
        Parent root = loader.load();
        controller = loader.getController();

        stage.setScene(new Scene(root));
        stage.show();
        stage.toFront();
    }

    @Test
    public void testCreateTourLog() {
        assertNotNull(controller, "Controller should not be null");

        // Create a tour first
        Tour testTour = new Tour(UUIDv7Generator.generateUUIDv7(),"Test Tour", "Description", "Vienna", "Salzburg", "Car");
        tourViewModel.createNewTour(testTour);

        // Wait for the UI to update
        WaitForAsyncUtils.waitForFxEvents();

        controller.setTourLogCreatedListener(tourLog -> {
            assertNotNull(tourLog, "Created tour log should not be null");
        });

        // Simulate user input and actions
        clickOn("#tourComboBox").clickOn(testTour.getName());
        clickOn("#datePicker").write("2023-01-01");
        clickOn("#timeField").write("10:00:00");
        clickOn("#commentField").write("Test Comment");
        clickOn("#difficultyField").write("3");
        clickOn("#distanceField").write("10");
        clickOn("#durationField").write("60");
        clickOn("#ratingField").write("4");
        clickOn("#createButton");

        // Wait for the UI to update
        WaitForAsyncUtils.waitForFxEvents();
    }
}
package dev.icefish.tourplanner.client.test;

import dev.icefish.tourplanner.client.controllers.TourEditViewController;
import dev.icefish.tourplanner.client.utils.UUIDv7Generator;
import dev.icefish.tourplanner.models.Tour;
import dev.icefish.tourplanner.models.TourLog;
import javafx.application.Platform;
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
public class TourEditViewControllerTest extends TestFXBase {

    private TourEditViewController controller;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/TourCreateWindow.fxml"));
        loader.setController(controller = new TourEditViewController());
        Parent root = loader.load();
        controller = loader.getController();

        stage.setScene(new Scene(root));
        stage.show();
        stage.toFront();
    }

    @Test
    public void testEditTour() {
        assertNotNull(controller, "Controller should not be null");

        Tour testTour = new Tour(UUIDv7Generator.generateUUIDv7(),"Test Tour", "Description", "Vienna", "Salzburg", "Car");
        Platform.runLater(() -> {
            controller.setTour(testTour);
            controller.setTourUpdatedListener(tour -> {
                assertNotNull(tour, "Updated tour should not be null");
            });

            // Simulate user input and actions
            clickOn("#tourNameField").write("Updated Test Tour");
            clickOn("#createButton");

            // Wait for the UI to update
            WaitForAsyncUtils.waitForFxEvents();
        });
    }
}
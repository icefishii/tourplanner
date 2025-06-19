package dev.icefish.tourplanner.client.test;

import dev.icefish.tourplanner.client.controllers.TourCreateViewController;
import dev.icefish.tourplanner.client.services.GeoCoder;
import dev.icefish.tourplanner.client.services.MapService;
import dev.icefish.tourplanner.client.services.OpenRouteService;
import dev.icefish.tourplanner.client.utils.ConfigLoader;
import dev.icefish.tourplanner.client.viewmodel.MapViewModel;
import dev.icefish.tourplanner.models.Tour;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.within;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;
import static org.testfx.api.FxToolkit.setupFixture;

@ExtendWith(ApplicationExtension.class)
public class TourCreationIntegrationTest extends TestFXBase {

    private TourCreateViewController controller;
    private CountDownLatch tourCreatedLatch;
    private Tour createdTour;

    @Mock
    private MapViewModel mapViewModel;

    @Start
    public void start(Stage stage) throws IOException {
        controller = new TourCreateViewController();
        tourCreatedLatch = new CountDownLatch(1);

        controller.setTourCreatedListener(tour -> {
            createdTour = tour;
            tourCreatedLatch.countDown();
        });

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/TourCreateWindow.fxml"));
        loader.setController(controller);
        Parent root = loader.load();

        Scene scene = new Scene(root, 800, 600);
        stage.setScene(scene);
        stage.show();

        scene.getStylesheets().add(getClass().getResource("/stylesheet.css").toExternalForm());
    }

    @Test
    public void testValidationErrors(FxRobot robot) throws Exception {
        setupFixture(() -> robot.clickOn("#createButton"));
        waitForFxEvents();

        setupFixture(() -> {
            TextField nameField = robot.lookup("#tourNameField").queryAs(TextField.class);
            TextField fromField = robot.lookup("#fromLocationField").queryAs(TextField.class);
            TextField toField = robot.lookup("#toLocationField").queryAs(TextField.class);
            ComboBox<?> transportBox = robot.lookup("#transportTypeBox").queryAs(ComboBox.class);

            assertThat(hasErrorStyle(nameField)).isTrue();
            assertThat(hasErrorStyle(fromField)).isTrue();
            assertThat(hasErrorStyle(toField)).isTrue();
            assertThat(hasErrorStyle(transportBox)).isTrue();
        });
    }

    private boolean hasErrorStyle(Control control) {
        return control.getStyleClass().contains("error-field") ||
                control.getStyle().contains("-fx-border-color: red");
    }

    @Test
    public void testLoadMapButton() {
        try (MockedStatic<GeoCoder> geocoderMock = Mockito.mockStatic(GeoCoder.class)) {
            // Mock the GeoCoder to return fixed coordinates
            geocoderMock.when(() -> GeoCoder.getCoordinates(anyString()))
                    .thenReturn(new double[]{48.2082, 16.3719}); // Vienna coordinates

            // Fill in the form fields
            clickOn("#tourNameField").write("Test Tour");
            clickOn("#tourDescriptionField").write("Test Description");
            clickOn("#fromLocationField").write("Vienna");
            clickOn("#toLocationField").write("Salzburg");
            clickOn("#transportTypeBox").clickOn("Car");

            // Click the load map button
            clickOn("#loadMapButton");

        }
    }

}

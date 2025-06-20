package dev.icefish.tourplanner.client.test;

import dev.icefish.tourplanner.client.controllers.TourCreateViewController;
import dev.icefish.tourplanner.client.controllers.TourEditViewController;
import dev.icefish.tourplanner.client.services.GeoCoder;
import dev.icefish.tourplanner.client.services.MapService;
import dev.icefish.tourplanner.client.services.OpenRouteService;
import dev.icefish.tourplanner.client.utils.ConfigLoader;
import dev.icefish.tourplanner.models.Tour;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.lang.reflect.Field;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.within;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.testfx.api.FxToolkit.setupFixture;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

@ExtendWith(ApplicationExtension.class)
public class TourCreateViewTest extends TestFXBase {

    private TourCreateViewController controller;
    private Tour createdTour;
    private CountDownLatch tourCreatedLatch;

    @Start
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/TourCreateWindow.fxml"));
        loader.setController(new TourCreateViewController());
        Parent root = loader.load();
        controller = loader.getController();

        tourCreatedLatch = new CountDownLatch(1);
        controller.setTourCreatedListener(tour -> {
            createdTour = tour;
            tourCreatedLatch.countDown();
        });

        stage.setScene(new Scene(root, 800, 600));
        stage.show();
    }

    @Test
    public void testCreateTourWithoutLoadingMap() {
        try (
                MockedStatic<GeoCoder> geoMock = org.mockito.Mockito.mockStatic(GeoCoder.class);
                MockedStatic<OpenRouteService> orsMock = org.mockito.Mockito.mockStatic(OpenRouteService.class);
                MockedStatic<ConfigLoader> configMock = org.mockito.Mockito.mockStatic(ConfigLoader.class);
                MockedStatic<MapService> mapMock = org.mockito.Mockito.mockStatic(MapService.class)
        ) {
            // Static mocks
            geoMock.when(() -> GeoCoder.getCoordinates(any()))
                    .thenReturn(new double[]{48.2, 16.3});

            orsMock.when(() -> OpenRouteService.getRouteInfo(any(), any(), any()))
                    .thenReturn(new OpenRouteService.RouteInfo(300.5, 3.5));

            configMock.when(() -> ConfigLoader.get("openrouteservice.api.key")).thenReturn("mock-api-key");
            configMock.when(() -> ConfigLoader.get("image.basePath")).thenReturn("maps");

            mapMock.when(() -> MapService.saveWebViewSnapshot(any(), any(), any()))
                    .thenAnswer(invocation -> null);

            // Prevent the need to load the map
            Field mapLoadedField = TourCreateViewController.class.getDeclaredField("mapLoaded");
            mapLoadedField.setAccessible(true);
            mapLoadedField.set(controller, true);

            // Fill form fields
            clickOn("#tourNameField").write("Test Tour");
            clickOn("#tourDescriptionField").write("Test Description");
            clickOn("#fromLocationField").write("Vienna");
            clickOn("#toLocationField").write("Salzburg");
            clickOn("#transportTypeBox").clickOn("Car");

            // Click "Create" directly
            clickOn("#createButton");
            waitForFxEvents();

            // Wait for creation
            boolean success = tourCreatedLatch.await(5, TimeUnit.SECONDS);
            assertThat(success).isTrue();

            // Assertions
            assertThat(createdTour).isNotNull();
            assertThat(createdTour.getName()).isEqualTo("Test Tour");
            assertThat(createdTour.getDistance()).isCloseTo(300.5, within(5.0));
            assertThat(createdTour.getEstimatedTime()).isCloseTo(3.0, within(1.0));

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}

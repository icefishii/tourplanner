package dev.icefish.tourplanner.client.utils;

import dev.icefish.tourplanner.client.controllers.*;
import dev.icefish.tourplanner.client.viewmodel.MapViewModel;
import dev.icefish.tourplanner.client.viewmodel.TourLogViewModel;
import dev.icefish.tourplanner.client.viewmodel.TourViewModel;
import javafx.fxml.FXMLLoader;
import javafx.util.Callback;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.IOException;

public class ControllerFactory implements Callback<Class<?>, Object> {
    public final static Logger logger = LogManager.getLogger(ControllerFactory.class);
    private final TourViewModel tourViewModel;
    private final TourLogViewModel tourLogViewModel;
    private final MapViewModel mapViewModel;

    public ControllerFactory(TourViewModel tourViewModel, TourLogViewModel tourLogViewModel, MapViewModel mapViewModel) {
        this.tourViewModel = tourViewModel;
        this.tourLogViewModel = tourLogViewModel;
        this.mapViewModel = mapViewModel;
    }

    @Override
    public Object call(Class<?> controllerClass) {
        if (controllerClass == MainViewController.class) {
            return new MainViewController(tourViewModel, tourLogViewModel, mapViewModel);
        } else if (controllerClass == TourCreateViewController.class) {
            return new TourCreateViewController();
        } else if (controllerClass == TourEditViewController.class) {
            return new TourEditViewController();
        } else if (controllerClass == TourLogCreateViewController.class) {
            return new TourLogCreateViewController(tourViewModel, tourLogViewModel);
        } else if (controllerClass == TourLogEditViewController.class) {
            return new TourLogEditViewController(tourViewModel);
        } else if (controllerClass == TourDetailViewController.class) {
            return new TourDetailViewController();
        } else if (controllerClass == TourLogDetailViewController.class) {
            return new TourLogDetailViewController();
        } else if (controllerClass == ImportViewController.class) {
            return new ImportViewController();
        }

        // If the controller isn't recognized, throw an exception
        throw new IllegalArgumentException("Unknown controller class: " + controllerClass.getName());
    }

    private <T> T loadView(String fxmlPath, Class<T> controllerType) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));

            // Create controller instance based on type
            T controller;
            if (controllerType.equals(MainViewController.class)) {
                controller = controllerType.cast(new MainViewController(tourViewModel, tourLogViewModel, mapViewModel));
            } else if (controllerType.equals(TourCreateViewController.class)) {
                controller = controllerType.cast(new TourCreateViewController());
            } else if (controllerType.equals(TourLogCreateViewController.class)) {
                controller = controllerType.cast(new TourLogCreateViewController(tourViewModel, tourLogViewModel));
            } else if (controllerType.equals(TourEditViewController.class)) {
                controller = controllerType.cast(new TourEditViewController());
            } else if (controllerType.equals(TourLogEditViewController.class)) {
                controller = controllerType.cast(new TourLogEditViewController(tourViewModel));
            } else if (controllerType.equals(ImportViewController.class)) {
                controller = controllerType.cast(new ImportViewController());
            } else {
                throw new IllegalArgumentException("Unsupported controller type: " + controllerType.getName());
            }

            loader.setController(controller);
            loader.load();
            return controller;
        } catch (IOException e) {
            logger.error("Failed to load view: {} - {}", fxmlPath, e.getMessage());
            throw new RuntimeException("Failed to load view: " + fxmlPath, e);
        }
    }
}

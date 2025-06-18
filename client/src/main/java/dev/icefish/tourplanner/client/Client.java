package dev.icefish.tourplanner.client;

import dev.icefish.tourplanner.client.controllers.MainViewController;
import dev.icefish.tourplanner.client.services.TourLogService;
import dev.icefish.tourplanner.client.services.TourService;
import dev.icefish.tourplanner.client.viewmodel.MapViewModel;
import dev.icefish.tourplanner.client.viewmodel.TourLogViewModel;
import dev.icefish.tourplanner.client.viewmodel.TourViewModel;
import dev.icefish.tourplanner.client.services.ReportService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Client extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        // Set up logging
        Logger logger = LogManager.getLogger(Client.class);
        logger.info("Starting Tour Planner Client...");
        // Initialize services with API connections
        TourService tourService = new TourService();
        TourLogService tourLogService = new TourLogService();
        ReportService reportService = new ReportService();
        TourLogViewModel tourLogViewModel = new TourLogViewModel(tourLogService);

        // Initialize ViewModels
        TourViewModel tourViewModel = new TourViewModel(tourService,tourLogService, reportService);

        MapViewModel mapViewModel = new MapViewModel();

        // Load FXML and inject dependencies
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/TourPlannerWindow.fxml"));
        loader.setControllerFactory(param -> new MainViewController(tourViewModel, tourLogViewModel, mapViewModel));

        Scene scene = new Scene(loader.load(), 620, 440);

        stage.setTitle("Tour Planner System");
        stage.setScene(scene);


        stage.setMinWidth(1400);
        stage.setMinHeight(800);

        stage.setMaximized(true);

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
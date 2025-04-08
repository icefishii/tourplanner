package dev.icefish.tourplanner.client;

import dev.icefish.tourplanner.client.controllers.MainViewController;
import dev.icefish.tourplanner.client.services.TourLogServiceTemp;
import dev.icefish.tourplanner.client.services.TourServiceTemp;
import dev.icefish.tourplanner.client.viewmodel.TourLogViewModel;
import dev.icefish.tourplanner.client.viewmodel.TourViewModel;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Client extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        // Initialize services
        TourServiceTemp tourService = new TourServiceTemp();
        TourLogServiceTemp tourLogService = new TourLogServiceTemp();

        // Initialize ViewModels
        TourViewModel tourViewModel = new TourViewModel(tourService);
        TourLogViewModel tourLogViewModel = new TourLogViewModel(tourLogService);

        // Load FXML and inject dependencies
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/TourPlannerWindow.fxml"));
        loader.setControllerFactory(param -> new MainViewController(tourViewModel, tourLogViewModel));

        Scene scene = new Scene(loader.load(), 620, 440);
        stage.setTitle("Tour Planner System");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
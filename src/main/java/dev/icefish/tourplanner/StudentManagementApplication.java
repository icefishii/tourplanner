package dev.icefish.tourplanner;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class StudentManagementApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(StudentManagementApplication.class.getResource("StudentManagementWindow.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 620, 440);
        stage.setTitle("Student Manegement System");
        stage.setScene(scene);
        stage.show();
        stage.sizeToScene();
    }

    public static void main(String[] args) {
        launch();
    }
}
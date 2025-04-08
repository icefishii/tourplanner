module dev.icefish.tourplanner.client {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.sql;
    requires dev.icefish.tourplanner.models;


    opens dev.icefish.tourplanner.client to javafx.fxml;
    exports dev.icefish.tourplanner.client;
    exports dev.icefish.tourplanner.client.controllers;
    opens dev.icefish.tourplanner.client.controllers to javafx.fxml;
}
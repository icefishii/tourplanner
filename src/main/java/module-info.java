module dev.icefish.tourplanner {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;


    opens dev.icefish.tourplanner to javafx.fxml;
    exports dev.icefish.tourplanner;
    exports dev.icefish.tourplanner.controllers;
    opens dev.icefish.tourplanner.controllers to javafx.fxml;
}
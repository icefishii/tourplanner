module dev.icefish.tourplanner {
    requires javafx.controls;
    requires javafx.fxml;


    opens dev.icefish.tourplanner to javafx.fxml;
    exports dev.icefish.tourplanner;
}
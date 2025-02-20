package dev.icefish.tourplanner;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

public class HelloController {
    @FXML
    private Label welcomeText;

    @FXML
    private ComboBox<String> myComboBox;

    @FXML
    protected void onHelloButtonClick() {
        String selectedValue = myComboBox.getValue(); // Wert der ComboBox holen
        if (selectedValue != null) {
            welcomeText.setText(selectedValue); // Den Wert ausgeben
        } else {
            welcomeText.setText("Bitte eine Option auswählen!");
        }
    }
}
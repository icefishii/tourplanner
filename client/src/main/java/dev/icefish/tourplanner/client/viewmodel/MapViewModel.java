package dev.icefish.tourplanner.client.viewmodel;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.image.Image;

import java.io.File;
import java.util.UUID;

public class MapViewModel {

    private final ObjectProperty<Image> currentMapImage = new SimpleObjectProperty<>();

    private static final String MAPS_FOLDER_PATH = "C:/Users/miria/IdeaProjects/tourplanner/client/maps/"; //ToDo Pfad
    private static final String DEFAULT_IMAGE_PATH = "/images/maps.jpeg"; // Dein Fallback-Bild aus dem Ressourcenordner

    public MapViewModel() {
        // Setze Standardbild beim Start
        currentMapImage.set(new Image(getClass().getResource(DEFAULT_IMAGE_PATH).toExternalForm()));
    }

    public ObjectProperty<Image> currentMapImageProperty() {
        return currentMapImage;
    }

    public Image getCurrentMapImage() {
        return currentMapImage.get();
    }

    public void setMapImageForTour(UUID tourId) {
        if (tourId == null) {
            setDefaultImage();
            return;
        }
        String filePath = MAPS_FOLDER_PATH + tourId.toString() + ".png";
        File imageFile = new File(filePath);
        if (imageFile.exists()) {
            currentMapImage.set(new Image(imageFile.toURI().toString()));
        } else {
            setDefaultImage();
        }
    }

    private void setDefaultImage() {
        currentMapImage.set(new Image(getClass().getResource(DEFAULT_IMAGE_PATH).toExternalForm()));
    }
}

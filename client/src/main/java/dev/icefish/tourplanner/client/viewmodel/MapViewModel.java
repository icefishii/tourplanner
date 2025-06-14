package dev.icefish.tourplanner.client.viewmodel;
import dev.icefish.tourplanner.client.utils.ConfigLoader;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.image.Image;

import java.io.File;
import java.util.UUID;
import java.nio.file.Paths;

public class MapViewModel {

    private final ObjectProperty<Image> currentMapImage = new SimpleObjectProperty<>();

    private final String imageBasePath;
    private static final String DEFAULT_IMAGE_PATH = "/images/maps.jpeg"; // Dein Fallback-Bild aus dem Ressourcenordner

    public MapViewModel() {
        // Setze Standardbild beim Start
        this.imageBasePath = ConfigLoader.get("image.basePath");
        setDefaultImage();
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
        File imageFile = Paths.get(imageBasePath, tourId.toString() + ".png").toFile();

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

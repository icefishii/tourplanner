// MapService.java
package dev.icefish.tourplanner.client.services;

import dev.icefish.tourplanner.client.viewmodel.MapViewModel;
import dev.icefish.tourplanner.models.Tour;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.web.WebView;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class MapService {

    public static void saveWebViewSnapshot(WebView webView, Tour tour, MapViewModel mapViewModel) {
        WritableImage snapshot = webView.snapshot(new SnapshotParameters(), null);
        int width = (int) snapshot.getWidth();
        int height = (int) snapshot.getHeight();

        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int argb = snapshot.getPixelReader().getArgb(x, y);
                bufferedImage.setRGB(x, y, argb);
            }
        }

        try {
            File dir = new File("maps/");
            if (!dir.exists() && !dir.mkdirs()) {
                System.err.println("Failed to create maps/ directory.");
                return;
            }

            File outputFile = new File(dir, tour.getId() + ".png");
            ImageIO.write(bufferedImage, "png", outputFile);

            System.out.println("Snapshot saved to: " + outputFile.getAbsolutePath());

            // Jetzt MapViewModel aktualisieren:
            mapViewModel.setMapImageForTour(tour.getId());

        } catch (IOException e) {
            System.err.println("Failed to save WebView snapshot: " + e.getMessage());
        }
    }


}

package dev.icefish.tourplanner.client.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import dev.icefish.tourplanner.client.utils.ConfigLoader;
import dev.icefish.tourplanner.models.Tour;
import dev.icefish.tourplanner.models.exceptions.ServiceException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.UUID;

public class TourService {
    private final static Logger logger = LogManager.getLogger(TourService.class);
    HttpClient httpClient;
    ObjectMapper objectMapper;
    private final String BASE_URL = "http://localhost:8080/api/tours";
    final ObservableList<Tour> tours = FXCollections.observableArrayList();

    public TourService(HttpClient httpClient, boolean fetchOnStartup) {
        this.httpClient = httpClient;
        this.objectMapper = new ObjectMapper()
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .setDateFormat(new StdDateFormat());

        if (fetchOnStartup) {
            fetchToursFromServer();
        }
    }


    public TourService() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper()
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .setDateFormat(new StdDateFormat());
        fetchToursFromServer();
    }

    private void fetchToursFromServer() {
        logger.info("Fetching tours from server...");
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                List<Tour> fetchedTours = objectMapper.readValue(response.body(),
                        objectMapper.getTypeFactory().constructCollectionType(List.class, Tour.class));
                tours.setAll(fetchedTours);
            }
        } catch (IOException | InterruptedException e) {
            logger.error(e.getMessage());
            throw new ServiceException("Error fetching tour logs: " + e.getMessage());
        }
    }

    public ObservableList<Tour> getAllTours() {
        fetchToursFromServer();
        return tours;
    }

    public void createNewTour(Tour tour) {
        logger.info("Creating new tour: {}", tour.getName());
        try {
            String tourJson = objectMapper.writeValueAsString(tour);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(tourJson))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 201) {
                Tour createdTour = objectMapper.readValue(response.body(), Tour.class);
                tours.add(createdTour);
            }
        } catch (IOException | InterruptedException e) {
            logger.error(e.getMessage());
            throw new ServiceException("Error fetching tour logs: " + e.getMessage());
        }
    }

    public void updateTour(Tour tour) {
        logger.info("Updating tour: {}", tour.getId());
        try {
            String tourJson = objectMapper.writeValueAsString(tour);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/" + tour.getId()))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(tourJson))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                Tour updatedTour = objectMapper.readValue(response.body(), Tour.class);
                int index = -1;
                for (int i = 0; i < tours.size(); i++) {
                    if (tours.get(i).getId().equals(tour.getId())) {
                        index = i;
                        break;
                    }
                }
                if (index != -1) {
                    tours.set(index, updatedTour);
                }
            }
        } catch (IOException | InterruptedException e) {
            logger.error(e.getMessage());
            throw new ServiceException("Error fetching tour logs: " + e.getMessage());
        }
    }

    public void deleteTour(Tour tour) {
        logger.info("Deleting tour: {}", tour.getId());
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/" + tour.getId()))
                    .DELETE()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 204) {
                //Tour aus Liste entfernen
                tours.removeIf(t -> t.getId().equals(tour.getId()));

                //Bild dazu löschen
                deleteMapImage(tour.getId());
            }
        } catch (IOException | InterruptedException e) {
            logger.error(e.getMessage());
            throw new ServiceException("Error fetching tour logs: " + e.getMessage());
        }
    }
    private void deleteMapImage(UUID tourId) {
        String basePath = ConfigLoader.get("image.basePath");
        if (basePath == null) {
            logger.warn("No image.basePath set in config.properties");
            return;
        }

        File imageFile = new File(basePath, tourId + ".png");
        if (imageFile.exists()) {
            if (imageFile.delete()) {
                logger.info("Deleted image file: {}", imageFile.getAbsolutePath());
            } else {
                logger.warn("Could not delete image file: {}", imageFile.getAbsolutePath());
            }
        } else {
            logger.info("No image file found to delete: {}", imageFile.getAbsolutePath());
        }
    }
}
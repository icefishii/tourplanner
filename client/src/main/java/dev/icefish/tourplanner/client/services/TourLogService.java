package dev.icefish.tourplanner.client.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import dev.icefish.tourplanner.models.TourLog;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.UUID;

public class TourLogService {
    private final static Logger logger = LogManager.getLogger(TourLogService.class);
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String BASE_URL = "http://localhost:8080/api/tourlogs";
    private final ObservableList<TourLog> tourLogs = FXCollections.observableArrayList();

    public TourLogService() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper()
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .setDateFormat(new StdDateFormat());
        fetchTourLogsFromServer();
    }

    public TourLogService(HttpClient httpClient, boolean fetchOnStartup) {
        this.httpClient = httpClient;
        this.objectMapper = new ObjectMapper()
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .setDateFormat(new StdDateFormat());

        if (fetchOnStartup) {
            fetchTourLogsFromServer();
        }
    }

    private void fetchTourLogsFromServer() {
        logger.info("Fetching tour logs from server...");
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                List<TourLog> fetchedTourLogs = objectMapper.readValue(response.body(),
                        objectMapper.getTypeFactory().constructCollectionType(List.class, TourLog.class));
                tourLogs.setAll(fetchedTourLogs);
            }
        } catch (IOException | InterruptedException e) {
            logger.error("Error fetching tour logs: {}", e.getMessage());
            e.printStackTrace();
        }
    }

    public ObservableList<TourLog> getAllTourLogs() {
        fetchTourLogsFromServer();
        return tourLogs;
    }

    public ObservableList<TourLog> getTourLogsfromTour(UUID tourId) {
        logger.info("Fetching tour logs for tour ID: {}", tourId);
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/tour/" + tourId))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                List<TourLog> fetchedTourLogs = objectMapper.readValue(response.body(),
                        objectMapper.getTypeFactory().constructCollectionType(List.class, TourLog.class));
                return FXCollections.observableArrayList(fetchedTourLogs);
            }
        } catch (IOException | InterruptedException e) {
            logger.error("Error fetching tour logs for tour ID {}: {}", tourId, e.getMessage());
            e.printStackTrace();
        }
        return FXCollections.observableArrayList();
    }

    public void createNewTourLog(TourLog tourLog) {
        logger.info("Creating new tour log: {}", tourLog.getId());
        try {
            String tourLogJson = objectMapper.writeValueAsString(tourLog);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(tourLogJson))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 201) {
                TourLog createdTourLog = objectMapper.readValue(response.body(), TourLog.class);
                tourLogs.add(createdTourLog);
            }
        } catch (IOException | InterruptedException e) {
            logger.error("Error creating tour log: {}", e.getMessage());
            e.printStackTrace();
        }
    }

    public void updateTourLog(TourLog tourLog) {
        logger.info("Updating tour log: {}", tourLog.getId());
        try {
            String tourLogJson = objectMapper.writeValueAsString(tourLog);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/" + tourLog.getId()))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(tourLogJson))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                TourLog updatedTourLog = objectMapper.readValue(response.body(), TourLog.class);
                int index = -1;
                for (int i = 0; i < tourLogs.size(); i++) {
                    if (tourLogs.get(i).getId().equals(tourLog.getId())) {
                        index = i;
                        break;
                    }
                }
                if (index != -1) {
                    tourLogs.set(index, updatedTourLog);
                }
            }
        } catch (IOException | InterruptedException e) {
            logger.error("Error updating tour log: {}", e.getMessage());
            e.printStackTrace();
        }
    }

    public void deleteTourLog(TourLog tourLog) {
        logger.info("Deleting tour log: {}", tourLog.getId());
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/" + tourLog.getId()))
                    .DELETE()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 204) {
                tourLogs.removeIf(t -> t.getId().equals(tourLog.getId()));
            }
        } catch (IOException | InterruptedException e) {
            logger.error("Error deleting tour log: {}", e.getMessage());
            e.printStackTrace();
        }
    }
}
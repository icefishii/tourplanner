package dev.icefish.tourplanner.client.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.icefish.tourplanner.models.Tour;
import dev.icefish.tourplanner.models.TourLog;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

public class ImportService {
    private final static Logger logger = LogManager.getLogger(ImportService.class);
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public ImportService() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public String importToursAndLogs(File file) {
        try {
            // Read the combined JSON structure
            Map<String, Object> data = objectMapper.readValue(file, Map.class);

            // Extract tours and tour logs
            List<Tour> tours = objectMapper.convertValue(data.get("tours"),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, Tour.class));
            List<TourLog> tourLogs = objectMapper.convertValue(data.get("tourLogs"),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, TourLog.class));

            // Import tours
            String toursJson = objectMapper.writeValueAsString(tours);
            HttpRequest toursRequest = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/tours/import"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(toursJson))
                    .build();
            HttpResponse<String> toursResponse = httpClient.send(toursRequest, HttpResponse.BodyHandlers.ofString());

            if (toursResponse.statusCode() != 200) {
                logger.error("Error importing tours: {}", toursResponse.body());
                return "Error importing tours: " + toursResponse.body();
            }

            // Import tour logs
            String tourLogsJson = objectMapper.writeValueAsString(tourLogs);
            HttpRequest logsRequest = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/tourlogs/import"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(tourLogsJson))
                    .build();
            HttpResponse<String> logsResponse = httpClient.send(logsRequest, HttpResponse.BodyHandlers.ofString());

            if (logsResponse.statusCode() != 200) {
                logger.error("Error importing tour logs: {}", logsResponse.body());
                return "Error importing tour logs: " + logsResponse.body();
            }

            return "Tours and tour logs imported successfully!";
        } catch (IOException | InterruptedException e) {
            logger.error("Error during import: {}", e.getMessage());
            return "An error occurred: " + e.getMessage();
        }
    }
}
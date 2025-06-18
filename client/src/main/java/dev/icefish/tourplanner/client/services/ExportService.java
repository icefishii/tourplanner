package dev.icefish.tourplanner.client.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExportService {
    private final static Logger logger = LogManager.getLogger(ExportService.class);
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public ExportService() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public String exportToursAndLogs(File file) {
        try {
            // Fetch tours
            HttpRequest toursRequest = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/tours/export"))
                    .GET()
                    .build();
            HttpResponse<String> toursResponse = httpClient.send(toursRequest, HttpResponse.BodyHandlers.ofString());

            // Fetch tour logs
            HttpRequest logsRequest = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/tourlogs/export"))
                    .GET()
                    .build();
            HttpResponse<String> logsResponse = httpClient.send(logsRequest, HttpResponse.BodyHandlers.ofString());

            if (toursResponse.statusCode() == 200 && logsResponse.statusCode() == 200) {
                // Combine tours and logs into a single JSON structure
                List<?> tours = objectMapper.readValue(toursResponse.body(), List.class);
                List<?> tourLogs = objectMapper.readValue(logsResponse.body(), List.class);

                Map<String, Object> combinedData = new HashMap<>();
                combinedData.put("tours", tours);
                combinedData.put("tourLogs", tourLogs);

                // Write combined data to file
                objectMapper.writeValue(file, combinedData);
                return "Tours and tour logs exported successfully!";
            } else {
                logger.error("Error exporting data: Tours response code {}, Logs response code {}",
                        toursResponse.statusCode(), logsResponse.statusCode());
                return "Error exporting data: " + toursResponse.body() + " | " + logsResponse.body();
            }
        } catch (IOException | InterruptedException e) {
            logger.error("Error during export: {}", e.getMessage());
            return "An error occurred: " + e.getMessage();
        }
    }
}
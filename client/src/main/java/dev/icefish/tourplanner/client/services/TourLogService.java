package dev.icefish.tourplanner.client.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import dev.icefish.tourplanner.models.TourLog;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class TourLogService {

    private final HttpClient httpClient;
    private final Gson gson;
    private final String BASE_URL = "http://localhost:8080/api/tourlogs";
    private final ObservableList<TourLog> tourLogs = FXCollections.observableArrayList();

    public TourLogService() {
        this.httpClient = HttpClient.newHttpClient();
        this.gson = new GsonBuilder().create();
        fetchTourLogsFromServer();
    }

    private void fetchTourLogsFromServer() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                Type tourLogListType = new TypeToken<List<TourLog>>(){}.getType();
                List<TourLog> fetchedTourLogs = gson.fromJson(response.body(), tourLogListType);
                tourLogs.setAll(fetchedTourLogs);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public ObservableList<TourLog> getAllTourLogs() {
        fetchTourLogsFromServer();
        return tourLogs;
    }

    public ObservableList<TourLog> getTourLogsfromTour(UUID tourId) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/tour/" + tourId))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                Type tourLogListType = new TypeToken<List<TourLog>>(){}.getType();
                List<TourLog> fetchedTourLogs = gson.fromJson(response.body(), tourLogListType);
                return FXCollections.observableArrayList(fetchedTourLogs);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return FXCollections.observableArrayList();
    }

    public void createNewTourLog(TourLog tourLog) {
        try {
            String tourLogJson = gson.toJson(tourLog);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(tourLogJson))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 201) {
                TourLog createdTourLog = gson.fromJson(response.body(), TourLog.class);
                tourLogs.add(createdTourLog);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void updateTourLog(TourLog tourLog) {
        try {
            String tourLogJson = gson.toJson(tourLog);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/" + tourLog.getId()))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(tourLogJson))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                TourLog updatedTourLog = gson.fromJson(response.body(), TourLog.class);
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
            e.printStackTrace();
        }
    }

    public void deleteTourLog(TourLog tourLog) {
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
            e.printStackTrace();
        }
    }
}
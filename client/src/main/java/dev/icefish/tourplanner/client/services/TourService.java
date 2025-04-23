package dev.icefish.tourplanner.client.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import dev.icefish.tourplanner.models.Tour;
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

public class TourService {

    private final HttpClient httpClient;
    private final Gson gson;
    private final String BASE_URL = "http://localhost:8080/api/tours";
    private final ObservableList<Tour> tours = FXCollections.observableArrayList();

    public TourService() {
        this.httpClient = HttpClient.newHttpClient();
        this.gson = new GsonBuilder().create();
        fetchToursFromServer();
    }

    private void fetchToursFromServer() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                Type tourListType = new TypeToken<List<Tour>>(){}.getType();
                List<Tour> fetchedTours = gson.fromJson(response.body(), tourListType);
                tours.setAll(fetchedTours);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public ObservableList<Tour> getAllTours() {
        fetchToursFromServer();
        return tours;
    }

    public void createNewTour(Tour tour) {
        try {
            String tourJson = gson.toJson(tour);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(tourJson))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 201) {
                Tour createdTour = gson.fromJson(response.body(), Tour.class);
                tours.add(createdTour);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void updateTour(Tour tour) {
        try {
            String tourJson = gson.toJson(tour);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/" + tour.getId()))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(tourJson))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                Tour updatedTour = gson.fromJson(response.body(), Tour.class);
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
            e.printStackTrace();
        }
    }

    public void deleteTour(Tour tour) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/" + tour.getId()))
                    .DELETE()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 204) {
                tours.removeIf(t -> t.getId().equals(tour.getId()));
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
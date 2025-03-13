/*package dev.icefish.tourplanner.client.services;

import dev.icefish.tourplanner.client.model.Tour;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import static com.google.gson.internal.bind.TypeAdapters.URI;

//TODO: Implement the REST API in he server

public class TourService {

    private static final String BASE_URL = "http://localhost/api/tours";

    public ObservableList<Tour> getAllTours() {
        ObservableList<Tour> tours = FXCollections.observableArrayList();
        try {
            URL url = new URL(BASE_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                throw new RuntimeException("HttpResponseCode: " + responseCode);
            } else {
                Scanner scanner = new Scanner(url.openStream());
                while (scanner.hasNext()) {
                    // Parse the response and add to tours list
                    //tours.add(parseTour(scanner.nextLine()));
                }
                scanner.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tours;
    }


    public void createNewTour(Tour tour) {
        try {
            URL url = new URL(BASE_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("PUT");
            conn.setDoOutput(true);
            // Write tour data to the output stream
            // Example: conn.getOutputStream().write(tour.toJson().getBytes());
            conn.getOutputStream().flush();
            conn.getOutputStream().close();

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                throw new RuntimeException("HttpResponseCode: " + responseCode);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


 */
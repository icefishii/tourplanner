package dev.icefish.tourplanner.client.services;
import dev.icefish.tourplanner.client.utils.ConfigLoader;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

//This was created with the help of ChatGPT Model-4o

public class OpenRouteService {
    public static final Logger logger = LogManager.getLogger(OpenRouteService.class);
    private static final String API_KEY = ConfigLoader.get("openrouteservice.api.key");
    private static final String BASE_URL = "https://api.openrouteservice.org/v2/directions/";

    public static RouteInfo getRouteInfo(String from, String to, String transportType) throws Exception {
        String profile = switch (transportType.toLowerCase()) {
            case "walk" -> "foot-walking";
            case "bike" -> "cycling-regular";
            case "car" -> "driving-car";
            default -> throw new IllegalArgumentException("Unsupported transport type: " + transportType);
        };

        //Koordinaten
        double[] fromCoords = GeoCoder.getCoordinates(from);
        double[] toCoords = GeoCoder.getCoordinates(to);

        // Gson JSON-Body
        JsonArray coords = new JsonArray();

        JsonArray fromArray = new JsonArray();
        fromArray.add(fromCoords[1]); // longitude
        fromArray.add(fromCoords[0]); // latitude

        JsonArray toArray = new JsonArray();
        toArray.add(toCoords[1]); // longitude
        toArray.add(toCoords[0]); // latitude

        coords.add(fromArray);
        coords.add(toArray);

        JsonObject body = new JsonObject();
        body.add("coordinates", coords);

        // HTTP Request vorbereiten
        URL url = new URL(BASE_URL + profile);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", API_KEY);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(body.toString().getBytes());
        }

        Scanner scanner = new Scanner(conn.getInputStream()).useDelimiter("\\A");
        String response = scanner.hasNext() ? scanner.next() : "";

        // JSON-Antwort mit Gson auswerten
        JsonObject jsonResponse = com.google.gson.JsonParser.parseString(response).getAsJsonObject();
        JsonObject summary = jsonResponse
                .getAsJsonArray("routes")
                .get(0).getAsJsonObject()
                .getAsJsonObject("summary");

        double distance = summary.get("distance").getAsDouble() / 1000.0; // Meter -> km
        double duration = summary.get("duration").getAsDouble() / 3600.0; // Sekunden -> Stunden

        return new RouteInfo(distance, duration);
    }

    public record RouteInfo(double distanceInKm, double durationInHours) {}
}

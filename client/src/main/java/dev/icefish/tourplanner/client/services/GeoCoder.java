package dev.icefish.tourplanner.client.services;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonArray;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

//This was created with the help of ChatGPT Model-4o

public class GeoCoder {
    public final static Logger logger = LogManager.getLogger(GeoCoder.class);
    private static final String NOMINATIM_URL = "https://nominatim.openstreetmap.org/search?format=json&q=";

    public static double[] getCoordinates(String location) throws Exception {
        // URL für die API mit dem Ortsnamen
        InputStreamReader reader = getInputStreamReader(location);
        JsonArray results = JsonParser.parseReader(reader).getAsJsonArray();

        // Wenn keine Ergebnisse zurückgegeben wurden, werfen wir eine Ausnahme
        if (results.isEmpty()) {
            logger.error("No coordinates found for location: {}", location);
            throw new Exception("No coordinates found for location: " + location);
        }

        // Holen der Koordinaten aus der Antwort (erstes Ergebnis)
        JsonObject firstResult = results.get(0).getAsJsonObject();
        double lat = firstResult.get("lat").getAsDouble();
        double lon = firstResult.get("lon").getAsDouble();

        // Gibt die Koordinaten als Array zurück (lat, lon)
        return new double[]{lat, lon};
    }

    private static InputStreamReader getInputStreamReader(String location) throws IOException {
        String urlString = NOMINATIM_URL + location.replaceAll(" ", "+");

        // Use URI instead of directly creating URL from string
        URI uri = URI.create(urlString);
        URL url = uri.toURL();

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", "TourPlannerApp/1.0"); // Important User-Agent for using Nominatim correctly

        return new InputStreamReader(connection.getInputStream());
    }
}

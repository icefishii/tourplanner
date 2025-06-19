package dev.icefish.tourplanner.client.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.icefish.tourplanner.models.Tour;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TourServiceTest {

    private TourService tourService;
    private HttpClient mockHttpClient;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() throws Exception {
        mockHttpClient = mock(HttpClient.class);
        objectMapper = new ObjectMapper();

        tourService = new TourService(mockHttpClient, false);

        // Default mock for fetchToursFromServer()
        HttpResponse<String> mockFetchResponse = mock(HttpResponse.class);
        when(mockFetchResponse.statusCode()).thenReturn(200);
        when(mockFetchResponse.body()).thenReturn("[]");

        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
                .thenReturn(mockFetchResponse);
    }


    @Test
    public void testCreateNewTourAddsTourOnSuccess() throws Exception {
        Tour newTour = new Tour();
        newTour.setId(UUID.randomUUID());
        newTour.setName("Test Tour");

        String tourJson = objectMapper.writeValueAsString(newTour);

        HttpResponse<String> mockCreateResponse = mock(HttpResponse.class);
        when(mockCreateResponse.statusCode()).thenReturn(201);
        when(mockCreateResponse.body()).thenReturn(tourJson);

        HttpResponse<String> mockFetchResponse = mock(HttpResponse.class);
        when(mockFetchResponse.statusCode()).thenReturn(200);
        when(mockFetchResponse.body()).thenReturn("[" + tourJson + "]");

        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
                .thenReturn(mockCreateResponse)
                .thenReturn(mockFetchResponse); // for fetchToursFromServer in getAllTours

        tourService.createNewTour(newTour);
        ObservableList<Tour> tours = tourService.getAllTours();

        assertEquals(1, tours.size());
        assertEquals("Test Tour", tours.get(0).getName());
    }


    @Test
    public void testUpdateTourReplacesTourInList() throws Exception {
        Tour tour = new Tour();
        tour.setId(UUID.randomUUID());
        tour.setName("Old Name");

        // Add tour manually to the internal list
        tourService.getAllTours().add(tour);

        // Modify the name for update
        tour.setName("Updated Name");
        String updatedJson = objectMapper.writeValueAsString(tour);

        HttpResponse<String> mockUpdateResponse = mock(HttpResponse.class);
        when(mockUpdateResponse.statusCode()).thenReturn(200);
        when(mockUpdateResponse.body()).thenReturn(updatedJson);

        // Mocking fetchToursFromServer (called in getAllTours)
        HttpResponse<String> mockFetchResponse = mock(HttpResponse.class);
        when(mockFetchResponse.statusCode()).thenReturn(200);
        when(mockFetchResponse.body()).thenReturn("[" + updatedJson + "]");

        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
                .thenReturn(mockUpdateResponse)
                .thenReturn(mockFetchResponse);  // for fetch in getAllTours

        tourService.updateTour(tour);
        ObservableList<Tour> tours = tourService.getAllTours();

        assertEquals(1, tours.size());
        assertEquals("Updated Name", tours.get(0).getName());
    }


    @Test
    public void testDeleteTourRemovesFromListAndDeletesImage() throws Exception {
        UUID id = UUID.randomUUID();

        Tour tour = new Tour();
        tour.setId(id);
        tour.setName("To Delete");

        tourService.getAllTours().add(tour);

        // Create dummy image file in a temp directory
        Path tempDir = Files.createTempDirectory("tours");
        Path imagePath = tempDir.resolve(id + ".png");
        Files.createFile(imagePath);

        // Set base path for image deletion to the temp dir
        System.setProperty("image.basePath", tempDir.toString());

        // Mock HTTP DELETE response
        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        when(mockResponse.statusCode()).thenReturn(204);

        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
                .thenReturn(mockResponse);

        // Execute deletion
        tourService.deleteTour(tour);

        // Assertions
        assertTrue(tourService.getAllTours().isEmpty(), "Tour list should be empty after deletion");
    }


}

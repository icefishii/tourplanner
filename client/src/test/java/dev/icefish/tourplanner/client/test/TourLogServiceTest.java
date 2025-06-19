package dev.icefish.tourplanner.client.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.icefish.tourplanner.client.services.TourLogService;
import dev.icefish.tourplanner.models.Tour;
import dev.icefish.tourplanner.models.TourLog;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.http.*;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TourLogServiceTest {

    private HttpClient mockHttpClient;
    private HttpResponse<String> mockResponse;
    private TourLogService tourLogService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockHttpClient = mock(HttpClient.class);
        mockResponse = mock(HttpResponse.class);
        tourLogService = new TourLogService(mockHttpClient, false); // don't auto-fetch
    }

    @Test
    void testGetAllTourLogsFetchesFromServer() throws Exception {
        Tour tour = new Tour();
        tour.setId(UUID.randomUUID());
        tour.setName("Test Tour");

        TourLog newLog = new TourLog(UUID.randomUUID(), tour, "log", LocalDate.now(), 1, 2.5, 3, 4);
        List<TourLog> fakeLogs = List.of(newLog);
        String json = objectMapper.writeValueAsString(fakeLogs);

        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
                .thenReturn(mockResponse);
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn(json);

        ObservableList<TourLog> logs = tourLogService.getAllTourLogs();
        assertEquals(1, logs.size());
    }

    @Test
    void testGetTourLogsFromTourReturnsCorrectList() throws Exception {
        UUID tourId = UUID.fromString("1925cb27-efed-4aef-a407-813ce7ca0c68");
        Tour tour = new Tour();
        tour.setId(tourId);
        tour.setName("Test Tour");

        TourLog log = new TourLog(UUID.randomUUID(), tour, "log", LocalDate.now(), 1, 2.5, 3, 4);
        List<TourLog> logs = List.of(log);
        String json = objectMapper.writeValueAsString(logs);

        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
                .thenReturn(mockResponse);
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn(json);

        ObservableList<TourLog> result = tourLogService.getTourLogsfromTour(tourId);
        assertEquals(1, result.size());
        assertEquals(tourId, result.getFirst().getTour().getId());
    }

    @Test
    void testCreateNewTourLogAddsToList() throws Exception {
        Tour tour = new Tour();
        tour.setId(UUID.randomUUID());
        tour.setName("Test Tour");

        TourLog newLog = new TourLog(UUID.randomUUID(), tour, "log", LocalDate.now(), 1, 2.5, 3, 4);
        String json = objectMapper.writeValueAsString(newLog);

        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
                .thenReturn(mockResponse);
        when(mockResponse.statusCode()).thenReturn(201);
        when(mockResponse.body()).thenReturn(json);

        tourLogService.createNewTourLog(newLog);
        assertTrue(tourLogService.getAllTourLogs().contains(newLog));
    }

    @Test
    void testUpdateTourLogUpdatesInList() throws Exception {
        UUID tourId = UUID.randomUUID();
        Tour tour = new Tour();
        tour.setId(tourId);

        UUID logId = UUID.randomUUID();

        TourLog original = new TourLog(logId, tour, "old", LocalDate.now(), 1, 1.0, 1, 1);
        TourLog updated = new TourLog(logId, tour, "updated", LocalDate.now(), 2, 2.0, 2, 2);

        String fetchJson = objectMapper.writeValueAsString(List.of(original));
        String updatedJson = objectMapper.writeValueAsString(updated);

        // Mock sequential calls: first fetch original logs, then update returns updated log
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
                .thenReturn(mockResponse) // first call: getAllTourLogs fetch
                .thenReturn(mockResponse); // second call: updateTourLog

        // Set up response codes and bodies for the sequence
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn(fetchJson, updatedJson);

        // Initial load of logs
        ObservableList<TourLog> logs = tourLogService.getAllTourLogs();

        // Now update the tour log
        tourLogService.updateTourLog(updated);

        // Verify the update reflected in the list
        assertEquals("updated", tourLogService.getAllTourLogs().get(0).getComment());
    }

    @Test
    void testDeleteTourLogRemovesFromList() throws Exception {
        // Arrange
        // Create a TourLog object for testing
        TourLog testLog = new TourLog();
        testLog.setId(UUID.randomUUID());
        testLog.setComment("Test log");

        // Convert a list with the testLog to JSON string (must be a JSON array)
        String jsonList = objectMapper.writeValueAsString(List.of(testLog));

        // Mock HttpResponse for fetching logs (returning list)
        HttpResponse<String> fetchResponse = mock(HttpResponse.class);
        when(fetchResponse.statusCode()).thenReturn(200);
        when(fetchResponse.body()).thenReturn(jsonList);

        // Mock HttpResponse for delete (usually empty body or confirmation)
        HttpResponse<String> deleteResponse = mock(HttpResponse.class);
        when(deleteResponse.statusCode()).thenReturn(204); // HTTP 204 No Content for successful delete
        when(deleteResponse.body()).thenReturn("");

        // Mock the HttpClient send() calls:
        // First call (fetch all logs) returns fetchResponse
        // Second call (delete log) returns deleteResponse
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
                .thenReturn(fetchResponse)   // for getAllTourLogs
                .thenReturn(deleteResponse); // for deleteTourLog

        // Initialize the service (assuming constructor or setter injection)
        TourLogService service = new TourLogService(mockHttpClient, false); // don't auto-fetch

        // Act
        // Fetch initial logs so that the list contains the testLog
        service.getAllTourLogs();

        // Delete the test log
        service.deleteTourLog(testLog);

        // Assert
        // After deletion, the observable list should no longer contain testLog
        assertFalse(service.getAllTourLogs().contains(testLog));
    }

}

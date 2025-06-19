package dev.icefish.tourplanner.client.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.icefish.tourplanner.client.services.TourLogService;
import dev.icefish.tourplanner.models.Tour;
import dev.icefish.tourplanner.models.TourLog;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.http.HttpClient;
import java.net.http.HttpResponse;
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
        String json = objectMapper.writeValueAsString(fakeLogs);  // This is a JSON array

        when(mockHttpClient.send(any(), eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(mockResponse);
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

        when(mockHttpClient.send(any(), eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(mockResponse);
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

        when(mockHttpClient.send(any(), eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(mockResponse);
        when(mockResponse.statusCode()).thenReturn(201);
        when(mockResponse.body()).thenReturn(json);

        tourLogService.createNewTourLog(newLog);
        assertTrue(tourLogService.getAllTourLogs().contains(newLog));
    }

    @Test
    void testDeleteTourLogRemovesFromList() throws Exception {
        TourLog testLog = new TourLog();
        testLog.setId(UUID.randomUUID());
        testLog.setComment("Test log");

        String jsonList = objectMapper.writeValueAsString(List.of(testLog));

        HttpResponse<String> fetchResponse = mock(HttpResponse.class);
        when(fetchResponse.statusCode()).thenReturn(200);
        when(fetchResponse.body()).thenReturn(jsonList);

        HttpResponse<String> deleteResponse = mock(HttpResponse.class);
        when(deleteResponse.statusCode()).thenReturn(204);
        when(deleteResponse.body()).thenReturn("");

        when(mockHttpClient.send(any(), eq(HttpResponse.BodyHandlers.ofString())))
                .thenReturn(fetchResponse)
                .thenReturn(deleteResponse);

        TourLogService service = new TourLogService(mockHttpClient, false);

        service.getAllTourLogs();

        service.deleteTourLog(testLog);

        assertFalse(service.getAllTourLogs().contains(testLog));
    }

}

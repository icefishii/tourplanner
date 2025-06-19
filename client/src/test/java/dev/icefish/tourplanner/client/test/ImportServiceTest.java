package dev.icefish.tourplanner.client.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.icefish.tourplanner.client.services.ImportService;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.io.File;
import java.nio.file.Files;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

class ImportServiceTest {

    @Mock
    private HttpClient httpClientMock;

    @Mock
    private HttpResponse<String> httpResponseMock;

    private ImportService importService;

    private final String sampleJson = """
    {
      "tours": [
        {
          "id": "01976927-c541-7000-9cd6-429afffc7973",
          "version": 0,
          "name": "Test",
          "description": "1",
          "fromLocation": "Wien",
          "toLocation": "Graz",
          "transportType": "Car",
          "distance": 195.6286,
          "estimatedTime": 2.2581944444444444,
          "routeImagePath": null
        }
      ],
      "tourLogs": [
        {
          "id": "01976928-8e6e-7000-ae9a-17a9c45ecff5",
          "version": 0,
          "tour": {
            "id": "01976927-c541-7000-9cd6-429afffc7973",
            "version": 0,
            "name": "Test",
            "description": "1",
            "fromLocation": "Wien",
            "toLocation": "Graz",
            "transportType": "Car",
            "distance": 195.6286,
            "estimatedTime": 2.2581944444444444,
            "routeImagePath": null
          },
          "date": "2025-06-20T10:12:12.000+00:00",
          "comment": "^1",
          "difficulty": 2,
          "distance": 3,
          "durationText": "45",
          "rating": 5
        }
      ]
    }
    """;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        // Create an ImportService instance but replace its httpClient with the mocked one
        importService = new ImportService() {
            {
                // Reflectively inject the mock HttpClient
                try {
                    var httpClientField = ImportService.class.getDeclaredField("httpClient");
                    httpClientField.setAccessible(true);
                    httpClientField.set(this, httpClientMock);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    @Test
    void importToursAndLogs() throws Exception {
        // Prepare temp file with sample JSON
        File tempFile = Files.createTempFile("importTest", ".json").toFile();
        Files.writeString(tempFile.toPath(), sampleJson);

        // Mock HTTP response to always return 200 OK with some dummy body
        when(httpResponseMock.statusCode()).thenReturn(200);
        when(httpResponseMock.body()).thenReturn("Success");

        // Mock HttpClient to return the mock HttpResponse for any request
        when(httpClientMock.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(httpResponseMock);

        // Call the method
        String result = importService.importToursAndLogs(tempFile);

        // Verify success result
        assertEquals("Tours and tour logs imported successfully!", result);

        // Cleanup temp file
        tempFile.deleteOnExit();
    }
}

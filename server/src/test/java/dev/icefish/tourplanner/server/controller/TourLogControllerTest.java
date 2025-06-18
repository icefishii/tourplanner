package dev.icefish.tourplanner.server.controller;

import dev.icefish.tourplanner.models.TourLog;
import dev.icefish.tourplanner.server.service.TourLogService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TourLogController.class)
class TourLogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TourLogService tourLogService;

    @Test
    void getAllTourLogs_returnsOk() throws Exception {
        Mockito.when(tourLogService.getAllTourLogs()).thenReturn(List.of(new TourLog()));
        mockMvc.perform(get("/api/tourlogs"))
                .andExpect(status().isOk());
    }

    @Test
    void getTourLogById_found() throws Exception {
        UUID id = UUID.randomUUID();
        TourLog log = new TourLog();
        log.setId(id);
        Mockito.when(tourLogService.getTourLogById(id)).thenReturn(Optional.of(log));
        mockMvc.perform(get("/api/tourlogs/" + id))
                .andExpect(status().isOk());
    }

    @Test
    void getTourLogById_notFound() throws Exception {
        UUID id = UUID.randomUUID();
        Mockito.when(tourLogService.getTourLogById(id)).thenReturn(Optional.empty());
        mockMvc.perform(get("/api/tourlogs/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    void getTourLogsByTourId_returnsOk() throws Exception {
        UUID tourId = UUID.randomUUID();
        Mockito.when(tourLogService.getTourLogsByTourId(tourId)).thenReturn(List.of(new TourLog()));
        mockMvc.perform(get("/api/tourlogs/tour/" + tourId))
                .andExpect(status().isOk());
    }

    @Test
    void createTourLog_returnsCreated() throws Exception {
        TourLog log = new TourLog();
        log.setId(UUID.randomUUID());
        Mockito.when(tourLogService.saveTourLog(any(TourLog.class))).thenReturn(log);
        mockMvc.perform(post("/api/tourlogs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"comment\":\"Test Log\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    void updateTourLog_found() throws Exception {
        UUID id = UUID.randomUUID();
        TourLog log = new TourLog();
        log.setId(id);
        Mockito.when(tourLogService.getTourLogById(id)).thenReturn(Optional.of(log));
        Mockito.when(tourLogService.saveTourLog(any(TourLog.class))).thenReturn(log);
        mockMvc.perform(put("/api/tourlogs/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"comment\":\"Updated Log\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void updateTourLog_notFound() throws Exception {
        UUID id = UUID.randomUUID();
        Mockito.when(tourLogService.getTourLogById(id)).thenReturn(Optional.empty());
        mockMvc.perform(put("/api/tourlogs/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"comment\":\"Updated Log\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteTourLog_returnsNoContent() throws Exception {
        UUID id = UUID.randomUUID();
        mockMvc.perform(delete("/api/tourlogs/" + id))
                .andExpect(status().isNoContent());
    }

    @Test
    void importTourLogs_returnsOk() throws Exception {
        Mockito.when(tourLogService.importTourLogs(any())).thenReturn(List.of(new TourLog()));
        mockMvc.perform(post("/api/tourlogs/import")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[]"))
                .andExpect(status().isOk());
    }

    @Test
    void exportTourLogs_returnsOk() throws Exception {
        Mockito.when(tourLogService.getAllTourLogs()).thenReturn(List.of(new TourLog()));
        mockMvc.perform(get("/api/tourlogs/export"))
                .andExpect(status().isOk());
    }
}
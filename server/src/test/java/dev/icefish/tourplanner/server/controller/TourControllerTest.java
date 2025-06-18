package dev.icefish.tourplanner.server.controller;

import dev.icefish.tourplanner.models.Tour;
import dev.icefish.tourplanner.server.service.TourService;
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

@WebMvcTest(TourController.class)
class TourControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TourService tourService;

    @Test
    void getAllTours_returnsOk() throws Exception {
        Mockito.when(tourService.getAllTours()).thenReturn(List.of(new Tour()));
        mockMvc.perform(get("/api/tours"))
                .andExpect(status().isOk());
    }

    @Test
    void getTourById_found() throws Exception {
        UUID id = UUID.randomUUID();
        Tour tour = new Tour();
        tour.setId(id);
        Mockito.when(tourService.getTourById(id)).thenReturn(Optional.of(tour));
        mockMvc.perform(get("/api/tours/" + id))
                .andExpect(status().isOk());
    }

    @Test
    void getTourById_notFound() throws Exception {
        UUID id = UUID.randomUUID();
        Mockito.when(tourService.getTourById(id)).thenReturn(Optional.empty());
        mockMvc.perform(get("/api/tours/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    void createTour_returnsCreated() throws Exception {
        Tour tour = new Tour();
        tour.setId(UUID.randomUUID());
        Mockito.when(tourService.saveTour(any(Tour.class))).thenReturn(tour);
        mockMvc.perform(post("/api/tours")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Test Tour\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    void updateTour_found() throws Exception {
        UUID id = UUID.randomUUID();
        Tour tour = new Tour();
        tour.setId(id);
        Mockito.when(tourService.getTourById(id)).thenReturn(Optional.of(tour));
        Mockito.when(tourService.saveTour(any(Tour.class))).thenReturn(tour);
        mockMvc.perform(put("/api/tours/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Updated Tour\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void updateTour_notFound() throws Exception {
        UUID id = UUID.randomUUID();
        Mockito.when(tourService.getTourById(id)).thenReturn(Optional.empty());
        mockMvc.perform(put("/api/tours/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Updated Tour\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteTour_returnsNoContent() throws Exception {
        UUID id = UUID.randomUUID();
        mockMvc.perform(delete("/api/tours/" + id))
                .andExpect(status().isNoContent());
    }

    @Test
    void importTours_returnsOk() throws Exception {
        Mockito.when(tourService.importTours(any())).thenReturn(List.of(new Tour()));
        mockMvc.perform(post("/api/tours/import")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[]"))
                .andExpect(status().isOk());
    }

    @Test
    void exportTours_returnsOk() throws Exception {
        Mockito.when(tourService.getAllTours()).thenReturn(List.of(new Tour()));
        mockMvc.perform(get("/api/tours/export"))
                .andExpect(status().isOk());
    }
}
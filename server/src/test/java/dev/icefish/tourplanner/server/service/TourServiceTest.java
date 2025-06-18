package dev.icefish.tourplanner.server.service;

import dev.icefish.tourplanner.models.Tour;
import dev.icefish.tourplanner.server.repository.TourRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TourServiceTest {

    private TourRepository tourRepository;
    private TourService tourService;

    @BeforeEach
    void setUp() {
        tourRepository = mock(TourRepository.class);
        tourService = new TourService(tourRepository);
    }

    @Test
    void getAllTours_returnsList() {
        List<Tour> tours = List.of(new Tour());
        when(tourRepository.findAll()).thenReturn(tours);

        List<Tour> result = tourService.getAllTours();

        assertEquals(1, result.size());
        verify(tourRepository).findAll();
    }

    @Test
    void getTourById_returnsTour() {
        UUID id = UUID.randomUUID();
        Tour tour = new Tour();
        tour.setId(id);
        when(tourRepository.findById(id)).thenReturn(Optional.of(tour));

        Optional<Tour> result = tourService.getTourById(id);

        assertTrue(result.isPresent());
        assertEquals(id, result.get().getId());
    }

    @Test
    void saveTour_savesAndReturnsTour() {
        Tour tour = new Tour();
        when(tourRepository.save(tour)).thenReturn(tour);

        Tour result = tourService.saveTour(tour);

        assertEquals(tour, result);
        verify(tourRepository).save(tour);
    }

    @Test
    void deleteTour_deletesTour() {
        UUID id = UUID.randomUUID();

        tourService.deleteTour(id);

        verify(tourRepository).deleteById(id);
    }
}
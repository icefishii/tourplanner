package dev.icefish.tourplanner.server.repository;

import dev.icefish.tourplanner.models.Tour;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@EntityScan("dev.icefish.tourplanner.models")
@DataJpaTest
public class TourRepositoryTest {

    @Autowired
    private TourRepository tourRepository;

    @Test
    void saveAndFindTour() {
        Tour tour = new Tour();
        tour.setId(UUID.randomUUID());
        tour.setName("Test Tour");
        tourRepository.save(tour);

        assertTrue(tourRepository.findById(tour.getId()).isPresent());
    }
}
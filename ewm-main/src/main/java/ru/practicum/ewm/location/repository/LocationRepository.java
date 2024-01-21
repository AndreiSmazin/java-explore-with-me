package ru.practicum.ewm.location.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.location.entity.Location;

import java.util.List;

public interface LocationRepository extends JpaRepository<Location, Long> {
    Location findByLatAndLon(float lat, float lon);

    @Query(value = "SELECT * FROM locations WHERE distance(?1, ?2, lat, lon) <= ?3", nativeQuery = true)
    List<Location> findByDistance(float lat, float lon, float distance);
}

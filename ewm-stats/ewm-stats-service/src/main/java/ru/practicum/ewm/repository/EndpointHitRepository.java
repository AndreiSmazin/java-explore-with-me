package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.entity.EndpointHit;

public interface EndpointHitRepository extends JpaRepository<EndpointHit, Long> {
}

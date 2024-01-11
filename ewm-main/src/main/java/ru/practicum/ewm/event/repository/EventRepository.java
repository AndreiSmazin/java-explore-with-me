package ru.practicum.ewm.event.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.event.entity.Event;

public interface EventRepository extends JpaRepository<Event, Long> {
    Page<Event> findEventByInitiatorId(Pageable pageable, Long initiatorId);
}

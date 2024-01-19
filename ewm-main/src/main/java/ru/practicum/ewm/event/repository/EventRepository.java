package ru.practicum.ewm.event.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.event.entity.Event;
import ru.practicum.ewm.event.entity.EventState;

import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event> {
    Page<Event> findEventByInitiatorId(Pageable pageable, Long initiatorId);

    @Transactional
    @Modifying
    @Query("UPDATE Event" +
            " SET confirmedRequests = ?2" +
            " WHERE id = ?1")
    void updateConfirmedRequests(Long id, long number);

    @Transactional
    @Modifying
    @Query("UPDATE Event" +
            " SET views = ?2" +
            " WHERE id = ?1")
    void updateViews(Long id, long number);

    Optional<Event> findEventByIdAndState(Long id, EventState state);

    long countByCategory_Id(long categoryId);
}

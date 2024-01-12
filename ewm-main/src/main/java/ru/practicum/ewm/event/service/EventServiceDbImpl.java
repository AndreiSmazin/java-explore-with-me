package ru.practicum.ewm.event.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.category.service.CategoryService;
import ru.practicum.ewm.client.StatsClient;
import ru.practicum.ewm.dto.EndpointHitCreateDto;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.event.dto.UpdateEventRequest;
import ru.practicum.ewm.event.dto.UpdateEventUserRequest;
import ru.practicum.ewm.event.entity.AdminEventStateAction;
import ru.practicum.ewm.event.entity.Event;
import ru.practicum.ewm.event.entity.EventState;
import ru.practicum.ewm.event.entity.SortingBy;
import ru.practicum.ewm.event.entity.UserEventStateAction;
import ru.practicum.ewm.event.entity.QEvent;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.mapper.LocationMapper;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.event.repository.LocationRepository;
import ru.practicum.ewm.exception.ObjectNotFoundException;
import ru.practicum.ewm.exception.ViolationOperationRulesException;
import ru.practicum.ewm.user.service.UserService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceDbImpl implements EventService {
    private final EventRepository eventRepository;
    private final LocationRepository locationRepository;
    private final CategoryService categoryService;
    private final UserService userService;
    private final EventMapper eventMapper;
    private final LocationMapper locationMapper;
    private final StatsClient statsClient;

    @Override
    public EventFullDto createNewEvent(long userId, NewEventDto eventDto) {
        log.debug("+ createNewEvent: {}, {}", userId, eventDto);

        Event event = eventMapper.newEventDtoToEvent(eventDto);

        event.setLocation(locationRepository.save(locationMapper.locationDtoToLocation(eventDto.getLocation())));
        event.setCategory(categoryService.checkCategory(eventDto.getCategory()));
        validateEventDate(event.getEventDate());
        event.setCreatedOn(LocalDateTime.now());
        event.setInitiator(userService.checkUser(userId));
        event.setState(EventState.PENDING);

        return eventMapper.eventToEventFullDto(eventRepository.save(event));
    }

    @Override
    public List<EventShortDto> getEventsOfUser(int from, int size, long userId) {
        return eventRepository.findEventByInitiatorId(PageRequest.of(from, size), userId).stream()
                .map(eventMapper::eventToEventShortDto)
                .collect(Collectors.toList());
    }

    @Override
    public Event checkEvent(long id) {
        return eventRepository.findById(id).orElseThrow(() -> {
            String message = "No Event entity with id " + id + " exists!";
            throw new ObjectNotFoundException("Event", id, message);
        });
    }

    @Override
    public EventFullDto getEventById(long id) {
        return eventMapper.eventToEventFullDto(checkEvent(id));
    }

    @Override
    public EventFullDto updateEventByUser(long userId, long id, UpdateEventUserRequest eventDto) {
        log.debug("+ updateEventByUser: {}, {}, {}", userId, id, eventDto);

        Event event = checkEvent(id);
        validateState(event.getState());

        updateEventFields(event, eventDto);

        if (eventDto.getStateAction() != null) {
            if (eventDto.getStateAction().equals(UserEventStateAction.CANCEL_REVIEW)
                    && event.getState().equals(EventState.PENDING)) {
                event.setState(EventState.CANCELED);
            }
            if (eventDto.getStateAction().equals(UserEventStateAction.SEND_TO_REVIEW)
                    && event.getState().equals(EventState.CANCELED)) {
                event.setState(EventState.PENDING);
            }
        }

        return eventMapper.eventToEventFullDto(eventRepository.save(event));
    }

    @Override
    public List<EventFullDto> getEvents(int from, int size, Long[] users, EventState[] states, Long[] categories,
                                        LocalDateTime rangeStart, LocalDateTime rangeEnd) {
        BooleanExpression usersPredicate = QEvent.event.initiator.id.in(users);
        BooleanExpression statesPredicate = QEvent.event.state.in(states);
        BooleanExpression categoriesPredicate = QEvent.event.category.id.in(categories);
        BooleanExpression rangePredicate = QEvent.event.eventDate.between(rangeStart, rangeEnd);

        BooleanExpression predicates = usersPredicate.and(statesPredicate).and(categoriesPredicate).and(rangePredicate);

        return eventRepository.findAll(predicates, PageRequest.of(from, size)).stream()
                .map(eventMapper::eventToEventFullDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto updateEventByAdmin(long id, UpdateEventAdminRequest eventDto) {
        log.debug("+ updateEventByAdmin: {}, {}", id, eventDto);

        Event event = checkEvent(id);

        updateEventFields(event, eventDto);

        if (eventDto.getStateAction() != null) {
            if (eventDto.getStateAction().equals(AdminEventStateAction.PUBLISH_EVENT)) {
                validateStateForPublishing(event.getState());

                LocalDateTime publicationTime = LocalDateTime.now();

                if (event.getEventDate().isBefore(publicationTime.plusHours(1L))) {
                    throw new ViolationOperationRulesException("EventDate must be not earlier then 1 hour after " +
                            "publication");
                }

                event.setState(EventState.PUBLISHED);
                event.setPublishedOn(publicationTime);
            }
            if (eventDto.getStateAction().equals(AdminEventStateAction.REJECT_EVENT)) {
                validateState(event.getState());

                event.setState(EventState.CANCELED);
            }
        }

        return eventMapper.eventToEventFullDto(eventRepository.save(event));
    }

    @Override
    public List<EventShortDto> getEventsWithFilters(int from, int size, String text, Long[] categories, Boolean paid,
                                                    LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                                    boolean onlyAvailable, SortingBy sortingBy,
                                                    HttpServletRequest request) {
        BooleanExpression predicates = QEvent.event.state.eq(EventState.PUBLISHED);

        if (text != null) {
            predicates = predicates.and(QEvent.event.annotation.likeIgnoreCase("%" + text + "%")
                    .or(QEvent.event.description.likeIgnoreCase("%" + text + "%")));
        }
        if (categories != null) {
            predicates = predicates.and(QEvent.event.category.id.in(categories));
        }
        if (paid != null) {
            predicates = predicates.and(QEvent.event.paid.eq(paid));
        }
        if (rangeStart != null) {
            predicates = predicates.and(QEvent.event.eventDate.after(rangeStart));
        }
        if (rangeEnd != null) {
            predicates = predicates.and(QEvent.event.eventDate.before(rangeEnd));
        }

        if (rangeStart == null && rangeEnd == null) {
            predicates = predicates.and(QEvent.event.eventDate.after(LocalDateTime.now()));
        }

        if (onlyAvailable) {
            predicates = predicates.and(QEvent.event.confirmedRequests.ne(QEvent.event.participantLimit.longValue())
                    .or(QEvent.event.participantLimit.eq(0)));
        }

        List<EventShortDto> events = eventRepository.findAll(predicates, PageRequest.of(from, size)).stream()
                .map(eventMapper::eventToEventShortDto)
                .collect(Collectors.toList());

        if (sortingBy != null) {
            if (sortingBy.equals(SortingBy.VIEWS)) {
                events.sort(Comparator.comparingLong(EventShortDto::getViews));
            } else if (sortingBy.equals(SortingBy.EVENT_DATE)) {
                events.sort(Comparator.comparing(EventShortDto::getEventDate));
            }
        }

        sendStatistics(request);

        return events;
    }

    @Override
    public EventFullDto getPublishedEvent(long id, HttpServletRequest request) {
        Event event = eventRepository.findEventByIdAndState(id, EventState.PUBLISHED).orElseThrow(() -> {
            String message = "No Event entity with id " + id + " exists!";
            throw new ObjectNotFoundException("Event", id, message);
        });

        increaseViews(event);
        sendStatistics(request);

        return eventMapper.eventToEventFullDto(event);
    }

    private void increaseViews(Event event){
        long views = event.getViews() + 1;
        eventRepository.updateViews(event.getId(), views);
    }

    private void validateEventDate(LocalDateTime eventDate) {
        if (eventDate.isBefore(LocalDateTime.now().plusHours(2L))) {
            throw new ViolationOperationRulesException("EventDate must be not earlier then 2 hour after event " +
                    "creation or changing");
        }
    }

    private void validateState(EventState state) {
        if (state.equals(EventState.PUBLISHED)) {
            throw new ViolationOperationRulesException("Only pending or canceled events can be changed");
        }
    }

    private void validateStateForPublishing(EventState state) {
        if (!state.equals(EventState.PENDING)) {
            throw new ViolationOperationRulesException("Only pending events can be published");
        }
    }

    private <T extends UpdateEventRequest> void updateEventFields(Event event, T eventDto) {
        if (eventDto.getAnnotation() != null) {
            event.setAnnotation(eventDto.getAnnotation());
        }

        if (eventDto.getCategory() != null) {
            event.setCategory(categoryService.checkCategory(eventDto.getCategory()));
        }

        if (eventDto.getDescription() != null) {
            event.setDescription(eventDto.getDescription());
        }

        if (eventDto.getEventDate() != null) {
            validateEventDate(eventDto.getEventDate());
            event.setEventDate(eventDto.getEventDate());
        }

        if (eventDto.getLocation() != null) {
            event.setLocation(locationRepository.save(locationMapper.locationDtoToLocation(eventDto.getLocation())));
        }

        if (eventDto.getPaid() != null) {
            event.setPaid(eventDto.getPaid());
        }

        if (eventDto.getParticipantLimit() != null) {
            event.setParticipantLimit(eventDto.getParticipantLimit());
        }

        if (eventDto.getRequestModeration() != null) {
            event.setRequestModeration(eventDto.getRequestModeration());
        }

        if (eventDto.getTitle() != null) {
            event.setTitle(eventDto.getTitle());
        }
    }

    private void sendStatistics(HttpServletRequest request) {
        EndpointHitCreateDto endpointDto = new EndpointHitCreateDto();
        endpointDto.setApp("ru.practicum.ewm.EwmMain");
        endpointDto.setUri(request.getRequestURI());
        endpointDto.setIp(request.getRemoteAddr());
        endpointDto.setTimestamp(LocalDateTime.now());

        statsClient.postEndpointHit(endpointDto);
    }
}

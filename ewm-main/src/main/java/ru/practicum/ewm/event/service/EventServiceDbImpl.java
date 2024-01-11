package ru.practicum.ewm.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.category.service.CategoryService;
import ru.practicum.ewm.event.dto.EventDto;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.dto.UpdateEventUserRequest;
import ru.practicum.ewm.event.entity.Event;
import ru.practicum.ewm.event.entity.EventState;
import ru.practicum.ewm.event.entity.EventStateAction;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.mapper.LocationMapper;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.event.repository.LocationRepository;
import ru.practicum.ewm.exception.ObjectNotFoundException;
import ru.practicum.ewm.exception.ViolationOperationRulesException;
import ru.practicum.ewm.participation.entity.ParticipationRequestStatus;
import ru.practicum.ewm.participation.repository.ParticipationRequestRepository;
import ru.practicum.ewm.user.service.UserService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceDbImpl implements EventService {
    private final EventRepository eventRepository;
    private final LocationRepository locationRepository;
    private final ParticipationRequestRepository participationRequestRepository;
    private final CategoryService categoryService;
    private final UserService userService;
    private final EventMapper eventMapper;
    private final LocationMapper locationMapper;

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
                .peek(this::addConfirmedRequestsAndViews)
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
        EventFullDto eventDto = eventMapper.eventToEventFullDto(checkEvent(id));
        addConfirmedRequestsAndViews(eventDto);

        return eventDto;
    }

    @Override
    public EventFullDto updateEventByUser(long userId, long id, UpdateEventUserRequest eventDto) {
        log.debug("+ updateEventByUser: {}, {}, {}", userId, id, eventDto);

        Event event = checkEvent(id);
        validateState(event.getState());

        updateEventFields(event, eventDto);

        if (eventDto.getStateAction().equals(EventStateAction.CANCEL_REVIEW)
                && event.getState().equals(EventState.PENDING)) {
            event.setState(EventState.CANCELED);
        }
        if (eventDto.getStateAction().equals(EventStateAction.SEND_TO_REVIEW)
                && event.getState().equals(EventState.CANCELED)) {
            event.setState(EventState.PENDING);
        }

        EventFullDto updatedEventDto = eventMapper.eventToEventFullDto(eventRepository.save(event));
        addConfirmedRequestsAndViews(updatedEventDto);
        return updatedEventDto;
    }



    private void validateEventDate(LocalDateTime eventDate) {
        if (eventDate.isBefore(LocalDateTime.now().plusHours(2L))) {
            throw new ViolationOperationRulesException("Field: eventDate. Error: должно содержать дату, которая еще " +
                    "не наступила. Value: " + eventDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        }
    }

    private void validateState(EventState state) {
        if (state.equals(EventState.PUBLISHED)) {
            throw new ViolationOperationRulesException("Only pending or canceled events can be changed");
        }
    }

    private <T extends UpdateEventUserRequest> void updateEventFields(Event event, T eventDto) {
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

    private <T extends EventDto> void addConfirmedRequestsAndViews(T eventDto) {
        eventDto.setConfirmedRequests(participationRequestRepository.countByEvent_IdAndStatus(eventDto.getId(),
                ParticipationRequestStatus.CONFIRMED));
        // добавить просмотры
    }
}

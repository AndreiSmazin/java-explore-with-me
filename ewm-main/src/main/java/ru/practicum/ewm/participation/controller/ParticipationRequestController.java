package ru.practicum.ewm.participation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.participation.dto.ParticipationRequestDto;
import ru.practicum.ewm.participation.service.ParticipationRequestService;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ParticipationRequestController {
    private final ParticipationRequestService participationRequestService;

    @PostMapping("/users/{userId}/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto createParticipationRequest(@PathVariable(name = "userId") long userId,
                                                              @RequestParam(name = "eventId") long eventId) {
        log.debug("Received POST-request /users/{}?eventId={}", userId, eventId);

        return participationRequestService.createNewParticipationRequest(userId, eventId);
    }
}

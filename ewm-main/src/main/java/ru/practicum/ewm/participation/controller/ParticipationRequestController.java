package ru.practicum.ewm.participation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.participation.dto.ParticipationRequestDto;
import ru.practicum.ewm.participation.service.ParticipationRequestService;

import java.util.List;

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

    @GetMapping("/users/{userId}/requests")
    public List<ParticipationRequestDto> getParticipationRequestsOfUser(@PathVariable(name = "userId") long userId) {
        return participationRequestService.getParticipationRequestsOfUser(userId);
    }

    @PatchMapping("/users/{userId}/requests/{id}/cancel")
    public ParticipationRequestDto cancelParticipationRequestByUser(@PathVariable(name = "userId") long userId,
                                                                    @PathVariable(name = "id") long id) {
        log.debug("Received PATCH-request /users/{}/requests/{}/cancel", userId, id);

        return participationRequestService.cancelParticipationRequestByUser(userId, id);
    }
}

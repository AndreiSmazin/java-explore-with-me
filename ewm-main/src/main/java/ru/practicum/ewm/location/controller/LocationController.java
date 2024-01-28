package ru.practicum.ewm.location.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.location.dto.LocationFullDto;
import ru.practicum.ewm.location.dto.NewExtendedLocationDto;
import ru.practicum.ewm.location.dto.UpdateLocationRequest;
import ru.practicum.ewm.location.service.LocationService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class LocationController {
    private final LocationService locationService;

    @PostMapping("/admin/locations")
    @ResponseStatus(HttpStatus.CREATED)
    public LocationFullDto createLocation(@Valid @RequestBody NewExtendedLocationDto locationDto) {
        log.debug("Received POST-request /admin/locations with body: {}", locationDto);

        return locationService.createNewLocation(locationDto);
    }

    @PatchMapping("/admin/locations/{id}")
    public LocationFullDto updateLocation(@PathVariable(name = "id") long id,
                                          @Valid @RequestBody UpdateLocationRequest locationDto) {
        log.debug("Received PATCH-request /admin/locations/{} with body: {}", id, locationDto);

        return locationService.updateLocation(id, locationDto);
    }

    @GetMapping("/locations")
    public List<LocationFullDto> findLocations(@RequestParam(name = "lat", required = false) Float lat,
                                               @RequestParam(name = "lon", required = false) Float lon,
                                               @RequestParam(name = "radius", required = false) Integer radius) {
        return locationService.findLocations(lat, lon, radius);
    }
}

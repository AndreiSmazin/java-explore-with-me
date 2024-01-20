package ru.practicum.ewm.location.service;

import ru.practicum.ewm.location.dto.LocationFullDto;
import ru.practicum.ewm.location.dto.NewExtendedLocationDto;
import ru.practicum.ewm.location.dto.NewLocationDto;
import ru.practicum.ewm.location.dto.UpdateLocationRequest;
import ru.practicum.ewm.location.entity.Location;

public interface LocationService {
    LocationFullDto createNewLocation(NewExtendedLocationDto locationDto);

    LocationFullDto updateLocation(long id, UpdateLocationRequest locationDto);

    Location checkLocation(NewLocationDto locationDto);
}

package ru.practicum.ewm.location.service;

import ru.practicum.ewm.location.dto.LocationFullDto;
import ru.practicum.ewm.location.dto.NewExtendedLocationDto;
import ru.practicum.ewm.location.dto.NewLocationDto;
import ru.practicum.ewm.location.dto.UpdateLocationRequest;
import ru.practicum.ewm.location.entity.Location;

import java.util.List;

public interface LocationService {
    LocationFullDto createNewLocation(NewExtendedLocationDto locationDto);

    LocationFullDto updateLocation(long id, UpdateLocationRequest locationDto);

    Location getOrCreateLocation(NewLocationDto locationDto);

    Location checkLocation(long id);

    List<LocationFullDto> findLocations(Float lat, Float lon, Integer radius);
}

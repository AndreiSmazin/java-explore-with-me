package ru.practicum.ewm.location.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.exception.ObjectNotFoundException;
import ru.practicum.ewm.location.dto.LocationFullDto;
import ru.practicum.ewm.location.dto.NewExtendedLocationDto;
import ru.practicum.ewm.location.dto.NewLocationDto;
import ru.practicum.ewm.location.dto.UpdateLocationRequest;
import ru.practicum.ewm.location.entity.Location;
import ru.practicum.ewm.location.mapper.LocationMapper;
import ru.practicum.ewm.location.repository.LocationRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class LocationServiceDbImpl implements LocationService {
    private final LocationRepository locationRepository;
    private final LocationMapper locationMapper;

    @Override
    public LocationFullDto createNewLocation(NewExtendedLocationDto locationDto) {
        log.debug("+ createNewLocation: {}", locationDto);

        Location location = locationMapper.newExtendedLocationDtoToLocation(locationDto);

        return locationMapper.locationToLocationFullDto(locationRepository.save(location));
    }

    @Override
    public LocationFullDto updateLocation(long id, UpdateLocationRequest locationDto) {
        log.debug("+ updateLocation: {}, {}", id, locationDto);

        Location location = locationRepository.findById(id).orElseThrow(() -> {
            String message = "No Location entity with id " + id + " exists!";
            throw new ObjectNotFoundException("Location", id, message);
        });

        if (locationDto.getName() != null) {
            location.setName(locationDto.getName());
        }
        if (locationDto.getDescription() != null) {
            location.setDescription(locationDto.getDescription());
        }
        if (locationDto.getLat() != null) {
            location.setLat(locationDto.getLat());
        }
        if (locationDto.getLon() != null) {
            location.setLon(locationDto.getLon());
        }

        return locationMapper.locationToLocationFullDto(locationRepository.save(location));
    }


    @Override
    public Location checkLocation(NewLocationDto locationDto) {
        Location location = locationRepository.findByLatAndLon(locationDto.getLat(), locationDto.getLon());
        if (location != null) {
            return location;
        } else {
            Location newLocation = locationMapper.newLocationDtoToLocation(locationDto);
            return locationRepository.save(newLocation);
        }
    }
}

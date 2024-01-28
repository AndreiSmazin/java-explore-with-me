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

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LocationServiceImpl implements LocationService {
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

        Location location = checkLocation(id);

        if ((locationDto.getName() != null) && (!locationDto.getName().isBlank())) {
            location.setName(locationDto.getName());
        }
        if ((locationDto.getDescription() != null) && (!locationDto.getDescription().isBlank())) {
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
    public Location getOrCreateLocation(NewLocationDto locationDto) {
        Location location = locationRepository.findByLatAndLon(locationDto.getLat(), locationDto.getLon());
        if (location != null) {
            return location;
        } else {
            Location newLocation = locationMapper.newLocationDtoToLocation(locationDto);
            return locationRepository.save(newLocation);
        }
    }

    @Override
    public Location checkLocation(long id) {
        return locationRepository.findById(id).orElseThrow(() -> {
            String message = "No Location entity with id " + id + " exists!";
            throw new ObjectNotFoundException("Location", id, message);
        });
    }

    @Override
    public List<LocationFullDto> findLocations(Float lat, Float lon, Integer radius) {
        if (lat != null && lon != null && radius != null) {
            return locationRepository.findByDistance(lat, lon, (float) radius).stream()
                    .map(locationMapper::locationToLocationFullDto)
                    .collect(Collectors.toList());
        }

        return locationRepository.findAll().stream()
                .map(locationMapper::locationToLocationFullDto)
                .collect(Collectors.toList());
    }
}

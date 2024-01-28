package ru.practicum.ewm.location.mapper;

import org.mapstruct.Mapper;
import ru.practicum.ewm.location.dto.LocationFullDto;
import ru.practicum.ewm.location.dto.LocationShortDto;
import ru.practicum.ewm.location.dto.NewExtendedLocationDto;
import ru.practicum.ewm.location.dto.NewLocationDto;
import ru.practicum.ewm.location.entity.Location;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface LocationMapper {
    Location newLocationDtoToLocation(NewLocationDto locationDto);

    Location newExtendedLocationDtoToLocation(NewExtendedLocationDto locationDto);

    LocationShortDto locationToLocationShortDto(Location location);

    LocationFullDto locationToLocationFullDto(Location location);
}

package ru.practicum.ewm.user.mapper;

import org.mapstruct.Mapper;
import ru.practicum.ewm.user.dto.UserCreateDto;
import ru.practicum.ewm.user.dto.UserResponseDto;
import ru.practicum.ewm.user.entity.User;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface UserMapper {
    User userCreateDtoToUser(UserCreateDto userDto);

    UserResponseDto userToUserResponseDto(User user);
}

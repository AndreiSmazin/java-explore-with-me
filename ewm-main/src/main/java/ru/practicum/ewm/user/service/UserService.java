package ru.practicum.ewm.user.service;

import ru.practicum.ewm.user.dto.UserCreateDto;
import ru.practicum.ewm.user.dto.UserResponseDto;

import java.util.List;

public interface UserService {
    UserResponseDto createNewUser(UserCreateDto userDto);

    void deleteUser(long id);

    List<UserResponseDto> getUsers(int from, int size, Long[] ids);
}

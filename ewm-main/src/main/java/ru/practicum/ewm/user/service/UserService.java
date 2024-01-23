package ru.practicum.ewm.user.service;

import ru.practicum.ewm.event.dto.PaginationParams;
import ru.practicum.ewm.user.dto.NewUserDto;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.entity.User;

import java.util.List;

public interface UserService {
    UserDto createNewUser(NewUserDto userDto);

    void deleteUser(long id);

    List<UserDto> getUsers(PaginationParams paginationParams, Long[] ids);

    User checkUser(long id);
}

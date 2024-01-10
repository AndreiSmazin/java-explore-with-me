package ru.practicum.ewm.user.dto;

import lombok.Data;

@Data
public class UserResponseDto {
    private long id;
    private String email;
    private String name;
}

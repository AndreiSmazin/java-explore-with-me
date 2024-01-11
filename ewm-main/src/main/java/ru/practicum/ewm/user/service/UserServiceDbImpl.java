package ru.practicum.ewm.user.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.exception.ObjectNotFoundException;
import ru.practicum.ewm.user.dto.NewUserDto;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.entity.QUser;
import ru.practicum.ewm.user.entity.User;
import ru.practicum.ewm.user.mapper.UserMapper;
import ru.practicum.ewm.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceDbImpl implements UserService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;

    @Override
    public UserDto createNewUser(NewUserDto userDto) {
        log.debug("+ createNewUser: {}", userDto);

        User user = userMapper.newUserDtoToUser(userDto);

        return userMapper.userToUserDto(userRepository.save(user));
    }

    @Override
    public void deleteUser(long id) {
        log.debug("+ deleteUser: {}", id);

        try {
            userRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new ObjectNotFoundException("User", id, e.getMessage());
        }
    }

    @Override
    public List<UserDto> getUsers(int from, int size, Long[] ids) {
        BooleanExpression predicates = Expressions.asBoolean(true).isTrue();

        if (ids != null) {
            predicates = predicates.and(QUser.user.id.in(ids));
        }

        return userRepository.findAll(predicates, PageRequest.of(from, size)).stream()
                .map(userMapper::userToUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public User checkUser(long id) {
        return userRepository.findById(id).orElseThrow(() -> {
            String message = "No User entity with id " + id + " exists!";
            throw new ObjectNotFoundException("User", id, message);
        });
    }
}

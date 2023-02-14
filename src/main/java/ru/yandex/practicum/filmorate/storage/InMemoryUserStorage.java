package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users = new HashMap<>();
    private Integer id = 0;

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getById(Integer id) {
        validId(id);
        return users.get(id);
    }

    @Override
    public User addUser(User user) {
        log.info("Добавление пользователя");
        if (user.getName() == null || user.getName().isEmpty() || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        int idUser = generateId();
        user.setId(idUser);
        users.put(idUser, user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        log.info("Обновление пользователя");
        validId(user.getId());
        if (user.getName() == null || user.getName().isEmpty() || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        return user;
    }

    private Integer generateId() {
        return ++id;
    }

    private void validId(Integer id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException(String.format("Пользователь с id = %s не найден", id));
        }
    }
}

package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    //private final List<User> users = new ArrayList<>();
    private final Map<Integer, User> users = new HashMap<>();
    private Integer id = 0;

    private Integer generateId() {
        id++;
        return id;
    }

    @GetMapping
    public List<User> getUsers() {
        return  new ArrayList<>(users.values());
    }

    @PostMapping
    public User addUser(@Valid @RequestBody User user){
        log.info("Добавление пользователя");
        if (user.getName() == null || user.getName().isEmpty() || user.getName().isBlank()){
            user.setName(user.getLogin());
        }
        int idUser = generateId();
        user.setId(idUser);
        users.put(idUser,user);
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user){
        log.info("Обновление пользователя");
        if (!users.containsKey(user.getId())) {
            throw new ValidationException("Такого пользователя не существует");
        }
        if (user.getName() == null || user.getName().isEmpty() || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(),user);
        return user;
    }



}

package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> getUsers() {
        log.info("Получаем всех пользователей");
        return userService.getUsers();
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable int id) {
        log.info("Получение пользователя");
        return userService.getUserById(id);
    }

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        log.info("Добавление пользователя");
        return userService.addUser(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        log.info("Обновление пользователя");
        return userService.updateUser(user);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public User putUserFriend(@PathVariable Integer id, @PathVariable Integer friendId) {
        log.info("Добавление друга");
        userService.addFriend(id, friendId);
        return userService.getUsers().get(id);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteUserFriend(@PathVariable Integer id, @PathVariable Integer friendId) {
        log.info("Удаление из друзей.");
        userService.deleteFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getUserFriends(@PathVariable Integer id) {
        log.info("Получение списка друзей.");
        return userService.getUserFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonUsersFriends(@PathVariable Integer id, @PathVariable Integer otherId) {
        log.info("Получение списка общих друзей.");
        return userService.getCommonFriend(id, otherId);
    }

}

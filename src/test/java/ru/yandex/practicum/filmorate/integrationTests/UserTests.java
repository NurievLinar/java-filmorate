package ru.yandex.practicum.filmorate.integrationTests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.FilmorateApplication;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = FilmorateApplication.class)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserTests {

    final UserDbStorage userStorage;

    @BeforeEach
    void createdUserForDB() {
        if (userStorage.getUsers().size() != 2) {
            User firstTestUser = new User("testUserOne@yandex.ru", "UserOne", "Tester", LocalDate.parse("2000-01-01"));
            userStorage.addUser(firstTestUser);
            User SecondTestUser = new User("testUserTwo@yandex.ru", "UserTwo", "Toster", LocalDate.parse("2000-02-01"));
            userStorage.addUser(SecondTestUser);
        }
        userStorage.deleteFriends(1, 2);
    }

    @Test
    void testCreatedUser() {
        checkFindUserById(1);
        checkFindUserById(2);
    }

    @Test
    void testFindAll() {
        List<User> currentList = userStorage.getUsers();
        assertEquals(2, currentList.size(), "Не корректное количество пользователей");
    }

    @Test
    void testUpgradeUser() {
        User updateUser = new User("updateUser@yandex.ru",
                "updateUser",
                "UpdateName",
                LocalDate.parse("2000-10-10"));
        updateUser.setId(1);
        userStorage.updateUser(updateUser);

        User userStorageUser = userStorage.getById(1);

        assertEquals(updateUser.getId(),userStorageUser.getId());
        assertEquals(updateUser.getName(),userStorageUser.getName());
        assertEquals(updateUser.getEmail(),userStorageUser.getEmail());
        assertEquals(updateUser.getLogin(),userStorageUser.getLogin());
        assertEquals(updateUser.getBirthday(),userStorageUser.getBirthday());

    }

    @Test
    void testFindUserById() {
        checkFindUserById(1);
    }

    @Test
    void testAddRequestsFriendship() {
        assertTrue(userStorage.addRequestsFriendship(1, 2), "Запрос на дружбу не отправлен");
        assertFalse(userStorage.addRequestsFriendship(1, 2), "Запрос на дружбу не должен быть отправлен");
    }

    @Test
    void testDeleteFriends() {
        userStorage.addRequestsFriendship(1, 2);
        assertTrue(userStorage.deleteFriends(1, 2), "Запрос на дружбу не удален");
        assertFalse(userStorage.deleteFriends(1, 2), "Запрос на дружбу не должен быть удален");
    }

    @Test
    void testFindAllFriends() {
        userStorage.addRequestsFriendship(1, 2);
        List<Integer> listFriendIdOne = userStorage.findAllFriends(1);
        assertEquals(1, listFriendIdOne.size(), "В списке друзей должен быть 1 друг");
        assertEquals(2, (int) listFriendIdOne.get(0), "Значение ID друга должно равнятся 2");

        List<Integer> listFriendIdTwo = userStorage.findAllFriends(2);
        assertEquals(0, listFriendIdTwo.size(), "В списке друзей НЕ должено быть друзей");

    }

    void checkFindUserById(Integer idUser) {
        User userStorageById = userStorage.getById(idUser);

        assertEquals(userStorageById.getId(),idUser);

    }
}

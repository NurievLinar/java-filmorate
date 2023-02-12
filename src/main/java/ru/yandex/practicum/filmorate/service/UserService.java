package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<User> getUsers() {
        return userStorage.getUsers();
    }

    public User getUserById(Integer id) {
        return userStorage.getById(id);
    }

    public User addUser(User user) {
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public void addFriend(Integer idUser, Integer idFriend) {
        userStorage.getById(idUser).addFriend(idFriend);
        userStorage.getById(idFriend).addFriend(idUser);
    }

    public void deleteFriend(Integer idUser, Integer idFriend) {
        userStorage.getById(idUser).deleteFriend(idFriend);
        userStorage.getById(idFriend).deleteFriend(idUser);
    }

    public List<User> getUserFriends(Integer idUser) {
        List<User> friends = new ArrayList<>();
        for (Integer friendId : userStorage.getById(idUser).getFriends()) {
            friends.add(userStorage.getById(friendId));
        }
        return friends;
    }

    public List<User> getCommonFriend(Integer idUser, Integer idFriend) {
        List<User> commonFriend = new ArrayList<>();
        for (Integer idFriendUser : userStorage.getById(idUser).getFriends()) {
            if (userStorage.getById(idFriend).getFriends().contains(idFriendUser)) {
                commonFriend.add(userStorage.getById(idFriendUser));
            }
        }
        return commonFriend;
    }
}

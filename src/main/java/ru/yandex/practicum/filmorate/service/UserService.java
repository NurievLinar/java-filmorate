package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class UserService {

    UserStorage userDbStorage;

    public List<User> getUsers() {
        return userDbStorage.getUsers();
    }

    public User getUserById(Integer id) {
        return userDbStorage.getById(id);
    }

    public User addUser(User user) {
        user.setId(userDbStorage.addUser(user));
        return user;
    }

    public void update(User user) {
        getUserById(user.getId());
        userDbStorage.updateUser(user);
    }

    public boolean addFriend(Integer idUser, Integer idFriend) {
        getUserById(idUser);
        getUserById(idFriend);
        return userDbStorage.addRequestsFriendship(idUser, idFriend);
    }

    public void deleteFriend(Integer idUser, Integer idFriend) {
        getUserById(idUser);
        getUserById(idFriend);
        if (!userDbStorage.deleteFriends(idUser, idFriend)) {
            throw new NotFoundException("Не удалось удалить пользователя из друзей");
        }
    }

    public List<User> getUserFriends(Integer idUser) {
        getUserById(idUser);
        List<Integer> idFriends = userDbStorage.findAllFriends(idUser);
        List<User> friends = new ArrayList<>();
        for (Integer friendId : idFriends) {
            friends.add(getUserById(friendId));
        }
        return friends;
    }

    public List<User> getCommonFriend(Integer idUser, Integer idFriend) {
        getUserById(idUser);
        getUserById(idFriend);
        List<User> commonFriend = new ArrayList<>();
        Set<Integer> common = new HashSet<>(userDbStorage.findAllFriends(idUser));
        common.retainAll(userDbStorage.findAllFriends(idFriend));
        for (Integer idFriendUser : common) {
            commonFriend.add(getUserById(idFriendUser));
        }
        return commonFriend;
    }
}

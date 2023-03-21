package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

@Component
public interface UserStorage {

    List<User> getUsers();

    User getById(Integer id);

    Integer addUser(User user);

    void updateUser(User user);

    boolean addRequestsFriendship(Integer idUser, Integer idFriend);

    boolean deleteFriends(Integer idUser, Integer idFriend);

    List<Integer> findAllFriends(Integer idUser);

    List<User> getUserFriends(Integer idUser);

    List<User> getCommonFriend(Integer id, Integer otherId);
}

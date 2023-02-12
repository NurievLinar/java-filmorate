package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Past;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {

    private Integer id;
    @NotEmpty
    @Email(message = "Неправильно введен Email")
    private String email;
    @NotEmpty
    private String login;
    private String name;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Past
    private LocalDate birthday;

    Set<Integer> friends = new HashSet<>();

    public User(String email, String login, String name, LocalDate birthday) {
        this.email = email;
        this.login = login;
        if (name == null || name.isEmpty() || name.isBlank()) {
            this.name = login;
        } else {
            this.name = name;
        }
        this.birthday = birthday;
    }

    public void addFriend(Integer idFriend) {
        validFriendId(idFriend);
        friends.add(idFriend);
    }

    public void deleteFriend(Integer idFriend) {
        validFriendId(idFriend);
        friends.remove(idFriend);
    }

    private void validFriendId(Integer id) {
        if (id < 0) {
            throw new NotFoundException("Должен быть положительный ID");
        }
    }
}

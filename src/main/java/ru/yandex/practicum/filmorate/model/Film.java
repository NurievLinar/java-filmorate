package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film {

    private static final int SIZE = 200;

    private Integer id;

    @NotEmpty(message = "Имя не может быть пустым")
    private String name;

    @Size(max = SIZE, message = "Максимальная длина описания " + SIZE + " символов")
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate releaseDate;

    @Positive
    private int duration;

    private Integer rating;

    Set<Integer> usersLikedFilm = new HashSet<>();

    public Film(String name, String description, LocalDate releaseDate, int duration, Integer rating) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        if (rating == null || rating < 0) {
            this.rating = 0;
        } else {
            this.rating = rating;
        }
    }

    public void addLike(Integer idUser) {
        validIdUser(idUser);
        usersLikedFilm.add(idUser);
        rating = rating + usersLikedFilm.size();
    }

    public void deleteLike(Integer idUser) {
        validIdUser(idUser);
        rating = rating - usersLikedFilm.size();
        usersLikedFilm.remove(idUser);
    }

    private void validIdUser(Integer id) {
        if (id < 0) {
            throw new NotFoundException("Неверный Id пользователя");
        }
    }

}

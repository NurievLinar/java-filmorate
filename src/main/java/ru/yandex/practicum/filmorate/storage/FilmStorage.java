package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

@Component
public interface FilmStorage {

    List<Film> getFilms();

    Film getById(Integer id);

    Film addFilm(Film film);

    Film updateFilm(Film film);


}

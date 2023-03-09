package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {

    public static final LocalDate DATE = LocalDate.of(1895, 12, 28);
    private final Map<Integer, Film> films = new HashMap<>();

    private Integer id = 0;

    @Override
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film getById(Integer id) {
        validId(id);
        return films.get(id);
    }

    @Override
    public Film addFilm(Film film) {
        validDate(film);
        log.info("Добавление фильма");
        int idFilm = generateId();
        film.setId(idFilm);
        films.put(idFilm, film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        log.info("Обновление фильма");
        validId(film.getId());
        films.put(film.getId(), film);
        return film;
    }

    private Integer generateId() {
        return ++id;
    }

    private void validDate(Film film) {
        if (film.getReleaseDate().isBefore(DATE)) {
            throw new ValidationException("дата релиза — раньше, чем " + DATE);
        }
    }

    private void validId(Integer id) {
        if (!films.containsKey(id)) {
            throw new NotFoundException(String.format("Фильм с id = %s не найден", id));
        }
    }

}

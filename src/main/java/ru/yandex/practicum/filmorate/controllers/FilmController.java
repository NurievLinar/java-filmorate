package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    public static final LocalDate DATE = LocalDate.of(1895, 12, 28);

    private final Map<Integer, Film> films = new HashMap<>();
    private Integer id = 0;
    @GetMapping
    public List<Film> getFilms (){
        return new ArrayList<>(films.values());
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film){
        if (validDate(film)){
            throw new ValidationException("дата релиза — раньше 28 декабря 1895 года;");
        }
        log.info("Добавление фильма");
        int idFilm = generateId();
        film.setId(idFilm);
        films.put(idFilm, film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film){
        log.info("Обновление фильма");
        if (validId(film)) {
            throw new ValidationException("Такого фильма не существует");
        }
        films.put(film.getId(), film);
        return film;
    }

    private Integer generateId() {
        id++;
        return id;
    }

    private boolean validDate (Film film) {
        return film.getReleaseDate().isBefore(DATE);
    }

    private boolean validId (Film film) {
        return !films.containsKey(film.getId());
    }

}

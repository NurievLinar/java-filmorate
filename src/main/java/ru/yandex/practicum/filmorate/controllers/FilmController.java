package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;


@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public List<Film> getFilms (){
        log.info("Получаем все фильмы.");
        return filmService.getFilms();
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film){
        log.info("Добавление фильма");
        return filmService.addFilm(film);
    }

    @PutMapping
    public ResponseEntity<Film> updateFilm(@Valid @RequestBody @NotNull Film film) {
        log.info("FilmController. updateFilm. Обновление фильма.");
        filmService.updateFilm(film);
        return new ResponseEntity<>(film, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Film> getFilm(@PathVariable int id) {
        log.info("Получение фильма");
        return new ResponseEntity<>(filmService.getById(id), HttpStatus.OK);
    }

    @PutMapping("/{id}/like/{userId}")
    public ResponseEntity addLikeFilm(@PathVariable Integer id, @PathVariable Integer userId) {
        log.info("Ставим лайк фильму.");
        filmService.addLikeFilm(id, userId);
        //return filmService.getById(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public ResponseEntity deleteLikeFilm(@PathVariable Integer id, @PathVariable Integer userId) {
        log.info("Удаляем лайк у фильма.");
        filmService.deleteLikeFilm(id, userId);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/popular")
    public ResponseEntity<List<Film>> getPopularFilms(@RequestParam(required = false, defaultValue = "10") Integer count) {
        log.info("Получение фильма.");
        return new ResponseEntity<>(filmService.getMostPopularMoviesOfLikes(count), HttpStatus.OK);
    }
}

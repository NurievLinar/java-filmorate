package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import javax.validation.Valid;
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
    public Film updateFilm(@Valid @RequestBody Film film){
        log.info("Обновление фильма");
        return filmService.updateFilm(film);
    }

    @GetMapping("/{id}")
    public Film getFilm(@PathVariable int id) {
        log.info("Получение фильма");
        return filmService.getFilmById(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public Film addLikeFilm(@PathVariable Integer id, @PathVariable Integer userId) {
        log.info("Ставим лайк фильму.");
        filmService.addLikeFilm(id, userId);
        return filmService.getFilmById(id);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLikeFilm(@PathVariable Integer id, @PathVariable Integer userId) {
        log.info("Удаляем лайк у фильма.");
        filmService.deleteLikeFilm(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(required = false, defaultValue = "10") Integer count) {
        log.info("Получение фильма.");
        return filmService.getMostPopularMoviesOfLikes(count);
    }














}

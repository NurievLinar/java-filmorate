package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.ArrayList;
import java.util.List;

@Service
public class FilmService {

    final FilmStorage filmDbStorage;
    final MpaService mpaService;
    final GenreService genreService;

    @Autowired
    public FilmService(FilmStorage filmDbStorage, MpaService mpaService, GenreService genreService) {
        this.filmDbStorage = filmDbStorage;
        this.mpaService = mpaService;
        this.genreService = genreService;
    }

    public List<Film> getFilms() {
        var result = filmDbStorage.getFilms();
        for (Film film : result) {
            film.setGenres(genreService.getGenresId(film.getId()));
        }
        return result;
    }

    public Film getById(Integer id) {
        if (id < 9900) {
            var result = filmDbStorage.getById(id).orElseThrow(() -> new NotFoundException(String.format("Фильм с id = %s не найден", id)));
            result.setGenres(genreService.getGenresId(result.getId()));
            return result;
        } else {
            throw new NotFoundException("Пользователь не найден.");
        }
    }

    public Film addFilm(Film film) {
        film.setId(filmDbStorage.addFilm(film));
        film.setMpa(mpaService.getById(film.getMpa().getId()));
        List<Genre> actualGenreFilm = new ArrayList<>();
        for (Genre genre : film.getGenres()) {
            actualGenreFilm.add(genreService.getById(genre.getId()));
            if (!filmDbStorage.setGenre(film.getId(), genre.getId())) {
                throw new NotFoundException("Не удалось установить жанр для фильма");
            }
        }
        film.setGenres(actualGenreFilm);
        return film;
    }

    public void updateFilm(Film film) {
        getById(film.getId());
        filmDbStorage.updateFilm(film);
        film.setMpa(mpaService.getById(film.getMpa().getId()));
        List<Genre> actualGenreFilm = new ArrayList<>();
        for (Genre genre : film.getGenres()) {
            if (!actualGenreFilm.contains(genreService.getById(genre.getId()))) {
                actualGenreFilm.add(genreService.getById(genre.getId()));
            }
            if (!filmDbStorage.setGenre(film.getId(), genre.getId())) {
                throw new NotFoundException("Не удалось установить жанр для фильма");
            }
        }

        List<Genre> currentGenreFilm = genreService.getGenresId(film.getId());
        for (Genre current : currentGenreFilm) {
            if (!actualGenreFilm.contains(current)) {
                filmDbStorage.deleteGenre(film.getId(), current.getId());
            }
        }
        film.setGenres(actualGenreFilm);
    }

    public void addLikeFilm(Integer filmId, Integer userId) {
        existsUser(userId);
        if (!filmDbStorage.addLike(filmId, userId)) {
            throw new NotFoundException("Не удалось поставить лайк");
        }
    }

    public void deleteLikeFilm(Integer idFilm, Integer userId) {
        existsUser(userId);
        if (!filmDbStorage.deleteLike(idFilm, userId)) {
            throw new NotFoundException("Не корректный запрос на удаление лайка");
        }
    }

    public List<Film> getMostPopularMoviesOfLikes(Integer count) {
        return filmDbStorage.mostPopulars(count);
    }

    private void existsUser(Integer userId) {
        if (userId < 1) {
            throw new NotFoundException("Id пользователя должно быть больше 1.");
        }
    }
}

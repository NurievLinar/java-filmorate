package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.List;

@Service
@AllArgsConstructor
public class FilmService {

    final FilmStorage filmDbStorage;
    final MpaService mpaService;
    final GenreService genreService;


    public List<Film> getFilms() {
        var result = filmDbStorage.getFilms();
        for (Film film : result) {
            film.setGenres(genreService.getGenresByFilmId(film.getId()));
        }
        return result;
    }

    public Film getFilmById(Integer id) {
        Film film = filmDbStorage.getFilmById(id);
        film.setGenres(genreService.getGenresByFilmId(film.getId()));
        return film;
    }

    public Film addFilm(Film film) {
        film.setId(filmDbStorage.addFilm(film));
        film.setMpa(mpaService.getById(film.getMpa().getId()));
        if (film.getGenres() == null || film.getGenres().isEmpty()) {
            return film;
        }
        filmDbStorage.insertFilmGenre(film);
        return film;
    }

    public void updateFilm(Film film) {
        getFilmById(film.getId());
        filmDbStorage.updateFilm(film);
        film.setMpa(mpaService.getById(film.getMpa().getId()));
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

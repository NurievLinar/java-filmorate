package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {

    FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public List<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public Film getFilmById(Integer id) {
        return filmStorage.getById(id);
    }

    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public void addLikeFilm(Integer filmId, Integer userId) {
        if (!filmStorage.getById(filmId).getUsersLikedFilm().contains(userId)) {
            filmStorage.getById(filmId).addLike(userId);
        }
    }

    public void deleteLikeFilm(Integer filmId, Integer userId) {
        filmStorage.getById(filmId).deleteLike(userId);
    }

    public List<Film> getMostPopularMoviesOfLikes(Integer count) {
        Comparator<Film> filmComparator = (film1, film2) -> {
            if (film2.getRating().compareTo(film1.getRating()) == 0) {
                return film1.getName().compareTo(film2.getName());
            }
            return film2.getRating().compareTo(film1.getRating());
        };
        return filmStorage.getFilms().stream()
                .sorted(filmComparator)
                .limit(count)
                .collect(Collectors.toList());
    }


}

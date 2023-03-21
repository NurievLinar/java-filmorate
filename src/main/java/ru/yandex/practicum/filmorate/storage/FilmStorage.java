package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

@Component
public interface FilmStorage {

    List<Film> getFilms();

    Film getFilmById(Integer id);

    Integer addFilm(Film film);

    Film updateFilm(Film film);

    boolean setGenre(Integer idFilm, Integer idGenre);

    boolean deleteGenre(Integer idFilm, Integer idGenre);

    boolean addLike(Integer idFilm, Integer idUser);

    List<Film> mostPopulars(Integer limit);

    boolean deleteLike(Integer idFilm, Integer idUser);

    Film insertFilmGenre(Film film);

}

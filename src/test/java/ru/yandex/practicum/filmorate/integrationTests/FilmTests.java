package ru.yandex.practicum.filmorate.integrationTests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.FilmorateApplication;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = FilmorateApplication.class)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmTests {

    final FilmDbStorage filmDbStorage;
    final GenreDbStorage genreDbStorage;
    final UserDbStorage userStorage;

    @BeforeEach
    void createdFilmForDB() {
        if (filmDbStorage.getFilms().size() != 2) {
            List<Genre> genres = new ArrayList<>();
            genres.add(new Genre(2, genreDbStorage.getById(2)));
            Film film = new Film("Достучатся до небес", "Немецкий кинофильм 1997 года режиссёра Томаса Яна", LocalDate.parse("1997-02-20"),
                    87, 4, new Mpa(1, "G"), genres);

            filmDbStorage.addFilm(film);
            filmDbStorage.setGenre(1, 2);

            Film filmNext = new Film("Тестовая драмма", "Тестовый фильм", LocalDate.parse("2022-01-01"),
                    75, 0, new Mpa(2, "PG"), genres);

            filmDbStorage.addFilm(filmNext);
            filmDbStorage.setGenre(2, 2);
        }

        if (userStorage.getUsers().size() != 2) {
            User firstTestUser = new User("testUserOne@yandex.ru", "UserOne", "Tester", LocalDate.parse("2000-01-01"));
            userStorage.addUser(firstTestUser);

            User SecondTestUser = new User("testUserTwo@yandex.ru", "UserTwo", "Toster", LocalDate.parse("2000-02-01"));
            userStorage.addUser(SecondTestUser);
        }
    }

    @Test
    void testAddFilm() {
        checkFindFilmById(1);
        checkFindFilmById(2);
    }

    @Test
    void testUpgradeFilm() {
        List<Genre> genres = new ArrayList<>();
        genres.add(new Genre(2, genreDbStorage.getById(2)));
        Film updateFilm = new Film("Достучатся до небес", "updateTest", LocalDate.parse("1997-02-20"), 87, 4, new Mpa(1, "G"), genres);
        updateFilm.setId(1);

        filmDbStorage.updateFilm(updateFilm);

        Film filmDbStorageFilm = filmDbStorage.getById(1);

        assertEquals(updateFilm.getId(),filmDbStorageFilm.getId());
        assertEquals(updateFilm.getName(),filmDbStorageFilm.getName());
        assertEquals(updateFilm.getDescription(),filmDbStorageFilm.getDescription());
        assertEquals(updateFilm.getReleaseDate(),filmDbStorageFilm.getReleaseDate());
        assertEquals(updateFilm.getMpa(),filmDbStorageFilm.getMpa());
    }

    @Test
    void testFindFilm() {
        checkFindFilmById(1);
    }

    @Test
    void testFindAll() {
        List<Film> current = filmDbStorage.getFilms();
        Assertions.assertEquals(2, current.size(), "Не корректное количество фильмов");
    }

    @Test
    void testAddLikeFilm() {
        assertTrue(filmDbStorage.addLike(1, 1), "пользователь не лайкнул фильм");
        filmDbStorage.deleteLike(1, 1);
    }

    @Test
    void testDeleteLike() {
        filmDbStorage.addLike(1, 1);
        assertTrue(filmDbStorage.deleteLike(1, 1), "Лайк не удален");
    }

    void checkFindFilmById(Integer idFilm) {
        Film filmOptional = filmDbStorage.getById(idFilm);

        assertEquals(filmOptional.getId(),idFilm);

    }
}
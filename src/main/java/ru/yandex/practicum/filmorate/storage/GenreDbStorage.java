package ru.yandex.practicum.filmorate.storage;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public String getById(Integer id) {
        log.info("GenreDbStorage. findById.");
        String sqlQuery = String.format("SELECT GENRE_NAME " +
                "FROM GENRE WHERE GENRE_ID = %d", id);
        List<String> names = jdbcTemplate.queryForList(sqlQuery, String.class);
        if (names.size() != 1) {
            throw new NotFoundException("Не корректный ID GENRE");
        }
        return names.get(0);
    }

    @Override
    public List<Genre> getGenresByFilmId(Integer idFilm) {
        log.info("FilmDbStorage. getGenresByFilmId");
        String sqlQuery = String.format("SELECT GENRE.GENRE_ID,\n" +
                "GENRE.GENRE_NAME\n" +
                "FROM FILM_TO_GENRE JOIN GENRE ON GENRE.GENRE_ID = FILM_TO_GENRE.GENRE_ID\n" +
                "WHERE FILM_ID = %d", idFilm);
        return jdbcTemplate.query(sqlQuery, this::mapRowToGenre);
    }

    @Override
    public List<Genre> getAll() {
        log.info("GenreDbStorage. findAll.");
        String sqlQuery = "SELECT GENRE_ID, GENRE_NAME FROM GENRE";
        return jdbcTemplate.query(sqlQuery, this::mapRowToGenre);
    }

    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        log.info("GenreDbStorage. mapRowToGenre.");
        return new Genre(resultSet.getInt("GENRE_ID")
                , resultSet.getString("GENRE_NAME"));
    }
}
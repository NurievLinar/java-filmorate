package ru.yandex.practicum.filmorate.storage;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@AllArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Integer addFilm(Film film) {
        log.info("FilmDbStorage. add: {}", film);
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("FILMS")
                .usingGeneratedKeyColumns("FILM_ID");
        return simpleJdbcInsert.executeAndReturnKey(film.toMap()).intValue();
    }

    @Override
    public Film updateFilm(Film film) {
        log.info("FilmDbStorage. update: {}", film);
        String sqlQuery = "UPDATE FILMS SET " +
                "FILM_NAME = ?, MPA_ID = ?, FILM_DESCRIPTION = ? , FILM_RELEASE_DATE = ?, FILM_DURATION = ?, FILM_RATE = ?" +
                "WHERE FILM_ID = ?";
        int updatedRows = jdbcTemplate.update(sqlQuery
                , film.getName()
                , film.getMpa().getId()
                , film.getDescription()
                , film.getReleaseDate()
                , film.getDuration()
                , film.getRating()
                , film.getId());
        if (updatedRows == 0) {
            throw new NotFoundException(
                    String.format("Фильм с идентификатором %d не найден.", film.getId()));
        } else {
            deleteByFilmId(film.getId());
            if (film.getGenres() == null || film.getGenres().isEmpty()) {
                return film;
            }
            return insertFilmGenre(film);
        }
    }

    @Override
    public Film getFilmById(Integer id) {
        log.info("FilmDbStorage. findById id: {}", id);
        String sqlQuery = "SELECT FILMS.FILM_ID, FILMS.FILM_NAME, FILMS.FILM_DESCRIPTION," +
                "FILMS.FILM_RELEASE_DATE, FILMS.FILM_DURATION, FILMS.FILM_RATE, FILMS.FILM_RATE_AND_LIKES, " +
                "FILMS.MPA_ID, MPA.MPA_NAME " +
                "                FROM FILMS JOIN MPA ON FILMS.MPA_ID = MPA.MPA_ID WHERE FILMS.FILM_ID = ? ";
        try {
            return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToFilm, id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException(String.format("Фильм с идентификатором %d не найден.", id));
        }
    }

    @Override
    public List<Film> getFilms() {
        log.info("FilmDbStorage. findAll.");
        String sqlQuery = "SELECT FILMS.FILM_ID,FILMS.FILM_NAME ,FILMS.MPA_ID ,FILMS.FILM_DESCRIPTION ,FILMS.FILM_RELEASE_DATE ,FILMS.FILM_DURATION , " +
                "       FILMS.FILM_RATE,  FILMS.FILM_RATE_AND_LIKES, MPA.MPA_ID, MPA.MPA_NAME " +
                " FROM FILMS JOIN MPA ON FILMS.MPA_ID = MPA.MPA_ID ";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
    }

    @Override
    public boolean setGenre(Integer idFilm, Integer idGenre) {
        log.info("FilmDbStorage. setGenre. idFilm:{}, idGenre:{} ", idFilm, idGenre);
        if (!findGenreToFilm(idFilm, idGenre)) {
            String sqlQuery = String.format("INSERT INTO FILM_TO_GENRE VALUES (%d, %d)", idFilm, idGenre);
            return jdbcTemplate.update(sqlQuery) == 1;
        }
        return true;
    }

    @Override
    public boolean deleteGenre(Integer idFilm, Integer idGenre) {
        log.info("FilmDbStorage. deleteGenre.idFilm:{}, idGenre:{} ", idFilm, idGenre);
        if (findGenreToFilm(idFilm, idGenre)) {
            String sqlQuery = "DELETE FROM FILM_TO_GENRE WHERE FILM_ID = ? AND GENRE_ID = ?";
            return jdbcTemplate.update(sqlQuery, idFilm, idGenre) > 0;
        }
        return false;
    }

    @Override
    public boolean addLike(Integer idFilm, Integer idUser) {
        log.info("FilmDbStorage. addLike.idFilm:{}, idUser:{} ", idFilm, idUser);
        if (!findLikeUserToFilm(idFilm, idUser)) {
            String sqlQuery = String.format("INSERT INTO USER_LIKE_FILM VALUES (%d, %d)", idFilm, idUser);
            return jdbcTemplate.update(sqlQuery) == 1;
        }
        return false;
    }

    @Override
    public List<Film> mostPopulars(Integer limit) {
        String query = " SELECT FILMS.FILM_ID, FILMS.FILM_NAME, FILMS.MPA_ID, FILMS.FILM_DESCRIPTION, FILMS.FILM_RELEASE_DATE, FILMS.FILM_DURATION, " +
                " FILMS.FILM_RATE,  FILMS.FILM_RATE_AND_LIKES, MPA.MPA_ID, MPA.MPA_NAME " +
                " FROM FILMS JOIN MPA ON FILMS.MPA_ID = MPA.MPA_ID " +
                " LEFT JOIN USER_LIKE_FILM AS UL on FILMS.FILM_ID = UL.FILM_ID GROUP BY FILMS.FILM_ID, UL.FILM_ID IN " +
                "(SELECT FILM_ID FROM USER_LIKE_FILM) ORDER BY COUNT(UL.FILM_ID) DESC LIMIT ?";
        return jdbcTemplate.query(query, this::mapRowToFilm, limit);
    }

    @Override
    public boolean deleteLike(Integer idFilm, Integer idUser) {
        if (findLikeUserToFilm(idFilm, idUser)) {
            String sqlQuery = "DELETE FROM USER_LIKE_FILM WHERE FILM_ID = ? AND USER_ID = ?";
            return jdbcTemplate.update(sqlQuery, idFilm, idUser) > 0;
        }
        return false;
    }

    private boolean findGenreToFilm(Integer idFilm, Integer idGenre) {
        String sqlQuery = String.format("SELECT COUNT(*)\n" +
                "FROM FILM_TO_GENRE\n" +
                "WHERE FILM_ID = %d AND GENRE_ID = %d", idFilm, idGenre);

        return jdbcTemplate.queryForObject(sqlQuery, Integer.class) == 1;
    }

    private boolean findLikeUserToFilm(Integer idFilm, Integer idUser) {
        String sqlQuery = String.format("SELECT COUNT(*)\n" +
                "FROM USER_LIKE_FILM\n" +
                "WHERE FILM_ID = %d AND USER_ID = %d", idFilm, idUser);

        return jdbcTemplate.queryForObject(sqlQuery, Integer.class) == 1;
    }

    private Integer getRateAndLikeFilm(Integer idFilm) {
        String sqlQuery = String.format("SELECT FILM_RATE_AND_LIKES\n" +
                "FROM FILMS\n" +
                "WHERE FILM_ID = %d", idFilm);
        List<Integer> countRateAndLike = jdbcTemplate.queryForList(sqlQuery, Integer.class);
        if (countRateAndLike.size() > 0) {
            return countRateAndLike.get(0);
        } else {
            return 0;
        }
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = new Film(resultSet.getString("FILM_NAME")
                , resultSet.getString("FILM_DESCRIPTION")
                , resultSet.getDate("FILM_RELEASE_DATE").toLocalDate()
                , resultSet.getInt("FILM_DURATION")
                , resultSet.getInt("FILM_RATE")
                , new Mpa(resultSet.getInt("MPA_ID"), resultSet.getString("MPA_NAME"))
                , new ArrayList<>());
        film.setId(resultSet.getInt("FILM_ID"));
        film.setRateAndLikes(getRateAndLikeFilm(film.getId()));
        film.setGenres(getGenresByFilmId(film.getId()));
        System.out.println(film);
        return film;
    }

    private List<Genre> getGenresByFilmId(Integer idFilm) {
        String sqlQuery = String.format("SELECT GENRE.GENRE_ID,\n" +
                "GENRE.GENRE_NAME\n" +
                "FROM FILM_TO_GENRE JOIN GENRE ON GENRE.GENRE_ID = FILM_TO_GENRE.GENRE_ID\n" +
                "WHERE FILM_ID = %d", idFilm);
        return jdbcTemplate.query(sqlQuery, this::mapRowToGenre);
    }

    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        log.info("GenreDbStorage. mapRowToGenre.");
        return new Genre(resultSet.getInt("GENRE_ID")
                , resultSet.getString("GENRE_NAME"));
    }

    public Film insertFilmGenre(Film film) {
        String sql = "INSERT INTO FILM_TO_GENRE(FILM_ID,GENRE_ID)  " +
                "VALUES(?,?)";
        List<Genre> uniqGenres = film.getGenres().stream().distinct().collect(Collectors.toList());
        jdbcTemplate.batchUpdate(sql,
                new BatchPreparedStatementSetter() {
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setLong(1, film.getId());
                        ps.setLong(2, uniqGenres.get(i).getId());
                    }

                    public int getBatchSize() {
                        return uniqGenres.size();
                    }
                });
        film.setGenres(uniqGenres);
        return film;
    }

    public void deleteByFilmId(Integer filmId) {

        String sql = "DELETE FROM FILM_TO_GENRE " +
                "WHERE FILM_ID = ?";
        jdbcTemplate.update(sql, filmId);
    }

}

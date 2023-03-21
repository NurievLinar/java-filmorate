package ru.yandex.practicum.filmorate.storage;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
public class MpaDbStorage implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public String getById(Integer id) {
        log.info("MpaDbStorage. findById.");
        String sqlQuery = String.format("SELECT MPA_NAME " +
                "FROM MPA WHERE MPA_ID = %d", id);
        List<String> names = jdbcTemplate.queryForList(sqlQuery, String.class);
        if (names.size() != 1) {
            throw new NotFoundException("Не корректный ID MPA");
        }
        return names.get(0);
    }

    @Override
    public List<Mpa> getAll() {
        log.info("MpaDbStorage. findAll.");
        String sqlQuery = "SELECT MPA_ID, MPA_NAME FROM MPA";
        return jdbcTemplate.query(sqlQuery, this::mapRowToMpa);
    }

    private Mpa mapRowToMpa(ResultSet resultSet, int rowNum) throws SQLException {
        log.info("MpaDbStorage. mapRowToMpa.");
        return new Mpa(resultSet.getInt("MPA_ID")
                , resultSet.getString("MPA_NAME"));
    }
}
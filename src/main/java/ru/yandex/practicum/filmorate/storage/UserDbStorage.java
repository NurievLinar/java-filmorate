package ru.yandex.practicum.filmorate.storage;

import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

@Component
@AllArgsConstructor
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Integer addUser(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("USERS")
                .usingGeneratedKeyColumns("USER_ID");

        return simpleJdbcInsert.executeAndReturnKey(user.toMap()).intValue();
    }

    @Override
    public void updateUser(User user) {
        String sqlQuery = "UPDATE USERS SET " +
                "USER_EMAIL = ?, USER_LOGIN = ?, USER_NAME = ? , USER_BIRTHDAY = ?" +
                "WHERE USER_ID = ?";
        jdbcTemplate.update(sqlQuery
                , user.getEmail()
                , user.getLogin()
                , user.getName()
                , user.getBirthday()
                , user.getId());
    }

    @Override
    public User getById(Integer id) {
        String sqlQuery = "SELECT USER_ID, USER_EMAIL, USER_LOGIN, USER_NAME, USER_BIRTHDAY " +
                "FROM USERS WHERE USER_ID = ?";
        try {
            return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToUser, id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException(String.format("Пользователь с идентификатором %d не найден.", id));
        }
    }

    @Override
    public List<User> getUsers() {
        String sqlQuery = "SELECT USER_ID, USER_EMAIL, USER_LOGIN, USER_NAME, USER_BIRTHDAY FROM USERS";

        return jdbcTemplate.query(sqlQuery, this::mapRowToUser);
    }

    @Override
    public boolean addRequestsFriendship(Integer sender, Integer recipient) {
        if (!findRequestsFriendship(sender, recipient)) {
            HashMap<String, Integer> map = new HashMap<>();
            map.put("SENDER_ID", sender);
            map.put("RECIPIENT_ID", recipient);
            SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                    .withTableName("FRIENDSHIP_REQUESTS")
                    .usingColumns("SENDER_ID", "RECIPIENT_ID");
            return simpleJdbcInsert.execute(map) == 1;
        }

        return false;
    }

    @Override
    public List<Integer> findAllFriends(Integer idUser) {
        String sqlQuery = String.format("SELECT RECIPIENT_ID AS friends\n" +
                "FROM FRIENDSHIP_REQUESTS\n" +
                "WHERE SENDER_ID = %d", idUser, idUser);

        return jdbcTemplate.queryForList(sqlQuery, Integer.class);
    }

    @Override
    public boolean deleteFriends(Integer idUser, Integer idFriend) {
        String sqlQuery = String.format("DELETE\n" +
                "FROM FRIENDSHIP_REQUESTS\n" +
                "WHERE SENDER_ID = %d AND RECIPIENT_ID = %d", idUser, idFriend);

        return jdbcTemplate.update(sqlQuery) > 0;
    }

    private boolean findRequestsFriendship(Integer firstId, Integer secondId) {
        String sqlQuery = String.format("SELECT COUNT(*)\n" +
                "FROM FRIENDSHIP_REQUESTS\n" +
                "WHERE (SENDER_ID = %d OR RECIPIENT_ID = %d)" +
                " AND (SENDER_ID = %d OR RECIPIENT_ID = %d)", firstId, firstId, secondId, secondId);

        return jdbcTemplate.queryForObject(sqlQuery, Integer.class) == 1;
    }

    @Override
    public List<User> getUserFriends(Integer idUser) {
        final String q = "SELECT u.* FROM FRIENDSHIP_REQUESTS f INNER JOIN USERS u ON f.RECIPIENT_ID = u.USER_ID WHERE f.SENDER_ID = ?";
        return jdbcTemplate.query(q, this::mapRowToUser, idUser);
    }

    @Override
    public List<User> getCommonFriend(Integer id, Integer otherId) {
        final String q = "SELECT u.* FROM (" +
                "SELECT RECIPIENT_ID FROM FRIENDSHIP_REQUESTS WHERE SENDER_ID = ? INTERSECT " +
                "SELECT RECIPIENT_ID FROM FRIENDSHIP_REQUESTS WHERE SENDER_ID = ?" +
                ") f INNER JOIN USERS u ON f.RECIPIENT_ID = u.USER_ID";
        return jdbcTemplate.query(q, this::mapRowToUser, id, otherId);
    }

    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        User user = new User(resultSet.getString("USER_EMAIL")
                , resultSet.getString("USER_LOGIN")
                , resultSet.getString("USER_NAME")
                , resultSet.getDate("USER_BIRTHDAY").toLocalDate());
        user.setId(resultSet.getInt("USER_ID"));

        return user;
    }


}
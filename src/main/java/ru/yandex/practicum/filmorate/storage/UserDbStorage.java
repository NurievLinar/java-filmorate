package ru.yandex.practicum.filmorate.storage;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

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
    public Optional<User> getById(Integer id) {
        String sqlQuery = "SELECT USER_ID, USER_EMAIL, USER_LOGIN, USER_NAME, USER_BIRTHDAY " +
                "FROM USERS WHERE USER_ID = ?";

        return Optional.ofNullable(jdbcTemplate.queryForObject(sqlQuery, this::mapRowToUser, id));
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

    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        User user = new User(resultSet.getString("USER_EMAIL")
                , resultSet.getString("USER_LOGIN")
                , resultSet.getString("USER_NAME")
                , resultSet.getDate("USER_BIRTHDAY").toLocalDate());
        user.setId(resultSet.getInt("USER_ID"));

        return user;
    }
}
package ru.yandex.practicum.filmorate.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


@Component
@Primary
public class UserDbStorage implements UserStorage {
    private static final Logger log = LoggerFactory.getLogger(UserDbStorage.class);
    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;

    }

    @Override
    public User createUser(User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "insert into users (name, email, login, birthday) values (?,?,?,?)", new String[]{"id"});
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getLogin());
            ps.setString(4, user.getBirthday());
            return ps;
        }, keyHolder);
        Long id = keyHolder.getKey().longValue();
        return getUserById(id);
    }

    @Override
    public User update(User user) {
        containsUser(user.getId());
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "update users set name=?,email=?,login=?,birthday=? where id=?");
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getLogin());
            ps.setString(4, user.getBirthday());
            ps.setLong(5, user.getId());
            return ps;
        });
        return user;
    }

    @Override
    public List<User> getFilms() {
        return null;
    }

    @Override
    public List<User> getUsers() {
        return jdbcTemplate.query("select * from users", new RowMapper<User>() {
            @Override
            public User mapRow(ResultSet rs, int rowNum) throws SQLException {
                User user = new User();
                user.setId(rs.getLong("id"));
                user.setName(rs.getString("name"));
                user.setLogin(rs.getString("login"));
                user.setEmail(rs.getString("email"));
                user.setBirthday(rs.getString("birthday"));
                return user;
            }
        });
    }

    @Override
    public User getUserById(long userId) {
        User user = new User();
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from users where id = ?", userId);
        if (userRows.next()) {
            user.setId(userRows.getLong("id"));
            user.setName(userRows.getString("name"));
            user.setEmail(userRows.getString("email"));
            user.setLogin(userRows.getString("login"));
            user.setBirthday(userRows.getString("birthday"));
        }
        user.setFriendIds(new HashSet<>(getUserFriends(userId)));
        return user;
    }

    @Override
    public Boolean containsUser(long userId) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from users where id = ?", userId);
        if (userRows.next()) {
            log.info("Найден пользователь: {} {}", userRows.getString("id"), userRows.getString("name"));
        } else {
            log.info("Пользователь с идентификатором {} не найден.", userId);
            return false;
        }
        return true;
    }

    public void requestFriendship(long userId, long friendId) {
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "insert into friendship (friend_id, receiver_friend_id, status) values (?,?,?)");
            ps.setLong(1, userId);
            ps.setLong(2, friendId);
            ps.setBoolean(3, false);
            return ps;
        });
    }

    public void confirmFriendship(long receiverId, long friendId) {
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "insert into friendship (friend_id, receiver_friend_id, status) values (?,?,?)");
            ps.setLong(1, receiverId);
            ps.setLong(2, friendId);
            ps.setBoolean(3, true);
            return ps;
        });
    }

    public void removeUserFromFriends(long userId, long friendId) {
        String sql = "delete from friendship where FRIEND_ID=? and RECEIVER_FRIEND_ID=?";
        jdbcTemplate.update(sql, userId, friendId);
    }

    public List<Long> getUserFriends(long userId) {
        List<Long> friends = new ArrayList<>();
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select distinct id from users\n" +
                "                where id in(select receiver_friend_id  from friendship where\n" +
                "                 friend_id = ?)", userId);
        while (userRows.next()) {
            Long friendId = userRows.getLong("id");
            if (friendId != userId) {
                friends.add(friendId);
            }
        }
        return friends;
    }
}

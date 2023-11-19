package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.sql.SQLException;
import java.util.List;

public interface UserStorage {
    User createUser(User user) throws SQLException;

    User update(User user);

    List<User> getFilms();

    List<User> getUsers();

    User getUserById(long userId);

    Boolean containsUser(long userId);

    void requestFriendship(long userId, long friendId);

    List<Long> getUserFriends(long userId);

    void removeUserFromFriends(long userId, long friendId);
}

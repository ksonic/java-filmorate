package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.text.SimpleDateFormat;
import java.util.*;

@RestController
public class UserController {
    private final Map<Integer, User> users;
    protected int sequence = 0;
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    public UserController() {
        this.users = new HashMap<>();
    }

    @GetMapping("/users")
    public List<User> getFilms() {
        log.info("Получен запрос GET /films.");
        return new LinkedList<>(users.values());
    }

    @PostMapping("/users")
    public User addUser(@RequestBody User user) {
        if (!isExists(user)) {
            log.info("Получен запрос POST /users на создание пользователя.");
            createUser(user);
        } else {
            throw new ValidationException("Пользователь с логином " + user.getLogin() + " уже существует.");
        }
        return user;
    }

    @PutMapping("/users")
    public User updateUser(@RequestBody User user) {
        if (isExists(user)) {
            log.info("Получен запрос PUT /users на обновление пользователя.");
            update(user);
        } else {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Пользователя с логином " + user.getLogin() + " не существует."
            );
        }
        return user;
    }

    private User createUser(User user) {
        if (isEmailValid(user) && isLoginValid(user) && isBirthdayValid(user)) {
            useLoginAsName(user);
            user.setId(generateId());
            users.put(user.getId(), user);
        } else {
            throw new ValidationException("Произошла ошибка валидации.");
        }
        return user;
    }

    private void update(User user) {
        users.put(user.getId(), user);
    }

    private int generateId() {
        return ++sequence;
    }

    private boolean isExists(User user) {
        return users.containsKey(user.getId());
    }

    private boolean isEmailValid(User user) {
        return !user.getEmail().isEmpty() && user.getEmail().contains("@");
    }

    private boolean isLoginValid(User user) {
        return !(user.getLogin().isEmpty() && user.getLogin().isBlank());
    }

    private boolean isBirthdayValid(User user) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date docDate = format.parse(user.getBirthday());
            Date now = new Date();
            return docDate.before(now);
        } catch (Exception exception) {
            throw new ValidationException("Произошла ошибка: ", exception);
        }
    }

    private void useLoginAsName(User user) {
        if (user.getName() == null || user.getName().isEmpty()) {
            String login = user.getLogin();
            user.setName(login);
        }
    }
}

package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@RestController
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final Map<Long, User> users;
    protected int sequence = 0;

    public UserController() {
        this.users = new HashMap<>();
    }

    @GetMapping("/users")
    public List<User> getFilms() {
        log.info("GET /films request received.");
        return new LinkedList<>(users.values());
    }

    @PostMapping("/users")
    public User addUser(@Valid @RequestBody User user) {
        log.info("POST /users request received for user creation.");
        createUser(user);
        return user;
    }

    @PutMapping("/users")
    public User updateUser(@Valid @RequestBody User user) {
        log.info("PUT /users request received for user update.");
        update(user);
        return user;
    }

    private User createUser(User user) {
        if (users.containsKey(user.getId())) {
            throw new ValidationException("User with login " + user.getLogin() + " is already exists.");
        }
        if (!isBirthdayValid(user)) {
            throw new ValidationException("Validation error happened.");
        }
        useLoginAsName(user);
        user.setId(generateId());
        users.put(user.getId(), user);
        return user;
    }

    private void update(User user) {
        if (!users.containsKey(user.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "User with login " + user.getLogin() + " isn't exist."
            );
        }
        users.put(user.getId(), user);
    }

    private long generateId() {
        return ++sequence;
    }

    private boolean isBirthdayValid(User user) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date docDate = format.parse(user.getBirthday());
            Date now = new Date();
            return docDate.before(now);
        } catch (Exception exception) {
            throw new ValidationException("Error happened: ", exception);
        }
    }

    private void useLoginAsName(User user) {
        if (user.getName() == null || user.getName().isEmpty()) {
            String login = user.getLogin();
            user.setName(login);
        }
    }
}

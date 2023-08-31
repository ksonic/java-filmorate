package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;


    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public List<User> getUsers() {
        log.info("GET /films request received.");
        return userService.getUsers();
    }

    @PostMapping("/users")
    public User addUser(@Valid @RequestBody User user) {
        log.info("POST /users request received for user creation.");
        return userService.addUser(user);
    }

    @PutMapping("/users")
    public User updateUser(@Valid @RequestBody User user) {
        log.info("PUT /users request received for user update.");
        return userService.updateUser(user);
    }

    @GetMapping("/users/{userId}")
    public User getUserById(@PathVariable long userId) {
        log.info("GET /users/{userId} request received.");
        return userService.getUserById(userId);
    }

    @PutMapping("/users/{id}/friends/{friendId}")
    public void addUserToFriends(@PathVariable @RequestBody long id, @PathVariable long friendId) {
        log.info("PUT /users/{id}/friends/{friendId} request received for user update.");
        userService.addUserToFriends(id, friendId);
    }

    @DeleteMapping("/users/{id}/friends/{friendId}")
    public void deleteUserFromFriends(@PathVariable @RequestBody long id, @PathVariable long friendId) {
        log.info("DELETE /users/{id}/friends/{friendId} request received for delete user from friends.");
        userService.deleteUserFromFriends(id, friendId);
    }

    @GetMapping("/users/{id}/friends")
    public List<User> getUserFriends(@PathVariable long id) {
        log.info("GET /users/{id}/friends request received user friends.");
        return userService.getUserFriends(id);
    }

    @GetMapping("/users/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable long id, @PathVariable long otherId) {
        log.info("GET users/{id}/friends/common/{otherId} request received for getting list of common with other user friends .");
        return userService.getCommonFriends(id, otherId);
    }
}

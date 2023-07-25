package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;

@Data
public class User {
    private long id;

    @Email
    private String email;

    @Pattern(regexp = "\\A(?!\\s*\\Z).+",
            message = "Login should be empty or with spaces")

    private String login;
    private String name;
    private String birthday;
}

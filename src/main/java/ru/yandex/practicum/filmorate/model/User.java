package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import org.springframework.stereotype.Component;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import java.util.HashSet;
import java.util.Set;

@Component
@Data
public class User {
    private long id;

    @Email
    private String email;

    @Pattern(regexp = "\\A(?!\\s*\\Z).+",
            message = "Login shouldn't be empty or with spaces")

    private String login;
    private String name;
    private String birthday;
    private Set<Long> friendIds = new HashSet<>();

    public void addFriendId(long friendId) {
        friendIds.add(friendId);
    }
}

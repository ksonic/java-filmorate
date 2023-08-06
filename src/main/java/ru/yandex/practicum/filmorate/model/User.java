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
    private Set<Long> friends;

    public void setFriend(long friendId) {
        if (friends == null) {
            Set<Long> friendsSet = new HashSet<>();
            friendsSet.add(friendId);
            setFriends(friendsSet);
        }
        friends.add(friendId);
    }

}

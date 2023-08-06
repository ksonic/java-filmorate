package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Component
@Data
public class Film {
    private long id;

    @NotBlank(message = "Name shouldn't be empty")
    private String name;

    @Size(max = 200, message = "Description should be less than 200 symbols")
    private String description;

    private String releaseDate;

    @Positive(message = "Duration should be positive")
    private int duration;

    private Set<Long> userLikes;

    public void setUserLike(long userId) {
        if (userLikes == null) {
            Set<Long> userSet = new HashSet<>();
            userSet.add(userId);
            setUserLikes(userSet);
        }
        userLikes.add(userId);
    }

    public Integer getLikeCount() {
        if (userLikes == null) {
            return 0;
        }
        return this.userLikes.size();
    }
}

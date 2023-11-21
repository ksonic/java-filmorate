package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import org.springframework.stereotype.Component;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film implements Serializable {
    @Id
    @GeneratedValue
    private long id;

    @NotBlank(message = "Name shouldn't be empty")
    private String name;

    @Size(max = 200, message = "Description should be less than 200 symbols")
    private String description;

    private String releaseDate;
    @Positive(message = "Duration should be positive")
    private int duration;

    private Set<Long> userLikeIds = new HashSet<>();

    private Set<Genre> genres = new HashSet<>();
    private MPA mpa;
}

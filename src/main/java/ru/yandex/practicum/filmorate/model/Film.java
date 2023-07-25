package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

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
}

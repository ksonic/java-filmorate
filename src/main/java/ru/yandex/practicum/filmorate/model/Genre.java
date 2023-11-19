package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.persistence.GeneratedValue;
import java.io.Serializable;


@Data
public class Genre implements Serializable {
    @GeneratedValue
    private long id;
    private String name;
}


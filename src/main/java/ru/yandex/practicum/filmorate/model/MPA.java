package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.persistence.GeneratedValue;


@Data
public class MPA {
    @GeneratedValue
    private long id;
    private String name;
}
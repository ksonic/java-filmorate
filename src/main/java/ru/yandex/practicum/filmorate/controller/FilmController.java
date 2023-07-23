package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.text.SimpleDateFormat;
import java.util.*;

@RestController
public class FilmController {
    private final Map<Integer, Film> films;
    protected int sequence = 0;
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);

    public FilmController() {
        this.films = new HashMap<>();
    }

    @GetMapping("/films")
    public List<Film> getFilms() {
        log.info("Получен запрос GET /films.");
        return new LinkedList<>(films.values());
    }

    @PostMapping("/films")
    public Film addFilm(@RequestBody Film film) {
        try {
            if (!isAlreadyPublished(film)) {
                log.info("Получен запрос POST /films на создание фильма.");
                createFilm(film);

            } else {
                throw new ValidationException("Фильм с названием " + film.getName() + " уже существует.");
            }
        } catch (Exception e) {
            throw new ValidationException("Произошла ошибка", e);
        }
        return film;
    }

    @PutMapping("/films")
    public Film updateFilm(@RequestBody Film film) {
        try {
            if (isAlreadyPublished(film)) {
                log.info("Получен запрос POST /films на обновление фильма.");
                update(film);
            } else {
                throw new ValidationException("Фильма с названием " + film.getName() + " не существует.");
            }
        } catch (Exception e) {
            throw new ValidationException("Произошла ошибка", e);
        }
        return film;
    }


    private Film createFilm(Film film) {
        if (isDurationValid(film) && isDescriptionValid(film)
                && isNameValid(film) && isReleaseDateValid(film)) {
            film.setId(generateId());
            films.put(film.getId(), film);
        } else {
            throw new ValidationException("Произошла ошибка валидации.");
        }
        return film;
    }

    public void update(Film film) {
        films.put(film.getId(), film);
    }

    private int generateId() {
        return ++sequence;
    }

    public boolean isAlreadyPublished(Film film) {
        return films.containsKey(film.getId());
    }

    public boolean isDescriptionValid(Film film) {
        return film.getDescription().length() <= 200;
    }

    public boolean isNameValid(Film film) {
        return !film.getName().isEmpty();
    }

    public boolean isReleaseDateValid(Film film) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date releaseDate = format.parse(film.getReleaseDate());
            Date oldestDate = format.parse("1895-12-28");
            return releaseDate.after(oldestDate);
        } catch (Exception exception) {
            throw new ValidationException("Произошла ошибка: ", exception);
        }

    }

    public boolean isDurationValid(Film film) {
        return film.getDuration() > 0;
    }
}

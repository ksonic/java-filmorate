package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@RestController
public class FilmController {
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    private final Map<Long, Film> films;
    protected int sequence = 0;

    public FilmController() {
        this.films = new HashMap<>();
    }

    @GetMapping("/films")
    public List<Film> getFilms() {
        log.info("GET /films request received.");
        return new LinkedList<>(films.values());
    }

    @PostMapping("/films")
    public Film addFilm(@Valid @RequestBody Film film) {
        log.info("POST /films request received for film creation.");
        createFilm(film);
        return film;
    }

    @PutMapping("/films")
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.info("POST /films request received for film update.");
        update(film);
        return film;
    }

    private Film createFilm(Film film) {
        if (films.containsKey(film.getId())) {
            throw new ValidationException("Film  with name " + film.getName() + " is already exists.");
        }
        if (!isReleaseDateValid(film)) {
            throw new ValidationException("Validation error happened.");
        }
        film.setId(generateId());
        films.put(film.getId(), film);
        return film;
    }

    public void update(Film film) {
        if (!films.containsKey(film.getId())) {
            throw new ValidationException("Film with name " + film.getName() + " isn't found.");
        }
        films.put(film.getId(), film);
    }

    private long generateId() {
        return ++sequence;
    }

    public boolean isReleaseDateValid(Film film) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date releaseDate = format.parse(film.getReleaseDate());
            Date oldestDate = format.parse("1895-12-28");
            return releaseDate.after(oldestDate);
        } catch (Exception exception) {
            throw new ValidationException("Error happened: ", exception);
        }
    }
}

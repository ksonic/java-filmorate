package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import javax.validation.Valid;
import java.util.List;

@RestController
public class FilmController {
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    private final FilmService filmService;
    private final FilmStorage filmStorage;

    @Autowired
    public FilmController(FilmService filmService, FilmStorage filmStorage) {
        this.filmService = filmService;
        this.filmStorage = filmStorage;
    }

    @GetMapping("/films")
    public List<Film> getFilms() {
        log.info("GET /films request received.");
        return filmStorage.getFilms();
    }

    @PostMapping("/films")
    public Film addFilm(@Valid @RequestBody Film film) {
        log.info("POST /films request received for film creation.");
        filmStorage.createFilm(film);
        return film;
    }

    @PutMapping("/films")
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.info("PUT /films request received for film update.");
        filmStorage.update(film);
        return film;
    }

    @GetMapping("/films/{filmId}")
    public Film getFilmById(@PathVariable long filmId) {
        log.info("GET /films/{filmId} request received.");
        return filmStorage.getFilmById(filmId);
    }

    @PutMapping("/films/{id}/like/{userId}")
    public void likeFilm(@PathVariable @RequestBody long id, @PathVariable long userId) {
        log.info("PUT /films/{id}/like/{userId} request received for give a User like to the Film.");
        filmService.likeFilm(id, userId);
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public void deleteLikeFromFilm(@PathVariable long id, @PathVariable long userId) {
        log.info("DELETE /films/{id}/like/{userId} request received for delete a User like to the Film.");
        filmService.deleteLikeFromFilm(id, userId);
    }

    @GetMapping("/films/popular")
    public List<Film> getMostLikedFilms(@RequestParam(required = false) Integer count) {
        log.info("GET /films/popular?count request received for getting a list of most popular films.");
        if (count == null) {
            count = 10;
        }
        return filmService.getMostLikedFilms(count);
    }
}

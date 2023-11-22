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
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.List;

@RestController
public class FilmController {
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping("/films")
    public List<Film> getFilms() {
        log.info("GET /films request received.");
        return filmService.getFilms();
    }

    @PostMapping("/films")
    public Film addFilm(@Valid @RequestBody Film film) {
        log.info("POST /films request received for film creation.");
        return filmService.createFilm(film);
    }

    @PutMapping("/films")
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.info("PUT /films request received for film update.");
        return filmService.updateFilm(film);
    }

    @GetMapping("/films/{filmId}")
    public Film getFilmById(@PathVariable long filmId) {
        log.info("GET /films/{filmId} request received.");
        return filmService.getFilmById(filmId);
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
    public List<Film> getMostLikedFilms(@RequestParam(defaultValue = "10") Integer count) {
        log.info("GET /films/popular?count request received for getting a list of most popular films.");
        return filmService.getMostLikedFilms(count);
    }

    @GetMapping("/genres")
    public List<Genre> getGenre() {
        log.info("GET /genres request received for getting a list of films genres.");
        return filmService.getGenres();
    }

    @GetMapping("/genres/{id}")
    public Genre getGenresById(@PathVariable long id) {
        log.info("GET /genres/{id} request received for getting a films genre by id.");
        return filmService.getGenreById(id);
    }

    @GetMapping("/mpa")
    public List<MPA> getMpa() {
        log.info("GET /mpa request received for getting a list of mpa.");
        return filmService.getMpaList();
    }

    @GetMapping("/mpa/{id}")
    public MPA getMpaById(@PathVariable long id) {
        log.info("GET /mpa/{id} request received for getting a mpa by id.");
        return filmService.getMpaById(id);
    }
}

package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final UserService userService;
    private final FilmStorage filmStorage;

    public FilmService(UserService userService, FilmStorage filmStorage) {
        this.userService = userService;
        this.filmStorage = filmStorage;
    }

    public void likeFilm(long filmId, long userId) {
        if (!userService.containsUser(userId)) {
            throw new NotFoundException("User with id" + userId + " is not found.");
        }
        Film film = filmStorage.getFilmById(filmId);
        if (!containsFilm(filmId)) {
            throw new NotFoundException("Film with id " + filmId + " is not found.");
        }
        film.addUserLike(userId);
        filmStorage.getFilms().add(film);
    }

    public void deleteLikeFromFilm(long filmId, long userId) {
        if (!userService.containsUser(userId)) {
            throw new NotFoundException("User with id" + userId + " is not found.");
        }
        Film film = filmStorage.getFilmById(filmId);
        if (!containsFilm(filmId)) {
            throw new NotFoundException("Film with id " + filmId + " is not found.");
        }
        film.getUserLikes().remove(filmId);
        filmStorage.getFilms().add(film);
    }

    public List<Film> getMostLikedFilms(int count) {
        return filmStorage.getFilms()
                .stream()
                .sorted((film1, film2) -> film2.getLikeCount() - film1.getLikeCount())
                .limit(count)
                .collect(Collectors.toList());
    }

    public Film createFilm(Film film) {
        if (containsFilm(film.getId())) {
            throw new ValidationException("Film  with name " + film.getName() + " is already exists.");
        }
        if (!isReleaseDateValid(film)) {
            throw new ValidationException("Validation error happened.");
        }
        filmStorage.createFilm(film);
        return film;
    }

    public void updateFilm(Film film) {
        if (!containsFilm(film.getId())) {
            throw new NotFoundException("Film with id " + film.getId() + " is not found.");
        }
        filmStorage.update(film);
    }

    public Film getFilmById(long filmId) {
        if (!containsFilm(filmId)) {
            throw new NotFoundException("Film with id " + filmId + " is not found.");
        }
        return filmStorage.getFilmById(filmId);
    }

    public List<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public Boolean containsFilm(long filmId) {
        return filmStorage.getFilms().contains(filmStorage.getFilmById(filmId));
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

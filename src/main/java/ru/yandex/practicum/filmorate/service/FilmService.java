package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public void likeFilm(long filmId, long userId) {
        if (!userStorage.containsUser(userId)) {
            throw new NotFoundException("User with id" + userId + " is not found.");
        }
        Film film = filmStorage.getFilmById(filmId);
        if (!filmStorage.containsFilm(filmId)) {
            throw new NotFoundException("Film with id " + filmId + " is not found.");
        }
        filmStorage.likeFilm(filmId, userId);
        film.addUserLikeId(userId);
        filmStorage.getFilms().add(film);
    }

    public void deleteLikeFromFilm(long filmId, long userId) {
        if (!userStorage.containsUser(userId)) {
            throw new NotFoundException("User with id" + userId + " is not found.");
        }
        Film film = filmStorage.getFilmById(filmId);
        if (!filmStorage.containsFilm(filmId)) {
            throw new NotFoundException("Film with id " + filmId + " is not found.");
        }
        film.getUserLikeIds().remove(filmId);
        filmStorage.getFilms().add(film);
    }

    public List<Film> getMostLikedFilms(int count) {
        return filmStorage.getFilms()
                .stream()
                .sorted((film1, film2) -> film2.getUserLikeIds().size() - film1.getUserLikeIds().size())
                .limit(count)
                .collect(Collectors.toList());
    }

    public Film createFilm(Film film) {
        if (filmStorage.containsFilm(film.getId())) {
            throw new ValidationException("Film  with name " + film.getName() + " is already exists.");
        }
        if (!validateReleaseDate(film)) {
            throw new ValidationException("Validation error happened.");
        }
        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film film) {
        if (!filmStorage.containsFilm(film.getId())) {
            throw new NotFoundException("Film with id " + film.getId() + " is not found.");
        }
        return filmStorage.update(film);
    }

    public Film getFilmById(long filmId) {
        if (!filmStorage.containsFilm(filmId)) {
            throw new NotFoundException("Film with id " + filmId + " is not found.");
        }
        return filmStorage.getFilmById(filmId);
    }

    public List<Film> getFilms() {
        return filmStorage.getFilms();
    }

    private boolean validateReleaseDate(Film film) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date releaseDate = format.parse(film.getReleaseDate());
            Date oldestDate = format.parse("1895-12-28");
            return releaseDate.after(oldestDate);
        } catch (Exception exception) {
            throw new ValidationException("Error happened: ", exception);
        }
    }

    public List<Genre> getGenres() {
        return filmStorage.getGenres();
    }

    public Genre getGenreById(long id) {
        if (!filmStorage.containsGenre(id)) {
            throw new NotFoundException("Genre with id " + id + " is not found.");
        }
        return filmStorage.getGenreById(id);
    }

    public List<MPA> getMpaList() {
        return filmStorage.getMpaList();
    }

    public MPA getMpaById(long id) {
        if (!filmStorage.containsMPA(id)) {
            throw new NotFoundException("Mpa with id " + id + " is not found.");
        }
        return filmStorage.getMpaById(id);
    }
}

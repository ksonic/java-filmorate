package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    public FilmService(UserStorage userStorage, FilmStorage filmStorage) {
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
    }

    public void likeFilm(long filmId, long userId) {
        User user = userStorage.getUserById(userId);
        Film film = filmStorage.getFilmById(filmId);
        if (!userStorage.getUsers().containsKey(user.getId())) {
            throw new ValidationException("User with login " + userId + " is not found.");
        }
        if (!filmStorage.getFilms().contains(film)) {
            throw new ValidationException("Film with id " + filmId + " is not found.");
        }
        film.setUserLike(userId);
        filmStorage.getFilms().add(film);
    }

    public void deleteLikeFromFilm(long filmId, long userId) {
        User user = userStorage.getUserById(userId);
        Film film = filmStorage.getFilmById(filmId);
        if (!userStorage.getUsers().containsKey(user.getId())) {
            throw new ValidationException("User with id " + userId + " is not found.");
        }
        if (!filmStorage.getFilms().contains(film)) {
            throw new ValidationException("Film with id " + filmId + " is not found.");
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

}

package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;

import java.util.List;

public interface FilmStorage {
    List<Film> getFilms();

    Film createFilm(Film film);

    Film update(Film film);

    Film getFilmById(long filmId);

    Boolean containsFilm(long filmId);

    List<MPA> getMpaList();

    List<Genre> getGenres();

    Genre getGenreById(long id);

    MPA getMpaById(long id);

    boolean containsMPA(long id);

    boolean containsGenre(long id);

    void likeFilm(long filmId, long userId);
    List<Film> getMostLikedFilms(int count);
}

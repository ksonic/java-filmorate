package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();
    protected int sequence = 0;

    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    public Film createFilm(Film film) {
        film.setId(generateId());
        films.put(film.getId(), film);
        return film;
    }

    public Film update(Film film) {
        films.put(film.getId(), film);
        return film;
    }

    private long generateId() {
        return ++sequence;
    }

    public Film getFilmById(long filmId) {
        return films.get(filmId);
    }

    public Boolean containsFilm(long filmId) {
        return films.containsKey(filmId);
    }

    @Override
    public List<MPA> getMpaList() {
        return null;
    }

    @Override
    public List<Genre> getGenres() {
        return null;
    }

    @Override
    public Genre getGenreById(long id) {
        return null;
    }

    @Override
    public MPA getMpaById(long id) {
        return null;
    }

    @Override
    public boolean containsMPA(long id) {
        return false;
    }

    @Override
    public boolean containsGenre(long id) {
        return false;
    }

    @Override
    public void likeFilm(long filmId, long userId) {

    }
    @Override
    public List<Film> getMostLikedFilms(int count) {
        return null;
    }
}

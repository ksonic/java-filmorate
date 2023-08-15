package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();
    protected int sequence = 0;

    public List<Film> getFilms() {
        return new LinkedList<>(films.values());
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
        return getFilms().contains(getFilmById(filmId));
    }
}

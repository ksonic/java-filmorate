package ru.yandex.practicum.filmorate.storage;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.text.SimpleDateFormat;
import java.util.Date;
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

    public Film getFilmById(long filmId) {
        if (!films.containsKey(filmId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Film with id " + filmId + " isn't exist."
            );
        }
        return films.get(filmId);
    }
}

package ru.yandex.practicum.filmorate.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Component
@Primary
public class FilmDbStorage implements FilmStorage {
    private static final Logger log = LoggerFactory.getLogger(FilmDbStorage.class);
    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Film> getFilms() {
        return jdbcTemplate.query("select * from films", new RowMapper<Film>() {
            @Override
            public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
                Film film = new Film();
                film.setId(rs.getLong("id"));
                film.setName(rs.getString("name"));
                film.setDescription(rs.getString("description"));
                film.setReleaseDate(rs.getString("release_date"));
                film.setDuration(rs.getInt("duration"));
                film.setUserLikeIds(new HashSet<>(getLikesByFilmId(film.getId())));
                film.setGenres(new HashSet<>(getGenresByFilmId(film.getId())));
                film.setMpa(getMpaById(rs.getLong("mpa")));
                return film;
            }
        });

    }

    @Override
    @Transactional
    public Film createFilm(Film film) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "insert into films (name, description, release_date, duration, mpa) values (?,?,?,?,?)", new String[]{"id"});
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setString(3, film.getReleaseDate());
            ps.setInt(4, film.getDuration());
            ps.setLong(5, film.getMpa().getId());
            return ps;
        }, keyHolder);
        long id = keyHolder.getKey().longValue();
        film.setId(id);
        createGenresRelations(film);
        return getFilmById(id);
    }

    @Override
    public Film update(Film film) {
        containsFilm(film.getId());
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "update films set name=?, description=?, release_date=?, duration=?, mpa=? where id=?");
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setString(3, film.getReleaseDate());
            ps.setInt(4, film.getDuration());
            ps.setLong(5, film.getMpa().getId());
            ps.setLong(6, film.getId());
            return ps;
        });
        dropGenresRelations(film);
        createGenresRelations(film);
        film.setGenres(new HashSet<>(getGenresByFilmId(film.getId())));
        return film;
    }

    private void createGenresRelations(Film film) {
        List<Genre> genre = new ArrayList<>(film.getGenres());
        if (!genre.isEmpty()) {
            jdbcTemplate.batchUpdate("insert into genres_relations(film_id, genre_id) values (?,?)",
                    new BatchPreparedStatementSetter() {
                        public void setValues(PreparedStatement ps, int i) throws SQLException {
                            ps.setLong(1, film.getId());
                            ps.setLong(2, genre.get(i).getId());
                        }

                        public int getBatchSize() {
                            return genre.size();
                        }
                    });
        }
    }

    private void dropGenresRelations(Film film) {
        jdbcTemplate.update("delete from genres_relations where film_id=?", film.getId());
    }

    @Override
    public Film getFilmById(long filmId) {
        Film film = new Film();
        SqlRowSet rows = jdbcTemplate.queryForRowSet("select * from films where id = ?", filmId);
        if (rows.next()) {
            film.setId(rows.getLong("id"));
            film.setName(rows.getString("name"));
            film.setDescription(rows.getString("description"));
            film.setReleaseDate(rows.getString("release_date"));
            film.setUserLikeIds(new HashSet<>(getLikesByFilmId(filmId)));
            film.setDuration(rows.getInt("duration"));
            film.setGenres(new HashSet<>(getGenresByFilmId(filmId)));
            film.setMpa(getMpaById(rows.getLong("mpa")));
        }
        return film;
    }

    @Override
    public Boolean containsFilm(long filmId) {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select * from users where id = ?", filmId);
        if (filmRows.next()) {
            log.info("Film found: {} {}", filmRows.getString("id"), filmRows.getString("name"));
        } else {
            log.info("Film with id {} is not found.", filmId);
            return false;
        }
        return true;
    }

    @Override
    public List<MPA> getMpaList() {
        return jdbcTemplate.query("select * from mpa", new RowMapper<MPA>() {
            @Override
            public MPA mapRow(ResultSet rs, int rowNum) throws SQLException {
                MPA mpa = new MPA();
                mpa.setId(rs.getLong("id"));
                mpa.setName(rs.getString("name"));
                return mpa;
            }
        });
    }

    @Override
    public MPA getMpaById(long id) {
        MPA mpa = new MPA();
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from mpa where id = ?", id);
        if (userRows.next()) {
            mpa.setId(userRows.getLong("id"));
            mpa.setName(userRows.getString("name"));
            return mpa;
        }
        return mpa;
    }

    @Override
    public boolean containsMPA(long id) {
        SqlRowSet rows = jdbcTemplate.queryForRowSet("select * from mpa where id = ?", id);
        if (rows.next()) {
            log.info("Mpa is found: {} {}", rows.getString("id"), rows.getString("name"));
        } else {
            log.info("Mpa with id {} is not found.", id);
            return false;
        }
        return true;
    }

    @Override
    public boolean containsGenre(long id) {
        SqlRowSet rows = jdbcTemplate.queryForRowSet("select * from genre where id = ?", id);
        if (rows.next()) {
            log.info("Genre is found: {} {}", rows.getString("id"), rows.getString("name"));
        } else {
            log.info("Genre with id {} is not found.", id);
            return false;
        }
        return true;
    }

    @Override
    public void likeFilm(long filmId, long userId) {
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "insert into likes(film_id, user_id) values (?,?)");
            ps.setLong(1, filmId);
            ps.setLong(2, userId);
            return ps;
        });
    }

    private List<Long> getLikesByFilmId(long filmId) {
        List<Long> likeIds = new ArrayList<>();
        SqlRowSet rows = jdbcTemplate.queryForRowSet("select user_id from likes where film_id=?", filmId);
        while (rows.next()) {
            likeIds.add(rows.getLong("user_id"));
        }
        return likeIds;
    }

    @Override
    public List<Genre> getGenres() {
        return jdbcTemplate.query("select * from genre", new RowMapper<Genre>() {
            @Override
            public Genre mapRow(ResultSet rs, int rowNum) throws SQLException {
                Genre genre = new Genre();
                genre.setId(rs.getLong("id"));
                genre.setName(rs.getString("name"));
                return genre;
            }
        });
    }

    private List<Genre> getGenresByFilmId(long filmId) {
        List<Genre> genres = new ArrayList<>();
        SqlRowSet rows = jdbcTemplate.queryForRowSet("select id, name from genre where id in (select GENRE_ID from GENRES_RELATIONS where film_id=?)", filmId);
        while (rows.next()) {
            Genre genre = new Genre();
            genre.setId(rows.getLong("id"));
            genre.setName(rows.getString("name"));
            genres.add(genre);
        }
        return genres;
    }

    @Override
    public Genre getGenreById(long id) {
        Genre genre = new Genre();
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from genre where id = ?", id);
        if (userRows.next()) {
            genre.setId(userRows.getLong("id"));
            genre.setName(userRows.getString("name"));
        }
        return genre;
    }

    @Override
    public List<Film> getMostLikedFilms(int count) {
        List<Film> films = new ArrayList<>();
        Film film = new Film();
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("SELECT * FROM FILMS f\n" +
                "LEFT JOIN (SELECT film_id,count(id) AS c FROM likes\n" +
                "GROUP BY FILM_ID ) l\n" +
                "ON f.id=l.film_id\n" +
                "ORDER BY l.c DESC\n" +
                "LIMIT ?", count);
        while (userRows.next()) {
            film.setId(userRows.getLong("id"));
            film.setName(userRows.getString("name"));
            film.setDescription(userRows.getString("description"));
            film.setReleaseDate(userRows.getString("release_date"));
            film.setUserLikeIds(new HashSet<>(getLikesByFilmId(userRows.getLong("id"))));
            film.setDuration(userRows.getInt("duration"));
            film.setGenres(new HashSet<>(getGenresByFilmId(userRows.getLong("id"))));
            film.setMpa(getMpaById(userRows.getLong("mpa")));
            films.add(film);
        }
        return films;
    }
}

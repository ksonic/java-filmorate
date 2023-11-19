CREATE TABLE IF NOT EXISTS films (
  id long auto_increment PRIMARY KEY,
  name varchar,
  description varchar,
  release_date date,
  duration integer,
  mpa long
);
CREATE TABLE IF NOT EXISTS genre (
  id long auto_increment PRIMARY KEY,
  name varchar
);
CREATE TABLE IF NOT EXISTS genres_relations (
  id long auto_increment PRIMARY KEY,
  film_id long,
  genre_id long,
  FOREIGN KEY (film_id) REFERENCES films (id),
  FOREIGN KEY (genre_id) REFERENCES genre (id)
);
CREATE TABLE IF NOT EXISTS likes (
  id long auto_increment PRIMARY KEY,
  film_id long,
  user_id long
);

CREATE TABLE IF NOT EXISTS mpa (
  id long auto_increment PRIMARY KEY,
  name varchar
);
CREATE TABLE IF NOT EXISTS users (
  id long auto_increment PRIMARY KEY,
  name varchar,
  email varchar,
  login varchar,
  birthday varchar
);
CREATE TABLE IF NOT EXISTS friendship (
  id long auto_increment PRIMARY KEY,
  friend_id long,
  receiver_friend_id long,
  status boolean
);

ALTER TABLE friendship ADD FOREIGN KEY (friend_id) REFERENCES users (id);

ALTER TABLE friendship ADD FOREIGN KEY (receiver_friend_id) REFERENCES users (id);

ALTER TABLE likes ADD FOREIGN KEY (film_id) REFERENCES films (id);

ALTER TABLE likes ADD FOREIGN KEY (user_id) REFERENCES users (id);

INSERT INTO genre (name) values ('Комедия'),('Драма'),('Мультфильм'),('Триллер'),('Документальный'),('Боевик');

INSERT INTO mpa (name) values ('G'),('PG'),('PG-13'),('R'),('NC-17');

commit;
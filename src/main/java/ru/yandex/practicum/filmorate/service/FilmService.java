package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.LikeStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toUnmodifiableList;

@Service
public class FilmService {
    protected FilmStorage filmStorage;
    protected LikeStorage likeStorage;
    protected UserStorage userStorage;
    protected int defaultPopularLimit = 10;

    public FilmService(final FilmStorage filmStorage, final LikeStorage likeStorage, final UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.likeStorage = likeStorage;

        this.userStorage = userStorage;
    }

    public void add(Film film) {
        final int filmId = filmStorage.add(film);
        likeStorage.registerFilm(filmId);
    }

    public void update(Film film) {
        filmStorage.update(film);
    }

    public Film getById(int id) {
        final Optional<Film> filmOpt = filmStorage.get(id);

        if (filmOpt.isEmpty()) {
            throw new NotFoundException(String.format("Нет фильма с id = %s", id));
        }

        return filmOpt.get();
    }

    public List<Film> getAll() {
        return filmStorage.getAll();
    }

    public void addLike(int filmId, int userId) {
        checkExistsWithException(isFilmExists(filmId), String.format("Нет фильма с id = %s", filmId));
        checkExistsWithException(isUserExists(userId), String.format("Нет пользователя с id = %s", userId));

        likeStorage.addLike(filmId, userId);
    }

    public void deleteLike(int filmId, int userId) {
        checkExistsWithException(isFilmExists(filmId), String.format("Нет фильма с id = %s", filmId));
        checkExistsWithException(isUserExists(userId), String.format("Нет пользователя с id = %s", userId));

        likeStorage.removeLike(filmId, userId);
    }

    public List<Film> getPopular(final Optional<Integer> count) {
        int finalCount = count.or(() -> Optional.of(defaultPopularLimit)).get();

        final List<Integer> popularFilmIds = likeStorage.getPopular(finalCount);
        return getFilmListByIds(popularFilmIds);
    }

    private boolean isUserExists(int id) {
        return userStorage.get(id).isPresent();
    }

    private boolean isFilmExists(int id) {
        return filmStorage.get(id).isPresent();
    }

    private void checkExistsWithException(boolean exists, final String message) {
        if (!exists) {
            throw new NotFoundException(message);
        }
    }

    private List<Film> getFilmListByIds(final List<Integer> filmIds) {
        return filmIds.stream().map(id -> filmStorage.get(id))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(toUnmodifiableList());
    }
}
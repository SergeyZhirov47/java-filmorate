package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.common.ErrorMessageUtil;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.LikeStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FilmService {
    protected final int defaultPopularLimit = 10;

    protected final FilmStorage filmStorage;
    protected final LikeStorage likeStorage;
    protected final UserStorage userStorage;

    public int add(Film film) {
        final int filmId = filmStorage.add(film);
        return filmId;
    }

    public void update(Film film) {
        filmStorage.update(film);
    }

    public Film getById(int id) {
        checkFilmExists(id);

        final Optional<Film> filmOpt = filmStorage.get(id);
        return filmOpt.get();
    }

    public List<Film> getAll() {
        return filmStorage.getAll();
    }

    public void addLike(int filmId, int userId) {
        checkFilmExists(filmId);
        checkUserExists(userId);

        likeStorage.addLike(filmId, userId);
    }

    public void deleteLike(int filmId, int userId) {
        checkFilmExists(filmId);
        checkUserExists(userId);

        likeStorage.removeLike(filmId, userId);
    }

    public List<Film> getPopular(final Optional<Integer> count) {
        Optional<Integer> countOrDefault = count.or(() -> Optional.of(defaultPopularLimit));
        return filmStorage.getPopular(countOrDefault);
    }

    private boolean isUserExists(int id) {
        return userStorage.contains(id);
    }

    private boolean isFilmExists(int id) {
        return filmStorage.contains(id);
    }

    private void checkExistsWithException(boolean exists, final String message) {
        if (!exists) {
            throw new NotFoundException(message);
        }
    }

    private void checkFilmExists(int id) {
        checkExistsWithException(isFilmExists(id), ErrorMessageUtil.getNoFilmWithIdMessage(id));
    }

    private void checkUserExists(int id) {
        checkExistsWithException(isUserExists(id), ErrorMessageUtil.getNoUserWithIdMessage(id));
    }
}

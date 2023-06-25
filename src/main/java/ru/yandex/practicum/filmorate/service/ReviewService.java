package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.common.ErrorMessageUtil;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.ReviewLikeStorage;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewService {
    protected final int defaultReviewsLimit = 10;

    protected final ReviewStorage reviewStorage;
    protected final ReviewLikeStorage reviewLikeStorage;

    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    public int add(final Review review) {
        checkUserExists(review.getUserId());
        checkFilmExists(review.getFilmId());

        return reviewStorage.add(review);
    }

    public void update(final Review review) {
        checkReviewExists(review.getId());
        reviewStorage.update(review);
    }

    public List<Review> getByFilmId(Optional<Integer> filmId, Optional<Integer> count) {
        final Optional<Integer> finalCount = Optional.of(count.orElse(defaultReviewsLimit));
        return reviewStorage.getByFilmId(filmId, finalCount);
    }

    public void deleteById(int id) {
        checkReviewExists(id);
        reviewStorage.deleteById(id);
    }

    public Review getById(int id) {
        final Optional<Review> reviewOpt = reviewStorage.get(id);

        if (reviewOpt.isEmpty()) {
            throw new NotFoundException(ErrorMessageUtil.getNoEntityWithIdMessage("Нет отзыва", id));
        }

        return reviewOpt.get();
    }

    public void addLike(int reviewId, int userId) {
        reviewLikeStorage.addLike(reviewId, userId);
    }

    public void addDislike(int reviewId, int userId) {
        reviewLikeStorage.addDislike(reviewId, userId);
    }

    public void deleteLike(int reviewId, int userId) {
        reviewLikeStorage.deleteLike(reviewId, userId);
    }

    public void deleteDislike(int reviewId, int userId) {
        reviewLikeStorage.deleteDislike(reviewId, userId);
    }

    private void checkReviewExists(int reviewId) {
        if (!reviewStorage.contains(reviewId)) {
            throw new NotFoundException(ErrorMessageUtil.getNoEntityWithIdMessage("Нет отзыва", reviewId));
        }
    }

    private void checkFilmExists(int filmId) {
        if (!filmStorage.contains(filmId)) {
            throw new NotFoundException(ErrorMessageUtil.getNoFilmWithIdMessage(filmId));
        }
    }

    private void checkUserExists(int userId) {
        if (!userStorage.contains(userId)) {
            throw new NotFoundException(ErrorMessageUtil.getNoUserWithIdMessage(userId));
        }
    }
}

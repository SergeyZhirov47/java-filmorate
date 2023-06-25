package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    // Добавление нового отзыва.
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Review add(@Valid @RequestBody Review newReview) {
        log.info("POST /reviews");
        final int reviewId = reviewService.add(newReview);
        log.info(String.format("Добавлен новый отзыв с id %s к фильму %s от пользователя %s", reviewId, newReview.getFilmId(), newReview.getUserId()));

        return reviewService.getById(reviewId);
    }

    // Редактирование уже имеющегося отзыва.
    @PutMapping
    public Review update(@Valid @RequestBody Review review) {
        log.info("PUT /reviews");
        reviewService.update(review);
        log.info(String.format("Обновлена информация об отзыве с id %s", review.getId()));

        return reviewService.getById(review.getId());
    }

    // Удаление уже имеющегося отзыва.
    @DeleteMapping("/{id}")
    public void delete(@PathVariable(name = "id") int id) {
        log.info("DELETE /reviews/{id}");
        reviewService.deleteById(id);
        log.info(String.format("Удален отзыв с id %s", id));
    }

    // Получение отзыва по идентификатору.
    @GetMapping("/{id}")
    public Review getReviewById(@PathVariable(name = "id") int id) {
        log.info(String.format("GET /reviews/{id}, {id} = %s", id));
        return reviewService.getById(id);
    }

    //  Получение всех отзывов по идентификатору фильма, если фильм не указан то все. Если кол-во не указано, то 10.
    @GetMapping
    public List<Review> getReviewsByFilm(@RequestParam(name = "filmId") Optional<Integer> filmId, @RequestParam(name = "count") Optional<Integer> count) {
        log.info(String.format("GET /reviews?filmId={filmId}&count={count}, {filmId} = %s, {count} = %s", filmId.isPresent() ? filmId.get() : "не указан", count.isPresent() ? count.get() : "не указан"));
        return reviewService.getByFilmId(filmId, count);
    }

    // пользователь ставит лайк отзыву.
    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable(name = "id") int id, @PathVariable(name = "userId") int userId) {
        log.info(String.format("PUT /reviews/{id}/like/{userId}, {id} = %s, {userId} = %s", id, userId));
        reviewService.addLike(id, userId);
        log.info(String.format("Лайк успешно добавлен для отзыва c id %s от пользователя с id %s", id, userId));
    }

    // пользователь ставит дизлайк отзыву.
    @PutMapping("/{id}/dislike/{userId}")
    public void addDislike(@PathVariable(name = "id") int id, @PathVariable(name = "userId") int userId) {
        log.info(String.format("PUT /reviews/{id}/dislike/{userId}, {id} = %s, {userId} = %s", id, userId));
        reviewService.addDislike(id, userId);
        log.info(String.format("Дизлайк успешно добавлен для отзыва c id %s от пользователя с id %s", id, userId));
    }

    // пользователь удаляет лайк отзыву.
    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable(name = "id") int id, @PathVariable(name = "userId") int userId) {
        log.info(String.format("DELETE /reviews/{id}/like/{userId}, {id} = %s, {userId} = %s", id, userId));
        reviewService.deleteLike(id, userId);
        log.info(String.format("Лайк успешно удален для отзыва c id %s от пользователя с id %s", id, userId));
    }

    // пользователь удаляет дизлайк отзыву.
    @DeleteMapping("/{id}/dislike/{userId}")
    public void deleteDislike(@PathVariable(name = "id") int id, @PathVariable(name = "userId") int userId) {
        log.info(String.format("DELETE /reviews/{id}/dislike/{userId}, {id} = %s, {userId} = %s", id, userId));
        reviewService.deleteDislike(id, userId);
        log.info(String.format("Дизлайк успешно удален для отзыва c id %s от пользователя с id %s", id, userId));
    }
}

package ru.yandex.practicum.filmorate.storage;

public interface ReviewLikeStorage {
    void addLike(int reviewId, int userId);

    void deleteLike(int reviewId, int userId);

    void addDislike(int reviewId, int userId);

    void deleteDislike(int reviewId, int userId);

    boolean contains(int reviewId, int userId);
}

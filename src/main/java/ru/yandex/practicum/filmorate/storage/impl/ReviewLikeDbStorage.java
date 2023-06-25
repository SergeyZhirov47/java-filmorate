package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.storage.ReviewLikeStorage;

@Component
@RequiredArgsConstructor
public class ReviewLikeDbStorage implements ReviewLikeStorage {
    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public void addLike(int id, int userId) {
        setFeedback(id, userId, true);
    }

    @Override
    public void deleteLike(int id, int userId) {
        setFeedback(id, userId, null);
    }

    @Override
    public void addDislike(int id, int userId) {
        setFeedback(id, userId, false);
    }

    @Override
    public void deleteDislike(int id, int userId) {
        setFeedback(id, userId, null);
    }

    @Override
    public boolean contains(int reviewId, int userId) {
        final String sql = "SELECT EXISTS(SELECT id " +
                "FROM \"reviews_likes\" " +
                "WHERE id_review = ? AND id_user = ?);";
        return jdbcTemplate.queryForObject(sql, Boolean.class, reviewId, userId);
    }

    private void setFeedback(int reviewId, int userId, Boolean isUseful) {
        final MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("reviewId", reviewId);
        parameters.addValue("userId", userId);
        parameters.addValue("isUseful", isUseful);

        String sql;

        if (contains(reviewId, userId)) {
            sql = "UPDATE \"reviews_likes\"\n" +
                    "SET isUseful = :isUseful\n" +
                    "WHERE id_review = :reviewId AND id_user = :userId;";
        } else {
            sql = "INSERT INTO \"reviews_likes\"\n" +
                    "(id_review, id_user, isUseful)\n" +
                    "VALUES(:reviewId, :userId, :isUseful);";
        }

        namedParameterJdbcTemplate.update(sql, parameters);
    }
}

package ru.yandex.practicum.filmorate.storage.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.common.mappers.ReviewRowMapper;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ReviewDbStorage implements ReviewStorage {
    private static final String SELECT_REVIEW_BASE = "SELECT r.id, r.id_user, r.id_film, r.content, r.isPositive, \n" +
            "(COALESCE(rl1.likes_count, 0) - COALESCE(rl2.dislikes_count, 0)) AS rating\n" +
            "FROM \"reviews\" r\n" +
            "LEFT JOIN (SELECT id_review, COUNT(id) as likes_count " +
            "FROM \"reviews_likes\" " +
            "WHERE isUseful = TRUE " +
            "GROUP BY id_review) rl1 ON rl1.id_review = r.id\n" +
            "LEFT JOIN (SELECT id_review, COUNT(id) as dislikes_count " +
            "FROM \"reviews_likes\" " +
            "WHERE isUseful = FALSE " +
            "GROUP BY id_review) rl2 ON rl2.id_review = r.id";
    private static final String ORDER_PART = " ORDER BY rating DESC ";

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final ReviewRowMapper reviewRowMapper = new ReviewRowMapper();

    @Override
    public Optional<Review> get(int id) {
        Review review;
        try {
            review = jdbcTemplate.queryForObject(SELECT_REVIEW_BASE + " WHERE r.id = ?;", reviewRowMapper, id);
        } catch (EmptyResultDataAccessException exp) {
            review = null;
        }

        return Optional.ofNullable(review);
    }

    @Override
    public List<Review> getByFilmId(Optional<Integer> filmId, Optional<Integer> count) {
        final MapSqlParameterSource parameters = new MapSqlParameterSource();

        final StringBuilder sqlStrBuilder = new StringBuilder(SELECT_REVIEW_BASE);
        if (filmId.isPresent()) {
            sqlStrBuilder.append(" WHERE r.id_film = :filmId ");
            parameters.addValue("filmId", filmId.get());

        }
        sqlStrBuilder.append(ORDER_PART);
        if (count.isPresent()) {
            sqlStrBuilder.append(" LIMIT :limitValue ");
            parameters.addValue("limitValue", count.get());
        }
        sqlStrBuilder.append(";");

        return namedParameterJdbcTemplate.query(sqlStrBuilder.toString(), parameters, reviewRowMapper);
    }

    @Override
    public int add(final Review review) {
        final String insertSql = "INSERT into \"reviews\" (id_user, id_film, content, isPositive) " +
                "VALUES (?, ?, ?, ?)";

        final KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            final PreparedStatement stmt = connection.prepareStatement(insertSql, new String[]{"id"});
            stmt.setInt(ReviewInsertColumn.USER_ID.getColumnIndex(), review.getUserId());
            stmt.setInt(ReviewInsertColumn.FILM_ID.getColumnIndex(), review.getFilmId());
            stmt.setString(ReviewInsertColumn.CONTENT.getColumnIndex(), review.getContent());
            stmt.setBoolean(ReviewInsertColumn.IS_POSITIVE.getColumnIndex(), review.getIsPositive());

            return stmt;
        }, keyHolder);

        int reviewId = Objects.requireNonNull(keyHolder.getKey()).intValue();
        review.setId(reviewId);

        return reviewId;
    }

    @Override
    public void update(final Review review) {
        final String updateSql = "UPDATE \"reviews\"\n" +
                "SET content = ?, isPositive = ?\n" +
                "WHERE id = ?;";

        jdbcTemplate.update(updateSql, review.getContent(), review.getIsPositive(), review.getId());
    }

    @Override
    public void deleteById(int id) {
        final String sqlQuery = "DELETE FROM \"reviews\" " +
                "WHERE id = ?";
        jdbcTemplate.update(sqlQuery, id);
    }

    @Override
    public boolean contains(int id) {
        final String sql = "SELECT EXISTS(SELECT r.id " +
                "FROM \"reviews\" r " +
                "WHERE r.id = ?);";
        return jdbcTemplate.queryForObject(sql, Boolean.class, id);
    }

    @AllArgsConstructor
    private enum ReviewInsertColumn {
        USER_ID(1),
        FILM_ID(2),
        CONTENT(3),
        IS_POSITIVE(4);

        @Getter
        private final int columnIndex;
    }
}

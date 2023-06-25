package ru.yandex.practicum.filmorate.common.mappers;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ReviewRowMapper implements RowMapper<Review> {
    @Override
    public Review mapRow(ResultSet rs, int rowNum) throws SQLException {
        final Review review = Review.builder()
                .id(rs.getInt("id"))
                .userId(rs.getInt("id_user"))
                .filmId(rs.getInt("id_film"))
                .content(rs.getString("content"))
                .isPositive(rs.getBoolean("isPositive"))
                .rating(rs.getInt("rating"))
                .build();

        return review;
    }
}

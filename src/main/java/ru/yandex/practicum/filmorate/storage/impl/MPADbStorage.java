package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.common.mappers.MPARatingRowMapper;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.MPAStorage;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MPADbStorage implements MPAStorage {
    private static final String SELECT_MPA_RATING = "SELECT id, name FROM \"MPA_ratings\"";

    private final JdbcTemplate jdbcTemplate;
    private final MPARatingRowMapper ratingRowMapper = new MPARatingRowMapper();

    @Override
    public Optional<MPA> getMPARatingById(int id) {
        final String sql = SELECT_MPA_RATING + " WHERE id = ?;";

        MPA rating;
        try {
            rating = jdbcTemplate.queryForObject(sql, ratingRowMapper, id);
        } catch (EmptyResultDataAccessException exp) {
            rating = null;
        }

        return Optional.ofNullable(rating);
    }

    @Override
    public List<MPA> getAllMPARatings() {
        final List<MPA> ratings = jdbcTemplate.query(SELECT_MPA_RATING + ";", ratingRowMapper);
        return ratings;
    }
}

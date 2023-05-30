package ru.yandex.practicum.filmorate.storage.impl.db;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.common.ErrorMessageUtil;
import ru.yandex.practicum.filmorate.common.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

@Component
@Qualifier("userDbStorage")
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;
    private final UserRowMapper userRowMapper = new UserRowMapper();
    private final String selectUserBase = "SELECT \"id\", \"email\", \"login\", \"name\", \"birthday\"\n" +
            "FROM \"users\";";

    @Override
    public Optional<User> get(int id) {
        final User user = jdbcTemplate.queryForObject(selectUserBase + " WHERE id = ?", userRowMapper, id);
        return Optional.ofNullable(user);
    }

    @Override
    public List<User> get(List<Integer> idList) {
        final String idListString = idList.stream().map(String::valueOf).collect(Collectors.joining(", "));
        final String sql = selectUserBase + " WHERE id IN (" + idListString + ")";

        final List<User> users = jdbcTemplate.query(sql, userRowMapper);
        return users;
    }

    @Override
    public int add(final User user) {
        final String sqlQuery = "INSERT INTO \"users\" (\"email\", \"login\", \"name\", \"birthday\")\n" +
                "VALUES(?, ?, ?, ?);";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getLogin());
            stmt.setString(3, user.getName());
            stmt.setDate(4, Date.valueOf(user.getBirthday()));
            return stmt;
        }, keyHolder);

        return keyHolder.getKey().intValue();
    }

    @Override
    public void update(final User user) {
        final int userId = user.getId();

        checkUserExists(userId);

        final String sqlQuery = "UPDATE \"users\" " +
                "SET \"email\" = ?, \"login\" = ?, \"name\" = ?, \"birthday\" = ? " +
                "WHERE \"id\" = ?;";
        jdbcTemplate.update(sqlQuery, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), userId);
    }

    @Override
    public void delete(final User user) {
        final int userId = user.getId();
        deleteById(userId);
    }

    @Override
    public void deleteById(int id) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public List<User> getAll() {
        final List<User> users = jdbcTemplate.query(selectUserBase, userRowMapper);
        return users;
    }

    @Override
    public boolean contains(int id) {
        final String sql = "SELECT id " +
                "FROM \"users\" " +
                "WHERE id = ?";
        final Integer userId = jdbcTemplate.queryForObject(sql, Integer.class, id);

        return nonNull(userId);
    }

    private void checkUserExists(int userId) {
        if (!contains(userId)) {
            throw new NotFoundException(ErrorMessageUtil.getNoUserWithIdMessage(userId));
        }
    }
}

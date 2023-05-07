package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.isNull;

@Component
public class InMemoryLikeStorage implements LikeStorage {
    // как хранить?
    // 1. ключ фильм - значение список лайкнувших (по текущему ТЗ самый простой вариант).
    // 2. ключ пользователь - значение список понравившихся (лайкнутым им фильмов)
    // 3. и то и другое.
    // 4. ???
    protected Map<Integer, Set<Integer>> filmLikes = new HashMap<>();

    @Override
    public void registerFilm(int filmId) {
        if (!filmLikes.containsKey(filmId)) {
            filmLikes.put(filmId, new HashSet<>());
        }
    }

    @Override
    public void addLike(int filmId, int userId) {
        final Set<Integer> userHowLikes = filmLikes.get(filmId);
        if (!isNull(userHowLikes)) {
            userHowLikes.add(userId);
        } else {
            filmLikes.put(filmId, new HashSet<>() {{
                add(userId);
            }});
        }
    }

    @Override
    public void removeLike(int filmId, int userId) {
        if (filmLikes.containsKey(filmId)) {
            filmLikes.get(filmId).remove(userId);
        }
    }

    @Override
    public List<Integer> getPopular(int count) {
        return filmLikes.entrySet().stream()
                .sorted(Comparator.<Map.Entry<Integer, Set<Integer>>>comparingInt (kv -> kv.getValue().size()).reversed())
                .map(Map.Entry::getKey)
                .limit(count)
                .collect(Collectors.toUnmodifiableList());
    }
}

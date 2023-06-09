package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Film add(@Valid @RequestBody Film newFilm) {
        log.info("POST /films");
        final int filmId = filmService.add(newFilm);
        log.info(String.format("Добавлен новый фильм %s", newFilm.getName()));

        return filmService.getById(filmId);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        log.info("PUT /films");
        filmService.update(film);
        log.info(String.format("Обновлена информация о фильме с id %s", film.getId()));

        return filmService.getById(film.getId());
    }

    @GetMapping
    public List<Film> getAll() {
        log.info("GET /films");
        return filmService.getAll();
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable(name = "id") int id) {
        log.info(String.format("GET /films/{id}, {id} = %s", id));
        return filmService.getById(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable(name = "id") int id, @PathVariable(name = "userId") int userId) {
        log.info(String.format("PUT /films/{id}/like/{userId}, {id} = %s, {userId} = %s", id, userId));
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable(name = "id") int id, @PathVariable(name = "userId") int userId) {
        log.info(String.format("DELETE /films/{id}/like/{userId}, {id} = %s, {userId} = %s", id, userId));
        filmService.deleteLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopular(@RequestParam(name = "count") Optional<Integer> count) {
        log.info(String.format("GET /films/popular?count={count}, {count} = %s", count.isPresent() ? count.get() : "не указан"));
        return filmService.getPopular(count);
    }
}

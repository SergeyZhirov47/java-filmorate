package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
@Jacksonized
public class Review {
    @JsonProperty("reviewId")
    private int id;
    @NotNull
    private Integer filmId; // id фильма, к которому относится этот отзыв.
    @NotNull
    private Integer userId; // id пользователя, который оставил этот отзыв.
    @NotBlank(message = "Текст отзыва не может быть пустым")
    private String content; // текст отзыва.
    @JsonProperty("isPositive")
    @NotNull
    private Boolean isPositive; // тип отзыва (положительный/отрицательный).

    /*
    Рейтинг отзыва.
        У отзыва имеется рейтинг. При создании отзыва рейтинг равен нулю.
        Если пользователь оценил отзыв как полезный, это увеличивает его рейтинг на 1.
        Если как бесполезный, то уменьшает на 1.
    */
    @JsonProperty("useful")
    private int rating; // рейтинг отзыва.
}

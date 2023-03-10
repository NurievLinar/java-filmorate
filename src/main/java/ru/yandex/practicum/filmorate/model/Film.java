package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import ru.yandex.practicum.filmorate.validator.FilmValid;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.*;

@Data
public class Film {

    private static final int SIZE = 200;

    private Integer id;

    @NotEmpty(message = "Имя не может быть пустым")
    private String name;

    @Size(max = SIZE, message = "Максимальная длина описания " + SIZE + " символов")
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @FilmValid
    private LocalDate releaseDate;

    @Positive
    private int duration;

    private Integer rating;

    private Mpa mpa;

    private List<Genre> genres;

    Integer rateAndLikes;

    public Film(String name, String description, LocalDate releaseDate, Integer duration, Integer rate,
                Mpa mpa, List<Genre> genres) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        if (rate != null) {
            this.rating = rate;
        } else {
            this.rating = 0;
        }
        this.mpa = mpa;
        if (genres == null) {
            this.genres = new ArrayList<>();
        } else {
            this.genres = genres;
        }
    }

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("FILM_NAME", name);
        values.put("FILM_DESCRIPTION", description);
        values.put("FILM_RELEASE_DATE", releaseDate);
        values.put("FILM_DURATION", duration);
        values.put("FILM_RATE", rating);
        values.put("MPA_ID", mpa.getId());
        values.put("FILM_RATE_AND_LIKES", rateAndLikes);
        return values;
    }
}

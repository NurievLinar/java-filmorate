package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
@Builder
public class Film {

    private static final int SIZE = 200;

    private int id;

    @NotEmpty(message = "Имя не может быть пустым")
    private String name;

    @Size(max = SIZE, message = "Максимальная длина описания " + SIZE + " символов")
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate releaseDate;

    @Positive
    private int duration;

}

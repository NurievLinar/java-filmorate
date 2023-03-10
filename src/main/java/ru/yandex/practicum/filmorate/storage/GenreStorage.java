package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;
import java.util.List;

@Component
public interface GenreStorage {
    List<Genre> getAll();
    String getById(Integer id);
    List<Genre> getGenres(Integer filmId);
}

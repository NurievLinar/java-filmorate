package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.List;

@Service
@AllArgsConstructor
public class GenreService {
    private final GenreStorage genreDbStorage;

    public Genre getById(Integer id) {
        return new Genre(id, genreDbStorage.getById(id));
    }

    public List<Genre> getAll() {
        return genreDbStorage.getAll();
    }

    public List<Genre> getGenresId(Integer id) {
        return genreDbStorage.getGenres(id);
    }
}
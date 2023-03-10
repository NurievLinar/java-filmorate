package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.List;

@Service
public class GenreService {
    private final GenreStorage genreDbStorage;

    @Autowired
    public GenreService(GenreDbStorage genreDbStorage) {
        this.genreDbStorage = genreDbStorage;
    }

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
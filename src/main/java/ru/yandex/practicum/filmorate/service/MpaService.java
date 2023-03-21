package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.List;

@Service
@AllArgsConstructor
public class MpaService {
    private final MpaStorage mpaDbStorage;

    public List<Mpa> getAll() {
        return mpaDbStorage.getAll();
    }

    public Mpa getById(Integer id) {
        return new Mpa(id, mpaDbStorage.getById(id));
    }
}

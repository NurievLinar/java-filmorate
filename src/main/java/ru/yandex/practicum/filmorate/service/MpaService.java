package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.List;

@Service
public class MpaService {
    private final MpaStorage mpaDbStorage;

    @Autowired
    public MpaService(MpaStorage mpaDbStorage){
        this.mpaDbStorage = mpaDbStorage;
    }

    public List<Mpa> getAll() {
        return mpaDbStorage.getAll();
    }

    public Mpa getById(Integer id) {
        return new Mpa(id, mpaDbStorage.getById(id));
    }
}

package ru.job4j.auth.service;

import ru.job4j.auth.model.City;

import java.util.Optional;

public interface CityService {
    Optional<City> findById(int id);
}

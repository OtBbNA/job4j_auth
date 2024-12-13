package ru.job4j.auth.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.job4j.auth.model.City;
import ru.job4j.auth.repository.CityRepository;

import java.util.Optional;

@Service
@AllArgsConstructor
public class SimpleCityService implements CityService {

    private final CityRepository cityRepository;

    @Override
    public Optional<City> findById(int id) {
        return cityRepository.findById(id);
    }
}

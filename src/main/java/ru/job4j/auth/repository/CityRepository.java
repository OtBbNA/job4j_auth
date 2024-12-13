package ru.job4j.auth.repository;

import org.springframework.data.repository.CrudRepository;
import ru.job4j.auth.model.City;

public interface CityRepository extends CrudRepository<City, Integer> {
}

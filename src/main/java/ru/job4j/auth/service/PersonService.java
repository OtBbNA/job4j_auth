package ru.job4j.auth.service;

import ru.job4j.auth.model.Person;

import java.util.Collection;
import java.util.Optional;

public interface PersonService {

    Optional<Person> findById(int id);

    Collection<Person> findAll();

    Optional<Person> save(Person person);

    Optional<Person> update(Person person);

    void deleteById(int id);

    boolean existsById(int id);

    Optional<Person> findByLogin(String username);
}

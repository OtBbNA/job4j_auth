package ru.job4j.auth.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.job4j.auth.model.Person;
import ru.job4j.auth.repository.PersonRepository;

import java.util.Collection;
import java.util.Optional;

@Service
@AllArgsConstructor
public class SimplePersonService implements PersonService {

    private final PersonRepository personRepository;

    @Override
    public Optional<Person> findById(int id) {
        return personRepository.findById(id);
    }

    @Override
    public Collection<Person> findAll() {
        return personRepository.findAll();
    }

    @Override
    public Optional<Person> save(Person person) {
        return Optional.of(personRepository.save(person));
    }

    @Override
    public Optional<Person> update(Person person) {
        if (personRepository.existsById(person.getId())) {
            return Optional.of(personRepository.save(person));
        }
        return Optional.empty();
    }

    @Override
    public void deleteById(int id) {
        if (personRepository.existsById(id)) {
            personRepository.deleteById(id);
        }
    }

    @Override
    public boolean existsById(int id) {
        return personRepository.existsById(id);
    }

    @Override
    public Optional<Person> findByLogin(String username) {
        return personRepository.findByLogin(username);
    }
}

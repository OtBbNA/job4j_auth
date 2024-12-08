package ru.job4j.auth.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;
import ru.job4j.auth.model.Person;
import ru.job4j.auth.service.PersonService;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;

@RestController
@RequestMapping("/person")
@AllArgsConstructor
public class PersonController {

    private final PersonService personService;

    @GetMapping("/")
    public Collection<Person> findAll() {
        return this.personService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Person> findById(@PathVariable int id) {
        return personService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/")
    public ResponseEntity<Person> create(@RequestBody Person person) throws SQLException {
        return this.personService.save(person)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new SQLException("Ошибка при сохранении данных"));
    }

    @PutMapping("/")
    public ResponseEntity<Person> update(@PathVariable Long id, @RequestBody Person person) {
        Optional<Person> updatedPerson = personService.update(person);
        return updatedPerson.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        this.personService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}

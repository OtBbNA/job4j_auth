package ru.job4j.auth.controller;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.job4j.auth.config.Operation;
import ru.job4j.auth.dto.PersonDTO;
import ru.job4j.auth.model.Person;
import ru.job4j.auth.service.CityService;
import ru.job4j.auth.service.PersonService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;

@RestController
@RequestMapping("/person")
@AllArgsConstructor
public class PersonController {

    private final PersonService personService;
    private final CityService cityService;
    private final BCryptPasswordEncoder encoder;
    private final ObjectMapper objectMapper;
    private static final Logger LOGGER = LoggerFactory.getLogger(PersonController.class.getSimpleName());

    @GetMapping("/all")
    public ResponseEntity<Collection<Person>> findAll() {
        var getPersons = personService.findAll();
        return ResponseEntity.ok().body(getPersons);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<Person> findById(@PathVariable int id) {
        return personService.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Account is not found. Please, check requisites."
                ));
    }

    @PostMapping("/sign-up")
    public ResponseEntity<Person> create(@Validated(Operation.OnCreate.class) @RequestBody PersonDTO person) {
        var setPerson = new Person(
                person.getId(),
                person.getLogin(),
                encoder.encode(person.getPassword()),
                cityService.findById(person.getCityId()).orElseThrow()
        );
        return this.personService.save(setPerson)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new RuntimeException("Error saving data"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@Validated(Operation.OnDelete.class) @PathVariable int id) {
        var validate = personService.findById(id);
        if (validate.isEmpty()) {
            throw new NullPointerException("User with such id does not exist or has already been deleted");
        }
        this.personService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/update/{id}")
    public ResponseEntity<Person> update(@PathVariable Integer id, @Validated(Operation.OnUpdate.class) @RequestBody PersonDTO person) {
        var setPerson = new Person(
                id,
                person.getLogin(),
                encoder.encode(person.getPassword()),
                cityService.findById(person.getCityId()).orElseThrow()
        );
        return personService.update(setPerson)
                .map(p -> ResponseEntity.ok().body(p))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Error while trying to update"
                ));
    }

    @ExceptionHandler(value = {RuntimeException.class, DataIntegrityViolationException.class, ConstraintViolationException.class})
    public void exceptionHandler(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(new HashMap<>() { {
            put("message", e.getMessage());
            put("type", e.getClass());
        }}));
        LOGGER.error(e.getLocalizedMessage());
    }
}

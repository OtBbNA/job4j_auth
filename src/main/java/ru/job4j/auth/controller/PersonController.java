package ru.job4j.auth.controller;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.job4j.auth.model.Person;
import ru.job4j.auth.service.PersonService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;

@RestController
@RequestMapping("/person")
@AllArgsConstructor
public class PersonController {

    private final PersonService personService;
    private final BCryptPasswordEncoder encoder;
    private final ObjectMapper objectMapper;
    private static final Logger LOGGER = LoggerFactory.getLogger(PersonController.class.getSimpleName());

    @GetMapping("/all")
    public Collection<Person> findAll() {
        return this.personService.findAll();
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
    public ResponseEntity<Person> create(@RequestBody Person person) {
        person.setPassword(encoder.encode(person.getPassword()));
        return this.personService.save(person)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new RuntimeException("Error saving data"));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Person> update(@PathVariable Integer id, @RequestBody Person person) {
        person.setId(id);
        Optional<Person> updatedPerson = personService.update(person);
        return updatedPerson.map(ResponseEntity::ok)
                .orElseThrow(() -> new RuntimeException("Error while trying to update"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        var validate = personService.findById(id);
        if (validate.isEmpty()) {
            throw new NullPointerException("User with such id does not exist or has already been deleted");
        }
        this.personService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @ExceptionHandler(value = {RuntimeException.class})
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

package ru.spring.auto.rest.docs.demo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.spring.auto.rest.docs.demo.dto.PostPeopleRequest;
import ru.spring.auto.rest.docs.demo.model.People;
import ru.spring.auto.rest.docs.demo.repository.PeopleRepository;

import javax.validation.Valid;
import java.util.List;

/**
 * Here some information about people controller
 */
@RestController
@RequiredArgsConstructor
@Validated
public class PeopleController {

    private final PeopleRepository peopleRepository;

    /**
     * Here some information about get peoples method
     */
    @GetMapping("/peoples")
    public @Valid List<People> getPeoples() {
        return peopleRepository.getPeoples();
    }

    /**
     * Here some information about add people method
     */
    @PostMapping("/peoples")
    public void addPeople(@RequestBody @Valid PostPeopleRequest postPeopleRequest) {
        peopleRepository.getPeoples().add(postPeopleRequest.getPeople());
    }
}

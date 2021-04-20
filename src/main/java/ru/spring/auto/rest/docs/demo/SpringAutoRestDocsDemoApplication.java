package ru.spring.auto.rest.docs.demo;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.spring.auto.rest.docs.demo.controller.PeopleController;
import ru.spring.auto.rest.docs.demo.dto.PostPeopleRequest;
import ru.spring.auto.rest.docs.demo.model.People;

import java.util.List;

@SpringBootApplication
@RequiredArgsConstructor
public class SpringAutoRestDocsDemoApplication {

    private final PeopleController peopleController;

    public static void main(String[] args) {
        SpringApplication.run(SpringAutoRestDocsDemoApplication.class, args);
    }

//    @Override
//    public void run(String... args) throws Exception {
//        People people = new People();
//        people.setSomeUsefulInfoList(List.of("", "   "));
//        System.out.println(people);
//        PostPeopleRequest request = new PostPeopleRequest();
//        request.setPeople(people);
//        peopleController.addPeople(request);
//        System.out.println(peopleController.getPeoples());
//    }
}

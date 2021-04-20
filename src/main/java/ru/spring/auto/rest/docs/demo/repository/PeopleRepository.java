package ru.spring.auto.rest.docs.demo.repository;

import org.springframework.stereotype.Repository;
import ru.spring.auto.rest.docs.demo.model.People;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Repository
public class PeopleRepository {

    private List<People> peoples;

    @PostConstruct
    public void init(){
        peoples = new ArrayList<>();
    }

    public List<People> getPeoples() {
        return peoples;
    }
}

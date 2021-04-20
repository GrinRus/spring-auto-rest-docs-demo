package ru.spring.auto.rest.docs.demo.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum SEX {

    MALE("MALE"), FEMALE("FEMALE");

    private final String value;
}


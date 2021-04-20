package ru.spring.auto.rest.docs.demo.model;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class People {

    /**
     * Here some information about field name
     */
    @NotNull
    private String name;
    /**
     * Here some information about field age
     */
    @Min(1)
    private int age;
    /**
     * Here some information about field sex
     */
    private SEX sex;
    /**
     * Here some information about field friends
     */
    private List<@Valid People> friends;
    /**
     * Here some information about field someUsefulInfoList
     */
    @Valid
    private List<@NotBlank @Size(max = 100) String> someUsefulInfoList;
}

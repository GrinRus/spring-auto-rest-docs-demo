package ru.spring.auto.rest.docs.demo.dto;

import lombok.Data;
import ru.spring.auto.rest.docs.demo.model.People;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * Here some information about PostPeopleRequest
 */
@Data
public class PostPeopleRequest {

    /**
     * Here some information about field someAnotherUsefulInformationList
     */
    @Valid
    private List<@NotBlank @Size(min = 1, max = 100) String> someAnotherUsefulInformationList;
    /**
     * Here some information about field people
     */
    @Valid
    private People people;
    /**
     * Here some information about field someUsefulInformation
     */
    @NotBlank
    @Size(min = 1, max = 100)
    private String someUsefulInformation;

}

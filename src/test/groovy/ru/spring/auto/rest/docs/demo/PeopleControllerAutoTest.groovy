package ru.spring.auto.rest.docs.demo

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.Test
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import ru.spring.auto.rest.docs.demo.dto.PostPeopleRequest
import ru.spring.auto.rest.docs.demo.model.People

import static org.mockito.Mockito.when
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class PeopleControllerAutoTest extends MockMvcDocumentSpec {

    ObjectMapper mapper = new ObjectMapper()

    @Test
    void getPeoples() throws Exception {
        def people = new People()
        def people2 = new People()
        people.setAge(1)
        people.setName("Name")
        people.setSomeUsefulInfoList(List.of("some"))
        people2.setAge(1)
        people2.setName("Name")
        people2.setSomeUsefulInfoList(List.of("some"))
        people.setFriends(List.of(people2))
        when(peopleRepository.getPeoples())
                .thenReturn(List.of(people))

        mockMvc.perform(
                RestDocumentationRequestBuilders.get("/peoples")
        ).andExpect(status().isOk())
                .andExpect(content().string(mapper.writeValueAsString(List.of(people))))
    }

    @Test
    void postPeople() throws Exception {
        def people = new People()
        def people2 = new People()
        def request = new PostPeopleRequest()
        people.setAge(1)
        people.setName("Name")
        people.setSomeUsefulInfoList(List.of("some"))
        people2.setAge(1)
        people2.setName("Name")
        people2.setSomeUsefulInfoList(List.of("some"))
        people.setFriends(List.of(people2))
        request.setPeople(people)
        request.setSomeUsefulInformation("some")
        request.setSomeAnotherUsefulInformationList(List.of("somesss"))

        mockMvc.perform(
                RestDocumentationRequestBuilders.post("/peoples").content(mapper.writeValueAsString(request)).contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk())
    }
}

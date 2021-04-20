package ru.spring.auto.rest.docs.demo

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.MockitoAnnotations
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.restdocs.JUnitRestDocumentation
import org.springframework.restdocs.constraints.ConstraintDescriptions
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler
import org.springframework.restdocs.payload.FieldDescriptor
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.util.StringUtils
import org.springframework.web.context.WebApplicationContext
import ru.spring.auto.rest.docs.demo.dto.PostPeopleRequest
import ru.spring.auto.rest.docs.demo.model.People
import spring.rest.docs.constraints.ResourceBundleInnerConstraintDescriptionResolver
import spring.rest.docs.constraints.ValidatorInnerConstraintResolver

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields
import static org.springframework.restdocs.snippet.Attributes.key
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@RunWith(SpringRunner.class)
@SpringBootTest
class PeopleControllerTest {

    @Autowired
    private WebApplicationContext context

    @Autowired
    private ObjectMapper objectMapper

    private RestDocumentationResultHandler documentationHandler

    protected MockMvc mockMvc

    @Rule
    public final JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation()

    @Before
    void setUp() throws Exception {
        MockitoAnnotations.initMocks(this)
        this.documentationHandler = document("{method-name}",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()))

        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .alwaysDo(this.documentationHandler)
                .apply(documentationConfiguration(restDocumentation))
                .build()
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

        ConstrainedFields fields = new ConstrainedFields(PostPeopleRequest.class)

        mockMvc.perform(
                RestDocumentationRequestBuilders.post("/peoples").content(objectMapper.writeValueAsString(request)).contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk())
                .andDo(documentationHandler.document(
                        requestFields(
                                fields.withPath("someAnotherUsefulInformationList").description("The someAnotherUsefulInformationList of the input"),
                                fields.withPath("people.name").description("The people.name of the input"),
                                fields.withPath("people.age").description("The people.age of the input"),
                                fields.withPath("people.sex").description("The people.sex of the input"),
                                fields.withPath("people.friends[].name").description("The people.friends of the input"),
                                fields.withPath("people.friends[].age").description("The people.friends of the input"),
                                fields.withPath("people.friends[].sex").description("The people.friends of the input"),
                                fields.withPath("people.friends[].friends").description("The people.friends of the input"),
                                fields.withPath("people.friends[].someUsefulInfoList").description("The people.friends of the input"),
                                fields.withPath("people.someUsefulInfoList").description("The people.someUsefulInfoList of the input"),
                                fields.withPath("someUsefulInformation").description("The someUsefulInformation of the input")
                        )))
    }

    private static class ConstrainedFields {

        private final ConstraintDescriptions constraintDescriptions

        ConstrainedFields(Class<?> input) {
            this.constraintDescriptions = new ConstraintDescriptions(
                    input,
                    new ValidatorInnerConstraintResolver(),
                    new ResourceBundleInnerConstraintDescriptionResolver()
            )
        }

        private FieldDescriptor withPath(String path) {
            return fieldWithPath(path).attributes(key("constraints").value(StringUtils
                    .collectionToDelimitedString(this.constraintDescriptions
                            .descriptionsForProperty(path), ". ")))
        }
    }
}

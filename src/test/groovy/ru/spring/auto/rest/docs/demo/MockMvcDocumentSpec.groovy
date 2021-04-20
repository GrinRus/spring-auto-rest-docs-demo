package ru.spring.auto.rest.docs.demo


import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.runner.RunWith
import org.mockito.MockitoAnnotations
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.restdocs.JUnitRestDocumentation
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler
import org.springframework.restdocs.operation.preprocess.OperationResponsePreprocessor
import org.springframework.restdocs.snippet.Snippet
import org.springframework.restdocs.templates.TemplateFormats
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter
import ru.spring.auto.rest.docs.demo.repository.PeopleRepository
import capital.scalable.restdocs.constraints.CustomDynamicResourceBundleConstraintDescriptionResolver

import static capital.scalable.restdocs.AutoDocumentation.*
import static capital.scalable.restdocs.response.ResponseModifyingPreprocessors.limitJsonArrayLength
import static capital.scalable.restdocs.response.ResponseModifyingPreprocessors.replaceBinaryContent
import static org.springframework.restdocs.cli.CliDocumentation.curlRequest
import static org.springframework.restdocs.http.HttpDocumentation.httpRequest
import static org.springframework.restdocs.http.HttpDocumentation.httpResponse
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint
import static capital.scalable.restdocs.jackson.JacksonPreparingResultHandler.prepareCustomJackson

@RunWith(SpringRunner.class)
@SpringBootTest
@Ignore
class MockMvcDocumentSpec {

    private static final String DEFAULT_AUTHORIZATION = "Resource is public."

    @Autowired
    private WebApplicationContext context

    @Autowired
    private ObjectMapper objectMapper

    @Autowired
    private RequestMappingHandlerAdapter requestMappingHandlerAdapter

    protected MockMvc mockMvc

    @MockBean
    PeopleRepository peopleRepository

    @Rule
    public final JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation()

    @Autowired
    RequestMappingHandlerAdapter requestMappingHandlerAdapter

    @Before
    void setUp() throws Exception {
        MockitoAnnotations.initMocks(this)
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .alwaysDo(prepareCustomJackson(objectMapper, new CustomDynamicResourceBundleConstraintDescriptionResolver()))
                .alwaysDo(commonDocumentation())
                .apply(documentationConfiguration(restDocumentation)
                        .uris()
                        .withScheme("http")
                        .withHost("localhost")
                        .withPort(8080)
                        .and()
                        .snippets()
                        .withTemplateFormat(TemplateFormats.asciidoctor())
                        .withDefaults(curlRequest(), httpRequest(), httpResponse(),
                                requestFields(), responseFields(), pathParameters(),
                                requestParameters(), description(), methodAndPath(),
                                section(), links(), embedded(), authorization(DEFAULT_AUTHORIZATION),
                                modelAttribute(requestMappingHandlerAdapter.getArgumentResolvers())))
                .build()
    }

    protected RestDocumentationResultHandler commonDocumentation(Snippet... snippets) {
        return document("rest-auto-documentation/{class-name}/{method-name}", commonResponsePreprocessor(), snippets)
    }

    protected OperationResponsePreprocessor commonResponsePreprocessor() {
        return preprocessResponse(replaceBinaryContent(), limitJsonArrayLength(objectMapper),
                prettyPrint())
    }
}

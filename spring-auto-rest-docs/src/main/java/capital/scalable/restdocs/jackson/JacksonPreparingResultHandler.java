package capital.scalable.restdocs.jackson;

import capital.scalable.restdocs.constraints.DynamicResourceBundleConstraintDescriptionResolver;
import capital.scalable.restdocs.i18n.SnippetTranslationManager;
import capital.scalable.restdocs.i18n.SnippetTranslationResolver;
import capital.scalable.restdocs.javadoc.JavadocReaderImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.restdocs.constraints.ConstraintDescriptionResolver;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultHandler;
import org.springframework.web.method.HandlerMethod;
import capital.scalable.restdocs.constraints.ConstraintReaderCustomImpl;

import static capital.scalable.restdocs.OperationAttributeHelper.*;

public class JacksonPreparingResultHandler implements ResultHandler {

    public static ResultHandler prepareCustomJackson(ObjectMapper objectMapper) {
        return new JacksonPreparingResultHandler(objectMapper, new TypeMapping(),
                SnippetTranslationManager.getDefaultResolver(),
                new DynamicResourceBundleConstraintDescriptionResolver());
    }

    public static ResultHandler prepareCustomJackson(ObjectMapper objectMapper,
                                                     ConstraintDescriptionResolver constraintDescriptionResolver) {
        return new JacksonPreparingResultHandler(objectMapper, new TypeMapping(),
                SnippetTranslationManager.getDefaultResolver(),
                constraintDescriptionResolver);
    }

    private final ObjectMapper objectMapper;
    private final TypeMapping typeMapping;
    private final SnippetTranslationResolver translationResolver;
    private final ConstraintDescriptionResolver constraintDescriptionResolver;

    public JacksonPreparingResultHandler(ObjectMapper objectMapper, TypeMapping typeMapping,
                                         SnippetTranslationResolver translationResolver,
                                         ConstraintDescriptionResolver constraintDescriptionResolver) {
        this.objectMapper = new SardObjectMapper(objectMapper);
        this.typeMapping = typeMapping;
        this.translationResolver = translationResolver;
        this.constraintDescriptionResolver = constraintDescriptionResolver;
    }

    @Override
    public void handle(MvcResult result) throws Exception {
        // HandlerMethod is not present in case of invalid endpoint
        // or in case of static resource url
        if (result.getHandler() instanceof HandlerMethod) {
            setHandlerMethod(result.getRequest(), (HandlerMethod) result.getHandler());
        }
        setObjectMapper(result.getRequest(), objectMapper);
        initRequestPattern(result.getRequest());
        setJavadocReader(result.getRequest(), JavadocReaderImpl.createWithSystemProperty());
        setConstraintReader(result.getRequest(),
                ConstraintReaderCustomImpl.create(objectMapper, translationResolver, constraintDescriptionResolver));
        setTypeMapping(result.getRequest(), typeMapping);
    }
}

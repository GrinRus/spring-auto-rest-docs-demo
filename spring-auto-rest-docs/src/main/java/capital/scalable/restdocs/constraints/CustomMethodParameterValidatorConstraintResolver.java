package capital.scalable.restdocs.constraints;

import org.springframework.restdocs.constraints.Constraint;
import spring.rest.docs.constraints.ValidatorInnerConstraintResolver;

import java.util.List;

public class CustomMethodParameterValidatorConstraintResolver extends MethodParameterValidatorConstraintResolver {

    private final ValidatorInnerConstraintResolver validatorInnerConstraintResolver;

    public CustomMethodParameterValidatorConstraintResolver() {
        this.validatorInnerConstraintResolver = new ValidatorInnerConstraintResolver();
    }

    @Override
    public List<Constraint> resolveForProperty(String property, Class<?> clazz) {
        return validatorInnerConstraintResolver.resolveForProperty(property, clazz);
    }
}

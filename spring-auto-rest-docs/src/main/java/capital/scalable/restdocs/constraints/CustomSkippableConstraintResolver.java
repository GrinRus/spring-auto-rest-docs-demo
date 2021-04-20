package capital.scalable.restdocs.constraints;

import org.springframework.core.MethodParameter;
import org.springframework.restdocs.constraints.Constraint;

import java.util.*;
import java.util.stream.Collectors;

import static capital.scalable.restdocs.constraints.SkippableConstraintResolver.MANDATORY_VALUE_ANNOTATIONS;

public class CustomSkippableConstraintResolver implements MethodParameterConstraintResolver {

    private final MethodParameterConstraintResolver delegate;
    private final GroupDescriptionResolver descriptionResolver;
    private final SkippableConstraintResolver skippableConstraintResolver;

    public CustomSkippableConstraintResolver(MethodParameterConstraintResolver delegate,
                                             GroupDescriptionResolver descriptionResolver) {
        this.delegate = delegate;
        this.descriptionResolver = descriptionResolver;
        this.skippableConstraintResolver = new SkippableConstraintResolver(delegate, descriptionResolver);
    }

    private boolean isSkippable(Constraint constraint) {
        return MANDATORY_VALUE_ANNOTATIONS.contains(constraint.getName());
    }

    private boolean isInner(Constraint constraint) {
        Object isInner = constraint.getConfiguration().get("isInner");
        return Objects.nonNull(isInner) && isInner.equals(true);
    }

    @Override
    public List<Constraint> resolveForProperty(String property, Class<?> clazz) {
        List<Constraint> result = new ArrayList<>();
        List<Constraint> constraints = delegate.resolveForProperty(property, clazz);
        result.addAll(constraints.stream().filter(c -> !isSkippable(c) && !isInner(c)).collect(Collectors.toList()));
        result.addAll(constraints.stream().filter(this::isInner).collect(Collectors.toList()));
        return result;
    }

    @Override
    public List<Constraint> resolveForParameter(MethodParameter parameter) {
        return skippableConstraintResolver.resolveForParameter(parameter);
    }

    public List<String> getOptionalMessages(String property, Class<?> clazz) {
        List<String> result = new ArrayList<>();
        List<Constraint> constraints = delegate.resolveForProperty(property, clazz);
        String defaultOptional = null;

        for (Constraint constraint : constraints) {
            if (isSkippable(constraint) && !isInner(constraint)) {
                List<Class<?>> groups = getGroups(constraint);

                if (groups.isEmpty()) {
                    defaultOptional = "false";
                } else {
                    for (Class group : groups) {
                        result.add(mandatoryForGroup(group));
                    }
                }
            }
        }

        Collections.sort(result);
        if (defaultOptional != null) {
            result.add(0, defaultOptional);
        }
        return result;
    }

    private List<Class<?>> getGroups(Constraint constraint) {
        return descriptionResolver.getGroups(constraint);
    }

    private String mandatoryForGroup(Class group) {
        return descriptionResolver.resolveGroupDescription(group, "false");
    }
}

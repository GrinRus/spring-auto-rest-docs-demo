/*
 * Copyright 2014-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package spring.rest.docs.constraints;

import org.springframework.restdocs.constraints.Constraint;
import org.springframework.restdocs.constraints.ConstraintResolver;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.NotNull;
import javax.validation.metadata.BeanDescriptor;
import javax.validation.metadata.ConstraintDescriptor;
import javax.validation.metadata.PropertyDescriptor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A {@link ConstraintResolver} that uses a Bean Validation {@link Validator} to resolve constraints and one level inner constraints. The
 * name of the constraint is the fully-qualified class name of the constraint annotation. For example, a {@link NotNull} constraint will be
 * named {@code javax.validation.constraints.NotNull}.
 *
 * @author Andy Wilkinson
 * @author Grigoriy Ryabov
 */
public class ValidatorInnerConstraintResolver implements ConstraintResolver {

    private final Validator validator;

    /**
     * Creates a new {@code ValidatorConstraintResolver} that will use a {@link Validator} in its default configuration to resolve
     * constraints.
     *
     * @see Validation#buildDefaultValidatorFactory()
     * @see ValidatorFactory#getValidator()
     */
    public ValidatorInnerConstraintResolver() {
        this(Validation.buildDefaultValidatorFactory().getValidator());
    }

    /**
     * Creates a new {@code ValidatorConstraintResolver} that will use the given {@code Validator} to resolve constraints.
     *
     * @param validator the validator
     */
    public ValidatorInnerConstraintResolver(Validator validator) {
        this.validator = validator;
    }

    @Override
    public List<Constraint> resolveForProperty(String property, Class<?> clazz) {
        List<Constraint> constraints = new ArrayList<>();
        BeanDescriptor beanDescriptor = this.validator.getConstraintsForClass(clazz);
        PropertyDescriptor propertyDescriptor = beanDescriptor.getConstraintsForProperty(property);
        if (propertyDescriptor != null) {
            for (ConstraintDescriptor<?> constraintDescriptor : propertyDescriptor.getConstraintDescriptors()) {
                constraints.add(new Constraint(constraintDescriptor.getAnnotation().annotationType().getName(),
                        constraintDescriptor.getAttributes()));
            }
            constraints.addAll(getInnerConstraints(propertyDescriptor));
        }
        return constraints;
    }

    private List<Constraint> getInnerConstraints(PropertyDescriptor propertyDescriptor) {
        List<Constraint> constraints = new ArrayList<>();
        List<ConstraintDescriptor<?>> constraintDescriptorList = propertyDescriptor
                .getConstrainedContainerElementTypes().stream()
                .flatMap((containerElementType) -> containerElementType.getConstraintDescriptors().stream())
                .collect(Collectors.toList());
        for (ConstraintDescriptor<?> constraintDescriptor : constraintDescriptorList) {
            Map<String, Object> constraintAttributes = new HashMap<>(constraintDescriptor.getAttributes());
            constraintAttributes.put("isInner", true);
            Constraint constraint = new Constraint(constraintDescriptor.getAnnotation().annotationType().getName(),
                    constraintAttributes);
            constraints.add(constraint);
        }
        return constraints;
    }

}

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

import org.assertj.core.api.Condition;
import org.assertj.core.description.TextDescription;
import org.hibernate.validator.constraints.CompositionType;
import org.hibernate.validator.constraints.ConstraintComposition;
import org.junit.Test;
import org.springframework.restdocs.constraints.Constraint;
import org.springframework.restdocs.constraints.ValidatorConstraintResolver;
import spring.rest.docs.constraints.ValidatorInnerConstraintResolver;

import javax.validation.Payload;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;
import java.lang.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link ValidatorConstraintResolver}.
 *
 * @author Andy Wilkinson
 */
public class ValidatorInnerConstraintResolverTests {

	private final ValidatorInnerConstraintResolver resolver = new ValidatorInnerConstraintResolver();

	@Test
	public void singleFieldConstraint() {
		List<Constraint> constraints = this.resolver.resolveForProperty("single", ConstrainedFields.class);
		assertThat(constraints).hasSize(1);
		assertThat(constraints.get(0).getName()).isEqualTo(NotNull.class.getName());
	}

	@Test
	public void multipleFieldConstraints() {
		List<Constraint> constraints = this.resolver.resolveForProperty("multiple", ConstrainedFields.class);
		assertThat(constraints).hasSize(2);
		assertThat(constraints.get(0)).is(constraint(NotNull.class));
		assertThat(constraints.get(1)).is(constraint(Size.class).config("min", 8).config("max", 16));
	}

	@Test
	public void noFieldConstraints() {
		List<Constraint> constraints = this.resolver.resolveForProperty("none", ConstrainedFields.class);
		assertThat(constraints).hasSize(0);
	}

	@Test
	public void compositeConstraint() {
		List<Constraint> constraints = this.resolver.resolveForProperty("composite", ConstrainedFields.class);
		assertThat(constraints).hasSize(1);
	}

	@Test
	public void innerConstraint() {
		List<Constraint> constraints = this.resolver.resolveForProperty("inner", ConstrainedFields.class);
		assertThat(constraints).hasSize(2);
		assertThat(constraints.get(0)).is(constraint(NotBlank.class).config("isInner", true));
		assertThat(constraints.get(1))
				.is(constraint(Size.class).config("min", 8).config("max", 16).config("isInner", true));
	}

	private ConstraintCondition constraint(final Class<? extends Annotation> annotation) {
		return new ConstraintCondition(annotation);
	}

	private static class ConstrainedFields {

		@NotNull
		private String single;

		@NotNull
		@Size(min = 8, max = 16)
		private String multiple;

		@SuppressWarnings("unused")
		private String none;

		@CompositeConstraint
		private String composite;

		List<@NotBlank @Size(min = 8, max = 16) String> inner;

	}

	@ConstraintComposition(CompositionType.OR)
	@Null
	@NotBlank
	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	@javax.validation.Constraint(validatedBy = {})
	private @interface CompositeConstraint {

		String message() default "Must be null or not blank";

		Class<?>[] groups() default {};

		Class<? extends Payload>[] payload() default {};

	}

	private static final class ConstraintCondition extends Condition<Constraint> {

		private final Class<?> annotation;

		private final Map<String, Object> configuration = new HashMap<>();

		private ConstraintCondition(Class<?> annotation) {
			this.annotation = annotation;
			as(new TextDescription("Constraint named %s with configuration %s", this.annotation, this.configuration));
		}

		private ConstraintCondition config(String key, Object value) {
			this.configuration.put(key, value);
			return this;
		}

		@Override
		public boolean matches(Constraint constraint) {
			if (!constraint.getName().equals(this.annotation.getName())) {
				return false;
			}
			for (Entry<String, Object> entry : this.configuration.entrySet()) {
				if (!constraint.getConfiguration().get(entry.getKey()).equals(entry.getValue())) {
					return false;
				}
			}
			return true;
		}

	}

}

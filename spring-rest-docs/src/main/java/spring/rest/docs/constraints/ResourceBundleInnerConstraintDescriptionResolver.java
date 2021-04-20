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

import org.hibernate.validator.constraints.*;
import org.springframework.restdocs.constraints.Constraint;
import org.springframework.restdocs.constraints.ConstraintDescriptionResolver;
import org.springframework.restdocs.constraints.ResourceBundleConstraintDescriptionResolver;
import org.springframework.util.PropertyPlaceholderHelper;
import org.springframework.util.PropertyPlaceholderHelper.PlaceholderResolver;
import org.springframework.util.StringUtils;

import javax.validation.constraints.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * A {@link ConstraintDescriptionResolver} that resolves constraint descriptions (with
 * inner prefix if constraint is inner) from a {@link ResourceBundle}. The resource
 * bundle's keys are the name of the constraint with {@code .description} appended. For
 * example, the key for the constraint named {@code javax.validation.constraints.NotNull}
 * is {@code javax.validation.constraints.NotNull.description}.
 * <p>
 * Default descriptions are provided for Bean Validation 2.0's constraints:
 *
 * <ul>
 * <li>{@link AssertFalse}
 * <li>{@link AssertTrue}
 * <li>{@link DecimalMax}
 * <li>{@link DecimalMin}
 * <li>{@link Digits}
 * <li>{@link Email}
 * <li>{@link Future}
 * <li>{@link FutureOrPresent}
 * <li>{@link Max}
 * <li>{@link Min}
 * <li>{@link Negative}
 * <li>{@link NegativeOrZero}
 * <li>{@link NotBlank}
 * <li>{@link NotEmpty}
 * <li>{@link NotNull}
 * <li>{@link Null}
 * <li>{@link Past}
 * <li>{@link PastOrPresent}
 * <li>{@link Pattern}
 * <li>{@link Positive}
 * <li>{@link PositiveOrZero}
 * <li>{@link Size}
 * </ul>
 *
 * <p>
 * Default descriptions are also provided for Hibernate Validator's constraints:
 *
 * <ul>
 * <li>{@link CodePointLength}
 * <li>{@link CreditCardNumber}
 * <li>{@link Currency}
 * <li>{@link EAN}
 * <li>{@link org.hibernate.validator.constraints.Email}
 * <li>{@link Length}
 * <li>{@link LuhnCheck}
 * <li>{@link Mod10Check}
 * <li>{@link Mod11Check}
 * <li>{@link org.hibernate.validator.constraints.NotBlank}
 * <li>{@link org.hibernate.validator.constraints.NotEmpty}
 * <li>{@link Range}
 * <li>{@link SafeHtml}
 * <li>{@link URL}
 * </ul>
 * @author Andy Wilkinson
 * @author Grigoriy Ryabov
 */
public class ResourceBundleInnerConstraintDescriptionResolver implements ConstraintDescriptionResolver {

	private final PropertyPlaceholderHelper propertyPlaceholderHelper = new PropertyPlaceholderHelper("${", "}");

	private final ResourceBundle defaultDescriptions;

	private final ResourceBundle userDescriptions;

	/**
	 * Creates a new {@code ResourceBundleConstraintDescriptionResolver} that will resolve
	 * descriptions by looking them up in a resource bundle with the base name
	 * {@code org.springframework.restdocs.constraints.ConstraintDescriptions} in the
	 * default locale loaded using the thread context class loader.
	 */
	public ResourceBundleInnerConstraintDescriptionResolver() {
		this(getBundle("ConstraintDescriptions"));
	}

	/**
	 * Creates a new {@code ResourceBundleConstraintDescriptionResolver} that will resolve
	 * descriptions by looking them up in the given {@code resourceBundle}.
	 * @param resourceBundle the resource bundle
	 */
	public ResourceBundleInnerConstraintDescriptionResolver(ResourceBundle resourceBundle) {
		this.defaultDescriptions = getBundle("DefaultConstraintDescriptions");
		this.userDescriptions = resourceBundle;
	}

	private static ResourceBundle getBundle(String name) {
		try {
			return ResourceBundle.getBundle(
					ResourceBundleConstraintDescriptionResolver.class.getPackage().getName() + "." + name,
					Locale.getDefault(), Thread.currentThread().getContextClassLoader());
		}
		catch (MissingResourceException ex) {
			return null;
		}
	}

	@Override
	public String resolveDescription(Constraint constraint) {
		String key = constraint.getName() + ".description";
		return this.propertyPlaceholderHelper.replacePlaceholders(getInnerDescription(constraint) + getDescription(key),
				new ConstraintPlaceholderResolver(constraint));
	}

	private String getInnerDescription(Constraint constraint) {
		Object isInner = constraint.getConfiguration().get("isInner");
		if (Objects.isNull(isInner) || isInner.equals(false)) {
			return "";
		}
		String key = "isInner.description";
		try {
			if (this.userDescriptions != null) {
				return this.userDescriptions.getString(key);
			}
		}
		catch (MissingResourceException ex) {
			// Continue and return default description, if available
		}
		return this.defaultDescriptions.getString(key);
	}

	private String getDescription(String key) {
		try {
			if (this.userDescriptions != null) {
				return this.userDescriptions.getString(key);
			}
		}
		catch (MissingResourceException ex) {
			// Continue and return default description, if available
		}
		return this.defaultDescriptions.getString(key);
	}

	private static final class ConstraintPlaceholderResolver implements PlaceholderResolver {

		private final Constraint constraint;

		private ConstraintPlaceholderResolver(Constraint constraint) {
			this.constraint = constraint;
		}

		@Override
		public String resolvePlaceholder(String placeholderName) {
			Object replacement = this.constraint.getConfiguration().get(placeholderName);
			if (replacement == null) {
				return null;
			}
			if (replacement.getClass().isArray()) {
				return StringUtils.arrayToDelimitedString((Object[]) replacement, ", ");
			}
			return replacement.toString();
		}

	}

}

package capital.scalable.restdocs.constraints;

import org.slf4j.Logger;
import org.springframework.restdocs.constraints.Constraint;
import org.springframework.restdocs.constraints.ConstraintDescriptionResolver;
import org.springframework.util.PropertyPlaceholderHelper;
import org.springframework.util.StringUtils;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Objects;
import java.util.ResourceBundle;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.slf4j.LoggerFactory.getLogger;

public class CustomDynamicResourceBundleConstraintDescriptionResolver implements ConstraintDescriptionResolver {
    private static final Logger log = getLogger(CustomDynamicResourceBundleConstraintDescriptionResolver.class);

    private final PropertyPlaceholderHelper propertyPlaceholderHelper = new PropertyPlaceholderHelper("${", "}");

    private final ResourceBundle defaultDescriptions;

    private final ResourceBundle userDescriptions;

    public CustomDynamicResourceBundleConstraintDescriptionResolver() {
        this(getBundle("ConstraintDescriptions"));
    }

    public CustomDynamicResourceBundleConstraintDescriptionResolver(ResourceBundle resourceBundle) {
        this.defaultDescriptions = getBundle("DefaultConstraintDescriptions");
        this.userDescriptions = resourceBundle;
    }

    private static ResourceBundle getBundle(String name) {
        try {
            return ResourceBundle.getBundle(
                    org.springframework.restdocs.constraints.ResourceBundleConstraintDescriptionResolver
                            .class.getPackage().getName() + "." + name,
                    Locale.getDefault(), Thread.currentThread().getContextClassLoader());
        } catch (MissingResourceException ex) {
            return null;
        }
    }

    /**
     * First resolves based on overridden message on constraint itself, then falls back to resource bundle resolution
     */
    @Override
    public String resolveDescription(Constraint constraint) {
        String message = (String) constraint.getConfiguration().get("message");
        if (isNotBlank(message) && !message.startsWith("{")) {
            return this.propertyPlaceholderHelper.replacePlaceholders(getInnerDescription(constraint) + message,
                    new CustomDynamicResourceBundleConstraintDescriptionResolver.ConstraintPlaceholderResolver(constraint));
        }

        try {
            String key = constraint.getName() + ".description";
            return this.propertyPlaceholderHelper.replacePlaceholders(getInnerDescription(constraint) + getDescription(key),
                    new CustomDynamicResourceBundleConstraintDescriptionResolver.ConstraintPlaceholderResolver(constraint));
        } catch (MissingResourceException e) {
            log.debug("No description found for constraint {}: {}.", constraint.getName(), e.getMessage());
            return "";
        }

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
        } catch (MissingResourceException ex) {
            // Continue and return default description, if available
        }
        return this.defaultDescriptions.getString(key);
    }

    private String getDescription(String key) {
        try {
            if (this.userDescriptions != null) {
                return this.userDescriptions.getString(key);
            }
        } catch (MissingResourceException ex) {
            // Continue and return default description, if available
        }
        return this.defaultDescriptions.getString(key);
    }

    private static final class ConstraintPlaceholderResolver implements PropertyPlaceholderHelper.PlaceholderResolver {

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

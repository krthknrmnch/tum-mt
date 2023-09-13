package com.tum.in.cm.platformservice.component.validation;

import com.tum.in.cm.platformservice.component.validation.validator.HttpMethodSubsetValidator;
import com.tum.in.cm.platformservice.util.Constants;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = HttpMethodSubsetValidator.class)
public @interface HttpMethodSubset {
    Constants.HttpMethod[] anyOf();

    String message() default "must be any of {anyOf}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

package com.tum.in.cm.platformservice.component.validation.validator;

import com.tum.in.cm.platformservice.component.validation.HttpMethodSubset;
import com.tum.in.cm.platformservice.util.Constants;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;

public class HttpMethodSubsetValidator implements ConstraintValidator<HttpMethodSubset, Constants.HttpMethod> {
    private Constants.HttpMethod[] subset;

    @Override
    public void initialize(HttpMethodSubset constraint) {
        this.subset = constraint.anyOf();
    }

    @Override
    public boolean isValid(Constants.HttpMethod value, ConstraintValidatorContext context) {
        return value == null || Arrays.asList(subset).contains(value);
    }
}

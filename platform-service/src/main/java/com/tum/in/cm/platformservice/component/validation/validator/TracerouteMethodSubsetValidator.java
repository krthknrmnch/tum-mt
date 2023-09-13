package com.tum.in.cm.platformservice.component.validation.validator;

import com.tum.in.cm.platformservice.component.validation.TracerouteMethodSubset;
import com.tum.in.cm.platformservice.util.Constants;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;

public class TracerouteMethodSubsetValidator implements ConstraintValidator<TracerouteMethodSubset, Constants.TracerouteMethod> {
    private Constants.TracerouteMethod[] subset;

    @Override
    public void initialize(TracerouteMethodSubset constraint) {
        this.subset = constraint.anyOf();
    }

    @Override
    public boolean isValid(Constants.TracerouteMethod value, ConstraintValidatorContext context) {
        return value == null || Arrays.asList(subset).contains(value);
    }
}

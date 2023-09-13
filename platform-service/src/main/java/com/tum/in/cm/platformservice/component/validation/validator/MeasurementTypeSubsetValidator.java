package com.tum.in.cm.platformservice.component.validation.validator;

import com.tum.in.cm.platformservice.component.validation.MeasurementTypeSubset;
import com.tum.in.cm.platformservice.util.Constants;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;

public class MeasurementTypeSubsetValidator implements ConstraintValidator<MeasurementTypeSubset, Constants.MeasurementType> {
    private Constants.MeasurementType[] subset;

    @Override
    public void initialize(MeasurementTypeSubset constraint) {
        this.subset = constraint.anyOf();
    }

    @Override
    public boolean isValid(Constants.MeasurementType value, ConstraintValidatorContext context) {
        return value == null || Arrays.asList(subset).contains(value);
    }
}

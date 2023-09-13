package com.tum.in.cm.platformservice.component.validation.validator;

import com.tum.in.cm.platformservice.component.validation.RegionSubset;
import com.tum.in.cm.platformservice.util.Constants;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;

public class RegionSubsetValidator implements ConstraintValidator<RegionSubset, Constants.Region> {
    private Constants.Region[] subset;

    @Override
    public void initialize(RegionSubset constraint) {
        this.subset = constraint.anyOf();
    }

    @Override
    public boolean isValid(Constants.Region value, ConstraintValidatorContext context) {
        return value == null || Arrays.asList(subset).contains(value);
    }
}

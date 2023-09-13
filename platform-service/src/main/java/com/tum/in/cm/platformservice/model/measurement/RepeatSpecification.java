package com.tum.in.cm.platformservice.model.measurement;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RepeatSpecification {
    @NotNull
    @Min(0)
    @Max(10)
    private int numberOfRepeats;
    @NotNull
    @Min(180)
    private int interval;
}

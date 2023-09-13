package com.tum.in.cm.platformservice.web.rest.dto.response;

import com.tum.in.cm.platformservice.model.measurement.Measurement;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MeasurementsResponseObject {
    private long count;
    private List<Measurement> measurementList;
    private int currentPage;
    private int totalPages;
}

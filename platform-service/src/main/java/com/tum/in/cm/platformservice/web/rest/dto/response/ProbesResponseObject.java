package com.tum.in.cm.platformservice.web.rest.dto.response;

import com.tum.in.cm.platformservice.model.probe.Probe;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProbesResponseObject {
    private long count;
    private List<Probe> probeList;
    private int currentPage;
    private int totalPages;
}

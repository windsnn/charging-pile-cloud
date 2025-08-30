package com.trick.backend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class FaultReportAddDTO {

    private Integer userId;
    private Integer pileId;
    private String faultType;
    private String description;
    private List<String> images;

}

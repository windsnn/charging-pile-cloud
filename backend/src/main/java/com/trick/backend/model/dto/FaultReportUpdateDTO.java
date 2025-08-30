package com.trick.backend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.annotations.Insert;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FaultReportUpdateDTO {

    private Integer id;
    private Integer status;
    private LocalDateTime updateTime;

}

package com.trick.user.model.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FaultReport {

    private Integer id;
    private Integer userId;
    private Integer pileId;
    private String faultType;
    private String description;
    private String images;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

}

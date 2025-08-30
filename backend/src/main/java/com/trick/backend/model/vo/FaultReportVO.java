package com.trick.backend.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FaultReportVO {

    private Integer id;
    private String faultType;
    private String description;
    private String imagesJson;//json的图片
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    private String pileNo;//充电桩编号
    private String nickname;//用户名
    private String phone;//用户手机号
    private List<String> images;

}

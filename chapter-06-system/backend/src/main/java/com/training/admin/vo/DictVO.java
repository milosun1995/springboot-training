package com.training.admin.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DictVO {
    private Long id;
    private String code;
    private String label;
    private String value;
    private String type;
    private Integer sort;
    private Integer status;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}



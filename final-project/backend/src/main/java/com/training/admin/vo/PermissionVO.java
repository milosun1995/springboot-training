package com.training.admin.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class PermissionVO {
    private Long id;
    private Long parentId;
    private String name;
    private String code;
    private String type;
    private String path;
    private String method;
    private Integer sort;
    private Integer status;
    private String description;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    private List<PermissionVO> children = new ArrayList<>();
}



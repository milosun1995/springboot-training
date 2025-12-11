package com.training.admin.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MenuUpdateDTO {
    // ID 从 URL 路径参数获取，不需要验证
    private Long id;

    private Long parentId;

    @Size(max = 50, message = "名称长度不能超过50")
    private String name;

    @Size(max = 100, message = "编码长度不能超过100")
    private String code;

    @Size(max = 255, message = "路径长度不能超过255")
    private String path;

    @Size(max = 255, message = "组件长度不能超过255")
    private String component;

    @Size(max = 100, message = "图标长度不能超过100")
    private String icon;

    private Integer sort;
    private Integer status;
}



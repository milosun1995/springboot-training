package com.training.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PermissionCreateDTO {
    private Long parentId;

    @NotBlank(message = "名称不能为空")
    @Size(max = 50, message = "名称长度不能超过50")
    private String name;

    @NotBlank(message = "编码不能为空")
    @Size(max = 100, message = "编码长度不能超过100")
    private String code;

    @Size(max = 20, message = "类型长度不能超过20")
    private String type; // menu/button/api

    @Size(max = 255, message = "路径长度不能超过255")
    private String path;

    @Size(max = 10, message = "方法长度不能超过10")
    private String method;

    @Size(max = 255, message = "描述长度不能超过255")
    private String description;

    private Integer sort = 0;
    private Integer status = 1;
}



package com.training.admin.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RoleUpdateDTO {
    // ID 从 URL 路径参数获取，不需要验证
    private Long id;

    @Size(max = 50, message = "角色名称不能超过50字符")
    private String name;

    @Size(max = 255, message = "描述不能超过255字符")
    private String description;

    private Integer status;
}



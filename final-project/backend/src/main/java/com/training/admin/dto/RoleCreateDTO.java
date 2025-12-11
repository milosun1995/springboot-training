package com.training.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RoleCreateDTO {
    @NotBlank(message = "角色名称不能为空")
    @Size(max = 50, message = "角色名称不能超过50字符")
    private String name;

    @NotBlank(message = "角色编码不能为空")
    @Size(max = 50, message = "角色编码不能超过50字符")
    private String code;

    @Size(max = 255, message = "描述不能超过255字符")
    private String description;

    private Integer status = 1;
}



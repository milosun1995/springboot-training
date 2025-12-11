package com.training.admin.dto;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class RoleQueryDTO {
    private String keyword;
    private Integer status;

    @Min(value = 0, message = "页码不能小于0")
    private Integer page = 0;

    @Min(value = 1, message = "每页数量至少为1")
    private Integer size = 10;
}



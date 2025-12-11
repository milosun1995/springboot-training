package com.training.admin.dto;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class PermissionQueryDTO {
    private String keyword;
    private Integer status;
}



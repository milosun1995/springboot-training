package com.training.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DictCreateDTO {
    @NotBlank(message = "编码不能为空")
    @Size(max = 100, message = "编码长度不能超过100")
    private String code;

    @NotBlank(message = "标签不能为空")
    @Size(max = 100, message = "标签长度不能超过100")
    private String label;

    @NotBlank(message = "值不能为空")
    @Size(max = 100, message = "值长度不能超过100")
    private String value;

    @Size(max = 100, message = "类型长度不能超过100")
    private String type;

    private Integer sort = 0;
    private Integer status = 1;

    @Size(max = 255, message = "备注长度不能超过255")
    private String remark;
}



package com.training.admin.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DictUpdateDTO {
    // ID 从 URL 路径参数获取，不需要验证
    private Long id;

    @Size(max = 100, message = "编码长度不能超过100")
    private String code;

    @Size(max = 100, message = "标签长度不能超过100")
    private String label;

    @Size(max = 100, message = "值长度不能超过100")
    private String value;

    @Size(max = 100, message = "类型长度不能超过100")
    private String type;

    private Integer sort;
    private Integer status;

    @Size(max = 255, message = "备注长度不能超过255")
    private String remark;
}



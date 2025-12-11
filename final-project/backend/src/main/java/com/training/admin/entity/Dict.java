package com.training.admin.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "sys_dict")
@Data
public class Dict {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100, unique = true)
    private String code;

    @Column(nullable = false, length = 100)
    private String label;

    @Column(name = "dict_value", nullable = false, length = 100)
    private String value;

    @Column(name = "dict_type", length = 100)
    private String type;

    @Column(columnDefinition = "int default 0")
    private Integer sort;

    @Column(columnDefinition = "integer default 1")
    private Integer status; // 0-禁用 1-启用

    @Column(length = 255)
    private String remark;

    @Column(name = "create_time")
    private LocalDateTime createTime;

    @Column(name = "update_time")
    private LocalDateTime updateTime;
}



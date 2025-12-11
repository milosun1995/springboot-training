package com.training.admin.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "sys_permission")
@Data
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "parent_id")
    private Long parentId;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, unique = true, length = 100)
    private String code;

    /**
     * 类型：menu / button / api
     */
    @Column(length = 20)
    private String type;

    @Column(length = 255)
    private String path;

    @Column(length = 10)
    private String method;

    @Column(columnDefinition = "int default 1")
    private Integer status; // 0-禁用 1-启用

    @Column(columnDefinition = "int default 0")
    private Integer sort;

    @Column(length = 255)
    private String description;

    @Column(name = "create_time")
    private LocalDateTime createTime;

    @Column(name = "update_time")
    private LocalDateTime updateTime;
}



package com.training.admin.vo;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;

import java.util.List;

/**
 * 用户个人信息 VO
 * 用于缓存时，需要添加类型信息以便反序列化
 */
@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@class")
public class ProfileVO {
    private String username;
    private String nickname;
    private List<String> roles;
    private List<String> permissions;
    private List<MenuVO> menus;
}



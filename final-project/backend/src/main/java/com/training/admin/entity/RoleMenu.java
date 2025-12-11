package com.training.admin.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.io.Serializable;

@Entity
@Table(name = "sys_role_menu")
@Data
@IdClass(RoleMenu.RoleMenuId.class)
public class RoleMenu {
    @Id
    @Column(name = "role_id", nullable = false)
    private Long roleId;

    @Id
    @Column(name = "menu_id", nullable = false)
    private Long menuId;

    @Data
    public static class RoleMenuId implements Serializable {
        private Long roleId;
        private Long menuId;
    }
}



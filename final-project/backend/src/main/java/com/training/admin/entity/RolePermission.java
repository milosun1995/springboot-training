package com.training.admin.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.io.Serializable;

@Entity
@Table(name = "sys_role_permission")
@Data
@IdClass(RolePermission.RolePermissionId.class)
public class RolePermission {
    @Id
    @Column(name = "role_id", nullable = false)
    private Long roleId;

    @Id
    @Column(name = "permission_id", nullable = false)
    private Long permissionId;

    @Data
    public static class RolePermissionId implements Serializable {
        private Long roleId;
        private Long permissionId;
    }
}



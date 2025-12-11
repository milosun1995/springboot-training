package com.training.admin.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.io.Serializable;

@Entity
@Table(name = "sys_user_role")
@Data
@IdClass(UserRole.UserRoleId.class)
public class UserRole {
    @Id
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Id
    @Column(name = "role_id", nullable = false)
    private Long roleId;

    @Data
    public static class UserRoleId implements Serializable {
        private Long userId;
        private Long roleId;
    }
}



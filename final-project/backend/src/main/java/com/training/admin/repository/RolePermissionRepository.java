package com.training.admin.repository;

import com.training.admin.entity.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RolePermissionRepository extends JpaRepository<RolePermission, Long> {
    List<RolePermission> findByRoleIdIn(List<Long> roleIds);
    List<RolePermission> findByPermissionId(Long permissionId);
}



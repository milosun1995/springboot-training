package com.training.admin.repository;

import com.training.admin.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PermissionRepository extends JpaRepository<Permission, Long> {
    boolean existsByCode(String code);
    List<Permission> findByStatus(Integer status);
    List<Permission> findByParentId(Long parentId);
}



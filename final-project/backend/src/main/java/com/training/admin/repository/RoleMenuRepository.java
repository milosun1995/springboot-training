package com.training.admin.repository;

import com.training.admin.entity.RoleMenu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoleMenuRepository extends JpaRepository<RoleMenu, Long> {
    List<RoleMenu> findByRoleIdIn(List<Long> roleIds);
}



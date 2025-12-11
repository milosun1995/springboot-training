package com.training.admin.repository;

import com.training.admin.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuRepository extends JpaRepository<Menu, Long> {
    boolean existsByCode(String code);
}



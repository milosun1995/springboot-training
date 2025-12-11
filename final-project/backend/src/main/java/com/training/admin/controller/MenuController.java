package com.training.admin.controller;

import com.training.admin.common.Result;
import com.training.admin.dto.MenuCreateDTO;
import com.training.admin.dto.MenuQueryDTO;
import com.training.admin.dto.MenuUpdateDTO;
import com.training.admin.service.MenuService;
import com.training.admin.vo.MenuVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜单管理 Controller
 * 权限控制：sys:menu:list, sys:menu:add, sys:menu:edit, sys:menu:del, sys:menu:status
 */
@RestController
@RequestMapping("/api/menus")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    @GetMapping("/tree")
    @PreAuthorize("hasAuthority('sys:menu:list')")  // ⭐ API 权限保护
    public Result<List<MenuVO>> tree(@Validated MenuQueryDTO query) {
        return Result.success(menuService.tree(query));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('sys:menu:add')")  // ⭐ API 权限保护
    public Result<MenuVO> create(@Validated @RequestBody MenuCreateDTO dto) {
        return Result.success("创建成功", menuService.create(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('sys:menu:edit')")  // ⭐ API 权限保护
    public Result<MenuVO> update(@PathVariable Long id, @Validated @RequestBody MenuUpdateDTO dto) {
        dto.setId(id);
        return Result.success("更新成功", menuService.update(dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('sys:menu:del')")  // ⭐ API 权限保护
    public Result<Void> delete(@PathVariable Long id) {
        menuService.delete(id);
        return Result.success("删除成功", null);
    }

    @PostMapping("/{id}/toggle")
    @PreAuthorize("hasAuthority('sys:menu:status')")  // ⭐ API 权限保护
    public Result<MenuVO> toggle(@PathVariable Long id) {
        return Result.success("状态已切换", menuService.toggleStatus(id));
    }
}



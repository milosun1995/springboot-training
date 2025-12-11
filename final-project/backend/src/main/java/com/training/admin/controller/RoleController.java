package com.training.admin.controller;

import com.training.admin.common.PermissionChangeNotification;
import com.training.admin.common.Result;
import com.training.admin.dto.RoleCreateDTO;
import com.training.admin.dto.RoleQueryDTO;
import com.training.admin.dto.RoleUpdateDTO;
import com.training.admin.service.PermissionCacheService;
import com.training.admin.service.RoleService;
import com.training.admin.vo.RoleVO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 角色管理 Controller
 * 权限控制：sys:role:list, sys:role:add, sys:role:edit, sys:role:del, sys:role:status, sys:role:perm, sys:role:menu
 */
@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;
    private final PermissionCacheService permissionCacheService;

    @GetMapping
    @PreAuthorize("hasAuthority('sys:role:list')")  // ⭐ API 权限保护
    public Result<Page<RoleVO>> page(@Validated RoleQueryDTO queryDTO) {
        return Result.success(roleService.page(queryDTO));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('sys:role:add')")  // ⭐ API 权限保护
    public Result<RoleVO> create(@Validated @RequestBody RoleCreateDTO dto) {
        return Result.success("创建成功", roleService.create(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('sys:role:edit')")  // ⭐ API 权限保护
    public Result<RoleVO> update(@PathVariable Long id, @Validated @RequestBody RoleUpdateDTO dto) {
        dto.setId(id);
        return Result.success("更新成功", roleService.update(dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('sys:role:del')")  // ⭐ API 权限保护
    public Result<Void> delete(@PathVariable Long id) {
        roleService.delete(id);
        return Result.success("删除成功", null);
    }

    @PostMapping("/{id}/toggle")
    @PreAuthorize("hasAuthority('sys:role:status')")  // ⭐ API 权限保护
    public Result<RoleVO> toggle(@PathVariable Long id) {
        return Result.success("状态已切换", roleService.toggleStatus(id));
    }

    @GetMapping("/{id}/permissions")
    @PreAuthorize("hasAuthority('sys:role:perm')")  // ⭐ API 权限保护 - 查询权限
    public Result<List<Long>> permissionIds(@PathVariable Long id) {
        return Result.success(roleService.permissionIds(id));
    }

    @PostMapping("/{id}/permissions")
    @PreAuthorize("hasAuthority('sys:role:perm')")  // ⭐ API 权限保护 - 分配权限
    public Result<Map<String, Object>> savePermissions(@PathVariable Long id, @RequestBody List<Long> permIds) {
        roleService.savePermissions(id, permIds);
        
        // 获取受影响用户并返回通知信息
        List<Long> affectedUserIds = permissionCacheService.getAffectedUserIds(id);
        PermissionChangeNotification notification = PermissionChangeNotification.rolePermissionChanged(id, affectedUserIds);
        
        Map<String, Object> result = new HashMap<>();
        result.put("message", "保存成功");
        result.put("notification", notification);
        
        return Result.success("保存成功", result);
    }

    @GetMapping("/{id}/menus")
    @PreAuthorize("hasAuthority('sys:role:menu')")  // ⭐ API 权限保护 - 查询菜单
    public Result<List<Long>> menuIds(@PathVariable Long id) {
        return Result.success(roleService.menuIds(id));
    }

    @PostMapping("/{id}/menus")
    @PreAuthorize("hasAuthority('sys:role:menu')")  // ⭐ API 权限保护 - 分配菜单
    public Result<Void> saveMenus(@PathVariable Long id, @RequestBody List<Long> menuIds) {
        roleService.saveMenus(id, menuIds);
        return Result.success("保存成功", null);
    }
}



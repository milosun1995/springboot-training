package com.training.admin.controller;

import com.training.admin.common.PermissionChangeNotification;
import com.training.admin.common.Result;
import com.training.admin.dto.PermissionCreateDTO;
import com.training.admin.dto.PermissionQueryDTO;
import com.training.admin.dto.PermissionUpdateDTO;
import com.training.admin.service.PermissionService;
import com.training.admin.vo.PermissionVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 权限管理 Controller
 * 权限控制：sys:permission:list, sys:permission:add, sys:permission:edit, sys:permission:del, sys:permission:status
 */
@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    @GetMapping("/tree")
    @PreAuthorize("hasAuthority('sys:permission:list')")  // ⭐ API 权限保护
    public Result<List<PermissionVO>> tree(@Validated PermissionQueryDTO query) {
        return Result.success(permissionService.tree(query));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('sys:permission:add')")  // ⭐ API 权限保护
    public Result<PermissionVO> create(@Validated @RequestBody PermissionCreateDTO dto) {
        return Result.success("创建成功", permissionService.create(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('sys:permission:edit')")  // ⭐ API 权限保护
    public Result<Map<String, Object>> update(@PathVariable Long id, @Validated @RequestBody PermissionUpdateDTO dto) {
        dto.setId(id);
        PermissionVO vo = permissionService.update(dto);
        
        // 返回权限变更通知
        PermissionChangeNotification notification = PermissionChangeNotification.permissionUpdated(id);
        
        Map<String, Object> result = new HashMap<>();
        result.put("permission", vo);
        result.put("notification", notification);
        
        return Result.success("更新成功", result);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('sys:permission:del')")  // ⭐ API 权限保护
    public Result<Void> delete(@PathVariable Long id) {
        permissionService.delete(id);
        return Result.success("删除成功", null);
    }

    @PostMapping("/{id}/toggle")
    @PreAuthorize("hasAuthority('sys:permission:status')")  // ⭐ API 权限保护
    public Result<PermissionVO> toggle(@PathVariable Long id) {
        return Result.success("状态已切换", permissionService.toggleStatus(id));
    }
}



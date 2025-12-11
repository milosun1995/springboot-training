package com.training.admin.controller;

import com.training.admin.common.Result;
import com.training.admin.dto.PermissionCreateDTO;
import com.training.admin.dto.PermissionQueryDTO;
import com.training.admin.dto.PermissionUpdateDTO;
import com.training.admin.service.PermissionService;
import com.training.admin.vo.PermissionVO;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    @GetMapping("/tree")
    public Result<List<PermissionVO>> tree(@Validated PermissionQueryDTO query) {
        return Result.success(permissionService.tree(query));
    }

    @PostMapping
    public Result<PermissionVO> create(@Validated @RequestBody PermissionCreateDTO dto) {
        return Result.success("创建成功", permissionService.create(dto));
    }

    @PutMapping("/{id}")
    public Result<PermissionVO> update(@PathVariable Long id, @Validated @RequestBody PermissionUpdateDTO dto) {
        dto.setId(id);
        return Result.success("更新成功", permissionService.update(dto));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        permissionService.delete(id);
        return Result.success("删除成功", null);
    }

    @PostMapping("/{id}/toggle")
    public Result<PermissionVO> toggle(@PathVariable Long id) {
        return Result.success("状态已切换", permissionService.toggleStatus(id));
    }
}



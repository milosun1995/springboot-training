package com.training.admin.controller;

import com.training.admin.common.Result;
import com.training.admin.dto.RoleCreateDTO;
import com.training.admin.dto.RoleQueryDTO;
import com.training.admin.dto.RoleUpdateDTO;
import com.training.admin.service.RoleService;
import com.training.admin.vo.RoleVO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @GetMapping
    public Result<Page<RoleVO>> page(@Validated RoleQueryDTO queryDTO) {
        return Result.success(roleService.page(queryDTO));
    }

    @PostMapping
    public Result<RoleVO> create(@Validated @RequestBody RoleCreateDTO dto) {
        return Result.success("创建成功", roleService.create(dto));
    }

    @PutMapping("/{id}")
    public Result<RoleVO> update(@PathVariable Long id, @Validated @RequestBody RoleUpdateDTO dto) {
        dto.setId(id);
        return Result.success("更新成功", roleService.update(dto));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        roleService.delete(id);
        return Result.success("删除成功", null);
    }

    @PostMapping("/{id}/toggle")
    public Result<RoleVO> toggle(@PathVariable Long id) {
        return Result.success("状态已切换", roleService.toggleStatus(id));
    }
}



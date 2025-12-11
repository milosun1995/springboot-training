package com.training.admin.controller;

import com.training.admin.common.Result;
import com.training.admin.dto.UserCreateDTO;
import com.training.admin.dto.UserQueryDTO;
import com.training.admin.dto.UserUpdateDTO;
import com.training.admin.service.UserService;
import com.training.admin.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 用户管理 Controller
 * 
 * 权限控制：
 * - 查询列表：sys:user:list
 * - 新增用户：sys:user:add
 * - 编辑用户：sys:user:edit
 * - 删除用户：sys:user:del
 * - 状态切换：sys:user:status
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasAuthority('sys:user:list')")  // ⭐ API 权限保护
    public Result<Page<UserVO>> pageUsers(@Validated UserQueryDTO queryDTO) {
        return Result.success(userService.pageUsers(queryDTO));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('sys:user:add')")  // ⭐ API 权限保护
    public Result<UserVO> create(@Validated @RequestBody UserCreateDTO dto) {
        return Result.success("创建成功", userService.create(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('sys:user:edit')")  // ⭐ API 权限保护
    public Result<UserVO> update(@PathVariable Long id, @Validated @RequestBody UserUpdateDTO dto) {
        dto.setId(id);
        return Result.success("更新成功", userService.update(dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('sys:user:del')")  // ⭐ API 权限保护
    public Result<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return Result.success("删除成功", null);
    }

    @PostMapping("/{id}/toggle")
    @PreAuthorize("hasAuthority('sys:user:status')")  // ⭐ API 权限保护
    public Result<UserVO> toggleStatus(@PathVariable Long id) {
        return Result.success("状态已切换", userService.toggleStatus(id));
    }
}


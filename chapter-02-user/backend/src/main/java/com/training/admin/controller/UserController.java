package com.training.admin.controller;

import com.training.admin.common.Result;
import com.training.admin.dto.UserCreateDTO;
import com.training.admin.dto.UserQueryDTO;
import com.training.admin.dto.UserUpdateDTO;
import com.training.admin.service.UserService;
import com.training.admin.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public Result<Page<UserVO>> pageUsers(@Validated UserQueryDTO queryDTO) {
        return Result.success(userService.pageUsers(queryDTO));
    }

    @PostMapping
    public Result<UserVO> create(@Validated @RequestBody UserCreateDTO dto) {
        return Result.success("创建成功", userService.create(dto));
    }

    @PutMapping("/{id}")
    public Result<UserVO> update(@PathVariable Long id, @Validated @RequestBody UserUpdateDTO dto) {
        dto.setId(id);
        return Result.success("更新成功", userService.update(dto));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return Result.success("删除成功", null);
    }

    @PostMapping("/{id}/toggle")
    public Result<UserVO> toggleStatus(@PathVariable Long id) {
        return Result.success("状态已切换", userService.toggleStatus(id));
    }
}


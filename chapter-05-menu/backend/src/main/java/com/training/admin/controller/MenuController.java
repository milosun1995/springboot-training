package com.training.admin.controller;

import com.training.admin.common.Result;
import com.training.admin.dto.MenuCreateDTO;
import com.training.admin.dto.MenuQueryDTO;
import com.training.admin.dto.MenuUpdateDTO;
import com.training.admin.service.MenuService;
import com.training.admin.vo.MenuVO;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/menus")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    @GetMapping("/tree")
    public Result<List<MenuVO>> tree(@Validated MenuQueryDTO query) {
        return Result.success(menuService.tree(query));
    }

    @PostMapping
    public Result<MenuVO> create(@Validated @RequestBody MenuCreateDTO dto) {
        return Result.success("创建成功", menuService.create(dto));
    }

    @PutMapping("/{id}")
    public Result<MenuVO> update(@PathVariable Long id, @Validated @RequestBody MenuUpdateDTO dto) {
        dto.setId(id);
        return Result.success("更新成功", menuService.update(dto));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        menuService.delete(id);
        return Result.success("删除成功", null);
    }

    @PostMapping("/{id}/toggle")
    public Result<MenuVO> toggle(@PathVariable Long id) {
        return Result.success("状态已切换", menuService.toggleStatus(id));
    }
}



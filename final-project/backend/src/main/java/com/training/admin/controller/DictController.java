package com.training.admin.controller;

import com.training.admin.common.Result;
import com.training.admin.dto.DictCreateDTO;
import com.training.admin.dto.DictQueryDTO;
import com.training.admin.dto.DictUpdateDTO;
import com.training.admin.service.DictService;
import com.training.admin.vo.DictVO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 字典管理 Controller
 * 权限控制：sys:dict:list, sys:dict:add, sys:dict:edit, sys:dict:del, sys:dict:status
 */
@RestController
@RequestMapping("/api/dicts")
@RequiredArgsConstructor
public class DictController {

    private final DictService dictService;

    @GetMapping
    @PreAuthorize("hasAuthority('sys:dict:list')")  // ⭐ API 权限保护
    public Result<Page<DictVO>> page(@Validated DictQueryDTO query) {
        return Result.success(dictService.page(query));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('sys:dict:add')")  // ⭐ API 权限保护
    public Result<DictVO> create(@Validated @RequestBody DictCreateDTO dto) {
        return Result.success("创建成功", dictService.create(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('sys:dict:edit')")  // ⭐ API 权限保护
    public Result<DictVO> update(@PathVariable Long id, @Validated @RequestBody DictUpdateDTO dto) {
        dto.setId(id);
        return Result.success("更新成功", dictService.update(dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('sys:dict:del')")  // ⭐ API 权限保护
    public Result<Void> delete(@PathVariable Long id) {
        dictService.delete(id);
        return Result.success("删除成功", null);
    }

    @PostMapping("/{id}/toggle")
    @PreAuthorize("hasAuthority('sys:dict:status')")  // ⭐ API 权限保护
    public Result<DictVO> toggle(@PathVariable Long id) {
        return Result.success("状态已切换", dictService.toggleStatus(id));
    }
}



package com.training.admin.service;

import com.training.admin.dto.PermissionCreateDTO;
import com.training.admin.dto.PermissionQueryDTO;
import com.training.admin.dto.PermissionUpdateDTO;
import com.training.admin.entity.Permission;
import com.training.admin.entity.RolePermission;
import com.training.admin.exception.BusinessException;
import com.training.admin.repository.PermissionRepository;
import com.training.admin.repository.RolePermissionRepository;
import com.training.admin.repository.UserRoleRepository;
import com.training.admin.service.PermissionCacheService;
import com.training.admin.vo.PermissionVO;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PermissionService {

    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final UserRoleRepository userRoleRepository;
    private final PermissionCacheService permissionCacheService;

    public List<PermissionVO> tree(PermissionQueryDTO queryDTO) {
        List<Permission> all = permissionRepository.findAll();
        List<Permission> filtered = all.stream()
                .filter(p -> queryDTO.getStatus() == null || Objects.equals(p.getStatus(), queryDTO.getStatus()))
                .filter(p -> {
                    if (!StringUtils.hasText(queryDTO.getKeyword())) return true;
                    String kw = queryDTO.getKeyword().trim().toLowerCase();
                    return (p.getName() != null && p.getName().toLowerCase().contains(kw))
                            || (p.getCode() != null && p.getCode().toLowerCase().contains(kw))
                            || (p.getPath() != null && p.getPath().toLowerCase().contains(kw));
                })
                .sorted(Comparator.comparing(Permission::getSort, Comparator.nullsLast(Integer::compareTo))
                        .thenComparing(Permission::getId))
                .collect(Collectors.toList());

        Map<Long, PermissionVO> voMap = new HashMap<>();
        for (Permission p : filtered) {
            voMap.put(p.getId(), toVO(p));
        }

        List<PermissionVO> roots = new ArrayList<>();
        for (Permission p : filtered) {
            PermissionVO vo = voMap.get(p.getId());
            if (p.getParentId() != null && voMap.containsKey(p.getParentId())) {
                voMap.get(p.getParentId()).getChildren().add(vo);
            } else {
                roots.add(vo);
            }
        }
        return roots;
    }

    public List<PermissionVO> treeByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        List<Permission> all = permissionRepository.findAllById(ids);
        // reuse tree logic with filter
        PermissionQueryDTO dummy = new PermissionQueryDTO();
        List<Permission> sorted = all.stream()
                .sorted(java.util.Comparator.comparing(Permission::getSort, java.util.Comparator.nullsLast(Integer::compareTo))
                        .thenComparing(Permission::getId))
                .toList();

        java.util.Map<Long, PermissionVO> voMap = new java.util.HashMap<>();
        for (Permission p : sorted) {
            voMap.put(p.getId(), toVO(p));
        }
        List<PermissionVO> roots = new java.util.ArrayList<>();
        for (Permission p : sorted) {
            PermissionVO vo = voMap.get(p.getId());
            if (p.getParentId() != null && voMap.containsKey(p.getParentId())) {
                voMap.get(p.getParentId()).getChildren().add(vo);
            } else {
                roots.add(vo);
            }
        }
        return roots;
    }

    /**
     * 创建权限（清除缓存）
     */
    @Transactional
    @CacheEvict(value = {"userPermissions", "rolePermissions"}, allEntries = true)
    public PermissionVO create(PermissionCreateDTO dto) {
        if (permissionRepository.existsByCode(dto.getCode())) {
            throw new BusinessException(400, "权限编码已存在");
        }
        Permission p = new Permission();
        p.setParentId(dto.getParentId());
        p.setName(dto.getName().trim());
        p.setCode(dto.getCode().trim());
        p.setType(dto.getType());
        p.setPath(dto.getPath());
        p.setMethod(dto.getMethod());
        p.setDescription(dto.getDescription());
        p.setSort(dto.getSort() == null ? 0 : dto.getSort());
        p.setStatus(dto.getStatus() == null ? 1 : dto.getStatus());
        p.setCreateTime(LocalDateTime.now());
        p.setUpdateTime(LocalDateTime.now());
        PermissionVO result = toVO(permissionRepository.save(p));
        
        // 清除所有用户个人信息缓存（因为权限变更会影响所有用户的 profile）
        permissionCacheService.evictAllUserProfiles();
        
        return result;
    }

    /**
     * 更新权限（清除缓存）
     * 优化：清除所有用户权限缓存和用户个人信息缓存
     */
    @Transactional
    @CacheEvict(value = {"userPermissions", "rolePermissions"}, allEntries = true)
    public PermissionVO update(PermissionUpdateDTO dto) {
        Permission p = permissionRepository.findById(dto.getId())
                .orElseThrow(() -> new BusinessException(404, "权限不存在"));
        if (dto.getCode() != null && !dto.getCode().equals(p.getCode()) && permissionRepository.existsByCode(dto.getCode())) {
            throw new BusinessException(400, "权限编码已存在");
        }
        if (dto.getParentId() != null) {
            p.setParentId(dto.getParentId());
        }
        if (dto.getName() != null) {
            p.setName(dto.getName());
        }
        if (dto.getCode() != null) {
            p.setCode(dto.getCode());
        }
        if (dto.getType() != null) {
            p.setType(dto.getType());
        }
        if (dto.getPath() != null) {
            p.setPath(dto.getPath());
        }
        if (dto.getMethod() != null) {
            p.setMethod(dto.getMethod());
        }
        if (dto.getDescription() != null) {
            p.setDescription(dto.getDescription());
        }
        if (dto.getSort() != null) {
            p.setSort(dto.getSort());
        }
        if (dto.getStatus() != null) {
            p.setStatus(dto.getStatus());
        }
        p.setUpdateTime(LocalDateTime.now());
        PermissionVO result = toVO(permissionRepository.save(p));
        
        // 清除所有用户个人信息缓存（因为权限变更会影响所有用户的 profile）
        permissionCacheService.evictAllUserProfiles();
        
        return result;
    }

    /**
     * 删除权限（带级联检查）
     * 
     * 检查项：
     * 1. 权限是否存在
     * 2. 是否被角色使用
     * 3. 是否有子权限
     */
    @Transactional
    @CacheEvict(value = {"userPermissions", "rolePermissions"}, allEntries = true)
    public void delete(Long id) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "权限不存在"));
        
        // 1. 检查是否被角色使用
        List<RolePermission> rolePerms = rolePermissionRepository.findByPermissionId(id);
        if (!rolePerms.isEmpty()) {
            // 获取使用该权限的角色名称
            Set<Long> roleIds = rolePerms.stream()
                    .map(RolePermission::getRoleId)
                    .collect(Collectors.toSet());
            throw new BusinessException(400, 
                    String.format("该权限正在被 %d 个角色使用，无法删除。请先解除角色权限关联。", roleIds.size()));
        }
        
        // 2. 检查是否有子权限
        List<Permission> children = permissionRepository.findByParentId(id);
        if (!children.isEmpty()) {
            throw new BusinessException(400, 
                    String.format("该权限下有 %d 个子权限，请先删除子权限。", children.size()));
        }
        
        // 3. 删除权限（清除相关缓存）
        permissionRepository.deleteById(id);
        
        // 清除所有用户个人信息缓存（因为权限变更会影响所有用户的 profile）
        permissionCacheService.evictAllUserProfiles();
    }

    /**
     * 切换权限状态（清除缓存）
     * 优化：清除所有用户权限缓存和用户个人信息缓存
     */
    @Transactional
    @CacheEvict(value = {"userPermissions", "rolePermissions"}, allEntries = true)
    public PermissionVO toggleStatus(Long id) {
        Permission p = permissionRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "权限不存在"));
        p.setStatus(p.getStatus() != null && p.getStatus() == 1 ? 0 : 1);
        p.setUpdateTime(LocalDateTime.now());
        PermissionVO result = toVO(permissionRepository.save(p));
        
        // 清除所有用户个人信息缓存（因为权限变更会影响所有用户的 profile）
        permissionCacheService.evictAllUserProfiles();
        
        return result;
    }

    private PermissionVO toVO(Permission p) {
        PermissionVO vo = new PermissionVO();
        vo.setId(p.getId());
        vo.setParentId(p.getParentId());
        vo.setName(p.getName());
        vo.setCode(p.getCode());
        vo.setType(p.getType());
        vo.setPath(p.getPath());
        vo.setMethod(p.getMethod());
        vo.setSort(p.getSort());
        vo.setStatus(p.getStatus());
        vo.setDescription(p.getDescription());
        vo.setCreateTime(p.getCreateTime());
        vo.setUpdateTime(p.getUpdateTime());
        return vo;
    }
}



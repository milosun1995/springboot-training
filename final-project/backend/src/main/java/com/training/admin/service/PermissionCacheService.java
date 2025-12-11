package com.training.admin.service;

import com.training.admin.entity.RolePermission;
import com.training.admin.entity.UserRole;
import com.training.admin.repository.RolePermissionRepository;
import com.training.admin.repository.UserRoleRepository;
import com.training.admin.service.PermissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 权限缓存服务
 * 负责权限缓存的查询和清除操作
 * 
 * 功能包括：
 * - 查询用户权限编码（带缓存）
 * - 清除权限缓存（当权限变更时）
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PermissionCacheService {

    private final UserRoleRepository userRoleRepository;
    private final RolePermissionRepository rolePermissionRepository;
    
    // 使用 @Lazy 延迟注入，打破与 PermissionService 的循环依赖
    // 注意：@Lazy 在构造函数注入时需要配合 @Autowired 使用
    @Lazy
    @Autowired
    private PermissionService permissionService;

    /**
     * 清除指定角色的所有用户权限缓存
     * 当角色权限变更时调用
     * 
     * @param roleId 角色ID
     */
    @CacheEvict(value = "userPermissions", allEntries = true)
    public void evictUserPermissionsByRole(Long roleId) {
        log.info("清除角色 {} 的所有用户权限缓存", roleId);
    }

    /**
     * 清除指定用户的所有权限缓存
     * 
     * @param userId 用户ID
     */
    @CacheEvict(value = "userPermissions", key = "#userId")
    public void evictUserPermissions(Long userId) {
        log.info("清除用户 {} 的权限缓存", userId);
    }

    /**
     * 清除所有用户权限缓存
     * 当权限数据变更时调用
     */
    @CacheEvict(value = "userPermissions", allEntries = true)
    public void evictAllUserPermissions() {
        log.info("清除所有用户权限缓存");
    }

    /**
     * 清除用户个人信息缓存
     * 当用户权限或角色变更时调用
     * 
     * @param username 用户名
     */
    @CacheEvict(value = "userProfile", key = "#username")
    public void evictUserProfile(String username) {
        log.info("清除用户 {} 的个人信息缓存", username);
    }

    /**
     * 清除所有用户个人信息缓存
     * 当权限数据发生重大变更时调用
     */
    @CacheEvict(value = "userProfile", allEntries = true)
    public void evictAllUserProfiles() {
        log.info("清除所有用户个人信息缓存");
    }

    /**
     * 获取受影响的用户ID列表（当角色权限变更时）
     * 
     * @param roleId 角色ID
     * @return 用户ID列表
     */
    public List<Long> getAffectedUserIds(Long roleId) {
        return userRoleRepository.findByRoleId(roleId).stream()
                .map(UserRole::getUserId)
                .distinct()
                .collect(Collectors.toList());
    }

    // ========== 查询操作（读缓存）==========

    /**
     * 获取用户的所有权限编码（用于生成 JWT）
     * 使用缓存提高性能
     * 
     * @param userId 用户ID
     * @return 权限编码列表
     */
    @Cacheable(value = "userPermissions", key = "#userId")
    public List<String> getUserPermissionCodes(Long userId) {
        // 1. 获取用户的所有角色
        List<UserRole> userRoles = userRoleRepository.findByUserId(userId);
        List<Long> roleIds = userRoles.stream().map(UserRole::getRoleId).toList();
        
        if (roleIds.isEmpty()) {
            return List.of();
        }
        
        // 2. 获取所有角色的权限ID
        List<RolePermission> rolePerms = rolePermissionRepository.findByRoleIdIn(roleIds);
        Set<Long> permIds = rolePerms.stream().map(RolePermission::getPermissionId).collect(Collectors.toSet());
        
        if (permIds.isEmpty()) {
            return List.of();
        }
        
        // 3. 获取权限编码列表（扁平化）
        return permissionService.treeByIds(permIds.stream().toList())
                .stream()
                .flatMap(p -> flattenPermCodes(p).stream())
                .collect(Collectors.toList());
    }

    /**
     * 扁平化权限编码（递归处理子权限）
     */
    private List<String> flattenPermCodes(com.training.admin.vo.PermissionVO p) {
        List<String> list = new java.util.ArrayList<>();
        list.add(p.getCode());
        if (p.getChildren() != null) {
            for (var c : p.getChildren()) {
                list.addAll(flattenPermCodes(c));
            }
        }
        return list;
    }
}


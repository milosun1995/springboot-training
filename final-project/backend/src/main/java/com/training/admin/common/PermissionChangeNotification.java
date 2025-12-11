package com.training.admin.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 权限变更通知
 * 用于通知前端权限已变更，需要重新加载权限信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PermissionChangeNotification {
    /**
     * 变更类型：ROLE_PERMISSION_CHANGED（角色权限变更）、PERMISSION_UPDATED（权限更新）
     */
    private String changeType;
    
    /**
     * 变更的角色ID（如果是角色权限变更）
     */
    private Long roleId;
    
    /**
     * 变更的权限ID（如果是权限更新）
     */
    private Long permissionId;
    
    /**
     * 受影响的用户ID列表
     */
    private List<Long> affectedUserIds;
    
    /**
     * 提示消息
     */
    private String message;
    
    /**
     * 是否需要重新登录
     */
    private boolean requireRelogin;
    
    public static PermissionChangeNotification rolePermissionChanged(Long roleId, List<Long> affectedUserIds) {
        return new PermissionChangeNotification(
                "ROLE_PERMISSION_CHANGED",
                roleId,
                null,
                affectedUserIds,
                String.format("角色权限已变更，%d 个用户受影响，请重新登录以获取最新权限", affectedUserIds.size()),
                true
        );
    }
    
    public static PermissionChangeNotification permissionUpdated(Long permissionId) {
        return new PermissionChangeNotification(
                "PERMISSION_UPDATED",
                null,
                permissionId,
                null,
                "权限信息已更新，请重新登录以获取最新权限",
                true
        );
    }
}


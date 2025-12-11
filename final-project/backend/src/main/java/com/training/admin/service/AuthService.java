package com.training.admin.service;

import com.training.admin.dto.LoginDTO;
import com.training.admin.entity.User;
import com.training.admin.entity.UserRole;
import com.training.admin.entity.RolePermission;
import com.training.admin.entity.RoleMenu;
import com.training.admin.exception.BusinessException;
import com.training.admin.repository.UserRepository;
import com.training.admin.repository.UserRoleRepository;
import com.training.admin.repository.RolePermissionRepository;
import com.training.admin.repository.RoleMenuRepository;
import com.training.admin.service.PermissionCacheService;
import com.training.admin.service.MenuService;
import com.training.admin.util.JwtUtil;
import com.training.admin.util.PasswordUtil;
import com.training.admin.vo.LoginVO;
import com.training.admin.vo.ProfileVO;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final RoleMenuRepository roleMenuRepository;
    private final PermissionCacheService permissionCacheService;
    private final MenuService menuService;
    private final PasswordUtil passwordUtil;
    private final JwtUtil jwtUtil;
    
    public LoginVO login(LoginDTO loginDTO) {
        // 查找用户
        User user = userRepository.findByUsername(loginDTO.getUsername())
                .orElseThrow(() -> new BusinessException(401, "用户名或密码错误"));
        
        // 验证密码
        if (!passwordUtil.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new BusinessException(401, "用户名或密码错误");
        }
        
        // 检查用户状态
        if (user.getStatus() == 0) {
            throw new BusinessException(403, "用户已被禁用");
        }
        
        // 获取用户权限编码列表（用于生成 JWT）⭐
        // 通过 PermissionCacheService 调用，确保缓存生效
        List<String> permissionCodes = permissionCacheService.getUserPermissionCodes(user.getId());
        
        // 生成 Token（包含权限列表）⭐
        String token = jwtUtil.generateToken(user.getUsername(), permissionCodes);
        
        // 返回结果
        LoginVO loginVO = new LoginVO();
        loginVO.setToken(token);
        loginVO.setUsername(user.getUsername());
        loginVO.setNickname(user.getNickname());
        
        return loginVO;
    }
    
    /**
     * 获取用户个人信息（包含角色、权限、菜单）
     * 使用缓存提高性能
     * 
     * 注意：此方法从 Controller 调用，会经过 AOP 代理，缓存会生效
     */
    @Cacheable(value = "userProfile", key = "#username")
    public ProfileVO profile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(404, "用户不存在"));

        List<UserRole> userRoles = userRoleRepository.findByUserId(user.getId());
        List<Long> roleIds = userRoles.stream().map(UserRole::getRoleId).toList();

        List<RolePermission> rolePerms = rolePermissionRepository.findByRoleIdIn(roleIds);
        Set<Long> permIds = rolePerms.stream().map(RolePermission::getPermissionId).collect(Collectors.toSet());

        List<RoleMenu> roleMenus = roleMenuRepository.findByRoleIdIn(roleIds);
        Set<Long> menuIds = roleMenus.stream().map(RoleMenu::getMenuId).collect(Collectors.toSet());

        ProfileVO vo = new ProfileVO();
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        vo.setRoles(roleIds.stream().map(String::valueOf).toList());
        
        // 使用 PermissionCacheService 获取权限编码（确保缓存生效）
        List<String> permissionCodes = permissionCacheService.getUserPermissionCodes(user.getId());
        vo.setPermissions(permissionCodes);
        
        vo.setMenus(menuService.treeByIds(menuIds.stream().toList()));
        return vo;
    }
}


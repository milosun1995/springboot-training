package com.training.admin.service;

import com.training.admin.dto.RoleCreateDTO;
import com.training.admin.dto.RoleQueryDTO;
import com.training.admin.dto.RoleUpdateDTO;
import com.training.admin.entity.Role;
import com.training.admin.entity.RoleMenu;
import com.training.admin.entity.RolePermission;
import com.training.admin.exception.BusinessException;
import com.training.admin.repository.RoleRepository;
import com.training.admin.repository.RoleMenuRepository;
import com.training.admin.repository.RolePermissionRepository;
import com.training.admin.repository.PermissionRepository;
import com.training.admin.repository.MenuRepository;
import com.training.admin.repository.UserRepository;
import com.training.admin.service.PermissionCacheService;
import com.training.admin.vo.RoleVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoleService {

    private final RoleRepository roleRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final RoleMenuRepository roleMenuRepository;
    private final PermissionRepository permissionRepository;
    private final MenuRepository menuRepository;
    private final PermissionCacheService permissionCacheService;
    private final UserRepository userRepository;

    public Page<RoleVO> page(RoleQueryDTO query) {
        Pageable pageable = PageRequest.of(query.getPage(), query.getSize(), Sort.by(Sort.Direction.DESC, "createTime"));
        Specification<Role> spec = (root, cq, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // 关键字查询：名称/编码/描述
            if (StringUtils.hasText(query.getKeyword())) {
                String like = "%" + query.getKeyword().trim() + "%";
                predicates.add(
                        cb.or(
                                cb.like(root.get("name"), like),
                                cb.like(root.get("code"), like),
                                cb.like(root.get("description"), like)
                        )
                );
            }
            
            // 状态查询
            if (query.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), query.getStatus()));
            }
            
            // 组合所有条件（AND 连接）
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        return roleRepository.findAll(spec, pageable).map(this::toVO);
    }

    public RoleVO create(RoleCreateDTO dto) {
        if (roleRepository.existsByCode(dto.getCode())) {
            throw new BusinessException(400, "角色编码已存在");
        }
        if (roleRepository.existsByName(dto.getName())) {
            throw new BusinessException(400, "角色名称已存在");
        }
        Role role = new Role();
        role.setCode(dto.getCode().trim());
        role.setName(dto.getName().trim());
        role.setDescription(dto.getDescription());
        role.setStatus(dto.getStatus() == null ? 1 : dto.getStatus());
        role.setCreateTime(LocalDateTime.now());
        role.setUpdateTime(LocalDateTime.now());
        return toVO(roleRepository.save(role));
    }

    public RoleVO update(RoleUpdateDTO dto) {
        Role role = roleRepository.findById(dto.getId())
                .orElseThrow(() -> new BusinessException(404, "角色不存在"));
        if (dto.getName() != null) {
            role.setName(dto.getName());
        }
        if (dto.getDescription() != null) {
            role.setDescription(dto.getDescription());
        }
        if (dto.getStatus() != null) {
            role.setStatus(dto.getStatus());
        }
        role.setUpdateTime(LocalDateTime.now());
        return toVO(roleRepository.save(role));
    }

    public void delete(Long id) {
        if (!roleRepository.existsById(id)) {
            throw new BusinessException(404, "角色不存在");
        }
        roleRepository.deleteById(id);
    }

    public RoleVO toggleStatus(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "角色不存在"));
        role.setStatus(role.getStatus() != null && role.getStatus() == 1 ? 0 : 1);
        role.setUpdateTime(LocalDateTime.now());
        return toVO(roleRepository.save(role));
    }

    public List<Long> permissionIds(Long roleId) {
        return rolePermissionRepository.findByRoleIdIn(List.of(roleId))
                .stream()
                .map(RolePermission::getPermissionId)
                .collect(Collectors.toList());
    }

    /**
     * 保存角色权限（带缓存清除和变更通知）
     * 优化：只清除受影响用户的缓存，而不是清除所有缓存
     */
    @Transactional
    public void savePermissions(Long roleId, List<Long> permIds) {
        if (!roleRepository.existsById(roleId)) {
            throw new BusinessException(404, "角色不存在");
        }
        
        // 获取变更前受影响的用户（在删除旧权限之前）
        List<Long> affectedUserIds = permissionCacheService.getAffectedUserIds(roleId);
        
        // 删除旧权限
        rolePermissionRepository.deleteAll(rolePermissionRepository.findByRoleIdIn(List.of(roleId)));
        
        // 保存新权限
        if (permIds != null && !permIds.isEmpty()) {
            List<RolePermission> list = permIds.stream().map(pid -> {
                RolePermission rp = new RolePermission();
                rp.setRoleId(roleId);
                rp.setPermissionId(pid);
                return rp;
            }).toList();
            rolePermissionRepository.saveAll(list);
        }
        
        // 优化：只清除受影响用户的缓存，而不是清除所有缓存
        if (!affectedUserIds.isEmpty()) {
            log.info("角色 {} 的权限已变更，受影响用户数: {}", roleId, affectedUserIds.size());
            log.info("受影响用户ID: {}", affectedUserIds);
            
            // 逐个清除受影响用户的权限缓存和个人信息缓存
            for (Long userId : affectedUserIds) {
                // 清除用户权限缓存
                permissionCacheService.evictUserPermissions(userId);
                
                // 清除用户个人信息缓存（因为权限变更会影响 profile）
                userRepository.findById(userId).ifPresent(user -> {
                    permissionCacheService.evictUserProfile(user.getUsername());
                });
            }
        } else {
            log.info("角色 {} 的权限已变更，无受影响用户", roleId);
        }
    }

    public List<Long> menuIds(Long roleId) {
        return roleMenuRepository.findByRoleIdIn(List.of(roleId))
                .stream()
                .map(RoleMenu::getMenuId)
                .collect(Collectors.toList());
    }

    public void saveMenus(Long roleId, List<Long> menuIds) {
        if (!roleRepository.existsById(roleId)) {
            throw new BusinessException(404, "角色不存在");
        }
        roleMenuRepository.deleteAll(roleMenuRepository.findByRoleIdIn(List.of(roleId)));
        if (menuIds != null && !menuIds.isEmpty()) {
            List<RoleMenu> list = menuIds.stream().map(mid -> {
                RoleMenu rm = new RoleMenu();
                rm.setRoleId(roleId);
                rm.setMenuId(mid);
                return rm;
            }).toList();
            roleMenuRepository.saveAll(list);
        }
    }

    private RoleVO toVO(Role role) {
        RoleVO vo = new RoleVO();
        vo.setId(role.getId());
        vo.setName(role.getName());
        vo.setCode(role.getCode());
        vo.setDescription(role.getDescription());
        vo.setStatus(role.getStatus());
        vo.setCreateTime(role.getCreateTime());
        vo.setUpdateTime(role.getUpdateTime());
        return vo;
    }
}



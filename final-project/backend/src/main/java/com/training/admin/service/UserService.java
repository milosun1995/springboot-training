package com.training.admin.service;

import com.training.admin.dto.UserCreateDTO;
import com.training.admin.dto.UserQueryDTO;
import com.training.admin.dto.UserUpdateDTO;
import com.training.admin.entity.User;
import com.training.admin.exception.BusinessException;
import com.training.admin.repository.UserRepository;
import com.training.admin.repository.UserRoleRepository;
import com.training.admin.service.PermissionCacheService;
import com.training.admin.util.PasswordUtil;
import com.training.admin.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final PermissionCacheService permissionCacheService;
    private final PasswordUtil passwordUtil;

    public Page<UserVO> pageUsers(UserQueryDTO queryDTO) {
        Pageable pageable = PageRequest.of(queryDTO.getPage(), queryDTO.getSize(), Sort.by(Sort.Direction.DESC, "createTime"));
        Specification<User> specification = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // 关键字查询：用户名/昵称/邮箱
            if (StringUtils.hasText(queryDTO.getKeyword())) {
                String likeValue = "%" + queryDTO.getKeyword().trim() + "%";
                predicates.add(
                        cb.or(
                                cb.like(root.get("username"), likeValue),
                                cb.like(root.get("nickname"), likeValue),
                                cb.like(root.get("email"), likeValue)
                        )
                );
            }
            
            // 状态查询
            if (queryDTO.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), queryDTO.getStatus()));
            }
            
            // 组合所有条件（AND 连接）
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        return userRepository.findAll(specification, pageable).map(this::toVO);
    }

    public UserVO create(UserCreateDTO dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new BusinessException(400, "用户名已存在");
        }
        User user = new User();
        user.setUsername(dto.getUsername().trim());
        user.setPassword(passwordUtil.encode(dto.getPassword()));
        user.setNickname(dto.getNickname());
        user.setEmail(dto.getEmail());
        user.setStatus(dto.getStatus() == null ? 1 : dto.getStatus());
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        user = userRepository.save(user);
        return toVO(user);
    }

    public UserVO update(UserUpdateDTO dto) {
        User user = userRepository.findById(dto.getId())
                .orElseThrow(() -> new BusinessException(404, "用户不存在"));
        if (dto.getNickname() != null) {
            user.setNickname(dto.getNickname());
        }
        if (dto.getEmail() != null) {
            user.setEmail(dto.getEmail());
        }
        if (dto.getStatus() != null) {
            user.setStatus(dto.getStatus());
        }
        user.setUpdateTime(LocalDateTime.now());
        user = userRepository.save(user);
        
        // 清除用户个人信息缓存（因为用户信息变更会影响 profile）
        permissionCacheService.evictUserProfile(user.getUsername());
        
        return toVO(user);
    }

    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new BusinessException(404, "用户不存在");
        }
        userRepository.deleteById(id);
    }

    public UserVO toggleStatus(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "用户不存在"));
        user.setStatus(user.getStatus() != null && user.getStatus() == 1 ? 0 : 1);
        user.setUpdateTime(LocalDateTime.now());
        user = userRepository.save(user);
        
        // 清除用户权限缓存和个人信息缓存（因为用户状态变更会影响权限和 profile）
        permissionCacheService.evictUserPermissions(user.getId());
        permissionCacheService.evictUserProfile(user.getUsername());
        
        return toVO(user);
    }
    
    /**
     * 保存用户角色（如果将来需要此功能）
     * 当用户角色变更时，清除该用户的权限缓存和个人信息缓存
     * 
     * @param userId 用户ID
     * @param roleIds 角色ID列表
     */
    public void saveUserRoles(Long userId, List<Long> roleIds) {
        // 删除旧角色
        userRoleRepository.deleteAll(userRoleRepository.findByUserId(userId));
        
        // 保存新角色
        if (roleIds != null && !roleIds.isEmpty()) {
            List<com.training.admin.entity.UserRole> list = roleIds.stream().map(roleId -> {
                com.training.admin.entity.UserRole ur = new com.training.admin.entity.UserRole();
                ur.setUserId(userId);
                ur.setRoleId(roleId);
                return ur;
            }).toList();
            userRoleRepository.saveAll(list);
        }
        
        // 清除用户权限缓存和个人信息缓存
        permissionCacheService.evictUserPermissions(userId);
        userRepository.findById(userId).ifPresent(user -> {
            permissionCacheService.evictUserProfile(user.getUsername());
        });
    }

    private UserVO toVO(User user) {
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        vo.setEmail(user.getEmail());
        vo.setStatus(user.getStatus());
        vo.setCreateTime(user.getCreateTime());
        vo.setUpdateTime(user.getUpdateTime());
        return vo;
    }
}


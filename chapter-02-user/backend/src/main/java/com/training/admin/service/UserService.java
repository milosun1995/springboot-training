package com.training.admin.service;

import com.training.admin.dto.UserCreateDTO;
import com.training.admin.dto.UserQueryDTO;
import com.training.admin.dto.UserUpdateDTO;
import com.training.admin.entity.User;
import com.training.admin.exception.BusinessException;
import com.training.admin.repository.UserRepository;
import com.training.admin.util.PasswordUtil;
import com.training.admin.vo.UserVO;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordUtil passwordUtil;

    public Page<UserVO> pageUsers(UserQueryDTO queryDTO) {
        Pageable pageable = PageRequest.of(queryDTO.getPage(), queryDTO.getSize(), Sort.by(Sort.Direction.DESC, "createTime"));
        
        Specification<User> specification = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // 关键字查询：用户名/昵称/邮箱
            if (StringUtils.hasText(queryDTO.getKeyword())) {
                String likeValue = "%" + queryDTO.getKeyword().trim() + "%";
                Predicate usernameLike = cb.like(root.get("username"), likeValue);
                Predicate nicknameLike = cb.like(root.get("nickname"), likeValue);
                Predicate emailLike = cb.like(root.get("email"), likeValue);
                predicates.add(cb.or(usernameLike, nicknameLike, emailLike));
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
        return toVO(user);
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


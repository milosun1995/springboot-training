package com.training.admin.service;

import com.training.admin.dto.RoleCreateDTO;
import com.training.admin.dto.RoleQueryDTO;
import com.training.admin.dto.RoleUpdateDTO;
import com.training.admin.entity.Role;
import com.training.admin.exception.BusinessException;
import com.training.admin.repository.RoleRepository;
import com.training.admin.vo.RoleVO;
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
public class RoleService {

    private final RoleRepository roleRepository;

    public Page<RoleVO> page(RoleQueryDTO query) {
        Pageable pageable = PageRequest.of(query.getPage(), query.getSize(), Sort.by(Sort.Direction.DESC, "createTime"));
        
        Specification<Role> spec = (root, cq, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // 关键字查询：名称/编码/描述
            if (StringUtils.hasText(query.getKeyword())) {
                String like = "%" + query.getKeyword().trim() + "%";
                Predicate nameLike = cb.like(root.get("name"), like);
                Predicate codeLike = cb.like(root.get("code"), like);
                Predicate descLike = cb.like(root.get("description"), like);
                predicates.add(cb.or(nameLike, codeLike, descLike));
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



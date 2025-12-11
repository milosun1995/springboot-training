package com.training.admin.service;

import com.training.admin.dto.PermissionCreateDTO;
import com.training.admin.dto.PermissionQueryDTO;
import com.training.admin.dto.PermissionUpdateDTO;
import com.training.admin.entity.Permission;
import com.training.admin.exception.BusinessException;
import com.training.admin.repository.PermissionRepository;
import com.training.admin.vo.PermissionVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PermissionService {

    private final PermissionRepository permissionRepository;

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
        return toVO(permissionRepository.save(p));
    }

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
        return toVO(permissionRepository.save(p));
    }

    public void delete(Long id) {
        if (!permissionRepository.existsById(id)) {
            throw new BusinessException(404, "权限不存在");
        }
        // 简化：直接删除，不做级联校验
        permissionRepository.deleteById(id);
    }

    public PermissionVO toggleStatus(Long id) {
        Permission p = permissionRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "权限不存在"));
        p.setStatus(p.getStatus() != null && p.getStatus() == 1 ? 0 : 1);
        p.setUpdateTime(LocalDateTime.now());
        return toVO(permissionRepository.save(p));
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



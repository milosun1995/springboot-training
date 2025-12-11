package com.training.admin.service;

import com.training.admin.dto.MenuCreateDTO;
import com.training.admin.dto.MenuQueryDTO;
import com.training.admin.dto.MenuUpdateDTO;
import com.training.admin.entity.Menu;
import com.training.admin.exception.BusinessException;
import com.training.admin.repository.MenuRepository;
import com.training.admin.vo.MenuVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository menuRepository;

    public List<MenuVO> tree(MenuQueryDTO query) {
        List<Menu> all = menuRepository.findAll();
        List<Menu> filtered = all.stream()
                .filter(m -> query.getStatus() == null || Objects.equals(m.getStatus(), query.getStatus()))
                .filter(m -> {
                    if (!StringUtils.hasText(query.getKeyword())) return true;
                    String kw = query.getKeyword().trim().toLowerCase();
                    return (m.getName() != null && m.getName().toLowerCase().contains(kw))
                            || (m.getCode() != null && m.getCode().toLowerCase().contains(kw))
                            || (m.getPath() != null && m.getPath().toLowerCase().contains(kw));
                })
                .sorted(Comparator.comparing(Menu::getSort, Comparator.nullsLast(Integer::compareTo))
                        .thenComparing(Menu::getId))
                .collect(Collectors.toList());

        Map<Long, MenuVO> voMap = new HashMap<>();
        for (Menu m : filtered) {
            voMap.put(m.getId(), toVO(m));
        }

        List<MenuVO> roots = new ArrayList<>();
        for (Menu m : filtered) {
            MenuVO vo = voMap.get(m.getId());
            if (m.getParentId() != null && voMap.containsKey(m.getParentId())) {
                voMap.get(m.getParentId()).getChildren().add(vo);
            } else {
                roots.add(vo);
            }
        }
        return roots;
    }

    public MenuVO create(MenuCreateDTO dto) {
        if (menuRepository.existsByCode(dto.getCode())) {
            throw new BusinessException(400, "菜单编码已存在");
        }
        Menu m = new Menu();
        m.setParentId(dto.getParentId());
        m.setName(dto.getName().trim());
        m.setCode(dto.getCode().trim());
        m.setPath(dto.getPath());
        m.setComponent(dto.getComponent());
        m.setIcon(dto.getIcon());
        m.setSort(dto.getSort() == null ? 0 : dto.getSort());
        m.setStatus(dto.getStatus() == null ? 1 : dto.getStatus());
        m.setCreateTime(LocalDateTime.now());
        m.setUpdateTime(LocalDateTime.now());
        return toVO(menuRepository.save(m));
    }

    public MenuVO update(MenuUpdateDTO dto) {
        Menu m = menuRepository.findById(dto.getId())
                .orElseThrow(() -> new BusinessException(404, "菜单不存在"));
        if (dto.getCode() != null && !dto.getCode().equals(m.getCode()) && menuRepository.existsByCode(dto.getCode())) {
            throw new BusinessException(400, "菜单编码已存在");
        }
        if (dto.getParentId() != null) m.setParentId(dto.getParentId());
        if (dto.getName() != null) m.setName(dto.getName());
        if (dto.getCode() != null) m.setCode(dto.getCode());
        if (dto.getPath() != null) m.setPath(dto.getPath());
        if (dto.getComponent() != null) m.setComponent(dto.getComponent());
        if (dto.getIcon() != null) m.setIcon(dto.getIcon());
        if (dto.getSort() != null) m.setSort(dto.getSort());
        if (dto.getStatus() != null) m.setStatus(dto.getStatus());
        m.setUpdateTime(LocalDateTime.now());
        return toVO(menuRepository.save(m));
    }

    public void delete(Long id) {
        if (!menuRepository.existsById(id)) {
            throw new BusinessException(404, "菜单不存在");
        }
        menuRepository.deleteById(id);
    }

    public MenuVO toggleStatus(Long id) {
        Menu m = menuRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "菜单不存在"));
        m.setStatus(m.getStatus() != null && m.getStatus() == 1 ? 0 : 1);
        m.setUpdateTime(LocalDateTime.now());
        return toVO(menuRepository.save(m));
    }

    private MenuVO toVO(Menu m) {
        MenuVO vo = new MenuVO();
        vo.setId(m.getId());
        vo.setParentId(m.getParentId());
        vo.setName(m.getName());
        vo.setCode(m.getCode());
        vo.setPath(m.getPath());
        vo.setComponent(m.getComponent());
        vo.setIcon(m.getIcon());
        vo.setSort(m.getSort());
        vo.setStatus(m.getStatus());
        vo.setCreateTime(m.getCreateTime());
        vo.setUpdateTime(m.getUpdateTime());
        return vo;
    }
}



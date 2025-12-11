package com.training.admin.service;

import com.training.admin.dto.DictCreateDTO;
import com.training.admin.dto.DictQueryDTO;
import com.training.admin.dto.DictUpdateDTO;
import com.training.admin.entity.Dict;
import com.training.admin.exception.BusinessException;
import com.training.admin.repository.DictRepository;
import com.training.admin.vo.DictVO;
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
public class DictService {

    private final DictRepository dictRepository;

    public Page<DictVO> page(DictQueryDTO query) {
        Pageable pageable = PageRequest.of(query.getPage(), query.getSize(), Sort.by(Sort.Direction.ASC, "sort").and(Sort.by("id")));
        
        Specification<Dict> spec = (root, cq, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // 关键字查询：编码/标签/值/类型
            if (StringUtils.hasText(query.getKeyword())) {
                String like = "%" + query.getKeyword().trim() + "%";
                Predicate codeLike = cb.like(root.get("code"), like);
                Predicate labelLike = cb.like(root.get("label"), like);
                Predicate valueLike = cb.like(root.get("value"), like);
                Predicate typeLike = cb.like(root.get("type"), like);
                predicates.add(cb.or(codeLike, labelLike, valueLike, typeLike));
            }
            
            // 状态查询
            if (query.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), query.getStatus()));
            }
            
            // 组合所有条件（AND 连接）
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        
        return dictRepository.findAll(spec, pageable).map(this::toVO);
    }

    public DictVO create(DictCreateDTO dto) {
        if (dictRepository.existsByCode(dto.getCode())) {
            throw new BusinessException(400, "编码已存在");
        }
        Dict d = new Dict();
        d.setCode(dto.getCode().trim());
        d.setLabel(dto.getLabel().trim());
        d.setValue(dto.getValue().trim());
        d.setType(dto.getType());
        d.setSort(dto.getSort() == null ? 0 : dto.getSort());
        d.setStatus(dto.getStatus() == null ? 1 : dto.getStatus());
        d.setRemark(dto.getRemark());
        d.setCreateTime(LocalDateTime.now());
        d.setUpdateTime(LocalDateTime.now());
        return toVO(dictRepository.save(d));
    }

    public DictVO update(DictUpdateDTO dto) {
        Dict d = dictRepository.findById(dto.getId())
                .orElseThrow(() -> new BusinessException(404, "字典不存在"));
        if (dto.getCode() != null && !dto.getCode().equals(d.getCode()) && dictRepository.existsByCode(dto.getCode())) {
            throw new BusinessException(400, "编码已存在");
        }
        if (dto.getCode() != null) d.setCode(dto.getCode());
        if (dto.getLabel() != null) d.setLabel(dto.getLabel());
        if (dto.getValue() != null) d.setValue(dto.getValue());
        if (dto.getType() != null) d.setType(dto.getType());
        if (dto.getSort() != null) d.setSort(dto.getSort());
        if (dto.getStatus() != null) d.setStatus(dto.getStatus());
        if (dto.getRemark() != null) d.setRemark(dto.getRemark());
        d.setUpdateTime(LocalDateTime.now());
        return toVO(dictRepository.save(d));
    }

    public void delete(Long id) {
        if (!dictRepository.existsById(id)) {
            throw new BusinessException(404, "字典不存在");
        }
        dictRepository.deleteById(id);
    }

    public DictVO toggleStatus(Long id) {
        Dict d = dictRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "字典不存在"));
        d.setStatus(d.getStatus() != null && d.getStatus() == 1 ? 0 : 1);
        d.setUpdateTime(LocalDateTime.now());
        return toVO(dictRepository.save(d));
    }

    private DictVO toVO(Dict d) {
        DictVO vo = new DictVO();
        vo.setId(d.getId());
        vo.setCode(d.getCode());
        vo.setLabel(d.getLabel());
        vo.setValue(d.getValue());
        vo.setType(d.getType());
        vo.setSort(d.getSort());
        vo.setStatus(d.getStatus());
        vo.setRemark(d.getRemark());
        vo.setCreateTime(d.getCreateTime());
        vo.setUpdateTime(d.getUpdateTime());
        return vo;
    }
}



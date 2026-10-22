package com.maoyan.service;

import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.maoyan.constant.ShowTypeConstant;
import com.maoyan.entity.ShowType;
import com.maoyan.mapper.ShowTypeMapper;
import com.maoyan.vo.ShowTypeVO;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShowTypeService extends ServiceImpl<ShowTypeMapper, ShowType> {

    public List<ShowTypeVO> listAll() {
        return lambdaQuery().orderByAsc(ShowType::getId).list().stream().map(ShowTypeVO::of).toList();
    }

    public ShowTypeVO get(Long id) {
        ShowType showType = getById(id);
        if (showType == null) {
            throw new IllegalArgumentException(ShowTypeConstant.NOT_FOUND);
        }
        return ShowTypeVO.of(showType);
    }

    public ShowTypeVO create(String name) {
        ShowType showType = new ShowType();
        showType.setName(name);
        try {
            save(showType);
        } catch (DuplicateKeyException exception) {
            throw new IllegalArgumentException(ShowTypeConstant.NAME_EXISTS);
        }
        return ShowTypeVO.of(showType);
    }

    public ShowTypeVO update(Long id, String name) {
        if (getById(id) == null) {
            throw new IllegalArgumentException(ShowTypeConstant.NOT_FOUND);
        }
        ShowType showType = new ShowType();
        showType.setId(id);
        showType.setName(name);
        try {
            updateById(showType);
        } catch (DuplicateKeyException exception) {
            throw new IllegalArgumentException(ShowTypeConstant.NAME_EXISTS);
        }
        return get(id);
    }

    public void delete(Long id) {
        if (getById(id) == null) {
            throw new IllegalArgumentException(ShowTypeConstant.NOT_FOUND);
        }
        try {
            removeById(id);
        } catch (DataIntegrityViolationException exception) {
            throw new IllegalArgumentException(ShowTypeConstant.HAS_SHOW);
        }
    }
}

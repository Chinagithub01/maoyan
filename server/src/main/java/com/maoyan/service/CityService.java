package com.maoyan.service;

import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.maoyan.entity.City;
import com.maoyan.mapper.CityMapper;
import com.maoyan.vo.CityVO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CityService extends ServiceImpl<CityMapper, City> {
    public List<CityVO> listAll() {
        return lambdaQuery().orderByAsc(City::getId).list().stream()
                .map(city -> new CityVO(city.getId(), city.getName()))
                .toList();
    }
}

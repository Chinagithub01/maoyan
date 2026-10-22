package com.maoyan.service;

import com.maoyan.vo.ActivityTypeVO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TicketTypeService {
    private final ShowTypeService showTypeService;

    public TicketTypeService(ShowTypeService showTypeService) {
        this.showTypeService = showTypeService;
    }

    public List<ActivityTypeVO> list() {
        return showTypeService.listAll().stream()
                .map(type -> new ActivityTypeVO(type.getId(), type.getName()))
                .toList();
    }
}

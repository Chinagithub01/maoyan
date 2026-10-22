package com.maoyan.controller.user;

import com.maoyan.result.Result;
import com.maoyan.service.CityService;
import com.maoyan.vo.CityVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/city")
public class CityController {
    private final CityService cityService;

    public CityController(CityService cityService) {
        this.cityService = cityService;
    }

    @GetMapping
    public Result<List<CityVO>> list() {
        return Result.success(cityService.listAll());
    }
}

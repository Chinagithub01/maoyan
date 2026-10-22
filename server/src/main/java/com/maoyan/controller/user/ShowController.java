package com.maoyan.controller.user;

import com.maoyan.result.Result;
import com.maoyan.service.ShowService;
import com.maoyan.vo.HotShowVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/show")
public class ShowController {
    private final ShowService showService;

    public ShowController(ShowService showService) {
        this.showService = showService;
    }

    @GetMapping
    public Result<List<HotShowVO>> list() {
        return Result.success(showService.listHot());
    }

    @GetMapping("/hot")
    public Result<List<HotShowVO>> hot() {
        return Result.success(showService.listHot());
    }
}

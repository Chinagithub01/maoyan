package com.maoyan.controller.user;

import com.maoyan.result.Result;
import com.maoyan.service.TicketTypeService;
import com.maoyan.vo.ActivityTypeVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/show-type")
public class ShowTypeController {
    private final TicketTypeService ticketTypeService;

    public ShowTypeController(TicketTypeService ticketTypeService) {
        this.ticketTypeService = ticketTypeService;
    }

    @GetMapping
    public Result<List<ActivityTypeVO>> list() {
        return Result.success(ticketTypeService.list());
    }
}

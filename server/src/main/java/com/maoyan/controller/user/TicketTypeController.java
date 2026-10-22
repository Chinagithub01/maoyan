package com.maoyan.controller.user;

import com.maoyan.result.Result;
import com.maoyan.service.TicketTypeService;
import com.maoyan.vo.ActivityTypeVO;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ticket-type")
public class TicketTypeController {
    private final TicketTypeService ticketTypeService;

    public TicketTypeController(TicketTypeService ticketTypeService) {
        this.ticketTypeService = ticketTypeService;
    }

    @GetMapping
    public Result<List<ActivityTypeVO>> list() {
        return Result.success(ticketTypeService.list());
    }
}

package com.maoyan.controller.user;

import com.maoyan.dto.RefundDTO;
import com.maoyan.result.Result;
import com.maoyan.service.OrderService;
import com.maoyan.vo.OrderVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/ticket")
public class TicketController {
    private final OrderService orderService;

    public TicketController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public Result<List<OrderVO>> list(@RequestParam(required = false) Long userId) {
        try {
            return Result.success(orderService.listByUser(userId));
        } catch (IllegalArgumentException exception) {
            return Result.error(exception.getMessage());
        }
    }

    @PostMapping("/refund")
    public Result<Void> refund(@RequestBody RefundDTO form) {
        try {
            Long userId = form == null ? null : form.getUserId();
            String orderNo = form == null ? null : form.getOrderNo();
            orderService.refund(userId, orderNo);
            return Result.success();
        } catch (IllegalArgumentException exception) {
            return Result.error(exception.getMessage());
        }
    }
}

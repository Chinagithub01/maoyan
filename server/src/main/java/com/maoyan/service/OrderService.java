package com.maoyan.service;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.maoyan.constant.ShowConstant;
import com.maoyan.constant.TicketConstant;
import com.maoyan.entity.Seat;
import com.maoyan.entity.Ticket;
import com.maoyan.mapper.SeatMapper;
import com.maoyan.mapper.TicketMapper;
import com.maoyan.vo.OrderTicketVO;
import com.maoyan.vo.OrderVO;
import com.maoyan.vo.TicketLineVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrderService {
    private final TicketMapper ticketMapper;
    private final SeatMapper seatMapper;
    private final UserService userService;

    public OrderService(TicketMapper ticketMapper, SeatMapper seatMapper, UserService userService) {
        this.ticketMapper = ticketMapper;
        this.seatMapper = seatMapper;
        this.userService = userService;
    }

    public List<OrderVO> listByUser(Long userId) {
        requireUser(userId);
        Map<String, List<TicketLineVO>> groups = new LinkedHashMap<>();
        for (TicketLineVO line : ticketMapper.listByUser(userId)) {
            groups.computeIfAbsent(line.getOrderNo(), key -> new ArrayList<>()).add(line);
        }
        List<OrderVO> orders = new ArrayList<>();
        for (List<TicketLineVO> group : groups.values()) {
            orders.add(toOrder(group));
        }
        return orders;
    }

    @Transactional
    public void refund(Long userId, String orderNo) {
        requireUser(userId);
        if (orderNo == null || orderNo.isBlank()) {
            throw new IllegalArgumentException(TicketConstant.ORDER_NOT_FOUND);
        }
        List<TicketLineVO> lines = ticketMapper.listByUser(userId).stream()
                .filter(line -> orderNo.equals(line.getOrderNo()))
                .toList();
        if (lines.isEmpty()) {
            throw new IllegalArgumentException(TicketConstant.ORDER_NOT_FOUND);
        }
        if (!Integer.valueOf(ShowConstant.FLAG_YES).equals(lines.get(0).getRefundEnabled())) {
            throw new IllegalArgumentException(TicketConstant.REFUND_DISABLED);
        }
        boolean issued = lines.stream().anyMatch(line -> TicketConstant.ISSUED == line.getStatus());
        if (!issued) {
            throw new IllegalArgumentException(TicketConstant.CANNOT_REFUND);
        }
        ticketMapper.update(null, new LambdaUpdateWrapper<Ticket>()
                .eq(Ticket::getUserId, userId)
                .eq(Ticket::getOrderNo, orderNo)
                .eq(Ticket::getStatus, TicketConstant.ISSUED)
                .set(Ticket::getStatus, TicketConstant.REFUNDED));
        for (TicketLineVO line : lines) {
            if (line.getSeatId() != null && TicketConstant.ISSUED == line.getStatus()) {
                seatMapper.update(null, new LambdaUpdateWrapper<Seat>()
                        .eq(Seat::getId, line.getSeatId())
                        .set(Seat::getStatus, TicketConstant.SEAT_AVAILABLE));
            }
        }
    }

    private void requireUser(Long userId) {
        if (userId == null || userService.getById(userId) == null) {
            throw new IllegalArgumentException(TicketConstant.LOGIN_REQUIRED);
        }
    }

    private OrderVO toOrder(List<TicketLineVO> group) {
        TicketLineVO first = group.get(0);
        OrderVO order = new OrderVO();
        order.setOrderNo(first.getOrderNo());
        order.setShowId(first.getShowId());
        order.setShowName(first.getShowName());
        order.setVenueName(first.getVenueName());
        order.setShowTime(first.getShowTime());
        order.setPriceTierName(first.getPriceTierName());
        order.setQuantity(group.size());
        order.setRefundEnabled(first.getRefundEnabled());
        BigDecimal total = BigDecimal.ZERO;
        boolean sameStatus = true;
        List<OrderTicketVO> tickets = new ArrayList<>();
        for (TicketLineVO line : group) {
            if (line.getPrice() != null) {
                total = total.add(line.getPrice());
            }
            if (!line.getStatus().equals(first.getStatus())) {
                sameStatus = false;
            }
            OrderTicketVO ticket = new OrderTicketVO();
            ticket.setSeatNo(line.getSeatNo());
            ticket.setPrice(line.getPrice());
            ticket.setStatus(line.getStatus());
            tickets.add(ticket);
        }
        order.setTotalPrice(total);
        order.setStatus(sameStatus ? first.getStatus() : TicketConstant.ISSUED);
        order.setTickets(tickets);
        return order;
    }
}

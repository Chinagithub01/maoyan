package com.maoyan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.maoyan.entity.Ticket;
import com.maoyan.vo.TicketLineVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface TicketMapper extends BaseMapper<Ticket> {
    @Select("""
            SELECT t.id, t.order_no, t.show_id, s.name AS show_name, t.venue_name, t.show_time,
                   pt.name AS price_tier_name, t.seat_id, t.seat_no, t.price, t.status, s.refund_enabled
            FROM ticket t
            JOIN `show` s ON s.id = t.show_id
            JOIN price_tier pt ON pt.id = t.price_tier_id
            WHERE t.user_id = #{userId}
            ORDER BY t.id
            """)
    List<TicketLineVO> listByUser(@Param("userId") Long userId);
}

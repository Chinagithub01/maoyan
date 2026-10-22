package com.maoyan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.maoyan.entity.Show;
import com.maoyan.vo.HotShowVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface ShowMapper extends BaseMapper<Show> {
    @Select("""
            SELECT s.id, s.name, s.artist, s.poster, s.venue_name, s.city_id,
                   c.name AS city_name, s.show_type_id, st.name AS show_type_name,
                   COUNT(tk.id) AS sold_count
            FROM `show` s
            LEFT JOIN city c ON c.id = s.city_id
            LEFT JOIN show_type st ON st.id = s.show_type_id
            LEFT JOIN ticket tk ON tk.show_id = s.id AND tk.status = #{status}
            GROUP BY s.id, s.name, s.artist, s.poster, s.venue_name, s.city_id, c.name, s.show_type_id, st.name
            ORDER BY sold_count DESC, s.id ASC
            LIMIT #{limit}
            """)
    List<HotShowVO> listHot(@Param("status") int status, @Param("limit") int limit);
}

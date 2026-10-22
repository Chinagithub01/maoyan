package com.maoyan.service;

import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.maoyan.constant.ShowConstant;
import com.maoyan.constant.ShowTypeConstant;
import com.maoyan.constant.TicketConstant;
import com.maoyan.dto.ShowDTO;
import com.maoyan.entity.Show;
import com.maoyan.mapper.ShowMapper;
import com.maoyan.vo.HotShowVO;
import com.maoyan.vo.ShowVO;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShowService extends ServiceImpl<ShowMapper, Show> {
    private final ShowTypeService showTypeService;
    private final CityService cityService;

    public ShowService(ShowTypeService showTypeService, CityService cityService) {
        this.showTypeService = showTypeService;
        this.cityService = cityService;
    }

    public List<HotShowVO> listHot() {
        return baseMapper.listHot(TicketConstant.ISSUED, ShowConstant.HOT_LIMIT);
    }

    public List<ShowVO> listAll() {
        return lambdaQuery().orderByAsc(Show::getId).list().stream().map(ShowVO::of).toList();
    }

    public ShowVO get(Long id) {
        Show show = getById(id);
        if (show == null) {
            throw new IllegalArgumentException(ShowConstant.NOT_FOUND);
        }
        return ShowVO.of(show);
    }

    public ShowVO create(ShowDTO form) {
        requireReferences(form);
        Show show = toEntity(form);
        try {
            save(show);
        } catch (DataIntegrityViolationException exception) {
            throw new IllegalArgumentException(ShowConstant.REFERENCE_MISSING);
        }
        return ShowVO.of(show);
    }

    public ShowVO update(Long id, ShowDTO form) {
        if (getById(id) == null) {
            throw new IllegalArgumentException(ShowConstant.NOT_FOUND);
        }
        requireReferences(form);
        Show show = toEntity(form);
        try {
            lambdaUpdate()
                    .eq(Show::getId, id)
                    .set(Show::getShowTypeId, show.getShowTypeId())
                    .set(Show::getCityId, show.getCityId())
                    .set(Show::getName, show.getName())
                    .set(Show::getArtist, show.getArtist())
                    .set(Show::getPoster, show.getPoster())
                    .set(Show::getDuration, show.getDuration())
                    .set(Show::getIntro, show.getIntro())
                    .set(Show::getNotice, show.getNotice())
                    .set(Show::getVenueName, show.getVenueName())
                    .set(Show::getAddress, show.getAddress())
                    .set(Show::getSeatEnabled, show.getSeatEnabled())
                    .set(Show::getRefundEnabled, show.getRefundEnabled())
                    .set(Show::getPickupType, show.getPickupType())
                    .set(Show::getMaxPerOrder, show.getMaxPerOrder())
                    .set(Show::getSaleStartTime, show.getSaleStartTime())
                    .set(Show::getSaleEndTime, show.getSaleEndTime())
                    .set(Show::getStopped, show.getStopped())
                    .update();
        } catch (DataIntegrityViolationException exception) {
            throw new IllegalArgumentException(ShowConstant.REFERENCE_MISSING);
        }
        return get(id);
    }

    public void delete(Long id) {
        if (getById(id) == null) {
            throw new IllegalArgumentException(ShowConstant.NOT_FOUND);
        }
        try {
            removeById(id);
        } catch (DataIntegrityViolationException exception) {
            throw new IllegalArgumentException(ShowConstant.HAS_SESSION_OR_TICKET);
        }
    }

    private void requireReferences(ShowDTO form) {
        if (showTypeService.getById(form.getShowTypeId()) == null) {
            throw new IllegalArgumentException(ShowTypeConstant.NOT_FOUND);
        }
        if (cityService.getById(form.getCityId()) == null) {
            throw new IllegalArgumentException(ShowConstant.CITY_NOT_FOUND);
        }
    }

    private Show toEntity(ShowDTO form) {
        Show show = new Show();
        show.setShowTypeId(form.getShowTypeId());
        show.setCityId(form.getCityId());
        show.setName(form.getName());
        show.setArtist(form.getArtist());
        show.setPoster(form.getPoster());
        show.setDuration(form.getDuration());
        show.setIntro(form.getIntro());
        show.setNotice(form.getNotice());
        show.setVenueName(form.getVenueName());
        show.setAddress(form.getAddress());
        show.setSeatEnabled(form.getSeatEnabled());
        show.setRefundEnabled(form.getRefundEnabled());
        show.setPickupType(form.getPickupType());
        show.setMaxPerOrder(form.getMaxPerOrder());
        show.setSaleStartTime(form.getSaleStartTime());
        show.setSaleEndTime(form.getSaleEndTime());
        show.setStopped(form.getStopped());
        return show;
    }
}

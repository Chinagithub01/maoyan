package com.maoyan.controller.admin;

import com.maoyan.constant.ShowConstant;
import com.maoyan.dto.ShowDTO;
import com.maoyan.result.Result;
import com.maoyan.service.ShowService;
import com.maoyan.vo.ShowVO;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/show")
public class ShowController {
    @Resource
    private ShowService showService;

    @GetMapping
    public Result<List<ShowVO>> list() {
        return Result.success(showService.listAll());
    }

    @GetMapping("/{id}")
    public Result<ShowVO> get(@PathVariable Long id) {
        try {
            return Result.success(showService.get(id));
        } catch (IllegalArgumentException exception) {
            return Result.error(exception.getMessage());
        }
    }

    @PostMapping
    public Result<ShowVO> create(@RequestBody ShowDTO form) {
        String message = check(form);
        if (message != null) {
            return Result.error(message);
        }
        try {
            return Result.success(showService.create(form));
        } catch (IllegalArgumentException exception) {
            return Result.error(exception.getMessage());
        }
    }

    @PutMapping("/{id}")
    public Result<ShowVO> update(@PathVariable Long id, @RequestBody ShowDTO form) {
        String message = check(form);
        if (message != null) {
            return Result.error(message);
        }
        try {
            return Result.success(showService.update(id, form));
        } catch (IllegalArgumentException exception) {
            return Result.error(exception.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        try {
            showService.delete(id);
            return Result.success();
        } catch (IllegalArgumentException exception) {
            return Result.error(exception.getMessage());
        }
    }

    private String check(ShowDTO form) {
        if (form == null) {
            return ShowConstant.REQUIRED;
        }
        form.setName(text(form.getName()));
        form.setVenueName(text(form.getVenueName()));
        form.setArtist(emptyToNull(text(form.getArtist())));
        form.setPoster(emptyToNull(text(form.getPoster())));
        form.setAddress(emptyToNull(text(form.getAddress())));
        form.setIntro(emptyToNull(text(form.getIntro())));
        form.setNotice(emptyToNull(text(form.getNotice())));
        form.setPickupType(emptyToNull(text(form.getPickupType())));
        if (form.getName().length() < ShowConstant.NAME_MIN || form.getName().length() > ShowConstant.NAME_MAX) {
            return ShowConstant.NAME_LENGTH;
        }
        if (form.getShowTypeId() == null) {
            return ShowConstant.TYPE_REQUIRED;
        }
        if (form.getCityId() == null) {
            return ShowConstant.CITY_REQUIRED;
        }
        if (form.getVenueName().length() < ShowConstant.NAME_MIN || form.getVenueName().length() > ShowConstant.VENUE_MAX) {
            return ShowConstant.VENUE_LENGTH;
        }
        if (length(form.getArtist()) > ShowConstant.ARTIST_MAX) {
            return ShowConstant.ARTIST_LENGTH;
        }
        if (length(form.getPoster()) > ShowConstant.POSTER_MAX) {
            return ShowConstant.POSTER_LENGTH;
        }
        if (length(form.getAddress()) > ShowConstant.ADDRESS_MAX) {
            return ShowConstant.ADDRESS_LENGTH;
        }
        if (form.getDuration() != null && form.getDuration() <= 0) {
            return ShowConstant.DURATION_INVALID;
        }
        String flag = flag(form.getSeatEnabled(), ShowConstant.SEAT_LABEL);
        if (flag != null) {
            return flag;
        }
        flag = flag(form.getRefundEnabled(), ShowConstant.REFUND_LABEL);
        if (flag != null) {
            return flag;
        }
        flag = flag(form.getStopped(), ShowConstant.STOPPED_LABEL);
        if (flag != null) {
            return flag;
        }
        if (form.getSeatEnabled() == null) {
            form.setSeatEnabled(ShowConstant.FLAG_NO);
        }
        if (form.getRefundEnabled() == null) {
            form.setRefundEnabled(ShowConstant.FLAG_NO);
        }
        if (form.getStopped() == null) {
            form.setStopped(ShowConstant.FLAG_NO);
        }
        if (form.getPickupType() == null) {
            form.setPickupType(ShowConstant.DEFAULT_PICKUP_TYPE);
        } else if (form.getPickupType().length() > ShowConstant.PICKUP_MAX) {
            return ShowConstant.PICKUP_LENGTH;
        }
        if (form.getMaxPerOrder() == null) {
            form.setMaxPerOrder(ShowConstant.DEFAULT_MAX_PER_ORDER);
        } else if (form.getMaxPerOrder() < ShowConstant.MAX_PER_ORDER_MIN
                || form.getMaxPerOrder() > ShowConstant.MAX_PER_ORDER_MAX) {
            return ShowConstant.MAX_PER_ORDER_RANGE;
        }
        if (form.getSaleStartTime() != null && form.getSaleEndTime() != null
                && !form.getSaleEndTime().isAfter(form.getSaleStartTime())) {
            return ShowConstant.SALE_TIME_ORDER;
        }
        return null;
    }

    private String flag(Integer value, String label) {
        if (value != null && value != ShowConstant.FLAG_NO && value != ShowConstant.FLAG_YES) {
            return label + ShowConstant.FLAG_INVALID;
        }
        return null;
    }

    private String text(String value) {
        return value == null ? "" : value.trim();
    }

    private String emptyToNull(String value) {
        return value.isEmpty() ? null : value;
    }

    private int length(String value) {
        return value == null ? 0 : value.length();
    }
}

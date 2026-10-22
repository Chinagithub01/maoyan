package com.maoyan.controller.admin;

import com.maoyan.constant.ShowTypeConstant;
import com.maoyan.dto.ShowTypeDTO;
import com.maoyan.result.Result;
import com.maoyan.service.ShowTypeService;
import com.maoyan.vo.ShowTypeVO;
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
@RequestMapping("/admin/showType")
public class ShowTypeController {
    @Resource
    private ShowTypeService showTypeService;

    @GetMapping()
    public Result<List<ShowTypeVO>> list() {
        return Result.success(showTypeService.listAll());
    }

    @GetMapping("/{id}")
    public Result<ShowTypeVO> get(@PathVariable Long id) {
        try {
            return Result.success(showTypeService.get(id));
        } catch (IllegalArgumentException exception) {
            return Result.error(exception.getMessage());
        }
    }

    @PostMapping
    public Result<ShowTypeVO> create(@RequestBody ShowTypeDTO form) {
        String name = text(form);
        if (name.length() < ShowTypeConstant.NAME_MIN || name.length() > ShowTypeConstant.NAME_MAX) {
            return Result.error(ShowTypeConstant.NAME_LENGTH);
        }
        try {
            return Result.success(showTypeService.create(name));
        } catch (IllegalArgumentException exception) {
            return Result.error(exception.getMessage());
        }
    }

    @PutMapping("/{id}")
    public Result<ShowTypeVO> update(@PathVariable Long id, @RequestBody ShowTypeDTO form) {
        String name = text(form);
        if (name.length() < ShowTypeConstant.NAME_MIN || name.length() > ShowTypeConstant.NAME_MAX) {
            return Result.error(ShowTypeConstant.NAME_LENGTH);
        }
        try {
            return Result.success(showTypeService.update(id, name));
        } catch (IllegalArgumentException exception) {
            return Result.error(exception.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        try {
            showTypeService.delete(id);
            return Result.success();
        } catch (IllegalArgumentException exception) {
            return Result.error(exception.getMessage());
        }
    }

    private String text(ShowTypeDTO form) {
        return form == null || form.getName() == null ? "" : form.getName().trim();
    }
}

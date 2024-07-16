package com.szy.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.util.BeanUtil;
import com.szy.reggie.common.R;
import com.szy.reggie.entity.Category;
import com.szy.reggie.entity.Dish;
import com.szy.reggie.entity.DishDto;
import com.szy.reggie.service.CategoryService;
import com.szy.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private CategoryService categoryService;
    @PostMapping
    public R<String> save(@RequestBody Dish dish) {
        dishService.save(dish);
        return R.success("新增菜品成功");
    }

    @GetMapping("/list")
    public R<List<Dish>> list(Dish dish) {
        Long categoryID = dish.getCategoryId();
        LambdaQueryWrapper<Dish> queryWrapper = new  LambdaQueryWrapper<>();
        queryWrapper.eq(categoryID != null, Dish::getCategoryId, categoryID);
        queryWrapper.eq(Dish::getStatus, 1);
        queryWrapper.orderByAsc(Dish::getSort);

        List<Dish> list = dishService.list(queryWrapper);

        return R.success(list);
    }

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        // 分页构造器
        Page<Dish> pageInfo = new Page(page, pageSize);
        Page<DishDto> pageInfoDishDto = new Page(page, pageSize);
        // 查询构造起
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper();
        // like 模糊匹配
        queryWrapper.like(name != null, Dish::getName, name);
        queryWrapper.orderByAsc(Dish::getUpdateTime);
        dishService.page(pageInfo, queryWrapper);

        BeanUtils.copyProperties(pageInfo, pageInfoDishDto, "records");
        List<Dish> records = pageInfo.getRecords();
        List<DishDto> list = records.stream().map((item) -> {
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            String categoryName = category.getName();

            DishDto dishDto = new DishDto();

            dishDto.setCategoryName(categoryName);
            BeanUtils.copyProperties(item, dishDto);
            return dishDto;
        }).collect(Collectors.toList());
        pageInfoDishDto.setRecords(list);

        return R.success(pageInfoDishDto);
    }
}

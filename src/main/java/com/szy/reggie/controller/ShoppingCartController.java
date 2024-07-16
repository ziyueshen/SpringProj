package com.szy.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.szy.reggie.common.BaseContext;
import com.szy.reggie.entity.ShoppingCart;
import com.szy.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.szy.reggie.common.R;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/shoppingCart")
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart) {
        Long userId = BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);
        // 查询当前菜品数量，然后在此基础上加1
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(ShoppingCart::getUserId, shoppingCart.getUserId());
        queryWrapper.eq(ShoppingCart::getDishId, shoppingCart.getDishId());
        ShoppingCart shoppedCart = shoppingCartService.getOne(queryWrapper);
        if (shoppedCart != null) {
            shoppedCart.setNumber(shoppedCart.getNumber() + 1);
            shoppingCartService.updateById(shoppedCart);
        } else {
            shoppingCart.setNumber(1);
            shoppingCartService.save(shoppingCart);
            shoppedCart = shoppingCart;
        }
        return R.success(shoppedCart);
    }

    @GetMapping("/list")
    public R<List<ShoppingCart>> list() {
        Long userId = BaseContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(ShoppingCart::getUserId, userId);
        List<ShoppingCart> list = shoppingCartService.list(queryWrapper);
        if (list != null) {
            return R.success(list);
        }
        return R.error("未添加商品到购物车");
    }
}

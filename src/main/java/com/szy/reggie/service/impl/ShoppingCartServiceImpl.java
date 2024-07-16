package com.szy.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szy.reggie.mapper.ShoppingCartMapper;
import com.szy.reggie.service.ShoppingCartService;
import org.springframework.stereotype.Service;
import com.szy.reggie.entity.ShoppingCart;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
}

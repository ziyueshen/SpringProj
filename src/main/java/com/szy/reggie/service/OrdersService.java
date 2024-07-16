package com.szy.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.szy.reggie.entity.Orders;

public interface OrdersService extends IService<Orders> {
    void submit(Orders orders);
}

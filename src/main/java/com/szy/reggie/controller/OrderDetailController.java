package com.szy.reggie.controller;

import com.szy.reggie.service.OrderDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping
public class OrderDetailController {
    @Autowired
    private OrderDetailService orderDetailService;
}

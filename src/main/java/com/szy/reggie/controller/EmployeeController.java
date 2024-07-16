package com.szy.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.szy.reggie.common.R;
import com.szy.reggie.entity.Employee;
import com.szy.reggie.service.EmployeeService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;


@Slf4j
@RestController
@RequestMapping("/employee")
/**
 * 员工登录
 * @param request
 * @param employee
 */
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {
        // 明文密码转为md5
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //查询数据库
        // 创建 LambdaQueryWrapper 对象，用于构建数据库查询条件
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        // 在查询条件中添加等值条件：查询字段 username 的值等于 employee 对象中的用户名
        // 添加一个条件到查询中，要求 Employee 表的 username 字段的值等于 employee 对象的用户名
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        // 调用 employeeService 的 getOne 方法，传入查询条件 queryWrapper，执行数据库查询并返回结果中的一条记录
        Employee emp = employeeService.getOne(queryWrapper);

        // 没有查询到就返回失败
        if (emp == null) {
            return R.error("登录失败");
        }

        // 密码比对，不相等则返回失败
        if (!emp.getPassword().equals(password)) {
            return R.error("登录失败");
        }

        // 查看员工状态是否禁用
        if (emp.getStatus() == 0) {
            return R.error("账号已禁用");
        }

        // 登陆成功，用户信息存入session并返回登录结果
        // 使用 request 对象的 getSession() 方法获取当前用户的 HttpSession 对象
        // HttpSession 用于在多个请求之间存储和共享用户的信息
        // 调用 HttpSession 对象的 setAttribute 方法，用于设置会话属性
        // 这里设置了一个名为 "employee" 的属性，其值为从数据库查询结果中获取的员工 ID
        request.getSession().setAttribute("employee", emp.getId());
        return R.success(emp);
    }

    /**
     * 员工退出
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
        // 清理session中保存的当前员工ID
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    /**
     * add new employees
     * @param employee
     * @return
     */
    @PostMapping
    public R<String> save(HttpServletRequest request, @RequestBody Employee employee) {
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
//
//        Long empId = (Long) request.getSession().getAttribute("employee");
//        employee.setCreateUser(empId);
//        employee.setUpdateUser(empId);

        employeeService.save(employee);
        return R.success("新增员工成功");
    }

    /**
     * 员工信息分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        // 分页构造器
        Page pageInfo = new Page(page, pageSize);
        // 条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();
        // Employee::getName 是一个方法引用，用于引用 getName 方法本身
        queryWrapper.like(!StringUtils.isEmpty(name), Employee::getName, name);
        // 查询排序
        queryWrapper.orderByDesc(Employee::getUpdateTime);

        // 执行查询
        employeeService.page(pageInfo, queryWrapper);
        return R.success(pageInfo);
    }

    @PutMapping
    public R<String> update(HttpServletRequest request, @RequestBody Employee employee) {
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());
        Long empId = (Long) request.getSession().getAttribute("employee");
        employee.setCreateUser(empId);
        employee.setUpdateUser(empId);
        employeeService.updateById(employee);
        return R.success("信息修改成功");
    }
}

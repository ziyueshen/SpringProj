package com.szy.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.szy.reggie.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

/** 继承了BaseMapper
 * 包含增删改查方法*/
@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}

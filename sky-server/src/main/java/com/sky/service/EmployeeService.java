package com.sky.service;

import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.result.PageResult;

public interface EmployeeService {

    /**
     * 员工登录
     * @param employeeLoginDTO 传入用户名和密码
     * @return 返回登录成功的员工对象
     */
    Employee login(EmployeeLoginDTO employeeLoginDTO);

    /**
     * 新增员工
     * @param employeeDTO 传入员工相关信息
     */
    void save(EmployeeDTO employeeDTO);

    /**
     * 分页查询方法
     * @param employeePageQueryDTO 传入name page pageSize
     * @return 返回相关员工的数据 分页查询
     */
    PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO);

    /**
     * 启用禁用员工账号
     * @param status 员工账号状态
     * @param id 员工id
     */
    void startOrStop(Integer status, Long id);

    /**
     * 根据id查询员工信息
     * @param id 员工id
     * @return  员工信息实体类
     */
    Employee getById(Long id);

    /**
     * 编辑员工信息
     * @param employeeDTO 封装员工信息
     */
    void update(EmployeeDTO employeeDTO);
}

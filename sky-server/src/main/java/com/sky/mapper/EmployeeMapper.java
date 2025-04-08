package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface EmployeeMapper {

    /**
     * 根据用户名查询员工
     * @param username 员工姓名
     * @return 返回员工对象
     */
    @Select("select * from employee where username = #{username}")
    Employee getByUsername(String username);
    /**
     * 插入员工数据
     * @param employee 员工数据
     **/
 @Insert("insert into employee (name, username, password, phone, sex, id_number, create_time, update_time, create_user, update_user, status) " +
         "values (#{name}, #{username}, #{password}, #{phone}, #{sex}, #{idNumber}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser}, #{status})")
 void insert(Employee employee);
    /**
     * 分页查询
     * @param employeePageQueryDTO 传入员工姓名页码，用户记录数 使用动态sql
     * @return  返回Page泛型
     **/
    Page<Employee> pageQuery(EmployeePageQueryDTO employeePageQueryDTO);

    /**
     * 根据主键动态修改员工账号字段属性
     * @param employee 员工信息
     */
    void update(Employee employee);
    /**
     * 根据id查询员工信息
     * @param id 用户id
     * @return  用户信息
     */
    @Select("select * from employee where id = #{id}")
    Employee getById(Long id);
}

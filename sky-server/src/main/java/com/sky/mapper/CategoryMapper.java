package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CategoryMapper {
    /**
     * 插入数据
     * @param category pojo
     */
    @AutoFill(value = OperationType.INSERT)
    @Insert("insert into category(type, name, sort, status, create_time, update_time, create_user, update_user)" +
            " VALUES" +
            " (#{type}, #{name}, #{sort}, #{status}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser})")
    void insert(Category category);

    /**
     * 分页查询
     * @param categoryPageQueryDTO DTO
     * @return 返回分页对象
     */
    Page<Category> pageQuery(CategoryPageQueryDTO categoryPageQueryDTO);
    /**
     * 根据id删除分类
     * @param id 分类id
     */
    @Delete("delete from category where id = #{id}")
    void deleteById(Long id);
    /**
     * 根据id修改分类
     * @param category pojo
     */
    @AutoFill(value = OperationType.UPDATE)
    void update(Category category);
    /**
     * 根据类型查询分类
     * @param type 类型
     * @return 返回列表
     */
    List<Category> list(Integer type);
}

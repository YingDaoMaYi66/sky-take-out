package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;


@Mapper
public interface DishMapper {
    /**
     * 根据分类id查询菜品数量
     * @param id 分类id
     * @return 菜品数量
     */
    @Select("select count(id) from dish where category_id = #{id}")
    Integer countByCategoryId(Long id);

    /**
     * 添加菜品
     * @param dish 菜品表类
     */
    @AutoFill(OperationType.INSERT)
    void insert(Dish dish);

    /**
     * 菜品的分页查询
     * @param dishPageQueryDTO 分页查询DTO
     * @return 分页查询VO
     */
    Page<DishVO> pageQuery(DishPageQueryDTO dishPageQueryDTO);
    /**
     * 根据主键查询菜品
     * @param id 菜品id集合
     */
    @Select("select * from dish where id = #{id}")
    Dish getById(Long id);
    /**
     * 根据id主键去删除菜品数据
     * @param id 菜品表类
     */
    @Delete("delete from dish where id = #{id}")
    void deleteById(Long id);

    /**
     * 根据菜品ids集合批量删除菜品
     * @param ids 菜品id集合
     */
    void deleteByIds(List<Long> ids);
    /**
     * 根据菜品id去修改菜品数据
     * @param dish 菜品表类
     */
    @AutoFill(value=OperationType.UPDATE)
    void update(Dish dish);
    /**
     * 根据菜品id去查询菜品数据
     * @param dish 菜品id
     * @return 菜品数据
     */
    List<Dish> list(Dish dish);
}

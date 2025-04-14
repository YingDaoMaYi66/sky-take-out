package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishFlavorMapper {
    /**
     * 批量插入口味节点
     * @param flavors 口味表数组
     */
    void insertBatch(List<DishFlavor> flavors);

    /**
     * 根据菜品id去删除口味数据
     * @param dishId 菜品id
     */
    @Delete("delete from dish_flavor where dish_id = #{dishId}")
    void deleteByDishId(Long dishId);

    /**
     * 根据菜品id集合去批量删除口味数据
     * @param dishids
     */
    void deleteByDishIds(List<Long> dishids);
    /**
     * 根据菜品id去查询口味数据
     * @param dishId 菜品id
     * @return 口味数据集合
     */
    @Select("select * from dish_flavor where dish_id = #{dishId}")
    List<DishFlavor> getByDishId(Long dishId);
}

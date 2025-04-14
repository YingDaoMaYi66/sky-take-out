package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;


public interface DishService {
    /**
     * 新增菜品和对应口味
     * @param dishDto 菜品DTO
     */
    void saveWithFlavor(DishDTO dishDto);
    /**
     * 菜品分页查询
     * @param dishPageQueryDTO 分页查询DTO
     * @return 返回分页查询VO
     */
    PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 批量删除菜品
     * @param ids 菜品id集合
     */
    void deleteBatch(List<Long> ids);

    /**
     * 根据id去查询菜品和对应的口味数据
     * @param id 菜品id
     * @return DishVO
     */
    DishVO getByIdWithFlavor(Long id);

    /**
     * 根据id去修该菜品数据与对应的口味数据
     * @param dishDTO 菜品id
     */

    void updateWithFlavor(DishDTO dishDTO);

    /**
     * 根据菜品id去查询菜品数据
     * @param categoryId 菜品id
     * @return 菜品数据
     */
    List<Dish> list(Long categoryId);

    /**
     * 菜品的起售与停售
     * @param status 菜品状态
     * @param id 菜品id
     */
    void stratOrStop(Integer status, Long id);
}

package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;


public interface DishService {
    /**
     * 新增菜品和对应口味
     * @param dishDto 菜品DTO
     */
    public void saveWithFlavor(DishDTO dishDto);
    /**
     * 菜品分页查询
     * @param dishPageQueryDTO 分页查询DTO
     * @return 返回分页查询VO
     */
    PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);
}

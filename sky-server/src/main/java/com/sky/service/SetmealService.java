package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.vo.SetmealVO;

import java.util.List;

public interface SetmealService {
    /**
     * 新增套餐,同时还要保存套餐和菜品的关联关系
     * @param setmealDTO 套餐数据传输对象
     */
    void saveWithDish(SetmealDTO setmealDTO);
    /**
     * 分页查询套餐
     * @param setmealPageQueryDTO 分页参数
     * @return 返回分页结果
     */
    PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);
    /**
     * 批量删除套餐
     * @param ids 套餐id集合
     */
    void deleteBatch(List<Long> ids);
    /**
     * 根据id查询套餐和菜品的关联关系
     * @param id 套餐id
     * @return 返回套餐和菜品的关联关系
     */
    SetmealVO getByIdWithDish(Long id);
    /**
     * 修改套餐
     * @param setmealDTO
     */
    void update(SetmealDTO setmealDTO);

    /**
     * 起售停售套餐
     * @param status 状态
     * @param id id
     */
    void startOrStop(Integer status, Long id);
}

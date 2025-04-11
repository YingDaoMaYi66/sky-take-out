package com.sky.service;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;

import java.util.List;

public interface  CategoryService {

    /**
     * 新增分类
     * @param categoryDTO 分类DTO
     */
    void save(CategoryDTO categoryDTO);

    /**
     * 分页查询
     * @param categoryPageQueryDTO 分类分页查询DTO 页码，每页记录数，分类名称，分类类型
     * @return
     */
    PageResult pageQuery(CategoryPageQueryDTO categoryPageQueryDTO);

    /**
     * 根据id删除分类
     * @param id 分类id
     */
    void deleteById(Long id);
    /**
     * 修改分类
     * @param categoryDTO 分类DTO
     */
    void update(CategoryDTO categoryDTO);

    /**
     * 启用禁用分类状态
     * @param status 启用禁用状态
     * @param id 分类id
     */
    void startOrStop(Integer status, Long id);
    /**
     * 根据类型查询分类
     * @param type 分类类型
     * @return 分类列表
     */
    List<Category> list(Integer type);
}

package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.mapper.CategoryMapper;
import com.sky.result.PageResult;
import com.sky.service.CategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    CategoryMapper categoryMapper;

    @Override
    public void save(CategoryDTO categoryDTO) {
        // 1. 属性拷贝
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO, category);


        // 2. 设置创建人和更新人
        Long currentId = BaseContext.getCurrentId();
        category.setCreateUser(currentId);
        category.setUpdateUser(currentId);
        LocalDateTime now = LocalDateTime.now();
        category.setCreateTime(now);
        category.setUpdateTime(now);

        //3. 设置分类状态为禁用
        category.setStatus(StatusConstant.DISABLE);

        //4. 插入数据到数据库
        categoryMapper.insert(category);

    }

    /**
     *
     * @param categoryPageQueryDTO 分类分页查询DTO 页码，每页记录数，分类名称，分类类型
     * @return
     */
    @Override
    public PageResult pageQuery(CategoryPageQueryDTO categoryPageQueryDTO) {
        //设置分页参数(当前页码，每页记录数)
        PageHelper.startPage(categoryPageQueryDTO.getPage(), categoryPageQueryDTO.getPageSize());
        //创建分页对象
        Page<Category> page = categoryMapper.pageQuery(categoryPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }
}

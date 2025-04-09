package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.mapper.CategoryMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    CategoryMapper categoryMapper;
    @Autowired
    DishMapper dishMapper;
    @Autowired
    SetmealMapper setmealMapper;

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

    @Override
    public void deleteById(Long id) {
        //1. 查询当前分类是否关联了菜品,如果关联了就抛出异常
        Integer count = dishMapper.countByCategoryId(id);
        if (count > 0) {
            //抛出业务异常,当前分类下有菜品，不能删除
            throw new RuntimeException(MessageConstant.CATEGORY_BE_RELATED_BY_DISH);
        }
        //2.查看当前分类是否关联了套餐，如果关联了就抛出异常
        count = setmealMapper.countByCategoryId(id);
        if (count > 0) {
            //抛出业务异常,当前分类下有套餐，不能删除
            throw new RuntimeException(MessageConstant.CATEGORY_BE_RELATED_BY_SETMEAL);
        }
        //删除分类数据
        categoryMapper.deleteById(id);
    }

    @Override
    public void update(CategoryDTO categoryDTO) {
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO, category);
        //设置修改时间和修改人
        category.setUpdateTime(LocalDateTime.now());
        category.setUpdateUser(BaseContext.getCurrentId());

        categoryMapper.update(category);
    }

    /**
     * 启用禁用分类状态
     * @param status 启用禁用状态
     * @param id 分类id
     */
    @Override
    public void startOrStop(Integer status, Long id) {
        Category category = Category.builder()
                .id(id)
                .status(status)
                .build();
        categoryMapper.update(category);
    }
    /**
     * 根据类型查询分类
     * @param type 分类类型
     * @return 分类列表
     */
    @Override
    public List<Category> list(Integer type) {

        return categoryMapper.list(type);
    }


}

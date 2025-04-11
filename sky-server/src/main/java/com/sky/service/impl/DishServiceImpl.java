package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DishServiceImpl implements DishService {
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    /**
     * 新增菜品和对应的口味 多表操作涉及到事务的一致性，需要添加事务注解
     * @param dishDto 前端DTO类
     */
    @Transactional
    @Override
    public void saveWithFlavor(DishDTO dishDto) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDto, dish);
        //向菜品添加一条数据
        dishMapper.insert(dish);
        //获取当前insert语句生成的主键值 然后会自动回填主键值到实体类的id属性中,
        // 这是通过useGeneratedKeys=true与keyProperty来实现的
        Long dishId = dish.getId();

        List<DishFlavor> flavors = dishDto.getFlavors();
        if (flavors != null && !flavors.isEmpty()) {
            //为口味表中的每一条数据设置菜品id
            flavors.forEach(flavor -> flavor.setDishId(dishId));
            //向口味表插入N条数据
            dishFlavorMapper.insertBatch(flavors);
        }
    }
    /**
     * 菜品分页查询
     * @param dishPageQueryDTO 分页查询DTO
     * @return 返回分页查询VO
     */
    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }
}

package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 菜品管理
 */

@Service
public class DishServiceImpl implements DishService {
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    @Autowired
    private SetmealMapper setmealMapper;

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

    /**
     * 批量删除菜品
     * @param ids 菜品id集合
     */
    @Transactional
    @Override
    public void deleteBatch(List<Long> ids) {
        //判断当前菜品是否能够删除---是否在起售中
        for(Long id : ids) {
            Dish dish = dishMapper.getById(id);
            if(dish.getStatus() == StatusConstant.ENABLE) {
                //当前菜品正在起售中，不能删除
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }

        //判断当前菜品是否能够删除---是否被套餐关联了
        List<Long> setmealIds = setmealDishMapper.getSetmealIdsByDishIds(ids);
        if (setmealIds != null && setmealIds.size() > 0) {
            //当前菜品被套餐关联了不能删除
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }
        //删除菜品表中的菜品数据
//        for(Long id : ids) {
//            dishMapper.deleteById(id);
//            //删除菜品关联的口味数据
//            dishFlavorMapper.deleteByDishId(id);
//        }
        //根据菜品id批量删除菜品数据
            dishMapper.deleteByIds(ids);
        //根据菜品id批量删除口味数据
            dishFlavorMapper.deleteByDishIds(ids);

    }
    /**
     * 根据id去查询菜品和对应的口味数据
     * @param id 菜品id
     * @return DishVO
     */
    @Override
    public DishVO getByIdWithFlavor(Long id) {
        //根据id去查询菜品数据
        Dish dish = dishMapper.getById(id);
        //根据菜品id去查询口味数据
        List<DishFlavor>dishFlavors = dishFlavorMapper.getByDishId(id);
        //将查询到的数据封装到VO
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish, dishVO);
        dishVO.setFlavors(dishFlavors);
        return dishVO;
    }
    /**
     * 根据id去修该菜品数据与对应的口味数据
     * @param dishDTO 菜品id
     */
    @Override
    public void updateWithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);

        //修改菜品表基本信息
        dishMapper.update(dish);
        //删除菜品表中的口味数据
        dishFlavorMapper.deleteByDishId(dishDTO.getId());
        //根据菜品id去增加口味数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && !flavors.isEmpty()) {
            //为口味表中的每一条数据设置菜品id
            flavors.forEach(flavor -> flavor.setDishId(dishDTO.getId()));
            //向口味表插入N条数据
            dishFlavorMapper.insertBatch(flavors);
        }
    }
    /**
     * 根据菜品分类id查询菜品
     * @param categoryId 菜品分类id
     * @return 菜品数据
     */
    @Override
    public List<Dish> list(Long categoryId) {
        Dish dish = Dish.builder()
                .categoryId(categoryId)
                .status(StatusConstant.ENABLE)
                .build();
        return dishMapper.list(dish);
    }
    /**
     * 菜品的起售与停售
     * @param status 菜品状态
     * @param id 菜品id
     */
    @Override
    public void stratOrStop(Integer status, Long id) {
        //修改菜品起售停售状态
        Dish dish = Dish.builder()
                .id(id)
                .status(status)
                .build();
        dishMapper.update(dish);
        //修改套餐起售停售状态
        if (status == StatusConstant.DISABLE) {
            //如果是停售操作，还需要将包含当前的菜品的套餐也停售
            List<Long> dishIds = new ArrayList<>();
            //将当前菜品id添加到集合中
            dishIds.add(id);
            //根据菜品id查询相关套餐id
            List<Long> setmealIds = setmealDishMapper.getSetmealIdsByDishIds(dishIds);
            //如果有相关套餐id，则将套餐也停售
            if(setmealIds != null && setmealIds.size() > 0) {
                for(long setmealId : setmealIds) {
                    //根据套餐id修改套餐状态
                    Setmeal setmeal = Setmeal.builder()
                            .id(setmealId)
                            .status(StatusConstant.DISABLE)
                            .build();
                    setmealMapper.update(setmeal);
                }
            }
        }
    }

    /**
     * 条件查询菜品和口味
     * @param dish
     * @return
     */
    public List<DishVO> listWithFlavor(Dish dish) {
        List<Dish> dishList = dishMapper.list(dish);

        List<DishVO> dishVOList = new ArrayList<>();

        for (Dish d : dishList) {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(d,dishVO);

            //根据菜品id查询对应的口味
            List<DishFlavor> flavors = dishFlavorMapper.getByDishId(d.getId());

            dishVO.setFlavors(flavors);
            dishVOList.add(dishVO);
        }

        return dishVOList;
    }

}

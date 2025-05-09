package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.exception.SetmealEnableFailedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
public class SetmealServiceImpl implements SetmealService {
    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    @Autowired
    private DishMapper dishMapper;

    /**
     * 新增套餐,同时还要保存套餐和菜品的关联关系
     * @param setmealDTO 套餐数据传输对象
     */
    @Transactional
    @Override
    public void saveWithDish(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        //向套餐表中插入数据
        setmealMapper.insert(setmeal);
        //获取生成套餐id
        Long setmealId = setmeal.getId();
        //从DTO中获取套餐菜品集合
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        //将套餐id设置到每个套餐菜品关联对象中
        setmealDishes.forEach(setmealDish -> setmealDish.setSetmealId(setmealId));
        //保存套餐和菜品的关联关系
        setmealDishMapper.insertBatch(setmealDishes);
    }

    /**
     * 分页查询套餐
     * @param setmealPageQueryDTO 分页参数
     * @return 返回分页结果
     */
    @Override
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        //获取分页参数
        int pageNum = setmealPageQueryDTO.getPage();
        int pageSize = setmealPageQueryDTO.getPageSize();
        //设置分页参数(当前页码，每页记录数)
        PageHelper.startPage(pageNum, pageSize);
        //创建分页对象
        Page<SetmealVO> page = setmealMapper.pageQuery(setmealPageQueryDTO);
        //返回分页对象
        return new PageResult(page.getTotal(), page.getResult());
    }
    /**
     * 根据id批量删除套餐
     * @param ids 套餐id集合
     */
    @Override
    public void deleteBatch(List<Long> ids) {
        ids.forEach(id -> {
            Setmeal setmeal = setmealMapper.getById(id);
            if (StatusConstant.ENABLE.equals(setmeal.getStatus())) {
                //起售中的菜品不能删除
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
            }
        });
        ids.forEach(setmealId -> {
            //删除套餐表中的数据
            setmealMapper.deleteById(setmealId);
            //删除套餐菜品关系表中的数据
            setmealDishMapper.deleteBySetmealId(setmealId);
        });
    }
    /**
     * 根据id查询套餐和套餐菜品关系
     *
     * @param id 套餐id
     * @return 返回套餐和套餐菜品关系
     */
    public SetmealVO getByIdWithDish(Long id) {
        //根据id查询套餐
        Setmeal setmeal = setmealMapper.getById(id);
        //根据套餐id查询套餐菜品关系
        List<SetmealDish> setmealDishes = setmealDishMapper.getBySetmealId(id);
        //将套餐与套餐菜品关系封装到SetmealVO对象中
        SetmealVO setmealVO = new SetmealVO();
        BeanUtils.copyProperties(setmeal, setmealVO);
        setmealVO.setSetmealDishes(setmealDishes);
        //返回套餐VO对象
        return setmealVO;
    }
    /**
     * 修改套餐
     *
     * @param setmealDTO 套餐信息DTO
     */
    @Transactional
    public void update(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);

        //1、修改套餐表，执行update
        setmealMapper.update(setmeal);

        //套餐id
        Long setmealId = setmealDTO.getId();

        //2、删除套餐和菜品的关联关系，操作setmeal_dish表，执行delete
        setmealDishMapper.deleteBySetmealId(setmealId);

        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        setmealDishes.forEach(setmealDish -> setmealDish.setSetmealId(setmealId));
        //3、重新插入套餐和菜品的关联关系，操作setmeal_dish表，执行insert
        setmealDishMapper.insertBatch(setmealDishes);
    }
    /**
     * 套餐起售、停售
     * @param status 套餐状态
     * @param id 套餐id
     */
    @Override
    public void startOrStop(Integer status, Long id) {
        //起售套餐时，判断套餐内是否有停售菜品，有停售菜品提示"套餐内包含未启售菜品，无法启售"
        if(status.equals(StatusConstant.ENABLE)){
            //select a.* from dish a left join setmeal_dish b on a.id = b.dish_id where b.setmeal_id = ?

            List<Dish> dishList = dishMapper.getBySetmealId(id);
            if(dishList != null && !dishList.isEmpty()){
                dishList.forEach(dish -> {
                    if(StatusConstant.DISABLE.equals(dish.getStatus())){
                        throw new SetmealEnableFailedException(MessageConstant.SETMEAL_ENABLE_FAILED);
                    }
                });
            }
        }

        Setmeal setmeal = Setmeal.builder()
                .id(id)
                .status(status)
                .build();
        setmealMapper.update(setmeal);
    }
    /**
     * 条件查询
     * @param setmeal 套餐实体类
     * @return 返回套餐列表
     */
    public List<Setmeal> list(Setmeal setmeal) {
        return setmealMapper.list(setmeal);
    }

    /**
     * 根据id查询菜品选项
     * @param id 套餐id
     * @return 返回菜品选项列表1
     */
    public List<DishItemVO> getDishItemById(Long id) {
        return setmealMapper.getDishItemBySetmealId(id);
    }
}

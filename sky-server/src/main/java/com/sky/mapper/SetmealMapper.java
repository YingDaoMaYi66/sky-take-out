package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;
import com.sky.vo.SetmealVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SetmealMapper {
    /**
     * 根据分类id查询套餐的数量
     * @param id 分类id
     * @return 套餐数量
     */
    @Select("SELECT count(id) FROM setmeal WHERE category_id = #{id}")
    Integer countByCategoryId(Long id);
    /**
     * 新增套餐
     * @param setmeal 套餐对象
     */
    @AutoFill(OperationType.INSERT)
    void insert(Setmeal setmeal);

    /**
     * 分页查询
     * @param setmealPageQueryDTO 分页查询dto
     * @return 返回分页数据
     */
    Page<SetmealVO> pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    /**
     * 根据ID查询套餐
     * @param id 套餐id
     * @return 套餐对象
     */
    @Select("SELECT * FROM setmeal WHERE id = #{id}")
    Setmeal getById(Long id);

    /**
     * 根据id删除套餐
     * @param setmealId
     */
    @Delete("delete from setmeal where id = #{id}")
    void deleteById(Long setmealId);
    /**
     * 更新套餐
     * @param setmeal 套餐id
     * @return 套餐和菜品的关联关系
     */
    @AutoFill(OperationType.UPDATE)
    void update(Setmeal setmeal);
}

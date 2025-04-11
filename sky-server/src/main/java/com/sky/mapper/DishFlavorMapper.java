package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DishFlavorMapper {
    /**
     * 批量插入口味节点
     * @param flavors 口味表数组
     */
    void insertBatch(List<DishFlavor> flavors);
}

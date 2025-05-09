package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {

    /**
     * 动态条件查询
     * @param shoppingCart 购物车对象
     * @return 购物车对象集合
     */
    List<ShoppingCart> list(ShoppingCart shoppingCart);
    /**
     * 根据id修改商品数量
     * @param shoppingCart 购物车对象
     */
    @Update("update shopping_cart set number = #{number} where id = #{id}")
    void updateNumberById(ShoppingCart shoppingCart);
    /**
     * 根据id插入购物车数据
     * @param shoppingCart 购物车
     */
    @Insert("insert into shopping_cart(name,user_id,dish_id,setmeal_id,dish_flavor,number,amount,image,create_time)"+
    "values (#{name}, #{userId}, #{dishId}, #{setmealId}, #{dishFlavor}, #{number}, #{amount}, #{image}, #{createTime})")
    void insert(ShoppingCart shoppingCart);

    /**
     * 根据用户Id删除购物车
     * @param currentId 用户id
     */
    @Delete("delete from shopping_cart where user_id = #{userId}")
    void deleteByUserId(Long currentId);

    /**
     * 根据id删除购物车数据
     * @param id 购物车id
     */
    @Delete("delete from shopping_cart where id = #{id}")
    void deleteById(Long id);
    /**
     * 批量插入购物车数据
     * @param shoppingCartList 购物车对象列表
     * @return 购物车对象
     */
    void insertBatch(List<ShoppingCart> shoppingCartList);
}

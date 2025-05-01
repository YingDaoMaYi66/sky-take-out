package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface UserMapper {
    /**
     * 根据openid查询用户
     * @param openid 用户唯一标识
     * @return 用户信息
     */
    @Select("select * from user where openid = #{openid}")
    User getByOpenid(String openid);

    /**
     * 插入用户
     * @param user 用户信息
     */
    void insert(User user);
    /**
     * 根据id查询用户
     * @param id 用户信息
     */
    @Select("select * from user where id = #{id}")
    User getById(Long id);
    /**
     * 根据时间段查询用户
     * @param beginTime 起始时间
     * @param endTime 截止时间
     * @return 用户信息
     */
    List<User> getCurrentUserCount(LocalDateTime beginTime, LocalDateTime endTime);

    /**
     * 根据动态条件统计用户数量
     * @param map
     * @return
     */
    Integer countByMap(Map map);
}

package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.GoodsSalesDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper {
    /**
     * 插入订单数据
     * @param orders 订单对象
     */
    void insert(Orders orders);
    /**
     * 根据订单号查询订单
     * @param outTradeNo 订单号
     * @return 订单对象
     */
    @Select("select * from orders where number = #{outTradeNo}")
    Orders getByNumber(String outTradeNo);
    /**
     * 修改订单信息
     * @param orders 订单对象
     */
    void update(Orders orders);

    /**
     * 用于替换微信支付更新数据库状态的问题
     * @param orderStatus 订单状态
     * @param orderPaidStatus 订单支付状态
     * @param checkOutTime 结账时间
     * @param orderNumber 订单号
     */
    @Update("update orders set status = #{orderStatus}, pay_status = #{orderPaidStatus}, checkout_time = #{checkOutTime} where number = #{orderNumber}")
    void updateStatus(Integer orderStatus, Integer orderPaidStatus, LocalDateTime checkOutTime, String orderNumber);

    Page<Orders> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 根据id查询订单
     * @param id 订单id
     * @return 订单
     */
    @Select("select * from orders where id = #{id}")
    Orders getById(Long id);

    /**
     * 待接单数量统计
     * @param status 待接单状态
     * @return 数量
     */
    @Select("select count(id) from orders where status = #{status}")
    Integer countStatus(Integer status);

    /**
     * 根据订单状态和查询时间进行订单查询
     * @param status 订单状态
     * @param orderTime 当前时间
     * @return 返回当前时间之前创建，并且满足订单状态的订单
     */
    @Select("select * from orders where status = #{status} and order_time < #{orderTime}")
    List<Orders> getByStatusAndOrderTimeLT(Integer status, LocalDateTime orderTime);

    /**
     * 根据订单状态和查询时间进行订单查询
     * @param beginTime 订单起始时间
     * @param endTime 订单结束时间
     * @param completed 订单状态
     * @return
     */

    List<Orders> getTurnoverStatistics(LocalDateTime beginTime, LocalDateTime endTime, Integer completed);

    /**
     * 根据订单时间进行订单批量查询
     * @param beginTime 订单起始时间
     * @param endTime 订单结束时间
     * @return 订单返回
     */
    List<Orders> getOrderStatistics(LocalDateTime beginTime, LocalDateTime endTime);

    /**
     * 查询指定时间内的销量排名前十
     * @param beginTime
     * @param endTime
     * @return
     */
    List<GoodsSalesDTO> getSalesTop(LocalDateTime beginTime, LocalDateTime endTime);

    /**
     * 根据动态条件统计订单数量
     * @param map
     * @return
     */
    Integer countByMap(Map map);

    /**
     * 根据动态条件统计营业额数据
     * @param map
     * @return
     */
    Double sumByMap(Map map);


}

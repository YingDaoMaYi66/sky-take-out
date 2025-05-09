package com.sky.service;

import com.sky.dto.*;
import com.sky.result.PageResult;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;

public interface OrderService {

    /**
     * 用户下单
     * @param ordersSubmitDTO 订单提交对象
     * @return 订单提交结果
     */
    OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO);
    /**
     * 订单支付
     * @param ordersPaymentDTO 订单支付对象
     * @return 订单支付结果
     */
    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception;
    /**
     * 订单支付成功修改订单状态
     * @param outTradeNo
     */
    void paySuccess(String outTradeNo);
    /**
     * 查询历史订单
     * @param page 页面
     * @param pageSize 每页记录数
     * @param status 订单状态
     * @return 返回orderPageResult包装类
     */
    PageResult pageQuery4User(int page, int pageSize, Integer status);

    /**
     * 查询订单详情
     * @param id 订单id
     * @return 返回订单详情
     */
    OrderVO details(Long id);

    /**
     * 用户取消订单
     * @param id 订单id
     */
    void userCancelById(Long id) throws Exception;
    /**
     * 完成再来一单
     * @param id 订单id
     */
    void repetition(Long id);
    /**
     * 订单搜索
     * @param ordersPageQueryDTO 订单搜索对象
     * @return 返回订单分页查询结果
     */
    PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 各个状态的订单数量统计
     * @return
     */
    OrderStatisticsVO statistics();

    /**
     * 商家接单
     * @param ordersConfirmDTO 订单配置DTO
     */
    void confirm(OrdersConfirmDTO ordersConfirmDTO);
    /**
     * 商家拒单
     * @param ordersRejectionDTO 订单拒单DTO
     */
    void rejection(OrdersRejectionDTO ordersRejectionDTO) throws Exception;
    /**
     * 商家取消订单
     *
     * @param ordersCancelDTO 订单取消DTO
     */
    void cancel(OrdersCancelDTO ordersCancelDTO) throws Exception;
    /**
     * 派送订单
     *
     * @param id 订单id
     */
    void delivery(Long id);
    /**
     * 完成订单
     *
     * @param id 订单id
     */
    void complete(Long id);
    /**
     * 客户催单
     *
     * @param id 订单id
     */
    void reminder(Long id);
}

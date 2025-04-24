package com.sky.service;

import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.result.PageResult;
import com.sky.vo.OrderPaymentVO;
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
}

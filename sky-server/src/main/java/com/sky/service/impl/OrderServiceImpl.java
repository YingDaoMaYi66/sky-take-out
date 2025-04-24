package com.sky.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.*;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.service.OrderService;
import com.sky.utils.WeChatPayUtil;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private AddressBookMapper addressBookMapper;
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private WeChatPayUtil weChatPayUtil;


    /**
     * 用户下单
     * @param ordersSubmitDTO 订单提交对象
     * @return 订单提交结果
     */
    @Override
    public OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO) {
        //1.处理业务异常（地址簿为空，购物车数据为空）
        AddressBook addressBook = addressBookMapper.getById(ordersSubmitDTO.getAddressBookId());
        if (addressBook == null) {
            //抛出业务异常
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }
        //查询当前用户购物车数据
        ShoppingCart shoppingCart = new ShoppingCart();
        Long userId = BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);
        List<ShoppingCart> shoppingCartList = shoppingCartMapper.list(shoppingCart);
        if (shoppingCartList == null || shoppingCartList.isEmpty()) {
            //抛出业务异常
            throw new AddressBookBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }
        //2.向订单表中插入一条数据
        Orders orders = new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO, orders);
        //设置订单创建时间:当前时间
        orders.setOrderTime(LocalDateTime.now());
        //设置订单支付信息:未付款
        orders.setPayStatus(Orders.UN_PAID);
        //设置订单状态:待付款
        orders.setStatus(Orders.PENDING_PAYMENT);
        //设置订单的订单号:当前时间戳
        orders.setNumber(String.valueOf(System.currentTimeMillis()));
        //设置订单的手机号:用户手机号
        orders.setPhone(addressBook.getPhone());
        //设置订单的收货人
        orders.setConsignee(addressBook.getConsignee());
        //设设置当前订单的用户ID
        orders.setUserId(userId);

        orderMapper.insert(orders);

        //3.向订单明细表插入n条数据
        List<OrderDetail>orderDetailList = new ArrayList<>();
        //shoppingCartList:购物车数据可能不止一个,这里批量查询购物车列表并将数据插入到订单类中
        for (ShoppingCart cart:shoppingCartList){
            OrderDetail orderDetail = new OrderDetail();//订单明细
            BeanUtils.copyProperties(cart, orderDetail);
            orderDetail.setOrderId(orders.getId());//设置当前订单明细关联的订单id
            orderDetailList.add(orderDetail);
        }
        orderDetailMapper.insertBatch(orderDetailList);
        //4.清空当前用户的购物车数据
        shoppingCartMapper.deleteById(userId);
        //5.封装VO返回结果
        return OrderSubmitVO.builder()
                .id(orders.getId())
                .orderTime(orders.getOrderTime())
                .orderNumber(orders.getNumber())
                .orderAmount(orders.getAmount())
                .build();
    }

    /**
     * 订单支付
     * @param ordersPaymentDTO 订单支付对象
     * @return 返回订单支付对象
     */
    @Override
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO)throws Exception {
        //当前登录用户id
        Long userId = BaseContext.getCurrentId();
        User user = userMapper.getById(userId);
/**     //调用微信支付接口，生成预支付交易订单
        JSONObject jsonObject = weChatPayUtil.pay(
                ordersPaymentDTO.getOrderNumber(),//商户订单号
                new BigDecimal(0.01),//支付金额，单位：元
                "苍穹外卖订单",//商品描述
                user.getOpenid()//用户的openid
        );
        if (jsonObject.getString("code") == null && jsonObject.getString("code").equals("ORDERPAID")){
            //如果code不为空，说明支付失败
            throw new OrderBusinessException("该订单已支付");
        }

**/

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", "ORDERPAID");
        OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
        vo.setPackageStr(jsonObject.getString("package"));

       // 为替代微信支付成功后的数据库订单状态更新，多定义一个方法进行修改
        Integer orderPaidStatus = Orders.PAID;  // 支付状态：已支付
        Integer orderStatus = Orders.TO_BE_CONFIRMED;  // 订单状态：待接单

       // 发现没有将支付时间,check_out属性赋值,所以在这里进行更新
        LocalDateTime checkOutTime = LocalDateTime.now();

       // 获取订单号
        String orderNumber = ordersPaymentDTO.getOrderNumber();

       // 更新数据库状态
        log.info("调用updateStatus，用于替换微信支付更新数据库状态的问题");
        orderMapper.updateStatus(orderStatus, orderPaidStatus, checkOutTime, orderNumber);

        return vo;
    }
    /**
     * 订单支付成功修改订单状态
     * @param outTradeNo 订单号
     */
    @Override
    public void paySuccess(String outTradeNo) {

        //根据订单号码更新订单的状态，支付方式支付状态，结账时间
        Orders ordersDB = orderMapper.getByNumber(outTradeNo);

        //根据订单id更新订单的状态，支付方式，支付状态，结账时间
        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.PAID)
                .payMethod(ordersDB.getPayMethod())
                .payStatus(Orders.PAID)
                .checkoutTime(LocalDateTime.now())
                .build();
        orderMapper.update(orders);
    }
    /**
     * 查询历史订单
     * @param pageNumber 页面
     * @param pageSize 每页记录数
     * @param status 订单状态
     * @return
     */
    @Override
    public PageResult pageQuery4User(int pageNumber, int pageSize, Integer status) {
        //设置分页参数
        PageHelper.startPage(pageNumber, pageSize);
        OrdersPageQueryDTO ordersPageQueryDTO = new OrdersPageQueryDTO();
        ordersPageQueryDTO.setStatus(status);
        ordersPageQueryDTO.setUserId(BaseContext.getCurrentId());
        //分页条件查询
        Page<Orders> page = orderMapper.pageQuery(ordersPageQueryDTO);
        List<OrderVO>list = new ArrayList<>();
        //查询订单明细
        if(page != null && page.getTotal() > 0){
            for(Orders orders:page){
                Long orderId = orders.getId();//获取订单id
                //查询订明细
                List<OrderDetail> orderDetaials = orderDetailMapper.getByOrderId(orderId);
                OrderVO orderVO = new OrderVO();
                BeanUtils.copyProperties(orders, orderVO);
                orderVO.setOrderDetailList(orderDetaials);
                list.add(orderVO);

            }
        }

        return new PageResult(page.getTotal(), list);
    }
}

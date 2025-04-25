package com.sky.controller.admin;

import com.sky.dto.OrdersConfirmDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.*;

/**
 * 订单管理
 */
@RestController("adminOrderController")
@RequestMapping("/admin/order")
@Slf4j
@Api(tags = "订单管理接口")
public class OrderController {
    @Autowired
    private OrderService orderService;
    @GetMapping("/conditionSearch")
    @ApiOperation("订单搜索")
    public Result<PageResult> conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO) {
        log.info("订单搜索,订单信息为:{}", ordersPageQueryDTO);
        PageResult pageResult = orderService.conditionSearch(ordersPageQueryDTO);
        return Result.success(pageResult);

    }

    /**
     * 各个订单状态的数量的统计
     * @return 订单状态统计结果
     */
    @GetMapping("/statistics")
    @ApiOperation("各个订单状态的数量的统计")
    public Result<OrderStatisticsVO> statistics() {
        log.info("各个订单状态的数量的统计");
        OrderStatisticsVO orderStatisticsVO = orderService.statistics();
        return Result.success(orderStatisticsVO);
    }
    /**
     * 订单详情
     * @param id 订单id
     * @return 订单详情
     */
    @GetMapping("/details/{id")
    @ApiOperation("查询订单详情")
    public Result<OrderVO> details(@PathVariable("id") Long id){
        OrderVO orderVO = orderService.details(id);
        return Result.success(orderVO);
    }
    /**
     * 商家接单:将订单状态修改为已接单
     * @return
     */
    @PutMapping("/confirm")
    @ApiOperation("商家接单")
    public Result confirm(@RequestBody OrdersConfirmDTO ordersConfirmDTO){
        orderService.confirm(ordersConfirmDTO);
        return Result.success();
    }
}

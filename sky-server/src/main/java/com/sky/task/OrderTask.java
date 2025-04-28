package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 定时任务类，定时处理订单状态
 */
@Component
@Slf4j
public class OrderTask {
    @Autowired
    private OrderMapper orderMapper;

    /**
     * 定时处理超时订单
     * 每分钟触发一次
     */
    @Scheduled(cron = "0 * * * * ?")
    public void processTimeoutOrder() {
        log.info("定时处理超时订单{}", LocalDateTime.now());
        LocalDateTime now = LocalDateTime.now().plusMinutes(-15);
        //select * from orders where status = ? and create_time < now() - 15分钟
        List<Orders> byStatusAndOrderTimeLT = orderMapper.getByStatusAndOrderTimeLT(Orders.PENDING_PAYMENT, now);
        if (byStatusAndOrderTimeLT!= null && !byStatusAndOrderTimeLT.isEmpty()) {
            for(Orders order : byStatusAndOrderTimeLT) {
                order.setStatus(Orders.CANCELLED);
                order.setCancelReason("订单超时,自动取消");
                order.setCancelTime(LocalDateTime.now());
                orderMapper.update(order);
            }
        }
    }

    /**
     * 处理一直处于派送中状态的订单
     */
    @Scheduled(cron ="0 0 1 * * ?")//每天凌晨触发一次
    public void processDeliveryOrder(){
        log.info("处理一直处于派送中状态的订单{}", LocalDateTime.now());
        LocalDateTime now = LocalDateTime.now().plusMinutes(-60);
        List<Orders> byStatusAndOrderTimeLT = orderMapper.getByStatusAndOrderTimeLT(Orders.DELIVERY_IN_PROGRESS, now);
        if (byStatusAndOrderTimeLT!= null && !byStatusAndOrderTimeLT.isEmpty()) {
            for(Orders order : byStatusAndOrderTimeLT) {
                order.setStatus(Orders.COMPLETED);
                orderMapper.update(order);
            }
        }
    }
}
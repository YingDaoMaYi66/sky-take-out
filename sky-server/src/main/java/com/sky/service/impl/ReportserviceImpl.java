package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.service.ReportService;
import com.sky.vo.TurnoverReportVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
@Slf4j
public class ReportserviceImpl implements ReportService {
    @Autowired
    private OrderMapper orderMapper;

    /**
     * 营业额统计
     *
     * @param begin 起始日期
     * @param end   截止日期
     * @return TurnoverReportVO
     */
    @Override
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end ) {
        //当前集合用于存放从begin到end范围内的每天的日期
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        LocalDate date = begin;
        while (!date.equals(end)) {
            //日期计算，计算指定日期的后一天对应的日期
             date = date.plusDays(1);
            dateList.add(date);
        }
        //查询相应日期的营业额
        //当天的最早时间
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        //当天的最晚时间
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);
        List<Orders> orderList= orderMapper.getTurnoverStatistics(beginTime, endTime,Orders.COMPLETED);

        // 构建日期与金额的映射
        Map<LocalDate, BigDecimal> dateToAmountMap = new HashMap<>();
        for (Orders order : orderList) {
            //这行代码的作用是从 `order` 对象中获取订单时间（`order.getOrderTime()`），并将其转换为 `LocalDate` 类型，只保留日期部分（去掉时间部分）。
            LocalDate orderDate = order.getOrderTime().toLocalDate();
            // 通过 `getOrDefault` 方法获取当前日期的金额，如果没有则默认为 0
            BigDecimal currentAmount = dateToAmountMap.getOrDefault(orderDate, BigDecimal.ZERO);
            dateToAmountMap.put(orderDate, currentAmount.add(order.getAmount()));
        }

        // 构建金额列表，确保与日期一一对应
        List<String> moneyList = new ArrayList<>();
        for (LocalDate currentDate : dateList) {
            BigDecimal amount = dateToAmountMap.getOrDefault(currentDate, BigDecimal.ZERO);
            moneyList.add(String.valueOf(amount));
        }

        return TurnoverReportVO
                .builder()
                .dateList(StringUtils.join(dateList, ","))
                .turnoverList( StringUtils.join(moneyList, ","))
                .build();
    }
}

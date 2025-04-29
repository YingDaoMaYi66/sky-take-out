package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.entity.User;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
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
    @Autowired
    private UserMapper userMapper;

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

    /**
     * 统计指定时间内的用户数据
     *
     * @param begin 起始日期
     * @param end   截止日期
     * @return UserReportVO
     */
    @Override
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        //当前集合用于存放从begin到end范围内的每天的日期
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        LocalDate date = begin;
        while (!date.equals(end)) {
            //日期计算，计算指定日期的后一天对应的日期
            date = date.plusDays(1);
            dateList.add(date);
        }

        //图表起始天的最早时间  注意：LocalDateTime 包含具体的时分秒，而LocalDate只包含日期
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        //图表结尾当天的最晚时间
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);

        //查询相应日期的用户数量
        List<User> userList = userMapper.getCurrentUserCount(beginTime,endTime);
        // 构建日期与用户数量的映射
        Map<LocalDate, Integer> dateToUserCountMap = new HashMap<>();
        for (LocalDate currentDate : dateList) {
            // 获取当天的起始时间和结束时间
            LocalDateTime dayStart = LocalDateTime.of(currentDate, LocalTime.MIN);
            LocalDateTime dayEnd = LocalDateTime.of(currentDate, LocalTime.MAX);
            // 查询当天的用户数量
            int userCount = (int) userList.stream()
                    .filter(user -> !user.getCreateTime().isBefore(dayStart) && !user.getCreateTime().isAfter(dayEnd))
                    .count();
            dateToUserCountMap.put(currentDate, userCount);
        }
        // 构建用户总量列表
        List<String> totalUserList = new ArrayList<>();
        int cumulativeUserCount = 0;
        for (LocalDate currentDate : dateList) {
            cumulativeUserCount += dateToUserCountMap.getOrDefault(currentDate, 0);
            totalUserList.add(String.valueOf(cumulativeUserCount));
        }
        // 构建新增用户列表
        List<String> newUserList = new ArrayList<>();
        for (LocalDate currentDate : dateList) {
            newUserList.add(String.valueOf(dateToUserCountMap.getOrDefault(currentDate, 0)));
        }
        return UserReportVO
                .builder()
                .dateList(StringUtils.join(dateList,","))
                .totalUserList(StringUtils.join(totalUserList,","))
                .newUserList(StringUtils.join(newUserList,","))
                .build();
    }
}

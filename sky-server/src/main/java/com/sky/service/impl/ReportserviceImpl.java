package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.entity.User;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
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
import java.util.stream.Collectors;

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

    /**
     * 统计指定时间区间的订单数据
     * @param begin 起始日期
     * @param end 截止日期
     * @return 返回OrderReportVO对象
     */
    @Override
    public OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end) {
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

        //获取时间范围内的总订单列表
        List<Orders> orderList= orderMapper.getOrderStatistics(beginTime, endTime);
        //构建日期与订单数量的映射
        Map<LocalDate, Integer> dateToOrderCountMap = new HashMap<>();
        for (LocalDate currentDate : dateList) {
            // 获取当天的起始时间和结束时间
            LocalDateTime dayStart = LocalDateTime.of(currentDate, LocalTime.MIN);
            LocalDateTime dayEnd = LocalDateTime.of(currentDate, LocalTime.MAX);
            // 查询当天的订单数量
            int orderCount = (int) orderList.stream()
                    .filter(order -> !order.getOrderTime().isBefore(dayStart) && !order.getOrderTime().isAfter(dayEnd))
                    .count();
            dateToOrderCountMap.put(currentDate, orderCount);
        }
        // 构建订单总量列表
        List<Integer> orderTotalList = new ArrayList<>();
        for (LocalDate currentDate : dateList) {
            Integer TheDayOrdersCount = dateToOrderCountMap.getOrDefault(currentDate, 0);
            orderTotalList.add(TheDayOrdersCount);
        }


        //构建当天完成订单数量列表
        Map<LocalDate, Integer> dateToCompletedOrderCountMap = new HashMap<>();
        for (LocalDate currentDate : dateList) {
            // 获取当天的起始时间和结束时间
            LocalDateTime dayStart = LocalDateTime.of(currentDate, LocalTime.MIN);
            LocalDateTime dayEnd = LocalDateTime.of(currentDate, LocalTime.MAX);
            // 查询当天的完成订单数量
            int completedOrderCount = (int) orderList.stream()
                    .filter(order -> Objects.equals(order.getStatus(), Orders.COMPLETED) && !order.getOrderTime().isBefore(dayStart) && !order.getOrderTime().isAfter(dayEnd))
                    .count();
            dateToCompletedOrderCountMap.put(currentDate, completedOrderCount);
        }
        //构建订单完成列表
        List<Integer> orderCompletedList = new ArrayList<>();
        for (LocalDate currentDate : dateList) {
            Integer TheDayOrdersCount = dateToCompletedOrderCountMap.getOrDefault(currentDate, 0);
            orderCompletedList.add(TheDayOrdersCount);
        }
        //订单总数
        Integer totalOrderCount = orderTotalList.size();
        //订单完成数
        Integer completedOrderCount = (int)orderCompletedList.stream()
                .filter(orderCount -> orderCount > 0)
                .count();
        //订单完成率
        double orderCompletionRate = 0.0;
        if(totalOrderCount !=0){
            orderCompletionRate =  completedOrderCount.doubleValue() / (double) totalOrderCount;
        }

        return OrderReportVO.builder()
                .orderCompletionRate(orderCompletionRate)
                .totalOrderCount(totalOrderCount)
                .validOrderCount(completedOrderCount)
                .dateList(StringUtils.join(dateList,","))
                .orderCountList(StringUtils.join(orderTotalList, ","))
                .validOrderCountList(StringUtils.join(orderCompletedList, ","))
                .build();
    }
    /**
     * 获取销售前十的商品数据
     *
     * @param begin 起始日期
     * @param end   截止日期
     * @return SalesTop10ReportVO
     */
    @Override
    public SalesTop10ReportVO getSalesTop10(LocalDate begin, LocalDate end) {
        //图表起始天的最早时间  注意：LocalDateTime 包含具体的时分秒，而LocalDate只包含日期
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        //图表结尾当天的最晚时间
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);
        List<GoodsSalesDTO> salesTop10 = orderMapper.getSalesTop(beginTime, endTime);

        //获取菜品及其套餐列表
        //:: 是 Java 中的方法引用符号，用于简化 Lambda 表达式的写法。它可以引用类的静态方法、实例方法或构造方法。
        List<String>names = salesTop10.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList());
        String nameList = StringUtils.join(names, ",");

        //获取菜品销量列表
        List<Integer> numbers = salesTop10.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList());
        String numberList = StringUtils.join(numbers, ",");

        return SalesTop10ReportVO.builder()
                .nameList(nameList)
                .numberList(numberList)
                .build();
    }
}

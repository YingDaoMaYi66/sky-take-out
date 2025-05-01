package com.sky.service;

import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;

import java.time.LocalDate;

public interface ReportService {
    /**
     * 营业额统计
     * @param begin 起始日期
     * @param end 截止日期
     * @return TurnoverReportVO
     */
    TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end);
    /**
     * 统计指定时间内的用户数据
     * @param begin 起始日期
     * @param end 截止日期
     * @return UserReportVO
     */
    UserReportVO getUserStatistics(LocalDate begin, LocalDate end);
    /**
     * 统计指定时间内的订单数据
     * @param begin 起始日期
     * @param end 截止日期
     * @return OrderReportVO
     */
    OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end);
    /**
     * 统计指定时间内的销量排名前10的商品
     * @param begin 起始日期
     * @param end 截止日期
     * @return OrderReportVO
     */
    SalesTop10ReportVO getSalesTop10(LocalDate begin, LocalDate end);
}

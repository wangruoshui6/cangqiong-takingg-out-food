package com.sky.service;

import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;


public interface ReportService {
    /**
     * 统计指定时间区域内的订单额
     * @param begin
     * @param end
     * @return
     */
    TurnoverReportVO getTurnoverStatistic(LocalDate begin,LocalDate end);
    /**
     * 统计指定时间区域内的用户数据
     * @param begin
     * @param end
     * @return
     */
    UserReportVO getUserStatistic(LocalDate begin,LocalDate end);
}

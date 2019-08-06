package com.yqg.manage.service.loan;


import com.yqg.order.dao.ManTodayDataStatisticsDao;
import com.yqg.order.entity.ManTodayDataStatistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author alan
 */
@Component
public class ManTodayDataStatisticsService {
    @Autowired
    private ManTodayDataStatisticsDao manTodayDataStatisticsDao;

    /**
     * 添加今日数据历史记录*/
    public void addManTodayDataStatistics(ManTodayDataStatistics data) throws Exception {
        this.manTodayDataStatisticsDao.insert(data);
    }
}

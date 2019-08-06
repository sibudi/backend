package com.yqg.manage.service.loan;

import com.yqg.order.dao.ManLoanDataStatisticsDao;
import com.yqg.order.entity.ManLoanDataStatistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author alan
 */
@Component
public class ManLoanDataStatisticsService {
    @Autowired
    private ManLoanDataStatisticsDao manLoanDataStatisticsDao;

    /**
     * 添加数据分析历史记录*/
    public void addManLoanDataStatistics(ManLoanDataStatistics data) throws Exception {
        this.manLoanDataStatisticsDao.insert(data);
    }
}

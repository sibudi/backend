package com.yqg.manage.controller.loan;


import com.yqg.common.annotations.H5Request;
import com.yqg.common.models.ResponseEntitySpec;
import com.yqg.common.models.builders.ResponseEntitySpecBuilder;
import com.yqg.manage.scheduling.OverDueOrderStatistics;
import com.yqg.manage.service.loan.LoanDataStatisticsService;
import com.yqg.manage.service.loan.request.FindOrderForCompanyRequest;
import com.yqg.mongo.entity.ManOverDueOrderAnalysisMongo;
import com.yqg.order.entity.ManLoanDataStatistics;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.core.MediaType;
import java.util.List;


/**
 * @author alan
 */
@RestController
@H5Request
public class LoanDataStatisticsController {
    @Autowired
    private LoanDataStatisticsService loanDataStatisticsService;

    @Autowired
    private OverDueOrderStatistics overDueOrderStatistics;

    @ApiOperation("数据App查询贷款数据统计")
    @RequestMapping(value = "/manage/orderLoanDataStatistics", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<ManLoanDataStatistics> orderLoanDataStatistics() throws Exception {
        return ResponseEntitySpecBuilder.success(this.loanDataStatisticsService.getLoanDataFromRedis());
    }

    @ApiOperation("数据App查询今日数据统计")
    @RequestMapping(value = "/manage/todayDataStatistics", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<Object> todayDataStatistics() throws Exception {
        return ResponseEntitySpecBuilder.success(this.loanDataStatisticsService.getTodayDataFromRedis());
    }

    @ApiOperation("数据App查询今日数据统计")
    @RequestMapping(value = "/manage/overDueOrderAnalysisInit", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<Object> overDueOrderAnalysisInit() throws Exception {
        this.overDueOrderStatistics.initData();
        return ResponseEntitySpecBuilder.success();
    }

    @ApiOperation("数据App查询逾期数据分析")
    @RequestMapping(value = "/manage/getOverDueDataByPage", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<List<ManOverDueOrderAnalysisMongo>> getOverDueDataByPage(@RequestBody FindOrderForCompanyRequest request) throws Exception {
        return ResponseEntitySpecBuilder.success(this.overDueOrderStatistics.getOverDueDataByPage(request));
    }
}

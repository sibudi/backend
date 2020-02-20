package com.yqg.scheduling;


import com.yqg.service.LoanService;
import com.yqg.service.OverdueService;
import com.yqg.service.PayService;
import com.yqg.service.pay.RepayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Created by Didit Dwianto on 2017/12/28.
 */
@Component
@Slf4j
public class PayScheduling {

    @Autowired
    private LoanService loanService;

    @Autowired
    private PayService payService;

    @Autowired
    private RepayService repayService;

    @Autowired
    private OverdueService overdueService;
    /**
     *    放款定时任务
     * */
    @Scheduled(cron = "0 0/5 * * * ? ")
    public void loanPay() {
        log.info("======================================loanPay begin ==========================================");
        this.loanService.loanPay();
        log.info("======================================loanPay end0 ==========================================");
    }

    /**
     *    查询待放款订单 任务
     * */
    @Scheduled(cron = "0 0/3 * * * ? ")
    public void cheakLoanOrder() {
        log.info("======================================cheakLoaningOrder begin ==========================================");
        this.loanService.cheakLoanOrder();
        log.info("======================================cheakLoaningOrder end0 ==========================================");
    }

    /**
     *    查询代还款 订单 任务
     * */
    @Scheduled(cron = "0 0/3 * * * ? ")
    public void cheakRepayOrder1() {
        log.info("======================================cheakRepayOrder1 begin ==========================================");
        this.payService.cheakRepayOrder(0);
        log.info("======================================cheakRepayOrder1 end0 ==========================================");
    }

    @Scheduled(cron = "0 0/3 * * * ? ")
    public void cheakRepayOrder2() {
        log.info("======================================cheakRepayOrder2 begin ==========================================");
        this.payService.cheakRepayOrder(1);
        log.info("======================================cheakRepayOrder2 end0 ==========================================");
    }

    @Scheduled(cron = "0 0/3 * * * ? ")
    public void cheakRepayOrder3() {
        log.info("======================================cheakRepayOrder3 begin ==========================================");
        this.payService.cheakRepayOrder(2);
        log.info("======================================cheakRepayOrder3 end0 ==========================================");
    }

    @Scheduled(cron = "0 0/3 * * * ? ")
    public void cheakRepayOrder4() {
        log.info("======================================cheakRepayOrder4 begin ==========================================");
        this.payService.cheakRepayOrder(3);
        log.info("======================================cheakRepayOrder4 end0 ==========================================");
    }

    @Scheduled(cron = "0 0/3 * * * ? ")
    public void cheakRepayOrder5() {
        log.info("======================================cheakRepayOrder5 begin ==========================================");
        this.payService.cheakRepayOrder(4);
        log.info("======================================cheakRepayOrder5 end0 ==========================================");
    }

    @Scheduled(cron = "0 0/3 * * * ? ")
    public void cheakRepayOrder6() {
        log.info("======================================cheakRepayOrder6 begin ==========================================");
        this.payService.cheakRepayOrder(5);
        log.info("======================================cheakRepayOrder6 end0 ==========================================");
    }

    @Scheduled(cron = "0 0/3 * * * ? ")
    public void cheakRepayOrder7() {
        log.info("======================================cheakRepayOrder7 begin ==========================================");
        this.payService.cheakRepayOrder(6);
        log.info("======================================cheakRepayOrder7 end0 ==========================================");
    }

    @Scheduled(cron = "0 0/3 * * * ? ")
    public void cheakRepayOrder8() {
        log.info("======================================cheakRepayOrder8 begin ==========================================");
        this.payService.cheakRepayOrder(7);
        log.info("======================================cheakRepayOrder8 end0 ==========================================");
    }

    @Scheduled(cron = "0 0/3 * * * ? ")
    public void cheakRepayOrder9() {
        log.info("======================================cheakRepayOrder9 begin ==========================================");
        this.payService.cheakRepayOrder(8);
        log.info("======================================cheakRepayOrder9 end0 ==========================================");
    }

    @Scheduled(cron = "0 0/3 * * * ? ")
    public void cheakRepayOrder() {
        log.info("======================================cheakRepayOrder begin ==========================================");
        this.payService.cheakRepayOrder(9);
        log.info("======================================cheakRepayOrder end0 ==========================================");
    }

    @Scheduled(cron = "0 0 12,18 * * ? ")
    public void checkRdnRepayment() {
        log.info("======================================BulkRdnRepayment begin ==========================================");
        this.repayService.BulkRdnRepayment();
        log.info("======================================BulkRdnRepayment end ==========================================");
    }

    /**
     *     p2p的代放款款订单查询
     * */
    @Scheduled(cron = "0 0/3 * * * ? ")
    public void checkP2PLoanStatus(){
        log.info("======================================checkP2PLoanStatus begin ==========================================");
        this.loanService.checkP2PLoanStatus();
        log.info("======================================checkP2PLoanStatus end ==========================================");
    }


     /**
     *    同步逾期订单状态
     *    每天凌晨12点20分 更改订单状态为8（代还款 已逾期）
     * */
    @Scheduled(cron = "0 20 0 * * ?")
    public void overdue() {
        log.info("overdue begin");
        this.overdueService.overdue();
        log.info("overdue end");
    }


//    /**
//     *    20190411 凌晨切换数据库 重新跑此定时任务
//     * */
//    @Scheduled(cron = "0 25 03 11 4 ?")
//    public void overdue2() {
//        log.info("20190411 overdue begin");
//        this.overdueService.overdue();
//        log.info("20190411 overdue end");
//    }


    //  每天定时处理打款失败的订单
    @Scheduled(cron = "0 0 08,15 * * ?")
    public void checkOrdLoanFaild() {
        log.info("checkOrdLoanFaild begin");
        this.payService.checkOrdLoanFaild();
        log.info("checkOrdLoanFaild end");
    }

    //  每天定时处理打款失败的订单
    @Scheduled(cron = "0 57 15 03 7 ?")
    public void checkOrdLoanFaildNow() {
        log.info("dealWithLoanFaildOrder begin");
        this.payService.dealWithLoanFaildOrder();
        log.info("dealWithLoanFaildOrder end");
    }

    /**
     *    活动放款定时任务
     * */
    @Scheduled(cron = "0 0/3 * * * ? ")
    public void activityLoan() {
        log.info("======================================activityLoan begin ==========================================");
        this.loanService.activityLoan();
        log.info("======================================activityLoan end0 ==========================================");
    }


    /**
     *    查询活动放款中订单 任务
     * */
    @Scheduled(cron = "0 0/3 * * * ? ")
    public void cheakActivityLoan() {
        log.info("======================================cheakActivityLoan begin ==========================================");
        this.loanService.cheakActivityLoan();
        log.info("======================================cheakActivityLoan end0 ==========================================");
    }


//    /**
//     *    ????????
//     *    ????12?20? ???????8???? ????
//     * */
//    @Scheduled(cron = "0 52 0 * * ?")
//    public void manualRepayOrder() {
//        log.info("manualRepayOrder begin");
//        ManualRepayOrderRequest repayOrderRequest = new ManualRepayOrderRequest();
//        this.payService.manualOperationRepayOrder(repayOrderRequest);
//        log.info("manualRepayOrder end");
//    }

//
//    // 处理CIMB打款异常的订单
//    @Scheduled(cron = "0 45 12 20 11 ?")
//    public void dealWithPendingOrderByCIMB() {
//        log.info("dealWithPendingOrderByCIMB begin");
//        this.payService.dealWithPendingOrderByCIMB();
//        log.info("dealWithPendingOrderByCIMB end");
//    }

//
//    // 处理CIMB打款异常的订单
//    @Scheduled(cron = "0 35 16 3 4 ?")
//    public void dealWithPendingOrderByCIMB() {
//        log.info("dealWithPendingOrderByCIMB begin");
//        this.payService.dealWithPendingOrderByCIMB();
//        log.info("dealWithPendingOrderByCIMB end");
//    }

    /**
     *    查询代还款 账单 任务
     * */
    @Scheduled(cron = "0 0/3 * * * ? ")
    public void cheakRepayBill() {
        log.info("======================================cheakRepayBill begin ==========================================");
        this.payService.cheakRepayBill();
        log.info("======================================cheakRepayBill end0 ==========================================");
    }


}

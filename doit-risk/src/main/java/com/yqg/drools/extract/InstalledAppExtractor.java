package com.yqg.drools.extract;

import com.yqg.drools.beans.InstalledAppData;
import com.yqg.drools.model.InstalledAppInfo;
import com.yqg.drools.model.KeyConstant;
import com.yqg.drools.model.base.RuleSetEnum;
import com.yqg.drools.utils.DateUtil;
import com.yqg.drools.utils.JsonUtil;
import com.yqg.drools.utils.RuleUtils;
import com.yqg.mongo.dao.UserAppsDal;
import com.yqg.mongo.entity.UserAppsMongo;
import com.yqg.order.dao.OrdDao;
import com.yqg.order.entity.OrdOrder;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;


/*****
 * @Author zengxiangcai
 * Created at 2018/1/23
 * @Email zengxiangcai@yishufu.com
 *
 ****/


@Service
@Slf4j
public class InstalledAppExtractor implements BaseExtractor<InstalledAppInfo> {

    @Autowired
    private UserAppsDal userAppsDal;

    @Autowired
    private OrdDao ordDao;


    @Override
    public boolean filter(RuleSetEnum ruleSet) {
        return RuleSetEnum.INSTALL_APP.equals(ruleSet) || RuleSetEnum.RE_BORROWING_INSTALLED_APP
            .equals(ruleSet);
    }

    @Override
    public Optional<InstalledAppInfo> extractModel(OrdOrder order, KeyConstant keyConstant) {
        return buildInstalledAppInfo(order, keyConstant, true);
    }


    /***
     *
     * @param order
     * @param keyConstant
     * @param recursion 防止多次递归查询，只循环去上一次的数据后就不再递归去数据
     * @return
     */
    private Optional<InstalledAppInfo> buildInstalledAppInfo(OrdOrder order,
        KeyConstant keyConstant, boolean recursion) {

        UserAppsMongo mongo = new UserAppsMongo();
        mongo.setUserUuid(order.getUserUuid());
        mongo.setOrderNo(order.getUuid());
        List<UserAppsMongo> mongoList = this.userAppsDal.find(mongo);
        if (CollectionUtils.isEmpty(mongoList)) {
            log.warn("mongodb installed app is empty, orderNo: {}", order.getUuid());
            return Optional.empty();
        }

        String appData = mongoList.get(0).getData();
        if (StringUtils.isEmpty(appData)) {
            log.warn("the installed app is empty, orderNo: {}", order.getUuid());
            return Optional.empty();
        }
        List<InstalledAppData> appList = JsonUtil.toList(appData, InstalledAppData.class);

        if (CollectionUtils.isEmpty(appList)) {
            log.warn("the installed app is empty, orderNo: {}", order.getUuid());
            return Optional.empty();
        }

        InstalledAppInfo installedAppInfo = new InstalledAppInfo();

        List<String> validAppNames = appList.stream()
            .filter(elem -> !StringUtils.isEmpty(elem.getAppName()))
            .map(InstalledAppData::getAppName)
            .collect(Collectors.toList());

        //贷款app个数
        installedAppInfo.setAppForLoanCount(
            RuleUtils.distinctEqualsCount(keyConstant.getLoanAppWords(), validAppNames));

        //贷款app占比
        BigDecimal ratio = BigDecimal.valueOf(installedAppInfo.getAppForLoanCount())
            .divide(BigDecimal.valueOf(validAppNames.size()), 4, BigDecimal.ROUND_HALF_DOWN);

        installedAppInfo.setAppForLoanRatio(ratio);

        installedAppInfo.setTotalApps(validAppNames.stream().count());

        //
        Date year1971 = DateUtil.stringToDate("1971-01-01",DateUtil.FMT_YYYY_MM_DD);//不少系统自带软件更新时间是1970，排除掉
        Optional<InstalledAppData> latestUpdateApp =
                appList.stream().filter(elem -> elem.getLastUpdateTime() != null && elem.getLastUpdateTime().compareTo(year1971) > 0).max(Comparator.comparing(InstalledAppData::getLastUpdateTime));
        Optional<InstalledAppData> earliestUpdateApp =
                appList.stream().filter(elem -> elem.getLastUpdateTime() != null && elem.getLastUpdateTime().compareTo(year1971) > 0).min(Comparator.comparing(InstalledAppData::getLastUpdateTime));

        if (latestUpdateApp.isPresent()) {
            installedAppInfo.setDiffDaysBetweenLatestUpdateTimeAndCommitTime(DateUtil.getDiffDays(latestUpdateApp.get().getLastUpdateTime(), order.getApplyTime()));
        }
        if (earliestUpdateApp.isPresent()) {
            installedAppInfo.setDiffDaysBetweenEarliestUpdateTimeAndCommitTime(DateUtil.getDiffDays(earliestUpdateApp.get().getLastUpdateTime(),
                    order.getApplyTime()));
        }
        if (latestUpdateApp.isPresent() && earliestUpdateApp.isPresent()) {
            installedAppInfo.setDiffDaysBetweenForEarliestAndLatestUpdateTime(DateUtil.getDiffDays(earliestUpdateApp.get().getLastUpdateTime(),
                    latestUpdateApp.get().getLastUpdateTime()));
        }

        //app分类信息
        Map<KeyConstant.AppCategoryEnum, String> categoryKeyWords = keyConstant.getAppCategoryKeyWords();
        if (categoryKeyWords != null) {
            installedAppInfo.setAppCountForNews(RuleUtils.distinctKeyWordsCount(categoryKeyWords.get(KeyConstant.AppCategoryEnum.NEWS), validAppNames));
            installedAppInfo.setAppCountForEnterprise(RuleUtils.distinctKeyWordsCount(categoryKeyWords.get(KeyConstant.AppCategoryEnum.ENTERPRISE)
                    , validAppNames));
            installedAppInfo.setAppCountForBeauty(RuleUtils.distinctKeyWordsCount(categoryKeyWords.get(KeyConstant.AppCategoryEnum.BEAUTY)
                    , validAppNames));
            installedAppInfo.setAppCountForGambling(RuleUtils.distinctKeyWordsCount(categoryKeyWords.get(KeyConstant.AppCategoryEnum.GAMBLING)
                    , validAppNames));
            installedAppInfo.setAppCountForCreditCard(RuleUtils.distinctKeyWordsCount(categoryKeyWords.get(KeyConstant.AppCategoryEnum.CREDIT_CARD)
                    , validAppNames));
            installedAppInfo.setAppCountForBeautyPicture(RuleUtils.distinctKeyWordsCount(categoryKeyWords.get(KeyConstant.AppCategoryEnum.BEAUTY_PICTURE),
                    validAppNames));
            installedAppInfo.setAppCountForPhotography(RuleUtils.distinctKeyWordsCount(categoryKeyWords.get(KeyConstant.AppCategoryEnum.PHOTOGRAPHY), validAppNames));
            installedAppInfo.setAppCountForEcommerce(RuleUtils.distinctKeyWordsCount(categoryKeyWords.get(KeyConstant.AppCategoryEnum.E_COMMERCE), validAppNames));
            installedAppInfo.setAppCountForGame(RuleUtils.distinctKeyWordsCount(categoryKeyWords.get(KeyConstant.AppCategoryEnum.GAME)
                    , validAppNames));
            installedAppInfo.setAppCountForSocial(RuleUtils.distinctKeyWordsCount(categoryKeyWords.get(KeyConstant.AppCategoryEnum.SOCIAL)
                    , validAppNames));
            installedAppInfo.setAppCountForTaxBPJS(RuleUtils.distinctKeyWordsCount(categoryKeyWords.get(KeyConstant.AppCategoryEnum.TAX_BPJS)
                    , validAppNames));
            installedAppInfo.setAppCountForBank(RuleUtils.distinctKeyWordsCount(categoryKeyWords.get(KeyConstant.AppCategoryEnum.BANK)
                    , validAppNames));
            installedAppInfo.setAppCountForCinema(RuleUtils.distinctKeyWordsCount(categoryKeyWords.get(KeyConstant.AppCategoryEnum.CINEMA)
                    , validAppNames));
            installedAppInfo.setAppCountForTicket(RuleUtils.distinctKeyWordsCount(categoryKeyWords.get(KeyConstant.AppCategoryEnum.TICKET)
                    , validAppNames));

        }

        if (order.getBorrowingCount() < 2 || recursion == false) {
            return Optional.of(installedAppInfo);
        }



        /////////////////////////////////复借
        //查询上一笔成功借款订单
        List<OrdOrder> historyOrders = ordDao.getLastSuccessOrder(order.getUserUuid());
        if (CollectionUtils.isEmpty(historyOrders)) {
            return Optional.of(installedAppInfo);
        }
        Optional<OrdOrder> latestOrder = historyOrders.stream().max(
            Comparator.comparing(OrdOrder::getId));

        //防止递归进入死循环[查询id最大的订单]
        if (!latestOrder.isPresent()) {
            return Optional.of(installedAppInfo);
        }
        installedAppInfo.setHasLatestOrder(true);//复借是否有上笔订单
        //递归查询上一笔订单安装的app信息
        Optional<InstalledAppInfo> lastLoanInstalledInfo = buildInstalledAppInfo(latestOrder.get(),
            keyConstant, false);
        if (lastLoanInstalledInfo.isPresent()) {
            BigDecimal lastRatio = lastLoanInstalledInfo.get().getAppForLoanRatio();
            Long lastCount = lastLoanInstalledInfo.get().getAppForLoanCount() == null ? 0L
                : lastLoanInstalledInfo.get().getAppForLoanCount();
            installedAppInfo.setAppForLoanRatioChange(installedAppInfo.getAppForLoanRatio()
                .subtract(lastRatio != null ? lastRatio : BigDecimal.ZERO));
            installedAppInfo.setIncrementalAppForLoanCount(
                installedAppInfo.getAppForLoanCount() - lastCount);
        }

        return Optional.of(installedAppInfo);
    }

}

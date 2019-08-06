package com.yqg.drools.model;

import com.yqg.common.enums.order.BlackListTypeEnum;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/*****
 * @Author zengxiangcai
 * created at ${date}
 * @email zengxiangcai@yishufu.com
 * 关键的产量信息配置
 ****/

@Getter
@Setter
public class KeyConstant {

    private String sensitiveWords;//敏感词以#隔开
    private String interrelatedWords;//同业词
    private String overdueWords;//逾期词
    private String negativeWords;//负面词
    private String rejectWords;//拒绝词

    private String relativeWords;//亲属词

    private String loanAppWords;//贷款app列表

    private String jarkatAddressWords;//雅加达地区地址

    private String jarkatTelNumbers;//雅加达号码区号

    private String smsRuleBody;// 短信：逾期天数关键词

    private String earthquakeArea;//地震省份

    private String earthquakeCity;//地震城市

    private String  jarkatAddressWordsFor100Rmb;//100rmb产品的地址关键词

    private String jarkatAddressWordsNormal; //新的600rmb产品放开的城市

    private String  overDuePositionMan;//男性高逾期职业

    private String overDuePositionFeMen; //女性高逾期职业

    private String  homeProviceMan;//男性高风险地区

    private String homeProviceFeMen; //女性高风险地区

    private String  homeProviceMan150;//男性高风险地区

    private String homeProviceFeMen150; //女性高风险地区
    private String  homeProviceMan80;//男性高风险地区



    private Map<AppCategoryEnum, String> appCategoryKeyWords;//app分类关键词

    @Getter
    public enum AppCategoryEnum {
        NEWS(BlackListTypeEnum.APP_COUNT_FOR_NEWS),
        ENTERPRISE(BlackListTypeEnum.APP_COUNT_FOR_ENTERPRISE),
        BEAUTY(BlackListTypeEnum.APP_COUNT_FOR_BEAUTY),
        GAMBLING(BlackListTypeEnum.APP_COUNT_FOR_GAMBLING),
        CREDIT_CARD(BlackListTypeEnum.APP_COUNT_FOR_CREDIT_CARD),
        BEAUTY_PICTURE(BlackListTypeEnum.APP_COUNT_FOR_BEAUTY_PICTURE),
        PHOTOGRAPHY(BlackListTypeEnum.APP_COUNT_FOR_PHOTOGRAPHY),
        E_COMMERCE(BlackListTypeEnum.APP_COUNT_FOR_E_COMMERCE),
        GAME(BlackListTypeEnum.APP_COUNT_FOR_GAME),
        SOCIAL(BlackListTypeEnum.APP_COUNT_FOR_SOCIAL),
        TAX_BPJS(BlackListTypeEnum.APP_COUNT_FOR_TAX_BPJS),
        BANK(BlackListTypeEnum.APP_COUNT_FOR_BANK),
        CINEMA(BlackListTypeEnum.APP_COUNT_FOR_CINEMA),
        TICKET(BlackListTypeEnum.APP_COUNT_FOR_TICKET);

        AppCategoryEnum(BlackListTypeEnum relatedRuleName) {
            this.relatedRuleName = relatedRuleName;
        }

        private BlackListTypeEnum relatedRuleName;
    }
}

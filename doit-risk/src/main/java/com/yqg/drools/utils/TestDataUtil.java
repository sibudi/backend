package com.yqg.drools.utils;

import com.yqg.common.enums.order.BlackListTypeEnum;
import com.yqg.drools.model.base.RuleEnum;
import com.yqg.system.entity.SysAutoReviewRule;
import java.util.HashMap;
import java.util.Map;

/*****
 * @Author Jeremy Lawrence
 * created at ${date}
 *
 * ????
 *
 ****/
public class TestDataUtil {


    public static Map<String, SysAutoReviewRule> getTestAllRules() {

        Map<String, SysAutoReviewRule> autoRules = testBaseRule();

        //??????
        SysAutoReviewRule rule = new SysAutoReviewRule();
        rule.setRuleValue("15");
        rule.setRuleDetailType(RuleEnum.LATEST_LOAN_OVERDUE_DAYS.getRuleName());
        autoRules.put(RuleEnum.LATEST_LOAN_OVERDUE_DAYS.getRuleName(), rule);


        //????????
        rule = new SysAutoReviewRule();
        rule.setRuleValue("15");
        rule.setRuleDetailType(RuleEnum.CONTACT_MOBILE_COUNT.getRuleName());
        autoRules.put(RuleEnum.CONTACT_MOBILE_COUNT.getRuleName(), rule);

        //?????
        rule = new SysAutoReviewRule();
        rule.setRuleValue("1");
        rule.setRuleDetailType(RuleEnum.CONTACT_SENSITIVE_WORD_COUNT.getRuleName());
        autoRules.put(RuleEnum.CONTACT_SENSITIVE_WORD_COUNT.getRuleName(), rule);

        //?????
        rule = new SysAutoReviewRule();
        rule.setRuleValue("5");
        rule.setRuleDetailType(RuleEnum.CONTACT_INTERRELATED_WORD_COUNT.getRuleName());
        autoRules.put(RuleEnum.CONTACT_INTERRELATED_WORD_COUNT.getRuleName(), rule);

        //????30??????????
        rule = new SysAutoReviewRule();
        rule.setRuleValue("1");
        rule.setRuleDetailType(RuleEnum.CONTACT_OWNER_MOBILE_OVERDUE30_COUNT.getRuleName());
        autoRules.put(RuleEnum.CONTACT_OWNER_MOBILE_OVERDUE30_COUNT.getRuleName(), rule);

        //?????30??????????
        rule = new SysAutoReviewRule();
        rule.setRuleValue("1");
        rule.setRuleDetailType(RuleEnum.CONTACT_MOBILE_OVERDUE30_COUNT.getRuleName());
        autoRules.put(RuleEnum.CONTACT_MOBILE_OVERDUE30_COUNT.getRuleName(), rule);



        //??????30????????
        rule = new SysAutoReviewRule();
        rule.setRuleValue("1");
        rule.setRuleDetailType(RuleEnum.CALL_INFO_MOBILE_OVERDUE30_COUNT.getRuleName());
        autoRules.put(RuleEnum.CALL_INFO_MOBILE_OVERDUE30_COUNT.getRuleName(), rule);


        //??????????????
        rule = new SysAutoReviewRule();
        rule.setRuleValue("1");
        rule.setRuleDetailType(RuleEnum.CALL_INFO_LATEST_CALL_DIFF_DAY.getRuleName());
        autoRules.put(RuleEnum.CALL_INFO_LATEST_CALL_DIFF_DAY.getRuleName(), rule);

        //?????15???????
        rule = new SysAutoReviewRule();
        rule.setRuleValue("");
        rule.setRuleDetailType(RuleEnum.CALL_INFO_ACTIVE_AT_EVENING.getRuleName());
        autoRules.put(RuleEnum.CALL_INFO_ACTIVE_AT_EVENING.getRuleName(), rule);

        //?????
        rule = new SysAutoReviewRule();
        rule.setRuleValue("3");
        rule.setRuleDetailType(RuleEnum.SHORT_MESSAGE_OVERDUE_WORDS.getRuleName());
        autoRules.put(RuleEnum.SHORT_MESSAGE_OVERDUE_WORDS.getRuleName(), rule);

        //?????
        rule = new SysAutoReviewRule();
        rule.setRuleValue("80");
        rule.setRuleDetailType(RuleEnum.SHORT_MESSAGE_NEGATIVE_WORDS.getRuleName());
        autoRules.put(RuleEnum.SHORT_MESSAGE_NEGATIVE_WORDS.getRuleName(), rule);

        //?????
        rule = new SysAutoReviewRule();
        rule.setRuleValue("12");
        rule.setRuleDetailType(RuleEnum.SHORT_MESSAGE_INTERRELATED_WORDS.getRuleName());
        autoRules.put(RuleEnum.SHORT_MESSAGE_INTERRELATED_WORDS.getRuleName(), rule);


        //??????????15???
        rule = new SysAutoReviewRule();
        rule.setRuleValue("0");
        rule.setRuleDetailType(RuleEnum.SHORT_MESSAGE_CALLEE_OVERDUE15_COUNT.getRuleName());
        autoRules.put(RuleEnum.SHORT_MESSAGE_CALLEE_OVERDUE15_COUNT.getRuleName(), rule);



        //?30???????
        rule = new SysAutoReviewRule();
        rule.setRuleValue("3");
        rule.setRuleDetailType(RuleEnum.SHORT_MESSAGE_RECENT30DAY_REJECT_COUNT.getRuleName());
        autoRules.put(RuleEnum.SHORT_MESSAGE_RECENT30DAY_REJECT_COUNT.getRuleName(), rule);


        //?30????????
        rule = new SysAutoReviewRule();
        rule.setRuleValue("10");
        rule.setRuleDetailType(RuleEnum.SHORT_MESSAGE_RECENT30DAY_OVERDUE_COUNT.getRuleName());
        autoRules.put(RuleEnum.SHORT_MESSAGE_RECENT30DAY_OVERDUE_COUNT.getRuleName(), rule);

        //?30??????????
        rule = new SysAutoReviewRule();
        rule.setRuleValue("1");
        rule.setRuleDetailType(RuleEnum.SHORT_MESSAGE_RECENT30DAY_DISTINCT_OVERDUE_COUNT.getRuleName());
        autoRules.put(RuleEnum.SHORT_MESSAGE_RECENT30DAY_DISTINCT_OVERDUE_COUNT.getRuleName(), rule);


        //?15?????
        rule = new SysAutoReviewRule();
        rule.setRuleValue("");
        rule.setRuleDetailType(RuleEnum.SHORT_MESSAGE_RECENT15DAY_MSG_COUNT.getRuleName());
        autoRules.put(RuleEnum.SHORT_MESSAGE_RECENT15DAY_MSG_COUNT.getRuleName(), rule);

        //??app??
        rule = new SysAutoReviewRule();
        rule.setRuleValue("10");
        rule.setRuleDetailType(RuleEnum.INSTALLED_APP_LOAN_APP_COUNT.getRuleName());
        autoRules.put(RuleEnum.INSTALLED_APP_LOAN_APP_COUNT.getRuleName(), rule);

        //??app??
        rule = new SysAutoReviewRule();
        rule.setRuleValue("0.25");
        rule.setRuleDetailType(RuleEnum.INSTALLED_APP_LOAN_APP_RATIO.getRuleName());
        autoRules.put(RuleEnum.INSTALLED_APP_LOAN_APP_RATIO.getRuleName(), rule);

        //??app????
        rule = new SysAutoReviewRule();
        rule.setRuleValue("");
        rule.setRuleDetailType(RuleEnum.INSTALLED_APP_LOAN_APP_INCREMENT.getRuleName());
        autoRules.put(RuleEnum.INSTALLED_APP_LOAN_APP_INCREMENT.getRuleName(), rule);

        //??app????
        rule = new SysAutoReviewRule();
        rule.setRuleValue("");
        rule.setRuleDetailType(RuleEnum.INSTALLED_APP_LOAN_APP_RATIO_CHANGE.getRuleName());
        autoRules.put(RuleEnum.INSTALLED_APP_LOAN_APP_RATIO_CHANGE.getRuleName(), rule);

        return autoRules;

    }

    private static Map<String, SysAutoReviewRule> testBaseRule() {
        Map<String, SysAutoReviewRule> autoRules = new HashMap<>();

        //???
        SysAutoReviewRule rule = new SysAutoReviewRule();
        rule.setRuleData(
            "rentenir#wakilkan kartu kredit#wakilkan pelunasan#pengejaran pelunasan#konsultan bisnis#pinjam dana#pinjaman dana#calo pinjaman.#wakilin# wakilkan#diwakilkan#akun bersih#nama bersih.pengguna bersih#black list#nama buruk#terlambat#lewat batas#jatuh tempo#rekening dibekukan#rekening beku#judi#judi bola#mesin kartu#cuci uang#penipu - pembohong#pinjam#pinjem#buat kartu bank#buat kartu master#buat kartu visa#buat kartu atm#buat kartu kredit#kredit#pinjaman#hutang#utang#judi#Pementasan#Pinjaman dengan cara mencicil#Angsuran#kwetansi#kuitansi#bukti#Tingkatkan jumlahnya#laporan kredit#reputasi#pinjam dana online# pinjaman online");
        rule.setRuleDetailType(BlackListTypeEnum.CONTACT_SENSITIVI_COUNT.getMessage());
        autoRules.put(BlackListTypeEnum.CONTACT_SENSITIVI_COUNT.getMessage(), rule);

        rule = new SysAutoReviewRule();
        rule.setRuleData(
            "lunasi utang#lunasi hutang#tagih utang#taguh hutang#menagih hutang#lewat batas#lewat jatuh tempo#lewat jatoh tempo#taguh hutang#tagih utang#hukum bisnis#hukum ekonomi#terlambat#telat#lewat jatuh tempo#jatoh tempo#menghindari hutang#menghindar dari utang#rekaman penagihan#video penagihan#penipuan dana kredit bank#penipu kredit bank#tidak bayar utang pada hari nya#kapan bayar utang#kapan kembaliin duit.#udah harus bayar#cepat kembalikan utang#cepet kembalikan utang#cepat kembalikan hutang#cepet kembalikan hutang#debt collector#deb kolektor#debt kolektor#lari dari hutang#lari dari utang#pinjam dana ke bank dengan maksud jahat#terlambat bayar tagihan salama#tagihan menunggak#lewat % tempo");
        rule.setRuleDetailType(BlackListTypeEnum.SMS_SAME_COUNT.getMessage());
        autoRules.put(BlackListTypeEnum.SMS_SAME_COUNT.getMessage(), rule);

        rule = new SysAutoReviewRule();
        rule.setRuleData(
            "rentenir#wakilkan kartu kredit#wakilkan pelunasan#pengejaran pelunasan#hukum#pinjam dana#pinjaman dana#debt collector#tadah#wakilin#wakilkan#diwakilkan#akun bersih#nama bersih.pengguna bersih#black list#nama buruk#terlambat#lewat batas#jatuh tempo#dibekukan#pembekuan#judi#judi bola#mesin kartu#cuci uang#penipu - pembohong#pinjam#pinjem#Pementasan#Pinjaman dengan cara mencicil#Angsuran#Bagaimana cara menaikkan batas#pembersihan riwayat kredit#Kartu pendukung#pinjam dana online#pinjaman online#narkoba#pengedear narkoba#ekstasi#LSD#kokain#ga bisa hidup lagi#ga ada kemungkinan hidup lagi#bangkrut#kalah semua#bantuin bayar kartu kredit");
        rule.setRuleDetailType(BlackListTypeEnum.SMS_OVERDUE_COUNT.getMessage());
        autoRules.put(BlackListTypeEnum.SMS_OVERDUE_COUNT.getMessage(), rule);

        rule = new SysAutoReviewRule();
        rule.setRuleData(
            "Rupiah Plus#Dana Cepat#Tangbull#Pinjam Uang#Vloan#UangTeman#Kredit Pintar#KTA Kilat#GoRupiah#Tunaiku#Tunaikita#Qreditku#easycash#Modalku Dana Usaha#masBro#Home Credit India#Ahli Pinjarman#doctor rupiah#Funding Societies#Taralite#Investree#Koinworks#Amartha#PundiPundi#DanaRupiah#D-Card#Vcard#Angel Cash#CashKilat#Dana Siaga#Zidisha#Danamas#RajaUang#masbro#Kredivo#mentimun#Kredit HP#Cicil#DanaBijak#Kredina#Dana Pintar#Kredit Usaha Rakyat");
        rule.setRuleDetailType(BlackListTypeEnum.SMS_NEGATIVE_COUNT.getMessage());
        autoRules.put(BlackListTypeEnum.SMS_NEGATIVE_COUNT.getMessage(), rule);

        rule = new SysAutoReviewRule();
        rule.setRuleData(
            "tidak lulus verifikasi#tidak lulus verifikasi#kredit tidak layak#kredit tidak lancar#blm dpt disetujui#belum dapat menyetujui#belum dapat menyetujuinya#belum dapat disetujui#belum dapat kami setujui#belum bisa disetujui#tidak dapat menyetujui#belum melewati audit#tidak melewati audit#belum memenuhi persyaratan#tidak dapat memenuhi persyaratan#tidak sesuai dengan persyaratan#harus kami tolak#tdk disetujui");
        rule.setRuleDetailType(BlackListTypeEnum.SMS_REFUSE_COUNT.getMessage());
        autoRules.put(BlackListTypeEnum.SMS_REFUSE_COUNT.getMessage(), rule);
        return autoRules;
    }


}

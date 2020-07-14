package com.yqg.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.yqg.common.enums.user.UsrAddressEnum;
import com.yqg.common.utils.CardIdUtils;
import com.yqg.common.utils.StringUtils;
import com.yqg.drools.service.UserService;
import com.yqg.order.dao.OrdDao;
import com.yqg.order.entity.OrdOrder;
import com.yqg.system.entity.SysAutoReviewRule;
import com.yqg.user.dao.UsrAddressDetailDao;
import com.yqg.user.dao.UsrStudentDetailDao;
import com.yqg.user.dao.UsrWorkDetailDao;
import com.yqg.user.entity.UsrAddressDetail;
import com.yqg.user.entity.UsrStudentDetail;
import com.yqg.user.entity.UsrUser;
import com.yqg.user.entity.UsrWorkDetail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import lombok.extern.slf4j.Slf4j;

/*****
 * @Author zengxiangcai
 * Created at 2018/6/5
 * @Email zengxiangcai@yishufu.com
 *
 ****/

@Service
@Slf4j
public class CaculateScoreHandler {
    private static BigDecimal BASIS_SCORE = new BigDecimal(478.34);

    @Autowired
    private UsrWorkDetailDao usrWorkDetailDao;

    @Autowired
    private UsrStudentDetailDao usrStudentDetailDao;

    @Autowired
    private UsrAddressDetailDao usrAddressDetailDao;


    @Autowired
    private UserService userService;

    @Autowired
    private OrdDao ordDao;

    public static void main(String[] args) {
        BigDecimal value = new BigDecimal(478.34);
        BigDecimal ss = value.add(BigDecimal.valueOf(4));
        System.err.println(ss.setScale(2, BigDecimal.ROUND_HALF_UP));
    }



    public boolean doHandler(UsrUser user, OrdOrder order, Map<String, SysAutoReviewRule> codeEntityMap) throws Exception {
        BigDecimal score = new BigDecimal(0);
        /** ???1???2??? **/
        if (user.getSex() == 1) {
            score = BASIS_SCORE.subtract(BigDecimal.valueOf(2));
            log.info(order.getUuid() + "===============>sex - 2");
        } else if (user.getSex() == 2) {
            score = BASIS_SCORE.add(BigDecimal.valueOf(8));
            log.info(order.getUuid() + "===============>sex + 8");
        } else {
            log.info(order.getUuid() + "===============>sex ??");
        }


        /** ?? **/
        String idCardNoStr = user.getIdCardNo();
        Integer age = 0;// ????
        if (StringUtils.isNotEmpty(idCardNoStr)) {
            age = CardIdUtils.getAgeByIdCard(idCardNoStr.trim());
        }
        if (age >= 18 && age <= 25) {
            score = score.subtract(BigDecimal.valueOf(3));
            log.info(order.getUuid() + "===============>age - 3");
        } else if (age > 25 && age <= 30) {
            score = score.add(BigDecimal.valueOf(1));
            log.info(order.getUuid() + "===============>age + 1");
        } else if (age > 30 && age <= 35) {
            score = score.add(BigDecimal.valueOf(6));
            log.info(order.getUuid() + "===============>age + 6");
        } else if (age > 35 && age <= 40) {
            score = score.add(BigDecimal.valueOf(8));
            log.info(order.getUuid() + "===============>age + 8");
        } else if (age > 40 && age <= 45) {
            score = score.add(BigDecimal.valueOf(3));
            log.info(order.getUuid() + "===============>age + 3");
        } else if (age > 45 && age <= 50) {
            score = score.add(BigDecimal.valueOf(5));
            log.info(order.getUuid() + "===============>age + 5");
        }


        /** ???? **/
        UsrWorkDetail usrWorkDetail = new UsrWorkDetail();
        usrWorkDetail.setUserUuid(user.getUuid());
        usrWorkDetail.setDisabled(0);
        List<UsrWorkDetail> usrWorkDetailList = usrWorkDetailDao.scan(usrWorkDetail);
        if (!CollectionUtils.isEmpty(usrWorkDetailList)) {
            Integer maritalStatus = usrWorkDetailList.get(0).getMaritalStatus();
            if (maritalStatus == 0) {
                score = score.add(BigDecimal.valueOf(6));
                log.info(order.getUuid() + "===============>maritalStatus + 6");
            } else if (maritalStatus == 1) {
                score = score.add(BigDecimal.valueOf(14));
                log.info(order.getUuid() + "===============>maritalStatus + 14");
            } else if (maritalStatus == 2) {
                score = score.add(BigDecimal.valueOf(3));
                log.info(order.getUuid() + "===============>maritalStatus + 3");
            } else if (maritalStatus == 3) {
                score = score.subtract(BigDecimal.valueOf(2));
                log.info(order.getUuid() + "===============>maritalStatus - 2");
            }
        }


        /** ?? **/
        UsrStudentDetail usrStudentDetail = new UsrStudentDetail();
        usrStudentDetail.setUserUuid(user.getUuid());
        usrStudentDetail.setDisabled(0);
        List<UsrStudentDetail> usrStudentDetailList = usrStudentDetailDao.scan(usrStudentDetail);
        if (!CollectionUtils.isEmpty(usrStudentDetailList)) {
            String academic = usrStudentDetailList.get(0).getAcademic();
            if (academic.equals("Sekolah dasar")) {
                score = score.subtract(BigDecimal.valueOf(4));
                log.info(order.getUuid() + "===============>academic - 4");
            } else if (academic.equals("Sekolah Menengah Pertama")) {
                score = score.subtract(BigDecimal.valueOf(2));
                log.info(order.getUuid() + "===============>academic - 2");
            } else if (academic.equals("Sekolah Menengah Atas")) {
                score = score.add(BigDecimal.valueOf(14));
                log.info(order.getUuid() + "===============>academic + 14");
            } else if (academic.equals("Diploma")) {
                score = score.add(BigDecimal.valueOf(18));
                log.info(order.getUuid() + "===============>academic + 18");
            } else if (academic.equals("Sarjana") || academic.equals("Pascasarjana") || academic.equals("Doktor")) {
                score = score.add(BigDecimal.valueOf(27));
                log.info(order.getUuid() + "===============>academic + 27");
            }
        }


        /** ???? **/
        UsrAddressDetail usrAddressDetail = new UsrAddressDetail();
        usrAddressDetail.setDisabled(0);
        usrAddressDetail.setAddressType(UsrAddressEnum.HOME.getType());
        usrAddressDetail.setUserUuid(order.getUserUuid());
        List<UsrAddressDetail> usrAddressDetailList = usrAddressDetailDao.scan(usrAddressDetail);
        if (!CollectionUtils.isEmpty(usrAddressDetailList)) {
            String province = usrAddressDetailList.get(0).getProvince();
            if (province.equalsIgnoreCase("DKI JAKARTA")) {
                score = score.add(BigDecimal.valueOf(6));
                log.info(order.getUuid() + "===============>province + 6");
            } else if (province.equalsIgnoreCase("JAWA BARAT")) {
                score = score.add(BigDecimal.valueOf(4));
                log.info(order.getUuid() + "===============>province + 4");
            } else if (province.equalsIgnoreCase("BANTEN")) {
                score = score.add(BigDecimal.valueOf(8));
                log.info(order.getUuid() + "===============>province + 8");
            } else if (province.equalsIgnoreCase("BALI")) {
                score = score.add(BigDecimal.valueOf(15));
                log.info(order.getUuid() + "===============>province + 15");
            }
        }


        /** ??????????? **/
        String firstContact = userService.getUserLinkManPhoneNumber(1, order.getUserUuid());
        String secondContact = userService.getUserLinkManPhoneNumber(2, order.getUserUuid());

        //ahalim remove CallRecord: call record vs usrLinkman
        Boolean firstContactBoolean = false;
        Boolean secondContactBoolean = false;
        if (!firstContactBoolean && !secondContactBoolean) {// ??????????
            score = score.subtract(BigDecimal.valueOf(7));
            log.info(order.getUuid() + "===============>Contact - 7");
        } else if (firstContactBoolean || secondContactBoolean) {// ????????????
            score = score.add(BigDecimal.valueOf(3));
            log.info(order.getUuid() + "===============>Contact + 3");
        } else if (firstContactBoolean && secondContactBoolean) {// ?2??????????
            score = score.add(BigDecimal.valueOf(15));
            log.info(order.getUuid() + "===============>Contact + 15");
        }

        //ahalim remove usermessage: sms vs SMS_REFUSE_COUNT
        //ahalim remoe usermessagee: sms vs SMS_OVERDUE_COUNT_NINETY_DAY
       
        //ahalim remove CallRecord: Min call record vs order applyTime
        //ahalim remove CallRecord: Last 30 days ratio Night call vs All call
        //ahalim remove CallRecord: Last 30 days ratio 0 second call vs All call
        // ??????
        updateOrderScore(order.getUuid(), score.setScale(2, BigDecimal.ROUND_HALF_UP));
        return true;
    }

    /**
     * ??????
     *
     * @param uuid
     * @param score
     */
    private void updateOrderScore(String uuid, BigDecimal score) {
        OrdOrder order = new OrdOrder();
        order.setUuid(uuid);
        order.setDisabled(0);
        List<OrdOrder> ordOrders = ordDao.scan(order);
        if (!CollectionUtils.isEmpty(ordOrders)) {
            order = ordOrders.get(0);
            order.setScore(score);
            ordDao.update(order);
        }
    }
}

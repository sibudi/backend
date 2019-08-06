package com.yqg.service.check;

import com.alibaba.fastjson.JSONObject;
import com.yqg.common.utils.DateUtils;
import com.yqg.common.utils.StringUtils;
import com.yqg.management.dao.QualityCheckingVoiceDao;
import com.yqg.management.entity.InfinityBillEntity;
import com.yqg.management.entity.QualityCheckingVoice;
import com.yqg.service.check.response.VoiceFileResponse;
import com.yqg.service.third.infinity.InfinityService;
import com.yqg.service.third.upload.UploadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @program: microservice
 * @description:
 * @author: 许金泉
 * @create: 2019-04-12 15:29
 **/
@Service
@Slf4j
public class QualityCheckingVoiceService {
    @Autowired
    private QualityCheckingVoiceDao qualityCheckingVoiceDao;

    @Autowired
    private InfinityService infinityService;

    /**
     * 更新语音外呼记录和文件
     */
    public void updateQualityVoiceRecord(int num) throws Exception {

        //获取需要更新的记录
        List<QualityCheckingVoice> needUpdateRecords = qualityCheckingVoiceDao.needUpdateList(num);
        if (CollectionUtils.isEmpty(needUpdateRecords)) {
            log.info("no quality voice need update======");
            return;
        }
        needUpdateRecords.stream().filter(e -> StringUtils.isNotBlank(e.getUserId())).forEach(elem -> {

            //get record by userid.
            try {
                //取得调用账单接口参数
                Date startTime = needUpdateRecords.stream().min(Comparator.comparing(QualityCheckingVoice::getCreateTime))
                        .get().getCreateTime();
                //时间为一个小时后（印尼时间晚了一个小时）
                startTime = DateUtils.addDateWithHour(startTime, 1);
                Date endTime = DateUtils.addDateWithHour(new Date(), 1);
                log.info("updateQualityVoiceRecord startTime is :{}, end time is {}",
                        DateUtils.DateToString2(startTime), DateUtils.DateToString2(endTime) );

                List<InfinityBillEntity> bills = getInfinityBillEntities(startTime, endTime, elem.getUserId(), "3");
                if (CollectionUtils.isEmpty(bills)) {
                    log.error("not found record. userid is {}", elem.getUserId());
                    return;
                } else if (bills.size() > 1) {
                    log.error("found record size > 1. userid is {}", elem.getUserId());
                    return;
                }
                InfinityBillEntity bill = bills.get(0);
                elem.setRecordBeginTime(DateUtils.stringToDate(bill.getStarttime()));
                elem.setRecordEndTime(DateUtils.stringToDate(bill.getEndtime()));
                if (StringUtils.isNotEmpty(bill.getAnswertime())) {
                    elem.setAnswerStartTime(DateUtils.stringToDate(bill.getAnswertime()));
                }
                if (StringUtils.isNotEmpty(bill.getHangupcause())) {
                    elem.setErrorId(Integer.valueOf(bill.getHangupcause()));
                }
                //接通时长
                if (StringUtils.isNotEmpty(bill.getBillsec())) {
                    elem.setRecordLength(Integer.valueOf(bill.getBillsec()));
                }
                elem.setDownUrl(bill.getRecordfilename());
                uploadVoiceFile(elem);

            } catch (Exception e) {
                log.error("get record is error.");
            }

        });
    }

    private List<InfinityBillEntity> getInfinityBillEntities(Date startTime, Date endTime,
                                                             String userId, String callMethod) throws Exception {

        String token = infinityService.getToken();
        Map<String, String> map = new HashMap<>();
        map.put("token", token);
        map.put("starttime", DateUtils.DateToString2(startTime));
        map.put("endtime", DateUtils.DateToString2(endTime));
        //1：获取未查询过的记录(默认)，2：获取已查询过的记录，3：获取全部记录
        map.put("syncflag", "3");
        if (StringUtils.isNotEmpty(userId)) {
            map.put("userid", userId);
        }
        map.put("itemsperpage", "1000");
        map.put("currentpage", "1");
        //呼叫方法 0：全部方式，1：分机互拨，2：分机直拔，3：API呼叫（默认），4：双呼
        map.put("callmethod", callMethod);
        return infinityService.getBill(map);
    }

    @Autowired
    private UploadService uploadService;

    /**
     * 通过文件名获得文件
     *
     * @param checkingVoice
     */
    public void uploadVoiceFile(QualityCheckingVoice checkingVoice) throws Exception {
        String fileName = checkingVoice.getDownUrl();
        if (StringUtils.isEmpty(fileName)) {
            checkingVoice.setCallState(2);
            checkingVoice.setCallResult(2);
            this.qualityCheckingVoiceDao.update(checkingVoice);
            log.info("desnumber is {}, not find file", checkingVoice.getDestNumber());
            return;
        }
        Map<String, String> map = new HashMap<>();
        map.put("token", infinityService.getToken());
        map.put("filename", fileName);
        Map<String, String> rMap = this.infinityService.getRecodeFile(map);
        if (rMap != null && StringUtils.isNotBlank(rMap.get("resultJson"))) {
            String resultRes = rMap.get("resultJson");
            VoiceFileResponse voiceFileResponses = JSONObject.parseObject(resultRes, VoiceFileResponse.class);
            // 当前时间+四个随机数 组成文件名
            String fileNameMp3 = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now()) + ".mp3";
            uploadService.downloadFileAndUpload(voiceFileResponses.getDownurl(),fileNameMp3).ifPresent(file -> {
                if (StringUtils.isNotBlank(file.getData())) {
                    checkingVoice.setAttachmentSavePath(file.getData());
                    checkingVoice.setCallState(1);
                    checkingVoice.setCallResult(1);
                    this.qualityCheckingVoiceDao.update(checkingVoice);
                } else {
                    log.warn(file.toString());
                }
            });
        }

    }

}

package com.yqg.datafix;

import java.util.List;

import com.yqg.service.risk.service.Overdue15UserService;
import com.yqg.user.dao.UsrBlackListDao;
import com.yqg.user.entity.UsrBlackList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import lombok.extern.slf4j.Slf4j;

/***
 * 逾期数据抽取
 */
@Service
@Slf4j
public class OverdueDataExtractService {

    @Autowired
    UsrBlackListDao usrBlackListDao;

    @Autowired
    private Overdue15UserService overdue15UserService;

    /****
     * 历史数据处理处理流程
     * 1、查询出到目前为止逾期7天以上的数据
     * 2、对逾期数据分为[7,15) , [15,30), [30,--)
     * 3、对[7,15) 订单通话记录，通讯录、短信中的手机号码记录到redis
     * 4、将数据记录到usrBlack表中type=7
     *
     */
    public void start(){

    }

    /***
     * Query all the orders of [15,--), and record the relevant data of the address book call record to redis
     */
    public void stepOne() {
        Long startTime = System.currentTimeMillis();
        try {

            List<UsrBlackList> dbList = usrBlackListDao.getHistoryBlackList();
            if(CollectionUtils.isEmpty(dbList)){
                return;
            }

            // fraud user related data
            overdue15UserService.addContactToRedis(dbList, true, UsrBlackList.BlackUserCategory.FRAUD);
            overdue15UserService.addSmsToRedis(dbList, true, UsrBlackList.BlackUserCategory.FRAUD);

//            for (UsrBlackList item: dbList){
//               overdue15UserService.addExternalData(item);
//            }

        } catch (Exception e) {
            log.info("error", e);
        }finally {
            Long endTime = System.currentTimeMillis();
            log.info("finished.... cost: {} ms",(endTime-startTime));
        }
    }
}

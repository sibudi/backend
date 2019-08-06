package com.yqg.service.user.service;

import com.yqg.common.utils.JsonUtils;
import com.yqg.mongo.dao.UserContactsDal;
import com.yqg.mongo.entity.UserContactsMongo;
import com.yqg.order.dao.OrdDao;
import com.yqg.order.entity.OrdOrder;
import com.yqg.system.entity.SysAutoReviewRule;
import com.yqg.user.dao.UsrContractInfoDao;
import com.yqg.user.entity.UsrContractInfo;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.net.URLDecoder;
import java.util.*;

/**
 * Created by Jacob on 2018/4/24.
 */
@Service
@Slf4j
public class UsrContactService {

    @Autowired
    private UserContactsDal orderUserContactDal;//
    @Autowired
    private OrdDao ordDao;
    @Autowired
    private UsrContractInfoDao usrContractInfoDao;

    public void getUserContactsFromMongo() throws Exception{

        List<OrdOrder> ordList = this.ordDao.getAllOrder();
        if (!CollectionUtils.isEmpty(ordList)){
            log.info("ordlist size is"+ordList.size());
            for (OrdOrder order:ordList){
                log.info("get orderNo ============>"+order.getUuid());
                UserContactsMongo search = new UserContactsMongo();
                search.setOrderNo(order.getUuid());
                List<UserContactsMongo> result = this.orderUserContactDal.find(search);
                if(result.size() > 0){

                    UserContactsMongo dataResult = result.get(0);
                    String temp = dataResult.getData();
                    dealWithContacts(temp,order.getUuid());
                }
            }
            log.info("====================== deal with Contact end ======================");
        }

    }

    public void dealWithContacts(String dataStr,String orderNo) throws Exception{

        List<UsrContractInfo> responseList = new ArrayList<>();

        if(!StringUtils.isEmpty(dataStr)) {
            List<LinkedHashMap> list = JsonUtils.deserialize(dataStr, List.class);

            List<String> nameAndPhoneList = new ArrayList<>();
            try {
                for (LinkedHashMap mapContact : list) {
                    UsrContractInfo cell = new UsrContractInfo();
                    if (!StringUtils.isEmpty((mapContact.get("phone")))){
                        String name = URLDecoder.decode(""+mapContact.get("name").toString().replaceAll("%", "%25"),"UTF-8");
                        String phone = (""+mapContact.get("phone")).replaceAll("-","").replaceAll(" ","");
                        String nameAndPhone = name + phone;
                        if (!nameAndPhoneList.contains(nameAndPhone)) {
                            cell.setName(name);
                            cell.setPhone(phone);
                            cell.setContactTime(mapContact.get("createTime").toString());
                            cell.setOrderNo(orderNo);
                            responseList.add(cell);
                            nameAndPhoneList.add(nameAndPhone);
                        }

                    }
                }
            }catch (Exception e){
                log.error("dealWithContacts error",e);
            }
        }

        //  deal with contacts array  这里处理去重后数组
        if (responseList.size() <= 50){
            for (UsrContractInfo contractInfo:responseList){
                usrContractInfoDao.insert(contractInfo);
            }
        }else {

            List<UsrContractInfo>   list1 = new ArrayList<>();
            List<UsrContractInfo>   list2 = new ArrayList<>();

            for(UsrContractInfo info:responseList){

                String sensitiveWords = "papah#daddy#ayah#pah#dad#pa#father#papa#mamah#mommy#ibu#bunda#ma#mah#mih#mum#mother#paman#pakde#ai#ii#iie#ayi#aku#kuku#jiujiu#susuk#shushu#kek#nek#paman#kuku#kakak#cici#kak#kaka#dek#dik#adek#adik#kakak#abang#koko#adek#adik#ade#dede#putra#anak laki-laki#putri#anak perempuan#suami#sayang#istri#nona#papa#ayah#bapak#babeh#ayah mertua#papah mertua#ibu mertua#mamah mertua#oma#nenek#kakek#opa";
                String[] sensiticeWordList = sensitiveWords.split("#");
                List<String> wordList =   Arrays.asList(sensiticeWordList);

                if (!StringUtils.isEmpty(info.getName())){

                    if (wordList.contains(info.getName())){
                        list1.add(info);
                    }else {
                        list2.add(info);
                    }

                }else {

                    list2.add(info);
                }
            }

            if (list1.size()<50){


                for (UsrContractInfo info:list1){
                    usrContractInfoDao.insert(info);
                }
                int count1 = list1.size();
                this.sortOrderUserContactByCreatTime(list2);
                for (UsrContractInfo info2:list2.subList(0,50-count1)){
                    usrContractInfoDao.insert(info2);
                }

            }else {

                for (UsrContractInfo info:list1.subList(0,50)){
                    usrContractInfoDao.insert(info);
                }
            }

        }

    }

    private void sortOrderUserContactByCreatTime (List<UsrContractInfo> result) {
        if (CollectionUtils.isEmpty(result)) {
            return ;
        }
        Collections.sort(result, new Comparator<UsrContractInfo>() {
            @Override
            public int compare(UsrContractInfo ord1, UsrContractInfo ord2) {
                return 0 - ord1.getContactTime().compareTo(ord2.getContactTime());
            }
        });
    }





}

package com.yqg.datafix;

import com.github.pagehelper.StringUtil;
import com.yqg.common.utils.DESUtils;
import com.yqg.common.utils.TextToFieldUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

@Service
@Slf4j
public class UsrBlackListDataService {
    public static void main(String[] args) {

        if(true){
            System.err.println(encrypt("82216768123"));
            return;
        }
        File f = new File("C:\\Users\\zxc20\\Desktop\\审核催收人员名单20190118.csv");
        try {
            FileReader reader = new FileReader(f);
            BufferedReader bufferedReader = new BufferedReader(reader);
            String line = bufferedReader.readLine();
            String sql = "insert into usrBlackList(mobileDes,idCardNo,remark,createTime,updateTime,type) values";
            while((line = bufferedReader.readLine())!=null){
               String columns[] = line.split(",");
               String mobile = columns[0];
               String idCard = columns[1];
               String realName = columns[2];
               String remark = columns[3];
               StringBuffer lineSql = new StringBuffer();
               lineSql.append("(");
               if(StringUtil.isNotEmpty(mobile)){
                   lineSql.append("'"+encrypt(mobile)+"',");
               }else{
                   lineSql.append("null,");
               }
               if(StringUtil.isNotEmpty(idCard)){
                   lineSql.append("'"+idCard+"',");
               }else{
                   lineSql.append("null,");
               }
               lineSql.append("'");
               if(StringUtil.isNotEmpty(realName)){
                   lineSql.append(realName+"-");
               }
               if(StringUtil.isNotEmpty(remark)){
                   lineSql.append(remark);
               }

               lineSql.append("',now(),now(),9),");

               sql+="\n"+lineSql.toString();
            }

            System.err.println(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static String encrypt(String data) {
        byte[] encrypt = DESUtils.encrypt(data.getBytes(), getPassword());
        String result = Base64.encodeBase64String(encrypt);
        return result;
    }
    private static String getPassword(){
        return
                "B5880DD82010913B5707433253118984263B7857298773549468758875018579537B57772163084478873699447306034466200AAA41196057412B434059469SSS23589270273686087290124712345B";
    }
}

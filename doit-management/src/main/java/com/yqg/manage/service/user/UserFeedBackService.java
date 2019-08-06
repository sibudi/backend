package com.yqg.manage.service.user;

import com.yqg.common.enums.system.ExceptionEnum;
import com.yqg.common.exceptions.ServiceExceptionSpec;
import com.yqg.common.models.PageData;
import com.yqg.common.utils.Base64Utils;
import com.yqg.common.utils.DESUtils;
import com.yqg.common.utils.DateUtils;
import com.yqg.manage.dal.user.ManUserDao;
import com.yqg.manage.dal.user.UserFeedBackDao;
import com.yqg.manage.entity.user.ManUser;
import com.yqg.manage.enums.EnumUtils;
import com.yqg.manage.service.user.request.ManUserUserRequest;
import com.yqg.manage.service.user.request.ManUsrFeedBackRemarkRequest;
import com.yqg.service.util.ImageUtil;
import com.yqg.service.util.LoginSysUserInfoHolder;
import com.yqg.user.entity.UsrFeedBack;
import com.yqg.user.entity.UsrUser;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Jacob
 */
@Component
public class UserFeedBackService {

    public static final Integer PAGESIZE = 3000;
    @Autowired
    private UserFeedBackDao userFeedBackDao;

    @Autowired
    private ManUserDao manUserDao;

    @Autowired
    private UserUserService userUserService;

    @Value("${upload.imagePath}")
    private String imagePath; //

    public PageData<List<UsrFeedBack>> getUserFeedBackList(ManUserUserRequest dataRequest)
            throws Exception {
        //区分反馈还是催收的标识不能为空
        if (dataRequest.getSourceType() == null) {
            throw new ServiceExceptionSpec(ExceptionEnum.MANAGE_SEARCH_ERROR);
        }
        PageData<List<UsrFeedBack>> userFeedBackPageData = new PageData<>();
        String mobile = dataRequest.getMobile();
        if (StringUtils.isNotBlank(mobile)) {
            dataRequest.setMobile(mobile.substring(0, mobile.length() - 6) + "******");
        }
        //封装用户手机号码
        List<UsrFeedBack> usrFeedBacks = this.userFeedBackDao.userFeedBackList(dataRequest);
        if (!CollectionUtils.isEmpty(usrFeedBacks)) {
            for (UsrFeedBack usrFeedBack : usrFeedBacks) {
                if (StringUtils.isEmpty(usrFeedBack.getUserUuid())) {
                    continue;
                }
                ManUserUserRequest userRequest = new ManUserUserRequest();
                userRequest.setUserUuid(usrFeedBack.getUserUuid());
                UsrUser usrUser = userUserService.userMobileByUuid(userRequest);
                usrFeedBack.setUserMobile(usrUser.getMobileNumberDES());
                if (StringUtils.isNotBlank(usrFeedBack.getFeedBackImages())) {
                    usrFeedBack.setFeedBackImages(this.imagePath + ImageUtil.encryptUrl(usrFeedBack.getFeedBackImages()) + "&sessionId="
                            + LoginSysUserInfoHolder.getUsrSessionId());
                }
            }
        }

        userFeedBackPageData.setData(usrFeedBacks);
        userFeedBackPageData.setPageSize(dataRequest.getPageSize());
        userFeedBackPageData.setRecordsTotal(this.userFeedBackDao.userFeedBackListCountNum(dataRequest));
        return userFeedBackPageData;
    }

    /**
     * 导出用户问题反馈Excel*/
    public void getUserFeedBackListExcel(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String,String[]> requestMap = request.getParameterMap();
        if(CollectionUtils.isEmpty(requestMap)){
            throw new ServiceExceptionSpec(ExceptionEnum.MANAGE_SEARCH_ERROR);
        }

        ManUserUserRequest searchRequest = new ManUserUserRequest();
        searchRequest.setMobile(requestMap.get("mobile")[0]);
        if(StringUtils.isNotEmpty(requestMap.get("questionType")[0])){
            searchRequest.setQuestionType(Integer.valueOf(requestMap.get("questionType")[0]));
        }

        if(StringUtils.isNotEmpty(requestMap.get("stageType")[0])){
            searchRequest.setStageType(Integer.valueOf(requestMap.get("stageType")[0]));
        }

        searchRequest.setStartTime(requestMap.get("startTime")[0]);
        searchRequest.setEndTime(requestMap.get("endTime")[0]);
        searchRequest.setPageSize(PAGESIZE);
        searchRequest.setSourceType(StringUtils.isBlank(requestMap.get("sourceType")[0]) ? 0
                : Integer.valueOf(requestMap.get("sourceType")[0]));

        Integer totalCount = this.userFeedBackDao.userFeedBackListCountNum(searchRequest);

        Integer pageCount = (totalCount / PAGESIZE)+1;

        OutputStream output=response.getOutputStream();
        response.reset();
        response.setHeader("Content-disposition","attachment;           filename=问题反馈表"+ DateUtils.dateToDay()+".xls");
        response.setContentType("application/msexcel");

         /*创建excel*/
        HSSFWorkbook wk=new HSSFWorkbook();
        /*分页sheet*/
        HSSFSheet[] sheetArr = new HSSFSheet[pageCount];

        for(int i = 1;i <= pageCount;i++){
            sheetArr[i-1] = wk.createSheet("用户反馈表"+i);
            searchRequest.setPageNo(i);

            List<UsrFeedBack> result = this.userFeedBackDao.userFeedBackListByPage(searchRequest);

            HSSFRow rowTemp = sheetArr[i-1].createRow(0);
            rowTemp.createCell(0).setCellValue("用户uuid");
            rowTemp.createCell(1).setCellValue("提交时间");
            rowTemp.createCell(2).setCellValue("反馈内容");
            if (StringUtils.isNotBlank(requestMap.get("sourceType")[0])
                    && requestMap.get("sourceType")[0].equals("1")) {
//                催收人员，备注，解决情况，客服名称，操作时间
                rowTemp.createCell(3).setCellValue("催收人员");
                rowTemp.createCell(4).setCellValue("备注");
                rowTemp.createCell(5).setCellValue("解决情况");
                rowTemp.createCell(6).setCellValue("客服名称");
                rowTemp.createCell(7).setCellValue("操作时间");
            }
            this.putData2Sheet(result,sheetArr[i-1], requestMap.get("sourceType")[0]);
        }

        wk.write(output);
        output.close();
    }
    public void putData2Sheet(List<UsrFeedBack> resource,HSSFSheet sheet, String sourceType) throws Exception {
        Integer count = 1;
        for(UsrFeedBack item:resource){
            HSSFRow rowTemp = sheet.createRow(count);
            rowTemp.createCell(0).setCellValue(item.getUserUuid());
            rowTemp.createCell(1).setCellValue(DateUtils.DateToString4(item.getCreateTime()));
            rowTemp.createCell(2).setCellValue(item.getFeedBackContent());
            if (StringUtils.isNotBlank(sourceType)
                    && "1".equals(sourceType)) {
//                催收人员，备注，解决情况，客服名称，操作时间
                rowTemp.createCell(3).setCellValue(item.getCollectionName());
                rowTemp.createCell(4).setCellValue(item.getRemark());
                rowTemp.createCell(5).setCellValue(EnumUtils.valueOfResolutionEnum(item.getStageType()).getName() +
                "  " + EnumUtils.valueOfResolutionEnum(item.getStageType()).getNameInn());
                ManUser manUser = new ManUser();
                manUser.setId(item.getUpdateUser());
                List<ManUser> users = manUserDao.scan(manUser);
                if (!CollectionUtils.isEmpty(users)) {
                    rowTemp.createCell(6).setCellValue(users.get(0).getUsername());
                }
                rowTemp.createCell(7).setCellValue(item.getStageType() == 0 ? ""
                        : DateUtils.DateToString4(item.getUpdateTime()));
            }
            count++;
        }
    }

    public void updateRemark2UserFeedBack(ManUsrFeedBackRemarkRequest remarkRequest) throws Exception {
        String uuid = remarkRequest.getUuid();
        String remark = remarkRequest.getRemark();
        Integer questionType = remarkRequest.getQuestionType();
        Integer stageType = remarkRequest.getStageType();
        String operatorName = remarkRequest.getOperatorName();
        Integer updateUser = remarkRequest.getUserId();

        if(StringUtils.isEmpty(uuid) || StringUtils.isEmpty(remark) || questionType == null || stageType == null
                || updateUser == null || StringUtils.isEmpty(operatorName) ){
            throw new ServiceExceptionSpec(ExceptionEnum.MANAGE_ADD_ITEM_ERROR);
        }
        UsrFeedBack feedBack = new UsrFeedBack();
        feedBack.setUpdateTime(new Date());
        feedBack.setRemark(remark);
        feedBack.setUuid(uuid);
        feedBack.setUpdateUser(updateUser);
        feedBack.setQuestionType(questionType);
        feedBack.setStageType(stageType);
        feedBack.setOperatorName(operatorName);
        this.userFeedBackDao.update(feedBack);
    }
}

package com.yqg.manage.service.user;

import com.yqg.manage.dal.user.ReviewerSchedulerDAO;
import com.yqg.manage.entity.user.ManUser;
import com.yqg.manage.enums.ReviewerPostEnum;
import com.yqg.manage.service.user.request.ReviewerSchedulerRequest;
import com.yqg.manage.service.user.response.ManSysUserResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/*****
 * @Author Jeremy Lawrence
 * created at ${date}
 *
 *
 * ????????
 ****/

@Service
@Slf4j
public class ReviewerSchedulerService {


    @Autowired
    private ReviewerSchedulerDAO reviewerSchedulerDao;

    @Autowired
    private ManUserService manUserService;

    @Transactional
    public void addReviewerScheduler(Integer operatorId, ReviewerSchedulerRequest request) {
        if (CollectionUtils.isEmpty(request.getReviewerIds())) {
            return;
        }

        String roleInfo = manUserService.getReviewerRoleInfoFromDict(request.getPostEnglishName());
        if(StringUtils.isEmpty(roleInfo)){
            log.error("can not get roleId from dicItem for post: "+request.getPostEnglishName());
            return;
        }
        //将历史调度的reviewer失效掉
        reviewerSchedulerDao
                .disableHistSchedulerByRoleId(Integer.valueOf(roleInfo.split("#")[1]), operatorId,
                        request.getPostEnglishName().name());
        //插入最新的调度信息
        reviewerSchedulerDao.batchInsertSchedulerInfo(request.getReviewerIds(), operatorId,request.getPostEnglishName().name());
    }

    public List<ManSysUserResponse> getAllReviewersByPostName(ReviewerPostEnum postName){

        String roleInfo = manUserService.getReviewerRoleInfoFromDict(postName);
        if(roleInfo == null){
            log.error("can not get roleId from dicItem for post: "+postName);
            return new ArrayList<>();
        }

        List<ManSysUserResponse> userList = manUserService.getUsersByRole(Integer.valueOf(roleInfo.split("#")[1]));
        if(CollectionUtils.isEmpty(userList)){
            return new ArrayList<>();
        }
        return userList;
    }


}

package com.yqg.manage.service.order;

import com.yqg.common.redis.RedisClient;
import com.yqg.common.utils.JsonUtils;
import com.yqg.manage.constants.ConstantsForLock;
import com.yqg.manage.dal.user.ReviewerOrderTaskDAO;
import com.yqg.manage.entity.order.ReviewOrderAssignParam;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/*****
 * @Author Jeremy Lawrence
 * created at ${date}
 *
 *
 ****/

@Service
@Slf4j
public class OrderAssignmentService {

    @Autowired
    private ReviewerOrderTaskDAO reviewerOrderTaskDao;

    @Autowired
    private RedisClient redisClient;

    /***
     * 将审核订单分配给用户
     * @param param
     */

    @Transactional
    public boolean assignReviewOrder(ReviewOrderAssignParam param) {

        //避免管理员和审核人员同时申请分配订单，加分布式锁
        String lockKey = ConstantsForLock.PREFIX_REVIEW_ASSIGNMENT_LOCK + param.getOrderUUID();
        boolean fetchLock = redisClient
                .lockRepeat(lockKey);
        if (!fetchLock) {
            log.error("fetch review order assignment lock failed, param = {}",
                    JsonUtils.serialize(param));
            return false;
        }
        reviewerOrderTaskDao
                .disableHistAssignment(Arrays.asList(param.getOrderUUID()), param.getOperatorId(),param.getReviewerRole());
        reviewerOrderTaskDao
                .batchInsertReviewTaskAssignment(Arrays.asList(param.getOrderUUID()),
                        param.getOperatorId(),
                        param.getReviewerId(),param.getReviewerRole());
        redisClient.unlockRepeat(lockKey);

        return true;
    }

    public static void main(String[] args) {
        System.err.println(JsonUtils.serialize("lock:order:11212"));
    }
}

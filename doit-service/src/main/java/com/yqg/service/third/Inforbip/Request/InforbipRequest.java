package com.yqg.service.third.Inforbip.Request;

import com.yqg.common.models.BaseRequest;
import lombok.Data;

/**
 * Created by wanghuaizhou on 2018/8/28.
 */
@Data
public class InforbipRequest extends BaseRequest{

    private String orderNo;
    private String mobileNumber;
    private Integer callType;   //1本人电话 2公司电话 3第一联系人 4 第二联系人  5 紧急联系人
    private Integer callNode;   // 外呼节点 1 初审到复审  2选择联系人
}


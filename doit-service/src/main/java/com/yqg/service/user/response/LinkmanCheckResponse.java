package com.yqg.service.user.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/***
 * 复借联系人填写检查：
 * needFill = true，则表示联系人未完成需要重新填写，这时step=ONE进入第一个页面，step=TWO 进入第二个页面
 * needFill = false表示已经完成不需要重新填写
 */
@Getter
@Setter
public class LinkmanCheckResponse {
    //需要重新填写联系人
    private Boolean needFill;
    //进入联系人第几步
    private LinkmanFillStep linkmanStep;

    private List<LinkmanDetail> linkmanList = new ArrayList<>();




    public enum LinkmanFillStep{
        ONE, //第一步
        TWO  //第二步
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LinkmanDetail{
        private String contactsName;  //联系人名称
        private String relation;//联系人关系：父亲，母亲等
        private String contactsMobile; //联系人电话
        private Integer sequence;  //联系人类型 1,2,4,5 分别表示第一第二第三第四联系人
        private String waOrLine; //whatsApp or line
        private Boolean isValid;//是否有效：催收联系且结果无效(联系结果是空号和停机) or 外呼无效

        private TelNumberValidationEnum validationStatus = TelNumberValidationEnum.NOT_SURE;

    }

    public enum TelNumberValidationEnum {
        NOT_SURE,//不确定，还没有审核的过的号码，号码可以重新填写，默认值
        VALID,//外呼后明确有效
        INVALID,//催收确定无效或者外呼无效
    }
}

package com.yqg.service.externalChannel.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Cash2ExtralInfo extends Cash2BaseRequest{
    @JsonProperty(value = "order_no")
    private String orderNo;//订单号


    @JsonProperty(value = "attachment_info")
    private List<AttachmentInfo> attachmentInfoList;//附件信息列表


    @Getter
    @Setter
    public static class AttachmentInfo {
        @JsonProperty(value = "type")
        private String type;
        @JsonProperty(value = "imgUrl")
        private String imgUrl;
    }
}

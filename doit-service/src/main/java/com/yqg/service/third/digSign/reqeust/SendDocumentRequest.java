package com.yqg.service.third.digSign.reqeust;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class SendDocumentRequest {

    private String userid;
    @JsonProperty(value = "document_id")
    private String documentId; //每个协议稳定的编号
    @JsonProperty(value = "req-sign")
    private List<RequestSign> requestSignList;
    @JsonProperty(value = "send-to")
    private List<SendToDetail> sendToList;
    @JsonProperty(value = "redirect")
    private Boolean redirect = true; // must be set true
    @JsonProperty(value = "sequence_option")
    private Boolean sequence_option = false; // default false: signing process will be carried out in no order
    // true: signing process will be carried out sequentially

    @Getter
    @Setter
    public static class RequestSign {
        private String name;
        private String email;
        @JsonProperty(value = "aksi_ttd")
        private String autoOrManual = "mt";  // at: 自动/mt: 手动
        @JsonProperty(value = "kuser")
        private String kuser="e8r5QbytnJ5g2HOo";

        private String user = "ttd1"; //签约类型

        private String payment = "3";//支付方式

        private String page; //签约所在的页码
        //坐标
        @JsonProperty(value = "llx")
        private String llx;
        @JsonProperty(value = "lly")
        private String lly;
        @JsonProperty(value = "urx")
        private String urx;
        @JsonProperty(value = "ury")
        private String ury;

    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class SendToDetail {
        private String name;
        private String email;

    }
}

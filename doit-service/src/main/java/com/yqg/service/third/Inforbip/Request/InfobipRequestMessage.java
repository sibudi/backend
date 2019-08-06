package com.yqg.service.third.Inforbip.Request;

import com.yqg.service.third.Inforbip.Response.ReportMessage;
import lombok.Data;

/**
 * Created by wanghuaizhou on 2018/9/3.
 */
@Data
public class InfobipRequestMessage {

    private String from;
    private String[] to;
    private String audioFileUrl;
//    private String language = "id-ID";
}

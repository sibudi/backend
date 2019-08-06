package com.yqg.service.third.jxl;

import com.yqg.common.utils.JsonUtils;
import com.yqg.common.utils.StringUtils;
import com.yqg.service.externalChannel.utils.HttpUtil;
import com.yqg.service.third.jxl.response.JXLBaseResponse;
import com.yqg.service.third.jxl.response.JXLBaseResponse.CreateReportTaskData;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;


/***
 * apikey: 43f4df6b145a47dcbd7e5d894104fec3
 *   secret: f68deeb0493e4f79be9b1e24d8db4b8f
 *   createReportTask: https://id.dotconnect.io/authorize_api/createReportTask
 */


@Service
@Slf4j
public class JXLService {

    @Autowired
    private JXLConfig jxlConfig;

    public String createReportTaskWithKtpAndName(String ktp, String realName) {
        Map<String, String> param = new HashMap<>();
        param.put("id_num", ktp);
        param.put("name", realName);
        param.put("id_type", "1");
        Header header = new BasicHeader("Authorization", jxlConfig.getApiKey());
        String response = HttpUtil.postJson(JsonUtils.serialize(param), jxlConfig.getCreateReportTaskUrl(), header);
        if (StringUtils.isEmpty(response)) {
            return null;
        }

        JXLBaseResponse baseResponse = JsonUtils.deserialize(response, JXLBaseResponse.class);
        if (baseResponse.isResponseSuccess() && baseResponse.getData() != null) {
            CreateReportTaskData data = JsonUtils.deserialize(JsonUtils.serialize(baseResponse.getData()), CreateReportTaskData.class);
            return data.getReportTaskToken();
        }
        return null;
    }

    public JXLBaseResponse identityVerify(String idCardNo, String realName) {
        if (StringUtils.isEmpty(idCardNo) || StringUtils.isEmpty(realName)) {
            log.info("the request idCardNo or realName is empty.");
        }
        String reportToken = createReportTaskWithKtpAndName(idCardNo, realName);
        if (StringUtils.isEmpty(reportToken)) {
            log.info("the response report token is empty for idCardNo: {}", idCardNo);
            return null;
        }
        Map<String, String> param = new HashMap<>();
        param.put("nik", idCardNo);
        param.put("name", realName);
        param.put("report_task_token", reportToken);
        Header header = new BasicHeader("Authorization", jxlConfig.getApiKey());
        String response = HttpUtil.postJson(JsonUtils.serialize(param), jxlConfig.getKtpVerifyUrl(), header);
        if (StringUtils.isEmpty(response)) {
            return null;
        }
        JXLBaseResponse baseResponse = JsonUtils.deserialize(response, JXLBaseResponse.class);

        return baseResponse;
    }

}

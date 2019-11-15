package com.yqg.service.externalChannel.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yqg.common.utils.JsonUtils;
import com.yqg.service.externalChannel.config.Cash2Config;
import com.yqg.common.utils.AesUtil;
import lombok.Getter;
import lombok.Setter;

/*****
 * @Author zengxiangcai
 * Created at 2018/3/5
 * @Email zengxiangcai@yishufu.com
 * cashcash 接口参数
 ****/

@Getter
@Setter
public class Cash2ApiParam {



    private EncryptData data;

    public <T> T getDecryptData(Class<T> clazz,Cash2Config config) {
        //解密后数据
        String decryptedText = AesUtil
            .decryptData(this.getData().getEncryptedData(), config.getAesKey(),
                config.getInitVector());
        return JsonUtils.deserialize(decryptedText, clazz);
    }


    @Getter
    @Setter
    public static class EncryptData {

        @JsonProperty(value = "partner_key")
        private String partnerKey;

        @JsonProperty(value = "en_data")
        private String encryptedData;
    }


}

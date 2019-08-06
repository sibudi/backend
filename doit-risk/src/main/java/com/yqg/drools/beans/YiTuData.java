package com.yqg.drools.beans;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

/*****
 * @Author zengxiangcai
 * Created at 2018/1/23
 * @Email zengxiangcai@yishufu.com
 *
 ****/

@Getter
@Setter
public class YiTuData {

//    @JsonProperty(value = "pair_verify_result")
//    private Integer pairVerifyResult;

    @JsonProperty(value = "pair_verify_similarity")
    private String pairVerifySimilarity;
}

package com.yqg.service.third.digSign.reqeust;

import java.io.Serializable;
import java.util.List;

import com.yqg.common.models.BaseRequest;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class SignContractBulkRequest implements Serializable {

	private List<String> orderNo;
}
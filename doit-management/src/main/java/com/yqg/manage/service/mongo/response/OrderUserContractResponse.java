package com.yqg.manage.service.mongo.response;

import lombok.Data;

/**
 * @Author Jacob
 */
@Data
public class OrderUserContractResponse {


    private String name ;

    private String mobile;

    public OrderUserContractResponse(){

    }

    public OrderUserContractResponse(String name, String mobile) {
        this.name = name;
        this.mobile = mobile;
    }
}

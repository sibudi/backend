package com.yqg.manage.service.third.request;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Author: tonggen
 * Date: 2019/4/17
 * time: 3:41 PM
 */
@NoArgsConstructor
@Data
public class OfflineRequest {


    private String funder;

    private String token;

    private Integer pageNo;

    private Integer pageSize;

    @Override
    public String toString() {
        return
                "funder=" + funder + "&page=" + pageNo
                + "&pageSize=" + pageSize;
    }
}

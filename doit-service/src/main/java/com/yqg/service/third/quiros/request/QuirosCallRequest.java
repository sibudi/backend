/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.yqg.service.third.quiros.request;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author user
 */
@NoArgsConstructor
@Data
public class QuirosCallRequest {
    private String extension;

    private String outbound;
    
    private String cusid;
    
    private String optid;
    
    private String docid;

    private String orderId;

    //--------------------------------------------------------
    
    private String realName;

    private String userName;

    private String destnumber;

    private String userUuid;

    private int callNode;

    private int callType;
}

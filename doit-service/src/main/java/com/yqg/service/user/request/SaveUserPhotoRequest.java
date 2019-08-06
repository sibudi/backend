package com.yqg.service.user.request;

import com.yqg.common.models.BaseRequest;
import lombok.Data;

/**
 * Created by Didit Dwianto on 2018/1/19.
 */
@Data
public class SaveUserPhotoRequest extends BaseRequest{

    private String photoUrl; //


    private String photoType;
}

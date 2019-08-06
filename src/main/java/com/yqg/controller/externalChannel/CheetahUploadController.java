package com.yqg.controller.externalChannel;

import com.yqg.common.annotations.CheetahRequest;
import com.yqg.common.exceptions.ServiceException;
import com.yqg.service.externalChannel.request.CheetahUploadRequest;
import com.yqg.service.externalChannel.response.CheetahResponse;
import com.yqg.service.externalChannel.service.CheetahUploadService;
import com.yqg.service.externalChannel.utils.CheetahResponseBuilder;
import com.yqg.service.externalChannel.utils.CheetahResponseCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by tonggen on 2018/12/26.
 */
@CheetahRequest
@RestController
@RequestMapping("/cash-loan/v1")
public class CheetahUploadController {

    @Autowired
    private CheetahUploadService cheetahUploadService;

    /***
     * 文件单独上传
     * @return
     */
    @RequestMapping(value = "/images", method = RequestMethod.POST)
    public CheetahResponse confirmLoanApplication(CheetahUploadRequest fileName,
                                                  @RequestParam("file") MultipartFile file) throws Exception{
        String name ;
        try {
            name = cheetahUploadService.uploadImages(file, fileName.getFilename());
        } catch (ServiceException e) {
            return CheetahResponseBuilder.buildResponse(CheetahResponseCode.PARAM_ERROR_1001)
                    .withMessage(e.getMessage());
        } catch (Exception e) {
            return CheetahResponseBuilder.buildResponse(CheetahResponseCode.SERVER_INTERNAL_ERROR_3001);
        }

        return CheetahResponseBuilder.buildResponse(CheetahResponseCode.CODE_OK_0).withData(name);
    }

}

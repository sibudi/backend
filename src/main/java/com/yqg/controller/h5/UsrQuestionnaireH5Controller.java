package com.yqg.controller.h5;

import com.yqg.common.annotations.H5Request;
import com.yqg.common.models.ResponseEntitySpec;
import com.yqg.common.models.builders.ResponseEntitySpecBuilder;
import com.yqg.service.user.request.UsrQuestionnaireAttachRequest;
import com.yqg.service.user.request.UsrQuestionnaireSaveRequet;
import com.yqg.service.user.service.UsrQuestionnaireService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.ws.rs.core.MediaType;

/**
 * Author: tonggen
 * Date: 2019/6/19
 * time: 12:14 PM
 */
@RestController
@H5Request
@RequestMapping("/web/questionnaire/")
public class UsrQuestionnaireH5Controller {

    @Autowired
    private UsrQuestionnaireService usrQuestionnaireService;

    @RequestMapping(value = "/insertQuestionnaire", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<Integer> insertQuestionnaire(@RequestBody UsrQuestionnaireSaveRequet requet)
            throws Exception {

        return ResponseEntitySpecBuilder.success(this.usrQuestionnaireService
                .insertQuestionnaire(requet));
    }

    @RequestMapping(value = "/uploadQuestionnaireAttach", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<String> uploadQuestionnaireAttach(@RequestParam("file") MultipartFile file,
                                                                 @RequestParam("attachType") Integer attachType,
                                                                 @RequestParam("sessionId") String sessionId)
            throws Exception {

        return ResponseEntitySpecBuilder.success(this.usrQuestionnaireService
                .uploadQuestionnaireAttach(file, attachType, sessionId));
    }

    @RequestMapping(value = "/insertQuestionnaireAttach", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<Integer> insertQuestionnaireAttach(@RequestBody UsrQuestionnaireAttachRequest request)
            throws Exception {

        return ResponseEntitySpecBuilder.success(this.usrQuestionnaireService
                .insertQuestionnaireAttach(request)) ;
    }

}

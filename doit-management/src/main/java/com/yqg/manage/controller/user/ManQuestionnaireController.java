package com.yqg.manage.controller.user;

import com.yqg.common.exceptions.ServiceExceptionSpec;
import com.yqg.common.models.PageData;
import com.yqg.common.models.ResponseEntitySpec;
import com.yqg.common.models.builders.ResponseEntitySpecBuilder;
import com.yqg.manage.service.user.ManUsrQuestionnaireService;
import com.yqg.service.user.request.Questionnaire;
import com.yqg.service.user.request.UsrQuestionnaireListRequest;
import com.yqg.service.user.response.UserQuestionnaireDetailResponse;
import com.yqg.service.user.response.UsrQuestionnaireBaseResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Author: tonggen
 * Date: 2019/6/20
 * time: 4:45 PM
 */
@RestController
@RequestMapping("/manage/manQuestionnaire/")
public class ManQuestionnaireController {

    @Autowired
    private ManUsrQuestionnaireService service ;


    @RequestMapping(value = "listQuestionnaire",method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    public ResponseEntitySpec<PageData<List<UsrQuestionnaireBaseResponse>>> listQuestionnaire(@RequestBody UsrQuestionnaireListRequest request) {

        return ResponseEntitySpecBuilder
                .success(service.listQuestionnaire(request));
    }

    @RequestMapping(value = "getManQuestionnaireDetail",method = RequestMethod.POST)
    public ResponseEntitySpec<UserQuestionnaireDetailResponse> getManQuestionnaireDetail(
            @RequestBody UsrQuestionnaireListRequest request) throws ServiceExceptionSpec {

        return ResponseEntitySpecBuilder
                .success(service.getManQuestionnaireDetail(request.getUserUuid()));
    }

    @RequestMapping(value = "checkManQuestionnaire",method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    public ResponseEntitySpec<Integer> checkManQuestionnaire(
            @RequestBody UsrQuestionnaireListRequest request) throws ServiceExceptionSpec {

        return ResponseEntitySpecBuilder
                .success(service.checkManQuestionnaire(request));
    }

    /**
     * 审核详情中新增补充资料
     * @param file
     * @param userUuid
     * @param sessionId
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "uploadQuestionnaireAttach", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<Integer> uploadQuestionnaireAttach(@RequestParam("file") MultipartFile file,
                                                                @RequestParam("userUuid") String userUuid,
                                                                @RequestParam("sessionId") String sessionId)
            throws Exception {

        return ResponseEntitySpecBuilder.success(service.uploadQuestionnaireAttach(file, userUuid, sessionId));
    }

    /**
      补充资料回显
     * @param request
     * @return
     * @throws ServiceExceptionSpec
     */
    @RequestMapping(value = "getSupplementData",method = RequestMethod.POST)
    public ResponseEntitySpec<List<Questionnaire>> getSupplementData(
            @RequestBody UsrQuestionnaireListRequest request) throws ServiceExceptionSpec {

        return ResponseEntitySpecBuilder
                .success(service.getSupplementData(request.getUserUuid()));
    }
}

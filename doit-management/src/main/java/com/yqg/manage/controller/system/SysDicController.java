package com.yqg.manage.controller.system;

import com.yqg.common.annotations.H5Request;
import com.yqg.common.models.PageData;
import com.yqg.common.models.ResponseEntitySpec;
import com.yqg.common.models.builders.ResponseEntitySpecBuilder;
import com.yqg.service.system.request.DictionaryRequest;
import com.yqg.service.system.service.SysDicItemService;
import com.yqg.service.system.service.SysDicService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javax.ws.rs.core.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Didit Dwianto on 2017/11/24.
 */
@RestController
@H5Request
@Api(tags = "??????")
public class SysDicController {
    @Autowired
    private SysDicService sysDicService;

    @Autowired
    private SysDicItemService sysDicItemService;

    @ApiOperation("添加父字典")
    @RequestMapping(value = "/manage/fatherDicAdd", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<Object> addAppVersion(@RequestBody DictionaryRequest request)
            throws Exception {
        this.sysDicService.addFatherDictionary(request);
        return ResponseEntitySpecBuilder.success();
    }

    @ApiOperation("修改父字典")
    @RequestMapping(value = "/manage/fatherDicEdit", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<Object> fatherDicEdit(@RequestBody DictionaryRequest request)
            throws Exception {
        this.sysDicService.editFatherDictionary(request);
        return ResponseEntitySpecBuilder.success();
    }

    @ApiOperation("父字典列表")
    @RequestMapping(value = "/manage/fatherDicList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<PageData> fatherDicList(@RequestBody DictionaryRequest request)
            throws Exception {
        return ResponseEntitySpecBuilder.success(this.sysDicService.fatherDictionaryList(request));
    }

    @ApiOperation("子字典添加")
    @RequestMapping(value = "/manage/sysDicItemAdd", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<Object> sysDicItemAdd(@RequestBody DictionaryRequest request)
            throws Exception {
        this.sysDicItemService.sysDicItemAdd(request);
        return ResponseEntitySpecBuilder.success();
    }

    @ApiOperation("子字典修改")
    @RequestMapping(value = "/manage/sysDicItemEdit", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<Object> sysDicItemEdit(@RequestBody DictionaryRequest request)
            throws Exception {
        this.sysDicItemService.sysDicItemEdit(request);
        return ResponseEntitySpecBuilder.success();
    }

    @ApiOperation("通过父字典的DicCode查询子字典List")
    @RequestMapping(value = "/manage/dicItemListByDicCode", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<Object> dicItemListByDicCode(@RequestBody DictionaryRequest request)
            throws Exception {
        return ResponseEntitySpecBuilder.success(this.sysDicService.dicItemListByDicCode(request));
    }

    @ApiOperation("子字典value修改")
    @RequestMapping(value = "/manage/sysDicItemValueEdit", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<Object> sysDicItemValueEdit(@RequestBody DictionaryRequest request)
            throws Exception {
        this.sysDicItemService.sysDicItemValueEdit(request);
        return ResponseEntitySpecBuilder.success();
    }
}

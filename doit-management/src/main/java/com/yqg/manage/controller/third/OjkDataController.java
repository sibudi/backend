package com.yqg.manage.controller.third;

import com.yqg.common.models.ResponseEntitySpec;
import com.yqg.common.models.builders.ResponseEntitySpecBuilder;
import com.yqg.ojk.entity.*;
import com.yqg.service.third.ojk.OJKService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.core.MediaType;

/**
 * Author: tonggen
 * Date: 2019/6/13
 * time: 6:06 PM
 */
@RestController
@RequestMapping("/manage/ojkData")
public class OjkDataController {

    @Autowired
    private OJKService ojkService;

    @RequestMapping(value = "/insertOrUpdateOjkProfilPenyelenggara", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    public ResponseEntitySpec<Object> insertOrUpdateOjkProfilPenyelenggara(@RequestBody ProfilPenyelenggara request)
            throws Exception {

        ojkService.insertOrUpdateOjkData(request);
        return ResponseEntitySpecBuilder.success();
    }

    @RequestMapping(value = "/insertOrUpdateOjkRincianDireksiKomisarisPS", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    public ResponseEntitySpec<Object> insertOrUpdateOjkRincianDireksiKomisarisPS(@RequestBody RincianDireksiKomisarisPS request)
            throws Exception {

        ojkService.insertOrUpdateOjkData(request);
        return ResponseEntitySpecBuilder.success();
    }

    @RequestMapping(value = "/insertOrUpdateOjkRincianEscrow", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    public ResponseEntitySpec<Object> insertOrUpdateOjkRincianEscrow(@RequestBody RincianEscrow request)
            throws Exception {

        ojkService.insertOrUpdateOjkData(request);
        return ResponseEntitySpecBuilder.success();
    }

    @RequestMapping(value = "/insertOrUpdateOjkRincianKerjasamaLJK", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    public ResponseEntitySpec<Object> insertOrUpdateOjkRincianKerjasamaLJK(@RequestBody RincianKerjasamaLJK request)
            throws Exception {

        ojkService.insertOrUpdateOjkData(request);
        return ResponseEntitySpecBuilder.success();
    }

    @RequestMapping(value = "/insertOrUpdateOjkRincianLayananPendukung", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    public ResponseEntitySpec<Object> insertOrUpdateOjkRincianLayananPendukung(@RequestBody RincianLayananPendukung request)
            throws Exception {

        ojkService.insertOrUpdateOjkData(request);
        return ResponseEntitySpecBuilder.success();
    }

    @RequestMapping(value = "/deleteOjkData", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public ResponseEntitySpec<Object> deleteOjkData(@RequestParam("idCode") Integer idCode)
            throws Exception {

        return ResponseEntitySpecBuilder.success(ojkService.deleteOjkData(idCode));
    }

    @RequestMapping(value = "/listOjkData", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public ResponseEntitySpec<Object> listOjkData(@RequestParam("type") String type)
            throws Exception {

        return ResponseEntitySpecBuilder.success(ojkService.listOjkData(type));
    }

    @RequestMapping(value = "/setOjkServerData", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public ResponseEntitySpec<Object> setOjkServerData(@RequestParam("type") String type)
            throws Exception {

        return ResponseEntitySpecBuilder.success(ojkService.setOjkServerData(type));
    }

}

package top.nino.chatbilibili.rest;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.nino.api.model.vo.setting.DanmuSettingStatusReqVo;
import top.nino.api.model.vo.Response;
import top.nino.chatbilibili.service.SettingService;

import javax.servlet.http.HttpServletRequest;

/**
 * @author : nino
 * @date : 2024/2/9 02:53
 */
@Slf4j
@RestController
@RequestMapping("/rest/setting")
public class RestSettingController {


    @Autowired
    private SettingService settingService;


    @ResponseBody
    @PostMapping(value = "/danmuUsing")
    public Response<?> danmuUsing(HttpServletRequest req, @RequestBody DanmuSettingStatusReqVo danmuSettingStatusReqVo) {
        settingService.loadCacheDanmuSettingByVo(danmuSettingStatusReqVo);
        settingService.writeAndReadSetting();
        return Response.success(true, req);
    }

    @ResponseBody
    @PostMapping(value = "/chatGPTUsing")
    public Response<?> chatGPTUsing(HttpServletRequest req, @RequestBody DanmuSettingStatusReqVo danmuSettingStatusReqVo) {
        settingService.loadCacheDanmuSettingByVo(danmuSettingStatusReqVo);
        settingService.writeAndReadSetting();
        return Response.success(true, req);
    }
}

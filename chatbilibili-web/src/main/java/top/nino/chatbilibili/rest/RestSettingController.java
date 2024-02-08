package top.nino.chatbilibili.rest;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.nino.api.model.enums.ResponseCode;
import top.nino.api.model.vo.AiCharacter;
import top.nino.api.model.vo.setting.ChatGPTSettingReqVo;
import top.nino.api.model.vo.setting.DanmuSettingStatusReqVo;
import top.nino.api.model.vo.Response;
import top.nino.chatbilibili.GlobalSettingCache;
import top.nino.chatbilibili.service.SettingService;
import top.nino.service.chatgpt.ChatGPTService;

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

    @Autowired
    private ChatGPTService chatGPTService;


    @ResponseBody
    @PostMapping(value = "/danmuUsing")
    public Response<?> danmuUsing(HttpServletRequest req, @RequestBody DanmuSettingStatusReqVo danmuSettingStatusReqVo) {
        settingService.loadCacheDanmuSettingByVo(danmuSettingStatusReqVo);
        settingService.writeAndReadSetting();
        return Response.success(true, req);
    }

    @ResponseBody
    @PostMapping(value = "/chatGPTUsing")
    public Response<?> chatGPTUsing(HttpServletRequest req, @RequestBody ChatGPTSettingReqVo chatGPTSettingReqVo) {
        if(!chatGPTService.checkChatGPTStatus()) {
            return Response.error(ResponseCode.AI_ERROR, req);
        }
        GlobalSettingCache.usingAiCharacterName = chatGPTSettingReqVo.getAiCharacterName();
        settingService.loadCacheChatGPTSettingByVo(chatGPTSettingReqVo);
        settingService.writeAndReadSetting();
        return Response.success(true, req);
    }

    @ResponseBody
    @PostMapping(value = "/addAiCharacter")
    public Response<?> addAiCharacter(HttpServletRequest req, @RequestBody AiCharacter aiCharacter) {
        if(!chatGPTService.checkChatGPTStatus()) {
            return Response.error(ResponseCode.AI_ERROR, req);
        }
        GlobalSettingCache.ALL_SETTING_CONF.getAiCharacterList().add(aiCharacter);
        settingService.writeAndReadSetting();
        return Response.success(true, req);
    }
}

package top.nino.chatbilibili.rest;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.nino.api.model.enums.ResponseCodeEnum;
import top.nino.api.model.vo.ai.AiCharacterReqVo;
import top.nino.api.model.vo.dto.ChatResDto;
import top.nino.api.model.vo.setting.ChatGPTSettingReqVo;
import top.nino.api.model.vo.setting.DanmuSettingStatusReqVo;
import top.nino.api.model.vo.Response;
import top.nino.chatbilibili.GlobalSettingCache;
import top.nino.chatbilibili.service.ClientService;
import top.nino.chatbilibili.service.SettingService;
import top.nino.service.chatgpt.ChatGPTService;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.atomic.AtomicInteger;

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

    @Autowired
    private ClientService clientService;


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
            return Response.error(ResponseCodeEnum.AI_ERROR, req);
        }
        settingService.loadCacheChatGPTSettingByVo(chatGPTSettingReqVo);
        GlobalSettingCache.aiQuestionCount = new AtomicInteger(0);
        settingService.writeAndReadSetting();

        if(GlobalSettingCache.ALL_SETTING_CONF.getAiReplyStatus() && GlobalSettingCache.ALL_SETTING_CONF.getAiReplyNum() > 0) {
            clientService.startAIThread();
        }
        return Response.success(true, req);
    }

    @ResponseBody
    @PostMapping(value = "/addAiCharacter")
    public Response<?> addAiCharacter(HttpServletRequest req, @RequestBody AiCharacterReqVo aiCharacterReqVo) {
        if(!chatGPTService.checkChatGPTStatus()) {
            return Response.error(ResponseCodeEnum.AI_ERROR, req);
        }
        if(!GlobalSettingCache.ALL_SETTING_CONF.isNewCharacterName(aiCharacterReqVo.getName())) {
            return Response.error(ResponseCodeEnum.AI_SAME_ROLE, req);
        }
        GlobalSettingCache.ALL_SETTING_CONF.getAiCharacterReqVoList().add(aiCharacterReqVo);
        settingService.writeAndReadSetting();
        return Response.success(true, req);
    }

    @ResponseBody
    @PostMapping(value = "/testHelloChatGPTByDescription")
    public Response<?> testHelloChatGPTByDescription(@RequestParam(required = true, name = "prompt") String prompt, HttpServletRequest req) {
        if(!chatGPTService.checkChatGPTStatus()) {
            return Response.error(ResponseCodeEnum.AI_ERROR, req);
        }
        ChatResDto chatResDto = chatGPTService.testHelloChatGPTByDescription(prompt);
        if(ObjectUtils.isEmpty(chatResDto)) {
            return Response.error(ResponseCodeEnum.AI_ERROR, req);
        }
        return Response.success(chatResDto, req);
    }

    @ResponseBody
    @GetMapping(value = "/deleteAllChatGPTCharacter")
    public Response<?> deleteAllChatGPTCharacter(HttpServletRequest req) {
        if(!chatGPTService.checkChatGPTStatus()) {
            return Response.error(ResponseCodeEnum.AI_ERROR, req);
        }
        GlobalSettingCache.ALL_SETTING_CONF.getAiCharacterReqVoList().clear();
        settingService.writeAndReadSetting();
        return Response.success(true, req);
    }
}
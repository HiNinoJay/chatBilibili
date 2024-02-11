package top.nino.chatbilibili.rest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.nino.api.model.enums.ResponseCodeEnum;
import top.nino.api.model.login.QrCodeInfo;
import top.nino.api.model.user.User;
import top.nino.api.model.vo.Response;
import top.nino.api.model.vo.dto.ChatResDto;
import top.nino.chatbilibili.GlobalSettingCache;
import top.nino.chatbilibili.service.ClientService;
import top.nino.chatbilibili.service.GlobalSettingFileService;
import top.nino.core.http.CookieUtils;
import top.nino.core.qrcode.QrcodeUtils;
import top.nino.service.http.HttpBilibiliServer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author : nino
 * @date : 2024/2/5 02:20
 */
@Slf4j
@RestController
@RequestMapping("/rest/ai")
public class RestAIController {


    /**
     *
     * @param req
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/getAnswer")
    public Response<?> getAnswer(HttpServletRequest req){
        if(CollectionUtils.isEmpty(GlobalSettingCache.aiAnswerList)) {
            return Response.success(null, req);
        }
        ChatResDto chatResDto = GlobalSettingCache.aiAnswerList.get(0);
        synchronized (GlobalSettingCache.aiAnswerList) {
            GlobalSettingCache.aiAnswerList.remove(0);
        }
        return Response.success(chatResDto, req);
    }


}

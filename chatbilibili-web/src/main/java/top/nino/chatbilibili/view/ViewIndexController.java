package top.nino.chatbilibili.view;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import top.nino.chatbilibili.GlobalSettingCache;
import top.nino.chatbilibili.service.ClientService;
import top.nino.chatbilibili.service.GlobalSettingFileService;
import top.nino.chatbilibili.service.SettingService;
import top.nino.chatbilibili.service.ThreadService;
import top.nino.service.http.HttpBilibiliServer;

import javax.servlet.http.HttpServletRequest;

/**
 * @author : nino
 * @date : 2024/2/4 20:48
 */
@Slf4j
@Controller
@RequestMapping
public class ViewIndexController {

    @Autowired
    private ThreadService threadService;

    @Autowired
    private SettingService settingService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private GlobalSettingFileService globalSettingFileService;

    @GetMapping(value = {"/", "/index"})
    public String index(HttpServletRequest req, Model model) {
        if(ObjectUtils.isNotEmpty(GlobalSettingCache.USER)) {
            model.addAttribute("loginUser", GlobalSettingCache.USER);
        }
        model.addAttribute("allSettingVo", GlobalSettingCache.ALL_SETTING_CONF.generateAllSettingVo());
        boolean haveConnectionFlag = ObjectUtils.isNotEmpty(GlobalSettingCache.bilibiliWebSocketProxy) && GlobalSettingCache.bilibiliWebSocketProxy.isOpen();
        model.addAttribute("haveConnectionFlag", haveConnectionFlag);
        if(haveConnectionFlag) {
            model.addAttribute("roomId", GlobalSettingCache.ROOM_ID);
        }
        return "index";
    }

    @RequestMapping(value = "/view/loginOut")
    public String loginOut(HttpServletRequest req) {
        req.getSession().removeAttribute("loginUser");

        if (StringUtils.isNotBlank(GlobalSettingCache.COOKIE_VALUE)) {

            HttpBilibiliServer.loginOut(GlobalSettingCache.COOKIE_VALUE);
            GlobalSettingCache.clearUserCache();

            // 如果之前的用户还有B站的弹幕服务器连接，就去关闭并且重新开启
            if(ObjectUtils.isNotEmpty(GlobalSettingCache.ROOM_ID) &&
                    ObjectUtils.isNotEmpty(GlobalSettingCache.bilibiliWebSocketProxy)) {

                clientService.closeConnection();

                try {
                    clientService.loadRoomInfoAndOpenWebSocket(GlobalSettingCache.ROOM_ID);
                } catch (Exception e) {
                    log.error("加载直播间信息并开启websocket异常", e);
                }
                if (ObjectUtils.isNotEmpty(GlobalSettingCache.ROOM_ID)) {
                    GlobalSettingCache.ALL_SETTING_CONF.setRoomId(GlobalSettingCache.ROOM_ID);
                    GlobalSettingCache.ROOMID_LONG = GlobalSettingCache.ROOM_ID;
                }
                settingService.writeAndReadSetting();
            }
        }
        return "redirect:/";
    }
}

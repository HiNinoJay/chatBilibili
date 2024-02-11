package top.nino.chatbilibili.rest;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.nino.api.model.enums.LiveStatusEnum;
import top.nino.api.model.vo.Response;
import top.nino.api.model.vo.room.RoomInfoVo;
import top.nino.chatbilibili.GlobalSettingCache;
import top.nino.chatbilibili.service.ClientService;
import top.nino.chatbilibili.service.SettingService;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author：nino
 * @Date：2024/2/6 14:39
 */
@Slf4j
@RestController
@RequestMapping("/rest/room")
public class RestRoomController {

    @Autowired
    private ClientService clientService;

    @Autowired
    private SettingService settingService;

    @ResponseBody
    @GetMapping(value = "/connectRoom")
    public Response<?> connectRoom(HttpServletRequest req, @RequestParam("roomId") Long roomId) {

        if (ObjectUtils.isEmpty(GlobalSettingCache.bilibiliWebSocketProxy) || !GlobalSettingCache.bilibiliWebSocketProxy.isOpen()) {
            try {
                clientService.loadRoomInfoAndOpenWebSocket(roomId);
            } catch (Exception e) {
                log.error("加载直播间信息并开启websocket异常", e);
            }
            if (ObjectUtils.isNotEmpty(GlobalSettingCache.ROOM_ID)) {
                GlobalSettingCache.ALL_SETTING_CONF.setRoomId(GlobalSettingCache.ROOM_ID);
                GlobalSettingCache.ROOMID_LONG = GlobalSettingCache.ROOM_ID;
            }
            settingService.writeAndReadSetting();
        }

        if(ObjectUtils.isNotEmpty(GlobalSettingCache.bilibiliWebSocketProxy) && GlobalSettingCache.bilibiliWebSocketProxy.isOpen()) {
            // 说明连接成功
            RoomInfoVo roomInfoVo = new RoomInfoVo();
            roomInfoVo.setLiveStatus(LiveStatusEnum.getByCode(GlobalSettingCache.LIVE_STATUS).getMsg());
            roomInfoVo.setShortRoomId(GlobalSettingCache.SHORT_ROOM_ID);
            roomInfoVo.setRoomId(GlobalSettingCache.ROOM_ID);
            roomInfoVo.setAnchorUid(GlobalSettingCache.ANCHOR_UID);
            roomInfoVo.setAnchorName(GlobalSettingCache.ANCHOR_NAME);
            return Response.success(roomInfoVo, req);
        }
        return Response.error(req);
    }


    @ResponseBody
    @GetMapping(value = "/closeConnection")
    public Response<?> closeConnection(HttpServletRequest req) {
        if(!ObjectUtils.isEmpty(GlobalSettingCache.bilibiliWebSocketProxy)) {
            clientService.closeConnection();
        }
        return Response.success(true, req);
    }


    @ResponseBody
    @GetMapping(value = "/startReceiveDanmu")
    public Response<?> startReceiveDanmu(HttpServletRequest req) {
        clientService.startReceiveDanmuThread();
        if(GlobalSettingCache.ALL_SETTING_CONF.getAiReplyStatus() && GlobalSettingCache.ALL_SETTING_CONF.getAiReplyNum() > 0) {
            clientService.startAIThread();
        }
        return Response.success(true, req);
    }
}

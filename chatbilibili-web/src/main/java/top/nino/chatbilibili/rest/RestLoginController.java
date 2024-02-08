package top.nino.chatbilibili.rest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.nino.api.model.enums.ResponseCode;
import top.nino.api.model.login.QrCodeInfo;
import top.nino.api.model.user.User;
import top.nino.api.model.user.UserCookieInfo;
import top.nino.api.model.vo.Response;
import top.nino.chatbilibili.GlobalSettingCache;
import top.nino.chatbilibili.service.ClientService;
import top.nino.chatbilibili.service.GlobalSettingFileService;
import top.nino.core.http.CookieUtils;
import top.nino.core.qrcode.QrcodeUtils;
import top.nino.service.http.HttpBilibiliServer;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author : nino
 * @date : 2024/2/5 02:20
 */
@Slf4j
@RestController
@RequestMapping("/rest/login")
public class RestLoginController {


    @Autowired
    private GlobalSettingFileService globalSettingFileService;

    @Autowired
    private ClientService clientService;


    /**
     * 手动输入cookie
     * @param cookieValue
     * @param req
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/loginByCookie")
    public Response<?> loginByCookie(@RequestParam(name = "cookieValue") String cookieValue, HttpServletRequest req){
        User user = HttpBilibiliServer.httpGetUserInfo(cookieValue);
        if(ObjectUtils.isNotEmpty(user)) {
            GlobalSettingCache.COOKIE_VALUE = cookieValue;
            GlobalSettingCache.USER_COOKIE_INFO = CookieUtils.parseCookie(cookieValue);;
            GlobalSettingCache.USER = user;
            if(ObjectUtils.isNotEmpty(GlobalSettingCache.bilibiliWebSocketProxy)) {
                // 先关闭以前的
                clientService.closeConnection();
                try {
                    clientService.loadRoomInfoAndOpenWebSocket(GlobalSettingCache.ROOM_ID);
                } catch (Exception e) {
                    log.error("重新开启b站弹幕服务器连接异常", e);
                }
            }
            globalSettingFileService.refreshValidLoginInfoToFile();
        }
        return Response.success(ObjectUtils.isNotEmpty(user), req);
    }

    // 拿到B站官方登录二维码信息
    @ResponseBody
    @PostMapping(value = "/getQrCodeInfo")
    public Response<?> qrCodeInfo(HttpServletRequest req) {
        QrCodeInfo qrCodeInfo = HttpBilibiliServer.httpGetQrcodeInfo();
        req.getSession().setAttribute("qrcode", qrCodeInfo);
        return Response.success(qrCodeInfo, req);
    }

    // 将url生成 二维码
    @ResponseBody
    @GetMapping(value = "/generateQrCodeByUrl")
    public void qrcode(HttpServletRequest req, HttpServletResponse resp, @RequestParam("url") String url) {
        QrcodeUtils.creatRrCode(url, 140, 140, resp);
    }


    // 检查二维码扫描情况
    @ResponseBody
    @PostMapping(value = "/checkScanQrCodeStatus")
    public Response<?> loginCheck(HttpServletRequest req) {
        QrCodeInfo qrCodeInfo = (QrCodeInfo) req.getSession().getAttribute("qrcode");
        if(ObjectUtils.isEmpty(qrCodeInfo)) {
            return Response.error(ResponseCode.NONE_QRCODE_KEY_INFO, req);
        }

        Response response = HttpBilibiliServer.httpCheckQrcodeScanStatus(qrCodeInfo.getQrcode_key());

        if(response.getCode().equals(ResponseCode.SUCCESS.getCode()) && ObjectUtils.isNotEmpty(response.getResult())) {
            GlobalSettingCache.COOKIE_VALUE = String.valueOf(response.getResult());
            GlobalSettingCache.USER_COOKIE_INFO = CookieUtils.parseCookie(GlobalSettingCache.COOKIE_VALUE);
            GlobalSettingCache.USER = HttpBilibiliServer.httpGetUserInfo(GlobalSettingCache.COOKIE_VALUE);;
            if(ObjectUtils.isNotEmpty(GlobalSettingCache.bilibiliWebSocketProxy)) {
                // 先关闭以前的
                clientService.closeConnection();
                try {
                    clientService.loadRoomInfoAndOpenWebSocket(GlobalSettingCache.ROOM_ID);
                } catch (Exception e) {
                    log.error("重新开启b站弹幕服务器连接异常", e);
                }
            }
            globalSettingFileService.refreshValidLoginInfoToFile();
        }
        return response;
    }
}

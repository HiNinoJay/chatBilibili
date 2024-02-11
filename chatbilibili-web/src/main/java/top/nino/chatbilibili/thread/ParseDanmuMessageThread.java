package top.nino.chatbilibili.thread;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import top.nino.api.model.vo.websocket.WebsocketMessagePackageVo;
import top.nino.chatbilibili.GlobalSettingCache;
import top.nino.api.model.danmu.*;
import top.nino.api.model.superchat.SuperChat;
import top.nino.chatbilibili.client.ChatBilibiliWebsocketController;
import top.nino.core.time.JodaTimeUtils;
import top.nino.core.websocket.DanmuUtils;
import top.nino.core.websocket.parse.*;
import top.nino.service.spring.SpringUtils;


/**
 *
 * 解析弹幕数据的线程
 * @author nino
 */
@Slf4j
public class ParseDanmuMessageThread extends Thread {


    public volatile boolean closeFlag = false;

    private final ChatBilibiliWebsocketController chatBilibiliWebsocketController = SpringUtils.getBean(ChatBilibiliWebsocketController.class);

    @Override
    public void run() {

        while (!closeFlag) {

            // 当没有弹幕需要解析时，等待
            if (CollectionUtils.isEmpty(GlobalSettingCache.danmuList) || StringUtils.isBlank(GlobalSettingCache.danmuList.get(0))) {
                synchronized (GlobalSettingCache.parseDanmuMessageThread) {
                    try {
                        GlobalSettingCache.parseDanmuMessageThread.wait();
                    } catch (InterruptedException e) {
                        log.info("处理弹幕包信息线程关闭", e);
                    }
                }
            }

                // 拿取第一条消息
            String message = GlobalSettingCache.danmuList.get(0);


            JSONObject messageJsonObject = JSONObject.parseObject(message);

            String haveKnownCmd = DanmuUtils.parseHaveKnownCmd(messageJsonObject.getString("cmd"));
            if (StringUtils.isBlank(haveKnownCmd)) {
                synchronized (GlobalSettingCache.danmuList) {
                    GlobalSettingCache.danmuList.remove(0);
                }
                continue;
            }
            // log.info("收到一条可解析的消息：{}", messageJsonObject.toString());
            String parseResultString = "";
            String cmdResultString = haveKnownCmd;
            Object objectResult = null;

            switch (haveKnownCmd) {
                // 弹幕
                case "DANMU_MSG":

                    if(!GlobalSettingCache.ALL_SETTING_CONF.isDanmuStatus()) {
                        break;
                    }

                    DanmuMessage danmuMessage = null;
                    try {
                        danmuMessage = DanmuMessage.getDanmuMessageByJSONArray(messageJsonObject.getJSONArray("info"));
                    } catch (Exception e) {
                        log.info("弹幕{}解析异常", messageJsonObject.toString(), e);
                        break;
                    }

                    if(danmuMessage.getMsg_type() != 0) {
                        break;
                    }

                    if(!GlobalSettingCache.ALL_SETTING_CONF.isNormalDanmuStatus() && ParseDanmuUserRoleUtils.judgeNormal(GlobalSettingCache.ANCHOR_UID, danmuMessage)) {
                        break;
                    }

                    // 老爷
                    if(!GlobalSettingCache.ALL_SETTING_CONF.isVipDanmuStatus() && ParseDanmuUserRoleUtils.judgeVip(danmuMessage)) {
                        break;
                    }

                    // 舰长
                    if(!GlobalSettingCache.ALL_SETTING_CONF.isGuardDanmuStatus() && ParseDanmuUserRoleUtils.judgeGuard(danmuMessage)) {
                        break;
                    }

                    // 房管
                    if(!GlobalSettingCache.ALL_SETTING_CONF.isManagerDanmuStatus() && ParseDanmuUserRoleUtils.judgeManager(GlobalSettingCache.ANCHOR_UID, danmuMessage)) {
                        break;
                    }

                    // 勋章弹幕
                    boolean is_xunzhang = true;
                    if (GlobalSettingCache.ALL_SETTING_CONF.is_barrage_anchor_shield()&& GlobalSettingCache.ROOM_ID!=null) {
                        // 房管
                        if (danmuMessage.getMedal_room() != (long) GlobalSettingCache.ROOM_ID) {
                            is_xunzhang = false;
                        }
                    }

                    if(!is_xunzhang) {
                        break;
                    }

                    DanmuUserRoleInfo danmuUserRoleInfo = DanmuUserRoleInfo.copyHbarrage(danmuMessage);
                    if (danmuMessage.getUid().equals(GlobalSettingCache.ANCHOR_UID)) {
                        danmuUserRoleInfo.setManager((short) 2);
                    }

                    StringBuilder danmuResultString = new StringBuilder();

                    // 添加弹幕时间
                    danmuResultString.append(JodaTimeUtils.formatDateTime(danmuMessage.getTimestamp()));

                    // 是不是表情弹幕
                    boolean is_emoticon = danmuMessage.getMsg_emoticon() != null && danmuMessage.getMsg_emoticon() == 1;
                    if (is_emoticon) {
                        danmuResultString.append(":收到表情:");
                    } else {
                        danmuResultString.append(":收到弹幕:");
                    }


                    if (GlobalSettingCache.ALL_SETTING_CONF.is_barrage_vip()) {
                        danmuResultString.append(ParseDanmuUserRoleUtils.parseVip(danmuMessage));
                    } else {
                        danmuUserRoleInfo.setVip((short) 0);
                        danmuUserRoleInfo.setSvip((short) 0);
                    }



                    // 舰长
                    if (GlobalSettingCache.ALL_SETTING_CONF.is_barrage_guard()) {
                        danmuResultString.append(ParseDanmuUserRoleUtils.parseGuard(danmuMessage));
                    } else {
                        danmuUserRoleInfo.setUguard((short) 0);
                    }

                    // 房管
                    if (GlobalSettingCache.ALL_SETTING_CONF.is_barrage_manager()) {
                        danmuResultString.append(ParseDanmuUserRoleUtils.parseManager(GlobalSettingCache.ANCHOR_UID, danmuMessage));
                    } else {
                        danmuUserRoleInfo.setManager((short) 0);
                    }

                    // 勋章+勋章等级
                    if (GlobalSettingCache.ALL_SETTING_CONF.is_barrage_medal()) {
                        if (StringUtils.isNotBlank(danmuMessage.getMedal_name())) {
                            danmuResultString.append("[").append(danmuMessage.getMedal_name()).append(" ")
                                    .append(danmuMessage.getMedal_level()).append("]");
                        }
                    } else {
                        danmuUserRoleInfo.setMedal_level(null);
                        danmuUserRoleInfo.setMedal_name(null);
                        danmuUserRoleInfo.setMedal_room(null);
                        danmuUserRoleInfo.setMedal_anchor(null);
                    }

                    // 用户等级
                    if (GlobalSettingCache.ALL_SETTING_CONF.is_barrage_ul()) {
                        danmuResultString.append(ParseDanmuUserRoleUtils.parseUserLevel(danmuMessage));
                    } else {
                        danmuUserRoleInfo.setUlevel(null);
                    }

                    danmuResultString.append(ParseDanmuUserRoleUtils.parseDanmuContent(danmuMessage));

                    parseResultString = danmuResultString.toString();
                    objectResult = danmuUserRoleInfo;
                    break;

                // 送普通礼物
                case "SEND_GIFT":
                    if(!GlobalSettingCache.ALL_SETTING_CONF.isGiftStatus()) {
                        break;
                    }

                    if(!GlobalSettingCache.ALL_SETTING_CONF.isFreeGiftStatus()) {
                        break;
                    }

                    JSONObject giftJsonObject = JSONObject.parseObject(messageJsonObject.getString("data"));
                    short gift_type = ParseDanmuGiftUtils.parseCoinType(giftJsonObject.getString("coin_type"));

                    if(gift_type != 1) {
                        break;
                    }

                    Gift normalGift = null;
                    try{
                        normalGift = Gift.getGiftByJsonObject(giftJsonObject, gift_type);
                        parseResultString = ParseDanmuGiftUtils.parseGiftDanmuContent(normalGift);
                    } catch (Exception e) {
                        log.info("礼物异常:", e);
                    }
                    objectResult = normalGift;
                    break;
                // 上舰
                case "GUARD_BUY":
                    if(!GlobalSettingCache.ALL_SETTING_CONF.isGiftStatus()) {
                        break;
                    }

                    if(!GlobalSettingCache.ALL_SETTING_CONF.isGuardBuyGiftStatus()) {
                        break;
                    }

                    Guard guard = JSONObject.parseObject(messageJsonObject.getString("data"), Guard.class);

                    parseResultString = ParseDanmuGuardUtils.parseGuardDanmuContent(guard);
                    objectResult = ParseDanmuGuardUtils.parseGuardDanmuToGiftClass(guard);
                    break;

                // 醒目留言
                case "SUPER_CHAT_MESSAGE":
                    if(!GlobalSettingCache.ALL_SETTING_CONF.isGiftStatus()) {
                        break;
                    }

                    if(!GlobalSettingCache.ALL_SETTING_CONF.isScGiftStatus()) {
                        break;
                    }

                    SuperChat superChat = JSONObject.parseObject(messageJsonObject.getString("data"), SuperChat.class);

                    parseResultString = ParseDanmuSuperChatUtils.parseSuperChatDanmeContent(superChat);
                    objectResult = superChat;
                    break;

                // 醒目留言日文翻译
                case "SUPER_CHAT_MESSAGE_JPN":
                    break;

                // 删除醒目留言
                case "SUPER_CHAT_MESSAGE_DELETE":
                    break;

                // 直播间粉丝数更新 经常
                case "ROOM_REAL_TIME_MESSAGE_UPDATE":
                    GlobalSettingCache.FANS_NUM = JSONObject.parseObject(messageJsonObject.getString("data")).getLong("fans");
                    break;

                // 直播开启
                case "LIVE":
                    GlobalSettingCache.LIVE_STATUS = 1;
                    GlobalSettingCache.IS_ROOM_POPULARITY = true;
                    break;

                // 直播超管被切断
                case "CUT_OFF":
                    break;

                // 本房间已被封禁
                case "ROOM_LOCK":
                    break;

                // 直播准备中(或者是关闭直播)
                case "PREPARING":
                    GlobalSettingCache.LIVE_STATUS = 0;
                    GlobalSettingCache.IS_ROOM_POPULARITY = false;
                    break;

                case "WATCHED_CHANGE":
                    GlobalSettingCache.ROOM_WATCHER = JSONObject.parseObject(messageJsonObject.getString("data")).getLong("num");
                    break;

                case "POPULARITY_RED_POCKET_NEW":
                    if(!GlobalSettingCache.ALL_SETTING_CONF.isGiftStatus()) {
                        break;
                    }
                    RedPackage redPackage = JSONObject.parseObject(messageJsonObject.getString("data"), RedPackage.class);
                    String redPackageResultString = ParseDanmuRedPackageUtils.parseRedPackageDanmeContent(redPackage);

                    parseResultString = redPackageResultString;
                    objectResult = ParseDanmuRedPackageUtils.parseRedPackageDanmuToGiftClass(redPackage);
                    break;
                case "LIKE_INFO_V3_UPDATE":
                    GlobalSettingCache.ROOM_LIKE = JSONObject.parseObject(messageJsonObject.getString("data")).getLong("click_count");
                    break;

                default:
                    break;
            }

            if(StringUtils.isNotBlank(parseResultString) && StringUtils.isNotBlank(cmdResultString) && ObjectUtils.isNotEmpty(objectResult)) {
                logAndSendToLocalWebSocket(parseResultString, haveKnownCmd, objectResult);
                putAIQuetion(haveKnownCmd, parseResultString);
            }

            // 解析完成移除
            synchronized (GlobalSettingCache.danmuList) {
                GlobalSettingCache.danmuList.remove(0);
            }
            // log.info("消息解析成功:{}", messageJsonObject);
        }

    }

    private void putAIQuetion(String haveKnownCmd, String parseResultString) {

        if(!"DANMU_MSG".equals(haveKnownCmd)){
            return;
        }


        // 先放AI是不是要回答
        if(!GlobalSettingCache.ALL_SETTING_CONF.getAiReplyStatus()
                || GlobalSettingCache.ALL_SETTING_CONF.getAiReplyNum() <= 0
                || StringUtils.isBlank(GlobalSettingCache.ALL_SETTING_CONF.getUsingAiCharacterName())) {
            return;
        }


        int num = GlobalSettingCache.aiQuestionCount.incrementAndGet();
        if(num < GlobalSettingCache.ALL_SETTING_CONF.getAiReplyNum()) {
            log.info("AI回答已探测到可弹幕，但还未到达回答频率：{}/{}", num, GlobalSettingCache.ALL_SETTING_CONF.getAiReplyNum());
            return;
        }

        synchronized (GlobalSettingCache.aiQuestionList) {
            GlobalSettingCache.aiQuestionList.add(parseResultString);
        }

        if (ObjectUtils.isNotEmpty(GlobalSettingCache.aiThread) && !GlobalSettingCache.aiThread.closeFlag) {
            synchronized (GlobalSettingCache.aiThread) {
                GlobalSettingCache.aiThread.notify();
            }
        }
        GlobalSettingCache.aiQuestionCount.set(0);
        log.info("已探测到AI可回答的弹幕：{}", parseResultString);
    }


    private void logAndSendToLocalWebSocket(String parseResultString, String cmd, Object result) {
        // 控制台打印
        if (GlobalSettingCache.ALL_SETTING_CONF.is_cmd()) {
            log.info(parseResultString);
        }

        try {
            // 发送到本地网页
            String sendToViewString = WebsocketMessagePackageVo.toJson(cmd, (short) 0, result);
            log.info("向前端页面发送:{}", sendToViewString);
            chatBilibiliWebsocketController.sendMessageToView(sendToViewString);
        } catch (Exception e) {
            log.info("弹幕消息发送到本地网页异常", e);
        }

        // 日志处理
        if (ObjectUtils.isNotEmpty(GlobalSettingCache.logThread) && !GlobalSettingCache.logThread.closeFlag) {
            synchronized (GlobalSettingCache.logList) {
                GlobalSettingCache.logList.add(parseResultString);
            }

            synchronized (GlobalSettingCache.logThread) {
                GlobalSettingCache.logThread.notify();
            }
        }
    }

}

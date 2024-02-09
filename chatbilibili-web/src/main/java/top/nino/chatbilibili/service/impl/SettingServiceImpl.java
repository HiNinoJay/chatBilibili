package top.nino.chatbilibili.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.nino.api.model.enums.AiReplyNumEnum;
import top.nino.api.model.vo.setting.ChatGPTSettingReqVo;
import top.nino.api.model.vo.setting.DanmuSettingStatusReqVo;
import top.nino.chatbilibili.GlobalSettingCache;
import top.nino.chatbilibili.service.GlobalSettingFileService;
import top.nino.chatbilibili.service.SettingService;
import top.nino.chatbilibili.service.ThreadService;
import top.nino.chatbilibili.AllSettingConfig;
import top.nino.chatbilibili.service.ClientService;
import top.nino.core.data.BASE64Utils;
import top.nino.core.file.LocalGlobalSettingFileUtils;


import java.util.HashMap;
import java.util.Map;


/**
 * @author nino
 */
@Slf4j
@Service
public class SettingServiceImpl implements SettingService {



    @Override
    public void writeAndReadSetting() {
        synchronized (GlobalSettingCache.ALL_SETTING_CONF) {

            Map<String, String> localGlobalSettingMap = new HashMap<>();

            if (ObjectUtils.isNotEmpty(GlobalSettingCache.USER)) {
                // cookie字符串放入map
                localGlobalSettingMap.put(GlobalSettingCache.FILE_COOKIE_PREFIX, BASE64Utils.encode(GlobalSettingCache.COOKIE_VALUE.getBytes()));
            }

            // 页面上的所有配置, 还把上一次连接到的直播间号保存了
            localGlobalSettingMap.put(GlobalSettingCache.FILE_SETTING_PREFIX, BASE64Utils.encode(GlobalSettingCache.ALL_SETTING_CONF.objectToJson().getBytes()));

            // 将当前的全局配置写到本地
            LocalGlobalSettingFileUtils.writeFile(GlobalSettingCache.GLOBAL_SETTING_FILE_NAME, localGlobalSettingMap);

            try {
                // 再读出来
                GlobalSettingCache.ALL_SETTING_CONF = JSONObject.parseObject(
                        new String(BASE64Utils.decode(LocalGlobalSettingFileUtils.readFile(GlobalSettingCache.GLOBAL_SETTING_FILE_NAME).get(GlobalSettingCache.FILE_SETTING_PREFIX))),
                        AllSettingConfig.class);
            } catch (Exception e) {
                log.error("读取配置文件历史房间失败:" + e);
            }
        }
    }

    @Override
    public void loadCacheDanmuSettingByVo(DanmuSettingStatusReqVo danmuSettingStatusReqVo) {
        synchronized (GlobalSettingCache.ALL_SETTING_CONF) {
            GlobalSettingCache.ALL_SETTING_CONF.setDanmuStatus(danmuSettingStatusReqVo.isDanmuStatus());
            GlobalSettingCache.ALL_SETTING_CONF.setNormalDanmuStatus(danmuSettingStatusReqVo.isNormalStatus());
            GlobalSettingCache.ALL_SETTING_CONF.setGuardDanmuStatus(danmuSettingStatusReqVo.isGuardStatus());
            GlobalSettingCache.ALL_SETTING_CONF.setVipDanmuStatus(danmuSettingStatusReqVo.isVipStatus());
            GlobalSettingCache.ALL_SETTING_CONF.setManagerDanmuStatus(danmuSettingStatusReqVo.isManagerStatus());

            GlobalSettingCache.ALL_SETTING_CONF.setGiftStatus(danmuSettingStatusReqVo.isGiftStatus());
            GlobalSettingCache.ALL_SETTING_CONF.setFreeGiftStatus(danmuSettingStatusReqVo.isNormalGiftStatus());
            GlobalSettingCache.ALL_SETTING_CONF.setScGiftStatus(danmuSettingStatusReqVo.isScGiftStatus());

        }

    }

    @Override
    public void loadCacheChatGPTSettingByVo(ChatGPTSettingReqVo chatGPTSettingReqVo) {
        synchronized (GlobalSettingCache.ALL_SETTING_CONF) {
            GlobalSettingCache.ALL_SETTING_CONF.setAiReplyStatus(chatGPTSettingReqVo.getAiReplyStatus());
            GlobalSettingCache.ALL_SETTING_CONF.setAiReplyNum(AiReplyNumEnum.getByMsg(chatGPTSettingReqVo.getAiReplyNum()).getCode());
            GlobalSettingCache.ALL_SETTING_CONF.setUsingAiCharacterName(chatGPTSettingReqVo.getAiCharacterName());
        }

    }
}

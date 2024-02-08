package top.nino.chatbilibili.service;


import top.nino.api.model.vo.setting.ChatGPTSettingReqVo;
import top.nino.api.model.vo.setting.DanmuSettingStatusReqVo;

/**
 * @author nino
 */
public interface SettingService {
	void writeAndReadSetting();

	void loadCacheDanmuSettingByVo(DanmuSettingStatusReqVo danmuSettingStatusReqVo);

	void loadCacheChatGPTSettingByVo(ChatGPTSettingReqVo chatGPTSettingReqVo);
}

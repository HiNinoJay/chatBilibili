package top.nino.chatbilibili.thread;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import top.nino.api.model.vo.dto.ChatResDto;
import top.nino.chatbilibili.GlobalSettingCache;
import top.nino.chatbilibili.service.impl.ClientServiceImpl;
import top.nino.core.file.LogFileUtils;
import top.nino.service.chatgpt.impl.ChatGPTServiceImpl;
import top.nino.service.spring.SpringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


/**
 * @author nino
 */
@Slf4j
public class AIThread extends Thread{

	public volatile boolean closeFlag = false;

	private ChatGPTServiceImpl chatGPTService = SpringUtils.getBean(ChatGPTServiceImpl.class);

	@Override
	public void run() {

		while (!closeFlag) {

			if(ObjectUtils.isEmpty(GlobalSettingCache.bilibiliWebSocketProxy)&& !GlobalSettingCache.bilibiliWebSocketProxy.isOpen()) {
				return;
			}


			if(CollectionUtils.isEmpty(GlobalSettingCache.aiQuestionList) || StringUtils.isBlank(GlobalSettingCache.aiQuestionList.get(0))) {
				synchronized (GlobalSettingCache.aiThread) {
					try {
						GlobalSettingCache.aiThread.wait();
					} catch (InterruptedException e) {
//						LOGGER.info("日志线程关闭:" + e);
					}
				}
			}

			if(StringUtils.isBlank(GlobalSettingCache.ALL_SETTING_CONF.getUsingAiCharacterName())) {
				log.info("已探测到需要AI回答的弹幕，但是未设置角色性格。");
				continue;
			}

			String dammuMessage = GlobalSettingCache.aiQuestionList.get(0);

			ChatResDto chatResDto = null;

			try {
				chatResDto = chatGPTService.chatCompletions(
						GlobalSettingCache.ALL_SETTING_CONF.getUsingAiCharacterName(),
						GlobalSettingCache.ALL_SETTING_CONF.getAiCharacterPromptByUsingName(),
						dammuMessage);
				int index = dammuMessage.indexOf(":收到弹幕:");
				String questionTime = dammuMessage.substring(0, index);
				String userName = dammuMessage.substring(index + ":收到弹幕:".length(), dammuMessage.length()).split(" ")[0];
				chatResDto.setUserName(userName);
				chatResDto.setPrompt(dammuMessage.substring(dammuMessage.indexOf("它说:"), dammuMessage.length()));
				chatResDto.setQuestionTime(questionTime);

			} catch (Exception e) {
				log.error("访问chatGPT回答问题{}异常", dammuMessage, e);
			}

			if(ObjectUtils.isNotEmpty(chatResDto)) {
				synchronized (GlobalSettingCache.aiThread) {
					GlobalSettingCache.aiQuestionList.remove(0);
				}
				synchronized (GlobalSettingCache.aiAnswerList) {
					GlobalSettingCache.aiAnswerList.add(chatResDto);
				}
				log.info("已获取到AI回答：{}", JSON.toJSONString(chatResDto));
			}

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}

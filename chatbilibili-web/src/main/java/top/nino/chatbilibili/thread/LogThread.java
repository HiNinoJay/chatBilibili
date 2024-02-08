package top.nino.chatbilibili.thread;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import top.nino.chatbilibili.GlobalSettingCache;
import top.nino.core.file.LogFileUtils;


/**
 * @author nino
 */
@Slf4j
public class LogThread extends Thread{

	public volatile boolean closeFlag = false;

	@Override
	public void run() {

		while (!closeFlag) {

			if(ObjectUtils.isEmpty(GlobalSettingCache.bilibiliWebSocketProxy)&& !GlobalSettingCache.bilibiliWebSocketProxy.isOpen()) {
				return;
			}
			String logString = GlobalSettingCache.logList.get(0);

			if(CollectionUtils.isEmpty(GlobalSettingCache.logList) || StringUtils.isBlank(GlobalSettingCache.logList.get(0))) {
				synchronized (GlobalSettingCache.logThread) {
					try {
						GlobalSettingCache.logThread.wait();
					} catch (InterruptedException e) {
//						LOGGER.info("日志线程关闭:" + e);
					}
				}
			}



			if(StringUtils.isBlank(logString)) {
				continue;
			}
			LogFileUtils.logFile(logString, GlobalSettingCache.GLOBAL_SETTING_FILE_NAME, GlobalSettingCache.ROOM_ID);

			GlobalSettingCache.logList.remove(0);

			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}

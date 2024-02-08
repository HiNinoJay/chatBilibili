package top.nino.chatbilibili.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import top.nino.chatbilibili.GlobalSettingCache;
import top.nino.chatbilibili.AllSettingConfig;
import top.nino.chatbilibili.service.ThreadService;
import top.nino.chatbilibili.thread.*;


/**
 * @author nino
 */
@Slf4j
@Service
public class ThreadServiceImpl implements ThreadService {

	//关闭全部线程
	public void closeAll(){
		closeHeartByteThread();
		closeLogThread();
		closeParseMessageThread();
	}

	/**
	 * 开启弹幕处理线程
	 *
	 */
	@Override
	public void startParseMessageThread() {


		if (ObjectUtils.isNotEmpty(GlobalSettingCache.parseDanmuMessageThread)  && !"TERMINATED".equals(GlobalSettingCache.parseDanmuMessageThread.getState().toString())) {
			return;
		}
		GlobalSettingCache.parseDanmuMessageThread = new ParseDanmuMessageThread();
		GlobalSettingCache.parseDanmuMessageThread.closeFlag = false;
		GlobalSettingCache.parseDanmuMessageThread.start();
	}

	@Override
	public void startHeartCheckBilibiliDanmuServerThread() {
		if (ObjectUtils.isNotEmpty(GlobalSettingCache.heartCheckBilibiliDanmuServerThread)) {
			return;
		}
		// 没有心跳线程，就去启动
		GlobalSettingCache.heartCheckBilibiliDanmuServerThread = new HeartCheckBilibiliDanmuServerThread();
		GlobalSettingCache.heartCheckBilibiliDanmuServerThread.HFLAG = false;
		GlobalSettingCache.heartCheckBilibiliDanmuServerThread.start();

	}

	@Override
	public void startLogThread() {
		if (GlobalSettingCache.logThread != null) {
			return;
		}
		GlobalSettingCache.logThread = new LogThread();
		GlobalSettingCache.logThread.closeFlag = false;
		GlobalSettingCache.logThread.start();
		if (GlobalSettingCache.logThread != null && !GlobalSettingCache.logThread.getState().toString().equals("TERMINATED")) {
		}
	}

	@Override
	public void closeParseMessageThread() {
		if (GlobalSettingCache.parseDanmuMessageThread != null) {
			GlobalSettingCache.parseDanmuMessageThread.closeFlag = true;
			GlobalSettingCache.parseDanmuMessageThread.interrupt();
			GlobalSettingCache.parseDanmuMessageThread = null;
		}
	}

	@Override
	public void closeHeartByteThread() {
		if (GlobalSettingCache.heartCheckBilibiliDanmuServerThread != null) {
			GlobalSettingCache.heartCheckBilibiliDanmuServerThread.HFLAG = true;
			GlobalSettingCache.heartCheckBilibiliDanmuServerThread.interrupt();
			GlobalSettingCache.heartCheckBilibiliDanmuServerThread = null;
		}
	}

	@Override
	public void closeLogThread() {
		if (GlobalSettingCache.logThread != null) {
			GlobalSettingCache.logThread.closeFlag = true;
			GlobalSettingCache.logThread.interrupt();
			GlobalSettingCache.logThread = null;
		}
	}

}

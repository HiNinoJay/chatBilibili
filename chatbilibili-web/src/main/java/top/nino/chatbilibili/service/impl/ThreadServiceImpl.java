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


	// 关闭用户相关线程
	@Override
	public void closeByUserLogOut(){
		// 关闭日志线程
		closeLogThread();
		// 关闭心跳检测线程
		closeHeartByteThread();
		// 关闭解析弹幕线程
		closeParseMessageThread();
		GlobalSettingCache.bilibiliWebSocketProxy.close();
		log.info("关闭 websocket 及其 相关线程成功");
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
		if (GlobalSettingCache.parseDanmuMessageThread != null
				&& !GlobalSettingCache.parseDanmuMessageThread.getState().toString().equals("TERMINATED")) {
		}
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
	public boolean startUserOnlineThread() {
		if (GlobalSettingCache.heartBeatThread != null || GlobalSettingCache.heartBeatsThread != null
				|| GlobalSettingCache.userOnlineHeartThread != null) {
			return false;
		}
		GlobalSettingCache.heartBeatThread = new HeartBeatThread();
		GlobalSettingCache.heartBeatThread.FLAG = false;
		GlobalSettingCache.heartBeatThread.start();

		GlobalSettingCache.heartBeatsThread = new HeartBeatsThread();
		GlobalSettingCache.heartBeatsThread.FLAG = false;
		GlobalSettingCache.heartBeatsThread.start();

		GlobalSettingCache.userOnlineHeartThread = new UserOnlineHeartThread();
		GlobalSettingCache.userOnlineHeartThread.FLAG = false;
		GlobalSettingCache.userOnlineHeartThread.start();

		if (GlobalSettingCache.heartBeatThread != null && GlobalSettingCache.heartBeatsThread != null
				&& GlobalSettingCache.userOnlineHeartThread != null
				&& !GlobalSettingCache.heartBeatThread.getState().toString().equals("TERMINATED")
				&& !GlobalSettingCache.heartBeatsThread.getState().toString().equals("TERMINATED")
				&& !GlobalSettingCache.userOnlineHeartThread.getState().toString().equals("TERMINATED")) {
			return true;
		} else {
			closeUserOnlineThread();
		}
		return false;
	}

	@Override
	public boolean startSmallHeartThread() {
		if (GlobalSettingCache.smallHeartThread != null
				&& !GlobalSettingCache.smallHeartThread.getState().toString().equals("TERMINATED")) {
			return false;
		}
		if(null== GlobalSettingCache.userOnlineHeartThread) {
			return false;
		}

		GlobalSettingCache.smallHeartThread = new SmallHeartThread();
		GlobalSettingCache.smallHeartThread.FLAG = false;
		GlobalSettingCache.smallHeartThread.start();
		if (GlobalSettingCache.smallHeartThread != null
				&& !GlobalSettingCache.smallHeartThread.getState().toString().equals("TERMINATED")) {
			return true;
		}
		return false;
	}

	public boolean startGiftShieldThread(String giftName, int time) {

		return false;
	}

	@Override
	public boolean startFollowShieldThread(int time) {

		return false;
	}

	public boolean startWelcomeShieldThread(int time) {

		return false;
	}

	@Override
	public void closeUserOnlineThread() {
	}

	@Override
	public void setParseMessageThread(AllSettingConfig allSettingConfig) {
		if (GlobalSettingCache.parseDanmuMessageThread != null) {

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

	@Override
	public void closeSmallHeartThread() {

	}

}

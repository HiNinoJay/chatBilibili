package top.nino.chatbilibili.service;



/**
 * @author nino
 */
public interface ThreadService {

	void closeAll();

	// 开启处理弹幕包线程
	void startParseMessageThread();

	// 关闭处理弹幕包线程 core
	void closeParseMessageThread();

	// 开启心跳线程
	void startHeartCheckBilibiliDanmuServerThread();

	// 关闭心跳线程 core
	void closeHeartByteThread();

	// 开启日志线程
	void startLogThread();

	// 关闭日志线程
	void closeLogThread();
}

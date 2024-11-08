package top.nino.chatbilibili.client;

import lombok.extern.slf4j.Slf4j;
import top.nino.api.model.room.RoomAnchorInfo;


import java.net.URISyntaxException;


/**
 * @author nino
 */
@Slf4j
public class BilibiliWebSocketProxy extends BilibiliWebsocket {


	public BilibiliWebSocketProxy(String url, RoomAnchorInfo roomAnchorInfo) throws URISyntaxException, InterruptedException {
		super(url, roomAnchorInfo);
		log.info("Connectin(连接中)...........................................");
		super.connectBlocking();
		log.info("Connecting Success(连接成功)");
	}

	@Override
	public void onClose(int code, String reason, boolean remote) {
		super.onClose(code, reason, remote);
		log.info("已关闭和B站弹幕服务器的连接。");
	}
}

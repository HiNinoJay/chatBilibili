package top.nino.chatbilibili.client;

import lombok.extern.slf4j.Slf4j;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import top.nino.api.model.room.RoomAnchorInfo;
import top.nino.chatbilibili.GlobalSettingConf;
import top.nino.chatbilibili.thread.ReConnThread;
import top.nino.chatbilibili.ws.HandleWebsocketPackage;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;

@Slf4j
public class Websocket extends WebSocketClient {


	public Websocket(String url, RoomAnchorInfo roomAnchorInfo) throws URISyntaxException {
		super(new URI(url));
		log.info("已尝试连接至服务器地址:" + url + ";真实房间号为:" + roomAnchorInfo.getRoomid() + ";主播名字为:" + roomAnchorInfo.getUname());
		// TODO 自动生成的构造函数存根
	}

	@Override
	public void onOpen(ServerHandshake handshakedata) {
		// TODO 自动生成的方法存根
		log.info("websocket connect open(连接窗口打开)");
	}

	@Override
	public void onMessage(ByteBuffer message) {
		// TODO 自动生成的方法存根
		if(GlobalSettingConf.parseDanmuMessageThread !=null && ! GlobalSettingConf.parseDanmuMessageThread.FLAG) {
		try {
			HandleWebsocketPackage.handleMessage(message);
		} catch (Exception e) {
			// TODO 自动生成的 catch 块
			log.info("解析错误日志生成，请将log底下文件发给管理员,或github开issue发送错误"+e);
		}
//			synchronized (PublicDataConf.parseMessageThread) {
//				PublicDataConf.parseMessageThread.notify();
//			}
		}
	}

	@Override
	public void onClose(int code, String reason, boolean remote) {
		log.info("websocket connect close(连接已经断开)，纠错码:" + code);
		GlobalSettingConf.heartCheckBilibiliDanmuServerThread.HFLAG = true;
		GlobalSettingConf.parseDanmuMessageThread.FLAG = true;
		if (code != 1000) {
			log.info("websocket connect close(连接意外断开，正在尝试重连)，错误码:" + code);
			if (!GlobalSettingConf.webSocketProxy.isOpen()) {
				if (GlobalSettingConf.reConnThread != null) {
					if (GlobalSettingConf.reConnThread.getState().toString().equals("TERMINATED")) {
						GlobalSettingConf.reConnThread = new ReConnThread();
						GlobalSettingConf.reConnThread.start();
					} else {

					}
				} else {
					GlobalSettingConf.reConnThread = new ReConnThread();
					GlobalSettingConf.reConnThread.start();
				}
			} else {
				GlobalSettingConf.reConnThread.RFLAG = true;
			}
		}
	}

	@Override
	public void onError(Exception ex) {
		// TODO 自动生成的方法存根
		log.error("[错误信息，请将log文件下的日志发送给管理员]websocket connect error,message:" + ex.getMessage());
		log.info("尝试重新链接");
		synchronized (GlobalSettingConf.webSocketProxy) {
			GlobalSettingConf.webSocketProxy.close(1006);
			if (!GlobalSettingConf.webSocketProxy.isOpen()) {
				if (GlobalSettingConf.reConnThread != null) {
					if (GlobalSettingConf.reConnThread.getState().toString().equals("TERMINATED")) {
						GlobalSettingConf.reConnThread = new ReConnThread();
						GlobalSettingConf.reConnThread.start();
					} else {

					}
				} else {
					GlobalSettingConf.reConnThread = new ReConnThread();
					GlobalSettingConf.reConnThread.start();
				}
			} else {
				GlobalSettingConf.reConnThread.RFLAG = true;
			}
		}
	}

	@Override
	public void onMessage(String message) {
		// TODO 自动生成的方法存根

	}

}

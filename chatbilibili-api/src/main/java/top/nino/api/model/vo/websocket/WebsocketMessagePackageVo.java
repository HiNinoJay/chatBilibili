package top.nino.api.model.vo.websocket;

import lombok.Data;
import top.nino.api.model.tools.FastJsonUtils;


import java.io.Serializable;

/**
 * 前端页面发送弹幕信息
 * @author nino
 */
@Data
public class WebsocketMessagePackageVo implements Serializable,Cloneable{
	
	private static final long serialVersionUID = 4807973278850564054L;
	private static WebsocketMessagePackageVo websocketMessagePackageVo = new WebsocketMessagePackageVo();
	private String cmd;
	private Short status;
	private Object result;
	

	public static WebsocketMessagePackageVo getWebsocketMessagePackageVo() {
		try {
			return (WebsocketMessagePackageVo) websocketMessagePackageVo.clone();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new WebsocketMessagePackageVo();
	}

	public static WebsocketMessagePackageVo getWebsocketMessagePackageVo(String cmd, Short status, Object result) {
		try {
			WebsocketMessagePackageVo ws = (WebsocketMessagePackageVo) websocketMessagePackageVo.clone();
			ws.setCmd(cmd);
			ws.setStatus(status);
			ws.setResult(result);
			return ws;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new WebsocketMessagePackageVo();
	}

	public static String toJson(String cmd,Short status,Object result) {
		try {
			WebsocketMessagePackageVo ws = (WebsocketMessagePackageVo) websocketMessagePackageVo.clone();
			ws.setCmd(cmd);
			ws.setStatus(status);
			ws.setResult(result);
			return FastJsonUtils.toJson(ws);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
}

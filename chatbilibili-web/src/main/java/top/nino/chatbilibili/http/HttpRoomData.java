package top.nino.chatbilibili.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.CollectionUtils;
import top.nino.api.model.danmu.RoomInfo;
import top.nino.api.model.room.*;
import top.nino.api.model.server.DanmuInfo;
import top.nino.chatbilibili.GlobalSettingConf;
import top.nino.chatbilibili.tool.CurrencyTools;
import top.nino.core.OkHttp3Utils;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class HttpRoomData {
	private static Logger LOGGER = LogManager.getLogger(HttpRoomData.class);

	/**
	 * 获取连接目标房间websocket端口 接口
	 *
	 * @return
	 */
	public static DanmuInfo httpGetConf() {
		String data = null;
		JSONObject jsonObject = null;
		DanmuInfo danmuInfo = null;
		short code = -1;
		Map<String, String> headers = null;
		Map<String, String> datas = null;
		headers = new HashMap<>(3);
		headers.put("referer", "https://live.bilibili.com/" + CurrencyTools.parseRoomId());
		headers.put("user-agent",
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36");
		if (StringUtils.isNotBlank(GlobalSettingConf.COOKIE_VALUE)) {
			headers.put("cookie", GlobalSettingConf.COOKIE_VALUE);
		}
		datas = new HashMap<>(3);
		datas.put("id", GlobalSettingConf.ROOM_ID.toString());
		datas.put("type", "0");
		try {
			data = OkHttp3Utils.getHttp3Utils()
					.httpGet("https://api.live.bilibili.com/xlive/web-room/v1/index/getDanmuInfo", headers, datas)
					.body().string();
		} catch (Exception e) {
			// TODO 自动生成的 catch 块
			LOGGER.error(e);
			data = null;
		}
		if (data == null)
			return null;
		jsonObject = JSONObject.parseObject(data);
		code = jsonObject.getShort("code");
		if (code == 0) {
			danmuInfo = jsonObject.getObject("data", DanmuInfo.class);
		} else {
			LOGGER.error("未知错误,原因:" + jsonObject.getString("message"));
		}
		return danmuInfo;
	}

	/**
	 * 获取目标房间部分信息
	 *
	 * @param roomid
	 * @return
	 */
	public static RoomAnchorInfo httpGetRoomData(Long roomid) {
		String data = null;
		JSONObject jsonObject = null;
		RoomAnchorInfo roomAnchorInfo = null;
		short code = -1;
		Map<String, String> headers = null;
		headers = new HashMap<>(3);
		headers.put("referer", "https://live.bilibili.com/" + CurrencyTools.parseRoomId());
		headers.put("user-agent",
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36");
//		if (StringUtils.isNotBlank(PublicDataConf.COOKIE_VALUE)) {
//			headers.put("cookie", PublicDataConf.COOKIE_VALUE);
//		}
		try {
			data = OkHttp3Utils.getHttp3Utils()
					.httpGet("https://api.live.bilibili.com/room_ex/v1/RoomNews/get?roomid=" + roomid, headers, null)
					.body().string();
		} catch (Exception e) {
			// TODO 自动生成的 catch 块
			LOGGER.error(e);
			data = null;
		}
		if (data == null)
			return roomAnchorInfo;
//		LOGGER.info("获取到的room:" + data);
		jsonObject = JSONObject.parseObject(data);
		code = jsonObject.getShort("code");
		if (code == 0) {
			roomAnchorInfo = jsonObject.getObject("data", RoomAnchorInfo.class);
		} else {
			LOGGER.error("直播房间号不存在，或者未知错误，请尝试更换房间号,原因:" + jsonObject.getString("message"));
		}
		return roomAnchorInfo;
	}

	/**
	 * 获取房间信息
	 *
	 * @param roomId
	 */
	public static RoomStatusInfo httpGetRoomInit(Long roomId) {
		String data = null;
		RoomStatusInfo roomStatusInfo = null;
		JSONObject jsonObject = null;
		short code = -1;
		Map<String, String> headers = null;
		headers = new HashMap<>(2);
		headers.put("user-agent",
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36");
//		if (StringUtils.isNotBlank(PublicDataConf.COOKIE_VALUE)) {
//			headers.put("cookie", PublicDataConf.COOKIE_VALUE);
//		}
		try {
			data = OkHttp3Utils.getHttp3Utils()
					.httpGet("https://api.live.bilibili.com/room/v1/Room/room_init?id=" + roomId, headers, null).body()
					.string();
		} catch (Exception e) {
			// TODO 自动生成的 catch 块
			LOGGER.error(e);
			data = null;
		}
		if (data == null)
			return roomStatusInfo;
//		LOGGER.info("获取到的room:" + data);
		jsonObject = JSONObject.parseObject(data);
		code = jsonObject.getShort("code");
		if (code == 0) {
			roomStatusInfo = jsonObject.getObject("data", RoomStatusInfo.class);
		} else {
			LOGGER.error("直播房间号不存在，或者未知错误，请尝试更换房间号,原因:" + jsonObject.getString("message"));
		}
		;
		return roomStatusInfo;
	}

	/**
	 * 获取房间最详细信息 日后扩容 目前只是获取主播uid 改
	 *
	 * @return
	 */
	public static RoomAllInfo httpGetRoomInfo() {
		String data = null;
		JSONObject jsonObject = null;
		RoomAllInfo roomAllInfo = new RoomAllInfo();
		AnchorMedalInfo anchorMedalInfo =null;
		RoomInfo roomInfo = null;
		short code = -1;
		Map<String, String> headers = null;
		headers = new HashMap<>(3);
		headers.put("user-agent",
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36");
		headers.put("referer", "https://live.bilibili.com/" + CurrencyTools.parseRoomId());
		try {
			data = OkHttp3Utils.getHttp3Utils()
					.httpGet("https://api.live.bilibili.com/xlive/web-room/v1/index/getInfoByRoom?room_id="
							+ CurrencyTools.parseRoomId(), headers, null)
					.body().string();
		} catch (Exception e) {
			// TODO 自动生成的 catch 块
			LOGGER.error(e);
			data = null;
		}
		if (data == null)
			return roomAllInfo;
//		LOGGER.info("获取到的room:" + data);
		jsonObject = JSONObject.parseObject(data);
		code = jsonObject.getShort("code");
		if (code == 0) {
			roomInfo = JSON.parseObject(((JSONObject) jsonObject.get("data")).getString("room_info"),
					RoomInfo.class);
			anchorMedalInfo = JSON.parseObject(jsonObject.getJSONObject("data").getJSONObject("anchor_info").getString("medal_info"),
					AnchorMedalInfo.class);
		} else {
			LOGGER.error("获取房间详细信息失败，请稍后尝试:" + jsonObject.getString("message"));
		}
		roomAllInfo.setRoomInfo(roomInfo);
		roomAllInfo.setAnchorMedalInfo(anchorMedalInfo);
		return roomAllInfo;
	}

	/**
	 * 获取关注名字集合
	 *
	 * @return 关注uname集
	 */
	public static ConcurrentHashMap<Long, String> httpGetFollowers() {
		String data = null;
		JSONObject jsonObject = null;
		Integer page = null;
		JSONArray jsonArray = null;
		short code = -1;
		ConcurrentHashMap<Long, String> followConcurrentHashMap = null;
		Map<String, String> headers = null;
		Map<String, String> datas = null;
		if (GlobalSettingConf.ANCHOR_UID == null) {
			return null;
		}
		if (GlobalSettingConf.FANS_NUM.equals(null) || GlobalSettingConf.FANS_NUM.equals(0L)) {
			page = 1;
		} else {
			page = (int) Math.ceil((float) GlobalSettingConf.FANS_NUM / 20F);
			page = page > 5 ? 5 : page;
		}
		followConcurrentHashMap = new ConcurrentHashMap<Long, String>();
		while (page > 0) {
			headers = new HashMap<>(3);
			headers.put("referer", "https://space.bilibili.com/{" + GlobalSettingConf.ANCHOR_UID + "}/");
			headers.put("user-agent",
					"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36");
//			if (StringUtils.isNotBlank(PublicDataConf.COOKIE_VALUE)) {
//				headers.put("cookie", PublicDataConf.COOKIE_VALUE);
//			}
			datas = new HashMap<>(6);
			datas.put("vmid", GlobalSettingConf.ANCHOR_UID.toString());
			datas.put("pn", String.valueOf(page));
			datas.put("ps", "50");
			datas.put("order", "desc");
			datas.put("jsonp", "jsonp");
			try {
				data = OkHttp3Utils.getHttp3Utils()
						.httpGet("https://api.bilibili.com/x/relation/followers", headers, datas).body().string();
			} catch (Exception e) {
				// TODO 自动生成的 catch 块
				LOGGER.error(e);
				data = null;
			}
			if (data == null)
				return null;
			jsonObject = JSONObject.parseObject(data);
			try {
				code = jsonObject.getShort("code");
			} catch (Exception e) {
				// TODO: handle exception
				LOGGER.error("获取关注错误");
				return followConcurrentHashMap;
			}

			if (code == 0) {
				GlobalSettingConf.FANS_NUM = ((JSONObject) jsonObject.get("data")).getLong("total");
				jsonArray = ((JSONObject) jsonObject.get("data")).getJSONArray("list");
				for (Object object : jsonArray) {
					followConcurrentHashMap.put(((JSONObject) object).getLong("mid"),
							((JSONObject) object).getString("uname"));
				}
			} else {
				LOGGER.error("获取关注数失败，请重试" + jsonObject.getString("message"));
			}
			page--;
		}
		return followConcurrentHashMap;
	}

	/**
	 * 获取关注数
	 *
	 * @return 返回关注数
	 */
	public static Long httpGetFollowersNum() {
		String data = null;
		JSONObject jsonObject = null;
		short code = -1;
		Map<String, String> headers = null;
		Map<String, String> datas = null;
		Long followersNum = 0L;
		if (GlobalSettingConf.ANCHOR_UID == null) {
			return followersNum;
		}
		headers = new HashMap<>(3);
		headers.put("referer", "https://space.bilibili.com/{" + GlobalSettingConf.ANCHOR_UID + "}/");
		headers.put("user-agent",
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36");
//			if (StringUtils.isNotBlank(PublicDataConf.COOKIE_VALUE)) {
//				headers.put("cookie", PublicDataConf.COOKIE_VALUE);
//			}
		datas = new HashMap<>(2);
		datas.put("vmid", GlobalSettingConf.ANCHOR_UID.toString());
		try {
			data = OkHttp3Utils.getHttp3Utils().httpGet("https://api.bilibili.com/x/relation/stat", headers, datas)
					.body().string();
		} catch (Exception e) {
			// TODO 自动生成的 catch 块
			LOGGER.error(e);
			data = null;
		}
		if (data == null)
			return followersNum;
		jsonObject = JSONObject.parseObject(data);
		code = jsonObject.getShort("code");
		if (code == 0) {
			followersNum = ((JSONObject) jsonObject.get("data")).getLong("follower");
		} else {
			LOGGER.error("获取关注数失败，请重试" + jsonObject.getString("message"));
		}
		return followersNum;
	}

	public static Map<Long, String> httpGetGuardList() {
		String data = null;
		Map<Long, String> guardMap = new ConcurrentHashMap<>();
		JSONObject jsonObject = null;
		JSONArray jsonArray = null;
		Map<String, String> headers = null;
		Map<String, String> datas = null;
		short code = -1;
		int totalSize = httpGetGuardListTotalSize();
		int page = 0;
		if (totalSize == 0) {
			return null;
		}
		page = (int) Math.ceil((float) totalSize / 29F);
		if (page == 0) {
			page = 1;
		}
		for (int i = 1; i <= page; i++) {
			headers = new HashMap<>(3);
			headers.put("referer", "https://live.bilibili.com/" + CurrencyTools.parseRoomId());
			headers.put("user-agent",
					"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36");
//			if (StringUtils.isNotBlank(PublicDataConf.COOKIE_VALUE)) {
//				headers.put("cookie", PublicDataConf.COOKIE_VALUE);
//			}
			datas = new HashMap<>(4);
			datas.put("roomid", GlobalSettingConf.ROOM_ID.toString());
			datas.put("page", String.valueOf(i));
			datas.put("ruid", GlobalSettingConf.ANCHOR_UID.toString());
			datas.put("page_size", "29");
			try {
				data = OkHttp3Utils.getHttp3Utils()
						.httpGet("https://api.live.bilibili.com/xlive/app-room/v1/guardTab/topList", headers, datas)
						.body().string();
			} catch (Exception e) {
				// TODO 自动生成的 catch 块
				LOGGER.error(e);
				data = null;
			}
			if (data == null)
				return null;
			jsonObject = JSONObject.parseObject(data);
			code = jsonObject.getShort("code");
			if (code == 0) {
				jsonArray = ((JSONObject) jsonObject.get("data")).getJSONArray("list");
				for (Object object : jsonArray) {
					try {
						Thread.sleep(1);
					} catch (InterruptedException e) {
						// TODO 自动生成的 catch 块
						e.printStackTrace();
					}
					guardMap.put(((JSONObject) object).getLong("uid"), ((JSONObject) object).getString("username"));
				}
				if (i == 1) {
					jsonArray = ((JSONObject) jsonObject.get("data")).getJSONArray("top3");
					for (Object object : jsonArray) {
						guardMap.put(((JSONObject) object).getLong("uid"),
								((JSONObject) object).getString("username"));
					}
				}
			} else {
				LOGGER.error("直播房间号不存在，或者未知错误，请尝试更换房间号,原因:" + jsonObject.getString("message"));
			}
		}
		return guardMap;
	}

	public static int httpGetGuardListTotalSize() {
		String data = null;
		Map<String, String> headers = null;
		Map<String, String> datas = null;
		int num = 0;
		JSONObject jsonObject = null;
		short code = -1;
		headers = new HashMap<>(3);
		headers.put("referer", "https://live.bilibili.com/" + CurrencyTools.parseRoomId());
		headers.put("user-agent",
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36");
//		if (StringUtils.isNotBlank(PublicDataConf.COOKIE_VALUE)) {
//			headers.put("cookie", PublicDataConf.COOKIE_VALUE);
//		}
		datas = new HashMap<>(5);
		datas.put("roomid", GlobalSettingConf.ROOM_ID.toString());
		datas.put("page", String.valueOf(1));
		datas.put("ruid", GlobalSettingConf.ANCHOR_UID.toString());
		datas.put("page_size", "29");
		try {
			data = OkHttp3Utils.getHttp3Utils()
					.httpGet("https://api.live.bilibili.com/xlive/app-room/v1/guardTab/topList", headers, datas).body()
					.string();
		} catch (Exception e) {
			// TODO 自动生成的 catch 块
			LOGGER.error(e);
			data = null;
		}
		if (data == null)
			return num;
		jsonObject = JSONObject.parseObject(data);
		code = jsonObject.getShort("code");
		if (code == 0) {
			num = ((JSONObject) ((JSONObject) jsonObject.get("data")).get("info")).getInteger("num");
		} else {
			LOGGER.error("直播房间号不存在，或者未知错误，请尝试更换房间号,原因:" + jsonObject.getString("message"));
		}
		return num;
	}

	public static CheckTx httpGetCheckTX() {
		String data = null;
		JSONObject jsonObject = null;
		short code = -1;
		Map<String, String> headers = null;
		headers = new HashMap<>(3);
		headers.put("user-agent",
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36");
		headers.put("referer", "https://live.bilibili.com/" + CurrencyTools.parseRoomId());
//		if (StringUtils.isNotBlank(PublicDataConf.COOKIE_VALUE)) {
//			headers.put("cookie", PublicDataConf.COOKIE_VALUE);
//		}
		try {
			data = OkHttp3Utils.getHttp3Utils()
					.httpGet("https://api.live.bilibili.com/xlive/lottery-interface/v1/Anchor/Check?roomid="
							+ CurrencyTools.parseRoomId(), headers, null)
					.body().string();
		} catch (Exception e) {
			// TODO 自动生成的 catch 块
			LOGGER.error(e);
			data = null;
		}
		if (data == null)
			return null;
		jsonObject = JSONObject.parseObject(data);
		code = jsonObject.getShort("code");
		if (code == 0) {
			if (jsonObject.get("data") != null) {
				return new CheckTx(((JSONObject) jsonObject.get("data")).getLong("room_id"),
						((JSONObject) jsonObject.get("data")).getString("gift_name"),
						((JSONObject) jsonObject.get("data")).getShort("time"));
			}
		} else {
			LOGGER.error("检查天选礼物失败,原因:" + jsonObject.getString("message"));
		}
		return null;
	}

	public static LotteryInfoWeb httpGetLotteryInfoWeb() {
		String data = null;
		JSONObject jsonObject = null;
		short code = -1;
		Map<String, String> headers = null;
		headers = new HashMap<>(3);
		headers.put("user-agent",
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36");
		headers.put("referer", "https://live.bilibili.com/" + CurrencyTools.parseRoomId());
		if (StringUtils.isNotBlank(GlobalSettingConf.COOKIE_VALUE)) {
			headers.put("cookie", GlobalSettingConf.COOKIE_VALUE);
		}
		try {
			data = OkHttp3Utils.getHttp3Utils()
					.httpGet("https://api.live.bilibili.com/xlive/lottery-interface/v1/lottery/getLotteryInfoWeb?roomid="
							+ GlobalSettingConf.ROOM_ID, headers, null)
					.body().string();
		} catch (Exception e) {
			// TODO 自动生成的 catch 块
			LOGGER.error(e);
			data = null;
		}
		if (data == null)
			return null;
		jsonObject = JSONObject.parseObject(data);
		code = jsonObject.getShort("code");
		if (code == 0) {
			if (jsonObject.get("data") != null) {
				LotteryInfoWeb lotteryInfoWeb = jsonObject.getJSONObject("data").toJavaObject(LotteryInfoWeb.class);
				return lotteryInfoWeb;
			}
		} else {
			LOGGER.error("获取房间抽奖失败,原因:" + jsonObject.getString("message"));
		}
		return null;
	}
//
//	public static void httpGetRoomGifts() {
//		String data = null;
//		JSONObject jsonObject = null;
//		JSONArray jsonArray = null;
//		short code = -1;
//		Map<String, String> headers = null;
//		headers = new HashMap<>(3);
//		headers.put("user-agent",
//				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36");
//		headers.put("referer", "https://live.bilibili.com/" + CurrencyTools.parseRoomId());
//		try {
//			data = OkHttp3Utils.getHttp3Utils()
//					.httpGet("https://api.live.bilibili.com/xlive/web-room/v1/giftPanel/giftConfig?platform=pc&room_id="
//							+ CurrencyTools.parseRoomId(), headers, null)
//					.body().string();
//		} catch (Exception e) {
//			// TODO 自动生成的 catch 块
//			LOGGER.error(e);
//			data = null;
//		}
//		if (data == null)
//			return;
//		jsonObject = JSONObject.parseObject(data);
//		code = jsonObject.getShort("code");
//		if (code == 0) {
//			jsonArray = ((JSONObject) jsonObject.get("data")).getJSONArray("list");
//			if (PublicDataConf.roomGiftConcurrentHashMap.size() < 1) {
//				for (Object object : jsonArray) {
//					PublicDataConf.roomGiftConcurrentHashMap.put(((JSONObject) object).getInteger("id"),
//							new RoomGift(((JSONObject) object).getInteger("id"), ((JSONObject) object).getString("name"), ParseIndentityTools.parseCoin_type(((JSONObject) object).getString("coin_type")), ((JSONObject) object).getString("webp")));
//				}
//			}
//		} else {
//			LOGGER.error("获取礼物失败,原因:" + jsonObject.getString("message"));
//		}
//	}



	public static Map<Integer,RoomGift> httpGetRoomGifts(Long roomid) {
		String data = null;
		JSONObject jsonObject = null;
		JSONArray jsonArray = null;
		short code = -1;
		Map<Integer,RoomGift> giftMaps = new HashMap<>();
		Map<String, String> headers = null;
		headers = new HashMap<>(3);
		headers.put("user-agent",
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36");
		headers.put("referer", "https://live.bilibili.com/" +roomid);
		try {
			data = OkHttp3Utils.getHttp3Utils()
					.httpGet("https://api.live.bilibili.com/xlive/web-room/v1/giftPanel/giftConfig?platform=pc&room_id="
							+ roomid, headers, null)
					.body().string();
		} catch (Exception e) {
			// TODO 自动生成的 catch 块
			LOGGER.error(e);
			data = null;
		}
		if (data == null)
			return giftMaps;
		jsonObject = JSONObject.parseObject(data);
		code = jsonObject.getShort("code");
		if (code == 0) {
			jsonArray = ((JSONObject) jsonObject.get("data")).getJSONArray("list");
			for (Object object : jsonArray) {
				RoomGift roomGift =JSONObject.parseObject(((JSONObject)object).toJSONString(),RoomGift.class);
				giftMaps.put(roomGift.getId(),roomGift);
			}
		} else {
			LOGGER.error("获取礼物失败,原因:" + jsonObject.getString("message"));
		}
		return giftMaps;
	}



	public static List<RoomBlock> getBlockList(int page){
		String data = null;
		JSONObject jsonObject = null;
		JSONArray jsonArray = null;
		List<RoomBlock> roomBlocks = new ArrayList<>();
		Map<String, String> headers = null;
		Map<String, String> datas = null;
		headers = new HashMap<>(4);
		datas = new HashMap<>(4);
		headers.put("user-agent",
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36");
		headers.put("referer", "https://live.bilibili.com/" + CurrencyTools.parseRoomId());
		if (StringUtils.isNotBlank(GlobalSettingConf.COOKIE_VALUE)) {
			headers.put("cookie", GlobalSettingConf.COOKIE_VALUE);
		}
		datas.put("roomid",String.valueOf(GlobalSettingConf.ROOM_ID));
		datas.put("page",String.valueOf(page));
		try {
			data = OkHttp3Utils.getHttp3Utils()
					.httpGet("https://api.live.bilibili.com/liveact/ajaxGetBlockList",headers,datas)
					.body().string();
		} catch (Exception e) {
			// TODO 自动生成的 catch 块
			LOGGER.error(e);
			data = null;
		}
		if (data == null)
			return roomBlocks;
		jsonObject = JSONObject.parseObject(data);
		short code = jsonObject.getShort("code");
		if (code == 0) {
			jsonArray = jsonObject.getJSONArray("data");
			if(!CollectionUtils.isEmpty(jsonArray)){
				roomBlocks = new ArrayList<>(jsonArray.toJavaList(RoomBlock.class));
			}
			return roomBlocks;
		}
		return roomBlocks;
	}
}

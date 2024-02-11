package top.nino.chatbilibili;

import lombok.ToString;
import org.springframework.context.annotation.Configuration;
import top.nino.api.model.user.UserCookieInfo;
import top.nino.api.model.enums.LiveStatusEnum;
import top.nino.api.model.room.AnchorMedalInfo;
import top.nino.api.model.user.User;
import top.nino.api.model.user.UserManager;
import top.nino.api.model.user_in_room_barrageMsg.UserBarrageMsg;
import top.nino.api.model.vo.dto.ChatResDto;
import top.nino.api.model.vo.setting.DanmuSettingStatusReqVo;
import top.nino.chatbilibili.client.BilibiliWebSocketProxy;
import top.nino.chatbilibili.thread.*;

import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * @author nino
 */
@ToString
@Configuration
public class GlobalSettingCache {

	//------------- 1.基本预设好的常量-----------开始----------------------
	public final static String GLOBAL_SETTING_FILE_NAME = "chatBilibili-globalSetting";
	public final static String FILE_COOKIE_PREFIX = "validCookie";
	public final static String FILE_SETTING_PREFIX = "setting";

	// 包头长
	public final static char PACKAGE_HEAD_LENGTH = 16;

	// 心跳包&验证包协议版本
	public final static char PACKAGE_VERSION = 1;

	// 验证包协议类型
	public final static int FIRST_PACKAGE_TYPE = 7;

	// 心跳包 16进制
	public final static String HEART_BYTE = "0000001f0010000100000002000000015b6f626a656374204f626a6563745d";

	//-------------1.基本预设好的常量-----------结束----------------------







	//------------- 2.运行中加载/缓存的数据-----------开始----------------------

	// 设置
	public static AllSettingConfig ALL_SETTING_CONF;

	// cookie String串
	public static String COOKIE_VALUE;

	// cookie parse 解析后 的最小有效pojo存储（还没有进行网络验证该cookie是否有效）
	public static UserCookieInfo USER_COOKIE_INFO;

	// user信息（应该是验证cookie有效后才有数值）
	public static User USER;

	// 房间号
	public static Long ROOM_ID;

	// 房间长号
	public static Long ROOMID_LONG;

	// 房间短号
	public static Long SHORT_ROOM_ID;

	//是否显示人气
	public static Boolean IS_ROOM_POPULARITY;

	// 主播uid
	public static Long ANCHOR_UID;

	// 主播名称
	public static String ANCHOR_NAME;

	// 粉丝数
	public static Long FANS_NUM;

	// 房间人气
	public static Long ROOM_POPULARITY;

	// 点赞数量
	public static Long ROOM_LIKE;

	// 房间观看人数（历史）
	public static Long ROOM_WATCHER;

	// 直播状态 0不直播 1直播 2轮播
	public static Integer LIVE_STATUS = LiveStatusEnum.CLOSED.getCode();

	// 主播勋章信息
	public static AnchorMedalInfo ANCHOR_MEDAL_INFO;

	// url 直播弹幕websocket地址
	public static String ROOM_DANMU_WEBSOCKET_URL = "wss://broadcastlv.chat.bilibili.com:2245/sub";

	// user弹幕长度
	public static UserBarrageMsg USER_BARRAGE_MESSAGE;

	// user房间管理信息
	public static UserManager USER_MANAGER;

	// AI回答频率计数
	public static AtomicInteger aiQuestionCount;


	//------------- 2.运行中加载/缓存的数据-----------结束----------------------



	//------------- 3.线程----------开始----------------------

	// websocket客户端主线程
	public static BilibiliWebSocketProxy bilibiliWebSocketProxy;

	// 处理弹幕包集合
	public volatile static Vector<String> danmuList;

	// log日志待写入集合
	public volatile static Vector<String> logList;

	// AI待回答集合
	public volatile static Vector<String> aiQuestionList;

	// AI已回答集合
	public volatile static Vector<ChatResDto> aiAnswerList;

	// 心跳线程
	public static HeartCheckBilibiliDanmuServerThread heartCheckBilibiliDanmuServerThread;

	// 处理信息分类线程
	public static ParseDanmuMessageThread parseDanmuMessageThread;

	// AI回答线程
	public static AIThread aiThread;

	// 日志线程
	public static LogThread logThread;

	// 重新连接线程
	public static ReConnThread reConnThread;

	//------------- 3.线程-----------结束----------------------



	//------------- 4.行为-----------开始----------------------

	public static void clearUserCache(){
		GlobalSettingCache.COOKIE_VALUE = null;
		GlobalSettingCache.USER_COOKIE_INFO = null;
		GlobalSettingCache.USER = null;
		GlobalSettingCache.USER_BARRAGE_MESSAGE = null;
		GlobalSettingCache.USER_MANAGER = null;
	}


	//------------- 4.行为-----------结束----------------------





	//心跳包协议类型
	public final static int heartPackageType = 2;

	//心跳包&验证包的尾巴其他
	public final static int packageOther = 1;

	//待发弹幕集
	public final static Vector<String> barrageString = new Vector<String>();

	public static String SMALLHEART_ADRESS = null;

	public static boolean is_sign= false;

	public static int manager_login_size=0;

	public static void init_connect(){
		GlobalSettingCache.barrageString.clear();
		GlobalSettingCache.ROOM_ID = null;
		GlobalSettingCache.ANCHOR_NAME = null;
		GlobalSettingCache.ANCHOR_UID= null;
		GlobalSettingCache.FANS_NUM = null;
		GlobalSettingCache.SHORT_ROOM_ID = null;
		GlobalSettingCache.LIVE_STATUS = 0;
		GlobalSettingCache.ROOM_POPULARITY = 1L;
	}

	public static void loadDanmuUsing(DanmuSettingStatusReqVo danmuSettingStatusReqVo) {
		GlobalSettingCache.ALL_SETTING_CONF.setDanmuStatus(danmuSettingStatusReqVo.isDanmuStatus());
		GlobalSettingCache.ALL_SETTING_CONF.setNormalDanmuStatus(danmuSettingStatusReqVo.isNormalStatus());
		GlobalSettingCache.ALL_SETTING_CONF.setGuardDanmuStatus(danmuSettingStatusReqVo.isGuardStatus());
		GlobalSettingCache.ALL_SETTING_CONF.setVipDanmuStatus(danmuSettingStatusReqVo.isVipStatus());
		GlobalSettingCache.ALL_SETTING_CONF.setManagerDanmuStatus(danmuSettingStatusReqVo.isManagerStatus());
	}
}

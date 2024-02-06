package top.nino.chatbilibili;

import lombok.ToString;
import org.springframework.context.annotation.Configuration;
import top.nino.api.model.auto_reply.AutoReply;
import top.nino.api.model.user.UserCookieInfo;
import top.nino.chatbilibili.conf.base.CenterSetConf;
import top.nino.api.model.danmu.Gift;
import top.nino.api.model.enums.LiveStatusEnum;
import top.nino.api.model.room.MedalInfoAnchor;
import top.nino.api.model.user.AutoSendGift;
import top.nino.api.model.user.User;
import top.nino.api.model.user.UserManager;
import top.nino.api.model.user_in_room_barrageMsg.UserBarrageMsg;
import top.nino.chatbilibili.client.WebSocketProxy;
import top.nino.chatbilibili.thread.*;

import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author nino
 */
@ToString
@Configuration
public class GlobalSettingConf {

	//------------- 1.基本预设好的常量-----------开始----------------------
	public final static String GLOBAL_SETTING_FILE_NAME = "chatBilibili-globalSetting";
	public final static String FILE_COOKIE_PREFIX = "validCookie";
	public final static String FILE_SETTING_PREFIX = "setting";

	// url 直播弹幕websocket地址
	public static String ROOM_DANMU_WEBSOCKET_URL = "wss://broadcastlv.chat.bilibili.com:2245/sub";

	// 包头长
	public final static char packageHeadLength = 16;

	// 心跳包&验证包协议版本
	public final static char packageVersion = 1;

	// 验证包协议类型
	public final static int firstPackageType = 7;

	// 心跳包 16进制
	public final static String heartByte="0000001f0010000100000002000000015b6f626a656374204f626a6563745d";

	//------------- 基本预设好的常量-----------结束----------------------


	//------------- 2.运行中加载/缓存的数据-----------开始----------------------

	// 设置
	public static CenterSetConf centerSetConf;

	// cookie String串
	public static String COOKIE_VALUE;

	// cookie parse 解析后 的最小有效pojo存储（还没有进行网络验证该cookie是否有效）
	public static UserCookieInfo USER_COOKIE_INFO;

	// user信息（应该是验证cookie有效后才有数值）
	public static User USER;

	// 房间号
	public static Long ROOM_ID;

	// 房间长号
	public static Long ROOMID_LONG = null;

	// 房间短号
	public static Long SHORT_ROOM_ID;

	//是否显示人气
	public static Boolean IS_ROOM_POPULARITY = false;

	// 主播uid
	public static Long ANCHOR_UID = null;

	// 主播名称
	public static String ANCHOR_NAME = null;

	// 粉丝数
	public static Long FANS_NUM =null;

	// 直播状态 0不直播 1直播 2轮播
	public static Integer LIVE_STATUS = LiveStatusEnum.CLOSED.getCode();

	// user弹幕长度
	public static UserBarrageMsg USER_BARRAGE_MESSAGE;

	//------------- 运行中加载/缓存的数据-----------结束----------------------


	//------------- 3.行为-----------开始----------------------
	public static void clearUserCache(){
		GlobalSettingConf.COOKIE_VALUE = null;
		GlobalSettingConf.USER_COOKIE_INFO = null;
		GlobalSettingConf.USER = null;
		GlobalSettingConf.USER_BARRAGE_MESSAGE = null;
	}
	//------------- 行为-----------结束----------------------



	//------------- 4.线程----------开始----------------------

	// websocket客户端主线程
	public static WebSocketProxy webSocketProxy;

	// 心跳线程
	public static HeartByteThread heartByteThread;

	// 处理信息分类线程
	public static ParseMessageThread parseMessageThread;

	// 日志线程
	public static LogThread logThread;

	//------------- 4.线程-----------结束----------------------

	// 主播粉丝数


	// 主播勋章信息
	public static MedalInfoAnchor MEDALINFOANCHOR = null;

	// 房间人气
	public static Long ROOM_POPULARITY =1L;

	// 房间观看人数（历史）
	public static Long ROOM_WATCHER = 0L;

	// 点赞数量
	public static Long ROOM_LIKE = 0L;

	// user房间管理信息
	public static UserManager USERMANAGER = null;

	//心跳包协议类型
	public final static int heartPackageType = 2;

	//心跳包&验证包的尾巴其他
	public final static int packageOther = 1;

	//重新连接线程
	public static ReConnThread reConnThread;

	//处理弹幕包集合
	public final static Vector<String> resultStrs = new Vector<String>(100);

	//礼物感谢集
	public final static Map<String, Vector<Gift>> thankGiftConcurrentHashMap = new ConcurrentHashMap<String,Vector<Gift>>(3000);

	//待发弹幕集
	public final static Vector<String> barrageString = new Vector<String>();

	//log日志待写入集合
	public final static Vector<String> logString = new Vector<String>(100);

	//自动回复处理弹幕
	public final static Vector<AutoReply> replys = new Vector<AutoReply>();

	//感谢礼物数据集线程
	public static ParseThankGiftThread parsethankGiftThread = new ParseThankGiftThread();

	//发送弹幕线程
	public static SendBarrageThread sendBarrageThread;

	//用户在线线程集
	public static HeartBeatThread heartBeatThread;

	public static HeartBeatsThread heartBeatsThread;

	public static UserOnlineHeartThread userOnlineHeartThread;

	//小心心线程
	public static SmallHeartThread smallHeartThread;

	public static String SMALLHEART_ADRESS = null;

	public static boolean is_sign= false;


	public static int manager_login_size=0;


	//可以赠送礼物集合 要初始化
	public static Map<Integer, AutoSendGift> autoSendGiftMap = null;

	//测试模式
	public static boolean TEST_MODE = false;


	public static void init_send(){
		GlobalSettingConf.replys.clear();
		GlobalSettingConf.thankGiftConcurrentHashMap.clear();
		GlobalSettingConf.barrageString.clear();
	}

	public static void init_all(){
		GlobalSettingConf.replys.clear();
		GlobalSettingConf.resultStrs.clear();
		GlobalSettingConf.thankGiftConcurrentHashMap.clear();
		GlobalSettingConf.barrageString.clear();
		GlobalSettingConf.logString.clear();

	}

	public static void init_connect(){
		GlobalSettingConf.replys.clear();
		GlobalSettingConf.resultStrs.clear();
		GlobalSettingConf.barrageString.clear();
		GlobalSettingConf.logString.clear();
		GlobalSettingConf.ROOM_ID = null;
		GlobalSettingConf.ANCHOR_NAME = null;
		GlobalSettingConf.ANCHOR_UID= null;
		GlobalSettingConf.FANS_NUM = null;
		GlobalSettingConf.SHORT_ROOM_ID = null;
		GlobalSettingConf.LIVE_STATUS = 0;
		GlobalSettingConf.ROOM_POPULARITY = 1L;
	}


}
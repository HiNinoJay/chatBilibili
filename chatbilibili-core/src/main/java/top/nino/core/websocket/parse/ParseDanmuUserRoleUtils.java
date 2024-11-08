package top.nino.core.websocket.parse;

import org.apache.commons.lang3.StringUtils;
import top.nino.api.model.danmu.DanmuMessage;

/**
 * @author : nino
 * @date : 2024/2/7 06:20
 */
public class ParseDanmuUserRoleUtils {


    /**
     * 判断是不是普通弹幕
     *
     * @param danmuMessage 弹幕对象
     * @return 类型字符串
     */
    public static boolean judgeNormal(Long anchorUid, DanmuMessage danmuMessage) {
        if(StringUtils.isNotBlank(parseGuard(danmuMessage))) {
            return false;
        }
        if(StringUtils.isNotBlank(parseVip(danmuMessage))) {
            return false;
        }
        if(StringUtils.isNotBlank(parseManager(anchorUid, danmuMessage))) {
            return false;
        }
        return true;
    }


    /**
     * 判断老爷类型 vip1 svip1 0 0
     *
     * @param danmuMessage 弹幕对象
     * @return 类型字符串
     */
    public static String parseVip(DanmuMessage danmuMessage) {
        if (danmuMessage.getVip() == 1 || danmuMessage.getSvip() == 1) {
            return "[爷]";
        } else {
            return "";
        }

    }

    /**
     * 判断是不是老爷
     *
     * @param danmuMessage 弹幕对象
     * @return 类型字符串
     */
    public static boolean judgeVip(DanmuMessage danmuMessage) {
        if(StringUtils.isNotBlank(parseVip(danmuMessage))) {
            return true;
        }
        return false;
    }


    /**
     * 判断舰长类型返回相应的字符串
     *
     * @param danmuMessage 弹幕对象
     * @return 舰长类型字符串
     */
    public static String parseGuard(DanmuMessage danmuMessage) {
        Short type = danmuMessage.getUguard();
        switch (type) {
            case 0:
                return "";
            case 1:
                return "[总督]";
            case 2:
                return "[提督]";
            case 3:
                return "[舰长]";
            default:
                return "";
        }
    }

    /**
     * 判断是不是舰长
     *
     * @param danmuMessage 弹幕对象
     * @return 类型字符串
     */
    public static boolean judgeGuard(DanmuMessage danmuMessage) {
        if(StringUtils.isNotBlank(parseGuard(danmuMessage))) {
            return true;
        }
        return false;
    }

    /**
     * 判断是否为房管 1 yes  当 uid相同时即是主播
     *
     * @return 类型字符串
     */
    public static String parseManager(Long anchorUid, DanmuMessage danmuMessage) {
        Long uid = danmuMessage.getUid();
        Short type = danmuMessage.getManager();
        if (type == 1) {
            return "[房管]";
        }
        if (uid.equals(anchorUid)) {
            return "[主播]";
        }
        return "";
    }

    /**
     * 判断是不是房管或者主播
     *
     * @param danmuMessage 弹幕对象
     * @return 类型字符串
     */
    public static boolean judgeManager(Long anchorUid, DanmuMessage danmuMessage) {
        if(StringUtils.isNotBlank(parseManager(anchorUid, danmuMessage))) {
            return true;
        }
        return false;
    }

    /**
     * 解析用户等级
     * @param danmuMessage
     * @return
     */
    public static String parseUserLevel(DanmuMessage danmuMessage) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[").append("UL").append(danmuMessage.getUlevel()).append("]");
        return stringBuilder.toString();
    }

    /**
     * 解析具体内容：该用户说了什么
     * @param danmuMessage
     * @return
     */
    public static String parseDanmuContent(DanmuMessage danmuMessage) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(danmuMessage.getUname());
        stringBuilder.append(" 它说:");
        stringBuilder.append(danmuMessage.getMsg());
        return stringBuilder.toString();
    }

}

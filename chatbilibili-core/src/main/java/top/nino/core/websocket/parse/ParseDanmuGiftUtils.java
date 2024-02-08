package top.nino.core.websocket.parse;


import org.apache.commons.lang3.StringUtils;
import top.nino.api.model.danmu.Gift;
import top.nino.core.time.JodaTimeUtils;

/**
 * @author : nino
 * @date : 2024/2/7 06:20
 */
public class ParseDanmuGiftUtils {


    public static String parseGiftDanmuContent(Gift normalGift) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(JodaTimeUtils.formatDateTime(normalGift.getTimestamp() * 1000));
        stringBuilder.append(":收到道具:");
        stringBuilder.append(normalGift.getUname());
        stringBuilder.append(" ");
        stringBuilder.append(normalGift.getAction());
        stringBuilder.append("的:");
        stringBuilder.append(normalGift.getGiftName());
        stringBuilder.append(" x ");
        stringBuilder.append(normalGift.getNum());
        return stringBuilder.toString();
    }

    /**
     * 过滤金银瓜子类型
     *
     * @param coin_type
     * @return
     */
    public static short parseCoinType(String coin_type) {
        if (StringUtils.isBlank(coin_type)) {
            return -1;
        }
        switch (coin_type.trim()) {
            case "silver":
                return 0;
            case "gold":
                return 1;
            default:
                return -1;
        }

    }
}

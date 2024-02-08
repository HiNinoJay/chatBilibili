package top.nino.api.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 直播状态 0不直播 1直播 2轮播
 *
 * @author : nino
 * @date : 2024/2/5 02:53
 */
@AllArgsConstructor
@Getter
public enum LiveStatusEnum {

    CLOSED(0, "已下播"),
    DOING(1, "正在直播"),
    PAST(2, "轮播");

    private final Integer code;
    private final String msg;

    public static LiveStatusEnum getByCode(Integer code) {
        LiveStatusEnum[] values = LiveStatusEnum.values();
        for(LiveStatusEnum value : values) {
            if(value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }

}

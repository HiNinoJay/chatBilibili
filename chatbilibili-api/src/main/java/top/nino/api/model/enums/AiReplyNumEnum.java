package top.nino.api.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author : nino
 * @date : 2024/2/9 04:32
 */
@Getter
@AllArgsConstructor
public enum AiReplyNumEnum {

    ZERO(0, "zero"),
    ONE(1, "one"),
    FIVE(5, "five"),
    TEN(10, "ten");

    private final Integer code;
    private final String msg;

    public static LiveStatusEnum getByMsg(String msg) {
        LiveStatusEnum[] values = LiveStatusEnum.values();
        for(LiveStatusEnum value : values) {
            if(value.getMsg().equals(msg)) {
                return value;
            }
        }
        return null;
    }
}

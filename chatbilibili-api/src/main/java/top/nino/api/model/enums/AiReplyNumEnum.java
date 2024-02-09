package top.nino.api.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author : nino
 * @date : 2024/2/9 04:32
 */
@Slf4j
@Getter
@AllArgsConstructor
public enum AiReplyNumEnum {

    ZERO(0, "zero"),
    ONE(1, "one"),
    FIVE(5, "five"),
    TEN(10, "ten");

    private final Integer code;
    private final String msg;

    public static AiReplyNumEnum getByMsg(String msg) {
        AiReplyNumEnum[] values = AiReplyNumEnum.values();
        for(AiReplyNumEnum value : values) {
            if(value.getMsg().equals(msg)) {
                return value;
            }
        }
        log.info("没有对应的回复频率：{}", msg);
        return AiReplyNumEnum.ZERO;
    }

    public static AiReplyNumEnum getByCode(Integer aiReplyNum) {
        AiReplyNumEnum[] values = AiReplyNumEnum.values();
        for(AiReplyNumEnum value : values) {
            if(value.getCode().equals(aiReplyNum)) {
                return value;
            }
        }
        log.info("没有对应的回复频率：{}", aiReplyNum);
        return AiReplyNumEnum.ZERO;
    }
}

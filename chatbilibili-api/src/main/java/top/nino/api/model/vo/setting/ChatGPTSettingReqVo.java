package top.nino.api.model.vo.setting;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * @author : nino
 * @date : 2024/2/9 03:10
 */
@Data
public class ChatGPTSettingReqVo {


    private String aiCharacterName;

    private Boolean aiReplyStatus;

    private String aiReplyNum;


}

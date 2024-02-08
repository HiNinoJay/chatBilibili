package top.nino.api.model.vo.ai;

import lombok.Data;

import java.util.List;

/**
 * @author : nino
 * @date : 2024/2/3 01:41
 */
@Data
public class ChatResVo {
    private String role;
    private List<String> answers;
}

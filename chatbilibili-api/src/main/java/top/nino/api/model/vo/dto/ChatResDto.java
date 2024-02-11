package top.nino.api.model.vo.dto;

import lombok.Data;

import java.util.List;

/**
 * @author : nino
 * @date : 2024/2/3 01:41
 */
@Data
public class ChatResDto {

    private String userName;
    private String questionTime;
    private String prompt;
    private String characterName;
    private String characterDescription;
    private List<String> answers;
    private String answerTime;
}

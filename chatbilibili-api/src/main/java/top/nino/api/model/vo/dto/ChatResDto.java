package top.nino.api.model.vo.dto;

import lombok.Data;

import java.util.List;

/**
 * @author : nino
 * @date : 2024/2/3 01:41
 */
@Data
public class ChatResDto {

    private String characterDescription;
    private String prompt;
    private List<String> answers;
}

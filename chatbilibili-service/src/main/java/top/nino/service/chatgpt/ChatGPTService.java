package top.nino.service.chatgpt;


import top.nino.api.model.vo.dto.ChatResDto;

/**
 * @author : nino
 * @date : 2024/2/3 01:27
 */
public interface ChatGPTService {

    Boolean checkChatGPTStatus();
    ChatResDto chatCompletions(String usingCharacterName, String characterDescription, String prompt) throws Exception;
    ChatResDto chatCompletions(String characterDescription, String prompt) throws Exception;
    ChatResDto testHelloChatGPTByDescription(String prompt);
}

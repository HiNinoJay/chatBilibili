package top.nino.service.chatgpt.impl;

import cn.codingguide.chatgpt4j.DefaultChatGptClient;
import cn.codingguide.chatgpt4j.constant.Role;
import cn.codingguide.chatgpt4j.domain.chat.ChatCompletionRequest;
import cn.codingguide.chatgpt4j.domain.chat.ChatCompletionResponse;
import cn.codingguide.chatgpt4j.domain.chat.Message;
import cn.codingguide.chatgpt4j.key.RandomKeySelectorStrategy;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import okhttp3.logging.HttpLoggingInterceptor;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import top.nino.api.model.vo.dto.ChatResDto;
import top.nino.service.chatgpt.ChatGPTService;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author : nino
 * @date : 2024/2/3 01:27
 */
@Slf4j
@Service
public class ChatGPTServiceImpl implements ChatGPTService {


    private DefaultChatGptClient client;

    @Value("${chatgpt.conf.CHAT_GPT_3_5.key}")
    private String apiKey;

    @Value("${chatgpt.conf.CHAT_GPT_3_5.proxyIP}")
    private String proxyIP;

    @Value("${chatgpt.conf.CHAT_GPT_3_5.proxyPort}")
    private Integer proxyPort;

    @Value("${chatgpt.conf.CHAT_GPT_3_5.apiHost}")
    private String apiHost;

    @Value("${chatgpt.defaultCharacter}")
    private String defaultCharacter;

    @PostConstruct
    private void init() {
        if(StringUtils.isNotBlank(apiKey)) {
            DefaultChatGptClient.Builder builder = DefaultChatGptClient.newBuilder()
                    // 这里替换成自己的key，该参数是必填项
                    .apiKeys(Lists.newArrayList(apiKey))
                    // 设置apiHost，如果没有自己的api地址，可以不用设置，默认是：https://api.openai.com/
                    .apiHost(apiHost)

                    // 支持自定义OkHttpClient，该参数非必填，没有填写将使用默认的OkHttpClient
                    .okHttpClient(null)
                    // 设置apiKey选择策略，该参数是非必填项，如果没有填写，将使用默认的随机选择器（RandomKeySelectorStrategy），用户可以通过实现KeySelectorStrategy接口提供自定义选择器
                    .keySelectorStrategy(new RandomKeySelectorStrategy())
                    // 设置开启日志，非必填项，默认没有打印请求日志，测试期间可以设置BODY日志，日志量较大，生产环境不建议开启
                    .logLevel(HttpLoggingInterceptor.Level.NONE);
            if(StringUtils.isNotBlank(proxyIP) && ObjectUtils.isNotEmpty(proxyPort)) {
                // 设置proxy代理，方便大陆通过代理访问OpenAI，支持Http代理或者Socks代理，两者只需要设置其一即可，两者都设置，后者将覆盖前者
                builder.proxyHttp(proxyIP, proxyPort).proxySocks(proxyIP, proxyPort);
            }
            client = builder.build();
            try {
                chatCompletions("猫", defaultCharacter, "你好");
            } catch (Exception e) {
                log.error("chatGPT服务器连接异常，有可能是apikey已经失效", e);
                return;
            }
            log.info("已经连接到chatGPT。");
        }
    }


    @Override
    public Boolean checkChatGPTStatus() {
        try {
            chatCompletions("猫", defaultCharacter, "你好");
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public ChatResDto chatCompletions(String usingCharacterName, String characterDescription, String prompt) throws Exception{

        ChatResDto chatResDto = chatCompletions(characterDescription, prompt);
        if(ObjectUtils.isNotEmpty(chatResDto)) {
            chatResDto.setUserName(usingCharacterName);
        }
        return chatResDto;
    }


    @Override
    public ChatResDto chatCompletions(String characterDescription, String prompt) throws Exception{

        ChatResDto chatResDto = new ChatResDto();
        chatResDto.setCharacterDescription(characterDescription);

        ChatCompletionRequest question = ChatCompletionRequest.newBuilder()
                .addMessage(Message.newBuilder().role(Role.SYSTEM).content(characterDescription).build())
                .addMessage(Message.newBuilder().role(Role.USER).content(prompt).build())
                .build();

        ChatCompletionResponse chatCompletion = client.chatCompletions(question);

//        question = question.toBuilder()
//                .addMessage(Message.newBuilder().role(Role.ASSISTANT)
//                .content(chatCompletion.getChoices()[0].getMessage().getContent()).build())
//                .addMessage(Message.newBuilder().role(Role.USER).content("他到目前为止，出了哪些Java相关的书籍？").build())
//                .build();
//
//        chatCompletion = client.chatCompletions(question);

        // 格式化输出整个过程
        List<Message> messages = question.getMessages();

        List<String> chatMessage = new ArrayList<>();

        for(int i = 0; i < messages.size(); i++) {
            if(i == 0) {
                chatResDto.setCharacterDescription(messages.get(i).getContent());
            } else {
                chatMessage.add(messages.get(i).getContent());
            }
        }
        chatMessage.add(chatCompletion.getChoices()[0].getMessage().getContent());

        if(chatMessage.size() % 2 != 0) {
            chatResDto.setAnswers(Lists.newArrayList("对话不完整。"));
        } else {
            List<String> returnChatMessage = new ArrayList<>();
            for(int i = 0; i < chatMessage.size(); i+=2) {
                returnChatMessage.add(chatMessage.get(i+1));
            }
            chatResDto.setAnswers(returnChatMessage);
        }
        chatResDto.setPrompt(prompt);
        // 格式化输出
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = LocalDateTime.now().format(formatter);
        chatResDto.setAnswerTime(formattedDateTime);
        return chatResDto;
    }


    @Override
    public ChatResDto testHelloChatGPTByDescription(String prompt) {
        ChatResDto chatResDto = null;
        try {
            chatResDto = chatCompletions(prompt, "你好");
        } catch (Exception e) {
            log.error("chatGPT连接异常", e);

        }
        return chatResDto;
    }


}

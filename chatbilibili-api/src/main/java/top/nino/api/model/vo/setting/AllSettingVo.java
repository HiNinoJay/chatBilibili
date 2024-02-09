package top.nino.api.model.vo.setting;

import lombok.Data;
import top.nino.api.model.vo.ai.AiCharacterReqVo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : nino
 * @date : 2024/2/9 14:25
 */
@Data
public class AllSettingVo {


    // 上一次的房间号
    private Long lastRoomId;

    // ---------1.弹幕设置---start-----------


    // 是否开启弹幕功能
    private boolean danmuStatus = true;

    // 是否开启普通弹幕
    private boolean normalDanmuStatus = true;

    // 是否开启舰长弹幕
    private boolean guardDanmuStatus = true;

    // 是否开启老爷弹幕
    private boolean vipDanmuStatus = true;

    // 是否开启老爷弹幕
    private boolean managerDanmuStatus = true;



    // ---------1.弹幕设置---end------------



    // ---------2.礼物设置---start-----------

    // 信息是否显示礼物消息
    private boolean giftStatus = true;

    // 信息是否显示免费礼物消息
    private boolean freeGiftStatus = true;

    // 信息是否显示免费礼物消息
    private boolean guardBuyGiftStatus = true;

    // 信息是否显示免费礼物消息
    private boolean scGiftStatus = true;


    // ---------2.礼物设置---end------------

    // ---------3.chatGPT设置---start-----------

    public String usingAiCharacterName;

    private List<AiCharacterReqVo> aiCharacterReqVoList = new ArrayList<>();

    private Boolean aiReplyStatus = false;

    private String aiReplyNum = "zero";

    // ---------3.chatGPT设置---end------------
}

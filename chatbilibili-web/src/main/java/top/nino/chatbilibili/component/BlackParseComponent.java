package top.nino.chatbilibili.component;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import top.nino.api.model.auto_reply.AutoReply;
import top.nino.api.model.danmu.Gift;
import top.nino.api.model.danmu.Interact;
import top.nino.chatbilibili.GlobalSettingConf;


@Component
public class BlackParseComponent {

    private static Logger LOGGER = LogManager.getLogger(BlackParseComponent.class);
    public boolean autoReplay_parse(AutoReply autoReply) {
        //全局开启
        if(GlobalSettingConf.centerSetConf.getBlack()!=null) {
            if (GlobalSettingConf.centerSetConf.getBlack().isAuto_reply() || GlobalSettingConf.centerSetConf.getBlack().isAll()) {
                return this.parse(autoReply);
            }
        }else{
            LOGGER.error("黑名单配置为空");
        }
        return true;
    }


    public boolean interact_parse(Interact interact) {
        //全局开启
        //1欢迎 2关注
//        if (interact.getMsg_type() == 1) {
//
//        } else if (interact.getMsg_type() == 2) {
//
//        }
        if(GlobalSettingConf.centerSetConf.getBlack()!=null) {
            if (GlobalSettingConf.centerSetConf.getBlack().isThank_follow() || GlobalSettingConf.centerSetConf.getBlack().isThank_welcome() || GlobalSettingConf.centerSetConf.getBlack().isAll()) {
                return this.parse(interact);
            }
        }else{
            LOGGER.error("黑名单配置为空");
        }
        return true;
    }

    public boolean gift_parse(Gift gift) {
        //全局开启
        if(GlobalSettingConf.centerSetConf.getBlack()!=null) {
            if (GlobalSettingConf.centerSetConf.getBlack().isThank_gift() || GlobalSettingConf.centerSetConf.getBlack().isAll()) {
                return this.parse(gift);
            }
        }else{
            LOGGER.error("黑名单配置为空");
        }
        return true;
    }


    public <T> boolean global_parse(T t) {
        //全局开启
        if(GlobalSettingConf.centerSetConf.getBlack()!=null) {
            if (GlobalSettingConf.centerSetConf.getBlack().isAll()) {
                return this.parse(t);
            }
        }
        return true;
    }


    public <T> boolean parse(T t) {
        if(GlobalSettingConf.centerSetConf.getBlack()==null)return true;
        boolean nameFlag = true;
        boolean uidFlag = true;
        //名称规则
        String name = "";
        for (String s : GlobalSettingConf.centerSetConf.getBlack().getNames()) {
            if (t instanceof AutoReply) {   //自动回复
                AutoReply autoReply = (AutoReply) t;
                name = autoReply.getName();
            } else if (t instanceof Interact) {  //关注感谢 或者 欢迎进入 取决于msg_type
                Interact interact = (Interact) t;
                name = interact.getUname();
            } else if (t instanceof Gift) {  //感谢礼物
                Gift gift = (Gift) t;
                name = gift.getUname();
            }
            //判断
            String replaced_s = s.replace("%", "");
            //非模糊模式
            if(!GlobalSettingConf.centerSetConf.getBlack().isFuzzy_query()) {
                if (s.startsWith("%") && s.endsWith("%")) {
                    if (StringUtils.contains(name, replaced_s)) {
                        nameFlag = false;
                        break;
                    }
                } else if (s.startsWith("%")) {
                    if (StringUtils.endsWith(name, replaced_s)) {
                        nameFlag = false;
                        break;
                    }
                } else if (s.endsWith("%")) {
                    if (StringUtils.startsWith(name, replaced_s)) {
                        nameFlag = false;
                        break;
                    }
                } else {
                    if (replaced_s.equals(name)) {
                        nameFlag = false;
                        break;
                    }
                }
            }else{
                if(StringUtils.contains(name, replaced_s)){
                    nameFlag = false;
                    break;
                }
            }
        }
        String uid = "";
        //uid规则
        for (String s : GlobalSettingConf.centerSetConf.getBlack().getUids()) {
            if (t instanceof AutoReply) {   //自动回复
                AutoReply autoReply = (AutoReply) t;
                uid = autoReply.getUid() + "";
            } else if (t instanceof Interact) {  //关注感谢 或者 欢迎进入 取决于msg_type
                Interact interact = (Interact) t;
                uid = interact.getUid() + "";
            } else if (t instanceof Gift) {  //感谢礼物
                Gift gift = (Gift) t;
                uid = gift.getUid() + "";
            }
            //直接eq
            if (s.equals(uid)) {
                uidFlag = false;
                break;
            }
        }
        boolean totalFlag = nameFlag && uidFlag;
        if(!totalFlag){
            LOGGER.info("黑名单过滤姬拦截到了一条数据：{} - {}",t.getClass().getName(),t);
        }
        return totalFlag;
    }
}

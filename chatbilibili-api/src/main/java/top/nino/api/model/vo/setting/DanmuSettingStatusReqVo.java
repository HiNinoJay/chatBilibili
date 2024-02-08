package top.nino.api.model.vo.setting;

import lombok.Data;

/**
 * @author : nino
 * @date : 2024/2/9 02:49
 */
@Data
public class DanmuSettingStatusReqVo {
    private boolean danmuStatus;
    private boolean normalStatus;
    private boolean guardStatus;
    private boolean vipStatus;
    private boolean managerStatus;

    private boolean giftStatus;
    private boolean normalGiftStatus;
    private boolean scGiftStatus;

    private boolean danmuLogStatus;
}

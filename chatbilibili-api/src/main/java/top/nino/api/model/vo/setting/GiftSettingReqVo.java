package top.nino.api.model.vo.setting;

import lombok.Data;

/**
 * @author : nino
 * @date : 2024/2/9 03:10
 */
@Data
public class GiftSettingReqVo {
    private boolean giftStatus;
    private boolean normalGiftStatus;
    private boolean scGiftStatus;
}

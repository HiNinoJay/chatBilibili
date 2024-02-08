package top.nino.api.model.vo.room;

import lombok.Data;

/**
 * @author : nino
 * @date : 2024/2/8 20:22
 */
@Data
public class RoomInfoVo {

    private String liveStatus;
    private Long roomId;
    private Long shortRoomId;
    private Long anchorUid;
    private String anchorName;

}

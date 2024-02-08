package top.nino.api.model.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author nino
 */
@AllArgsConstructor
@Getter
public enum ResponseCode {
	SUCCESS("200", "成功", "成功"),
	SYSTEM_ERROR("400", "System is busy!", "系统繁忙"),

	AI_ERROR("50000", "AI连接状态异常", "AI连接状态异常"),

	NONE_QRCODE_KEY_INFO("80000", "没有QrKey", "没有QrKey"),
	QRCODE_UN_VALID_INFO("86038", "二维码失效", "二维码失效"),
	QRCODE_NO_SCAN_INFO("86101", "未扫码", "未扫码"),
	QRCODE_NO_SECOND_CHECK_INFO("86090", "已扫码,未在手机上确认登录", "已扫码,未在手机上确认登录"),
	QRCODE_UN_KNOW_INFO("89999", "未知二维码请求错误", "未知二维码请求错误");

	private final String code;
	private final String msg;
	private final String cnMsg;
}


package org.seally.base.common;
/**
 * @Date 2018年3月25日
 * @author dnc
 * @Description 系统统一消息码值枚举
 */
public enum ResponseEnum {
	SUCCESS("200","OK"),
	ERROR("500","系统内部异常"),
	FAILED("501","操作失败"),
	NOT_FOUND("404","未能识别"),
	PARAM_ERR("405","参数错误"),
	PARAM_EMPT("406","参数为空"),
	AUTH_ERR("601","无权操作"),
	USER_UNKNOW("602","用户不存在"),
	PASS_UNMATCH("603","密码错误"),
	;
	private String code;
	private String message;
	
	ResponseEnum(String code,String message){
		this.code = code;
		this.message=message;
	}
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
}

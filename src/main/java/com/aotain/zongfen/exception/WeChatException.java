package com.aotain.zongfen.exception;

/**
 * 微信消息异常信息类
 * 
 * @author Jason.CW.Cheung
 * @date 2017年7月21日 上午9:44:46
 * @version 1.0
 */
public class WeChatException extends Exception {

	private static final long serialVersionUID = 8369544085406141821L;

	public WeChatException() {
		super();
	}

	public WeChatException(String msg) {
		super(msg);
	}
}
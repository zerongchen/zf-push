package com.aotain.zongfen.bean;

import java.math.BigInteger;

/**
 * @Author ligh 
 */
public class ValidateMessageResult {
	
	public static final int SUCCESS = 0; // 0——处理完成
	public static final int ENCRYPT_ERROR = 1; // 1——文件解密失败
	public static final int VALIDATE_ERROR = 2; // 2——文件校验失败
	public static final int UNZIP_ERROR = 3; // 3——文件解压缩失败
	public static final int FILE_FORMAT_ERROR = 4; // 4——文件格式异常
	public static final int FILE_CONTENT_ERROR = 5; // 5——文件内容异常
	public static final int OTHER_ERROR = 900; // 900——其他异常（存在其他错误，需重新发送）
	
	private boolean valid = true;
	
	private String msg; 
	
	private int resultCode = SUCCESS; 
	
	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	} 
	
	public int getResultCode() {
		return resultCode;
	}

	public void setResultCode(int resultCode) {
		this.resultCode = resultCode;
	}

	public Return getReturn() {
		Return r = new Return(); 
		r.setResultCode(BigInteger.valueOf(this.getResultCode()));
		r.setMsg(msg); 
		return r; 
	}
	
}

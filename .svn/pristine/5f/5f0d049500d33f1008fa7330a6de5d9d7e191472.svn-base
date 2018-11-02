package com.aotain.zongfen.bean;

import com.aotain.zongfen.util.StringUtil;

/**
 * 
 * ClassName: SendData
 * Description: TODO
 * date: 2018年4月25日 下午4:37:39
 * 
 * @author tanzj 
 * @version  
 * @since JDK 1.8
 */
public class SendData {
	private String province;
	private String alarmTime;
	private String alarmMessage;
	private String alarmParameter;


	public String getProvince() {
		return province;
	}


	public void setProvince(String province) {
		this.province = province;
	}


	public String getAlarmTime() {
		return alarmTime;
	}


	public void setAlarmTime(String alarmTime) {
		this.alarmTime = alarmTime;
	}


	public String getAlarmMessage() {
		return alarmMessage;
	}


	public void setAlarmMessage(String alarmMessage) {
		this.alarmMessage = alarmMessage;
	}


	public String getAlarmParameter() {
		return alarmParameter;
	}


	public void setAlarmParameter(String alarmParameter) {
		this.alarmParameter = alarmParameter;
	}


	@Override
	public String toString() {
		return  "告警省份：" + (StringUtil.isEmptyString(province) ? "" : province) + "\n" + 
				"告警时间：" + (StringUtil.isEmptyString(alarmTime) ? "" : alarmTime) + "\n" + 
			    "告警信息：" + (StringUtil.isEmptyString(alarmMessage) ? "" : alarmMessage) + "\n" + 
			    "告警项参数：" + (StringUtil.isEmptyString(alarmParameter) ? "" : alarmParameter);
	}

}

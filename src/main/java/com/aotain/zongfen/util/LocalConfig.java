package com.aotain.zongfen.util;

import java.util.ResourceBundle;

public class LocalConfig {
	
	private static LocalConfig instance;
	
	//企业微信的企业唯一ID
	private String corpId;
	//企业微信的应用的Secret值
	private String appSecert;
	//企业微信的应用的agentId值
	private int agentId;
	//企业微信的请求URI地址信息
	private String weChatUri;
	//企业微信的获取token值的方法名
	private String getTokenMethod;
	//企业微信的发送消息的方法名
	private String sendMessageMethod;
	//企业微信的获取获取部门列表的方法名
	private String listDepartment;
	//企业微信的获取用户基础信息的方法名
	private String listDepartmentSimpleUser;
	
	//email邮件服务器信息
	private String emailMailServer;
	//email邮件发送方账号
	private String emailSenderUsername;
	//email邮件发送方账号密码
	private String emailSenderPassword;
	//email邮件发送超时时间
	private String emailSenderTimeout;
	
	//短信网关IP
	private String phoneGatewayIp;
	//短信网关端口
	private int phoneGatewayPort;
	//短信发送者账号
	private String phoneSender;
	//SP账号信息
	private String phoneSPName;
	//SP密码信息
	private String phoneSPPassword;
	
	public synchronized static LocalConfig getInstance() {
		if (instance == null) {
			instance = new LocalConfig(); 
		}
		return instance;
	}
	
	private LocalConfig(){
		ResourceBundle config = ResourceBundle.getBundle("application");
		
		try {
			corpId = config.getString("weChat.corpID");
			appSecert = config.getString("weChat.appSecret");
			agentId = Integer.parseInt(config.getString("weChat.agentId"));
			weChatUri = config.getString("weChat.uri");
			getTokenMethod = config.getString("weChat.gettoken.method");
			sendMessageMethod = config.getString("weChat.sendMessage.method");
			listDepartment = config.getString("weChat.listDepartment.method");
			listDepartmentSimpleUser = config.getString("weChat.listDepartmentSimpleUser.method");
			
			emailMailServer = config.getString("email.mailServer");
			emailSenderUsername = config.getString("email.sender.username");
			emailSenderPassword = config.getString("email.sender.password");
			emailSenderTimeout = config.getString("email.sender.timeout");
			
			phoneGatewayIp = config.getString("phone.gatewayIp");
			phoneGatewayPort = Integer.parseInt(config.getString("phone.gatewayPort"));
			phoneSender = config.getString("phone.phoneSender");
			phoneSPName = config.getString("phone.spName");
			phoneSPPassword = config.getString("phone.spPassword");
			
		} catch (NumberFormatException e) {
			PushLog.config.info("The agentId or the phone gateway port is not the number!", e);
		}
	}

	public String getCorpId() {
		return corpId;
	}

	public void setCorpId(String corpId) {
		this.corpId = corpId;
	}

	public String getAppSecert() {
		return appSecert;
	}

	public void setAppSecert(String appSecert) {
		this.appSecert = appSecert;
	}

	public int getAgentId() {
		return agentId;
	}

	public void setAgentId(int agentId) {
		this.agentId = agentId;
	}

	public String getWeChatUri() {
		return weChatUri;
	}

	public void setWeChatUri(String weChatUri) {
		this.weChatUri = weChatUri;
	}

	public String getGetTokenMethod() {
		return getTokenMethod;
	}

	public void setGetTokenMethod(String getTokenMethod) {
		this.getTokenMethod = getTokenMethod;
	}

	public String getSendMessageMethod() {
		return sendMessageMethod;
	}

	public void setSendMessageMethod(String sendMessageMethod) {
		this.sendMessageMethod = sendMessageMethod;
	}

	public String getListDepartment() {
		return listDepartment;
	}

	public void setListDepartment(String listDepartment) {
		this.listDepartment = listDepartment;
	}

	public String getListDepartmentSimpleUser() {
		return listDepartmentSimpleUser;
	}

	public void setListDepartmentSimpleUser(String listDepartmentSimpleUser) {
		this.listDepartmentSimpleUser = listDepartmentSimpleUser;
	}

	public String getEmailMailServer() {
		return emailMailServer;
	}

	public void setEmailMailServer(String emailMailServer) {
		this.emailMailServer = emailMailServer;
	}

	public String getEmailSenderUsername() {
		return emailSenderUsername;
	}

	public void setEmailSenderUsername(String emailSenderUsername) {
		this.emailSenderUsername = emailSenderUsername;
	}

	public String getEmailSenderPassword() {
		return emailSenderPassword;
	}

	public void setEmailSenderPassword(String emailSenderPassword) {
		this.emailSenderPassword = emailSenderPassword;
	}

	public String getEmailSenderTimeout() {
		return emailSenderTimeout;
	}

	public void setEmailSenderTimeout(String emailSenderTimeout) {
		this.emailSenderTimeout = emailSenderTimeout;
	}

	public String getPhoneGatewayIp() {
		return phoneGatewayIp;
	}

	public void setPhoneGatewayIp(String phoneGatewayIp) {
		this.phoneGatewayIp = phoneGatewayIp;
	}

	public int getPhoneGatewayPort() {
		return phoneGatewayPort;
	}

	public void setPhoneGatewayPort(int phoneGatewayPort) {
		this.phoneGatewayPort = phoneGatewayPort;
	}

	public String getPhoneSender() {
		return phoneSender;
	}

	public void setPhoneSender(String phoneSender) {
		this.phoneSender = phoneSender;
	}

	public String getPhoneSPName() {
		return phoneSPName;
	}

	public void setPhoneSPName(String phoneSPName) {
		this.phoneSPName = phoneSPName;
	}

	public String getPhoneSPPassword() {
		return phoneSPPassword;
	}

	public void setPhoneSPPassword(String phoneSPPassword) {
		this.phoneSPPassword = phoneSPPassword;
	}

}

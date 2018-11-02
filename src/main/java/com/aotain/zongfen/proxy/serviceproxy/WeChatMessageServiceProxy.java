package com.aotain.zongfen.proxy.serviceproxy;

import com.alibaba.fastjson.JSONObject;
import com.aotain.zongfen.bean.SendMessageResult;
import com.aotain.zongfen.bean.ValidateMessageResult;
import com.aotain.zongfen.bean.WeChatPush;
import com.aotain.zongfen.entity.IsmsMessage;
import com.aotain.zongfen.service.WeChatService;
import com.aotain.zongfen.util.PushLog;
import com.aotain.zongfen.util.StringUtil;

/**
 * @Author ligh 
 */
public class WeChatMessageServiceProxy extends BaseMessageServiceProxy implements IMessageServiceProxy {
	
	public final static int PUSHTYPE = 2; 
	
	private WeChatService weChatService; // 微信接口 
	
	private WeChatPush weChat = null; 
	
	public WeChatMessageServiceProxy(IsmsMessage ismsMessage) {
		super(ismsMessage); 
		this.weChatService = new WeChatService(); 
	}
	
	@Override
	protected void innerValidateMessage() {
		if (!validateResult.isValid()) { 
			return ; 
		} 
		try {
			// 校验微信消息 
			JSONObject obj = JSONObject.parseObject(ismsMessage.getData()); 
			weChat = obj.toJavaObject(WeChatPush.class); 
			// 长度不做任何校验，也没有校验的依据，只能是接口报错截获错误
			if (validateResult.isValid() && weChat.getReceiver().getDepartmentId().isEmpty() && weChat.getReceiver().getUserId().isEmpty()) { 
				PushLog.weChatLog.info("校验不通过，微信部门ID和微信号不能全部为空，" + weChat.toString());
				validateResult.setValid(false); 
				validateResult.setResultCode(ValidateMessageResult.FILE_CONTENT_ERROR);
				validateResult.setMsg("微信部门ID和微信号不能全部为空"); 
			} 
			// 长度不做任何校验，也没有校验的依据，只能是接口报错截获错误
			if (validateResult.isValid() && (weChat.getSendData() == null || StringUtil.isBlank(weChat.getSendData().getMsgType()))) { 
				PushLog.weChatLog.info("校验不通过，微信发送消息类型不能为空，" + weChat.toString());
				validateResult.setValid(false); 
				validateResult.setResultCode(ValidateMessageResult.FILE_CONTENT_ERROR);
				validateResult.setMsg("微信发送消息类型不能为空"); 
			} 
			if (validateResult.isValid() && (weChat.getSendData() != null && !StringUtil.isBlank(weChat.getSendData().getMsgType()) && !weChat.getSendData().getMsgType().equals("text"))) { 
				PushLog.weChatLog.info("校验不通过，微信发送消息只支持文本消息，" + weChat.toString());
				validateResult.setValid(false); 
				validateResult.setResultCode(ValidateMessageResult.FILE_CONTENT_ERROR);
				validateResult.setMsg("微信发送消息只支持文本消息"); 
			} 
			if (validateResult.isValid() && (weChat.getSendData() == null || StringUtil.isBlank(weChat.getSendData().getContent()))) { 
				PushLog.weChatLog.info("校验不通过，微信发送消息内容不能为空，" + weChat.toString());
				validateResult.setValid(false); 
				validateResult.setResultCode(ValidateMessageResult.FILE_CONTENT_ERROR);
				validateResult.setMsg("微信发送消息内容不能为空"); 
			} 
			if (validateResult.isValid() && StringUtil.isBlank(weChat.getTimeStamp())) { 
				PushLog.weChatLog.info("校验不通过，微信生成时间不能为空，" + weChat.toString());
				validateResult.setValid(false); 
				validateResult.setResultCode(ValidateMessageResult.FILE_CONTENT_ERROR);
				validateResult.setMsg("微信生成时间不能为空");
			} 
		} catch (Exception e) { // 转换失败 
			PushLog.weChatLog.error("校验不通过，无法识别email类型的push信息，" + weChat.toString());
			validateResult.setValid(false); 
			validateResult.setResultCode(ValidateMessageResult.FILE_CONTENT_ERROR);
			validateResult.setMsg("无法识别微信类型的push信息");
		} 
		if (validateResult.isValid()) { 
			ismsMessage.setPushObj(weChat);
		}
	}
	
	@Override
	protected void innerSendMessage() {
		try {
			weChatService.sendMessage(constructWeChatPush()); 
			sendResult.setSuccess(true); 
		} catch (Exception e) {
			PushLog.weChatLog.error("推送失败，" + e.getMessage());
			sendResult.setSuccess(false); 
			sendResult.setMsg(e.getMessage());
			sendResult.setResultCode(SendMessageResult.FAIL);
		} 
	} 

	/**
	 * 组装默认的参数
	 * @return
	 */
	private WeChatPush constructWeChatPush() { 
		/* 
		WeChatPush weChatPush = new WeChatPush();
		weChatPush.setCorpId("wwd19143ad6ac57e80");
		weChatPush.setAppSecert("cqwr7gfsXKW_EFyCvk5-1UHQ59kB0hw-N0J56KTwEGg");
		weChatPush.setAgentId(new BigInteger("1000002"));
		
		List<WeChatPush.Receiver> receivers = new ArrayList<WeChatPush.Receiver>();
		List<BigInteger> departmentIds = new ArrayList<BigInteger>();
		departmentIds.add(new BigInteger("4"));
        List<String> userIds = new ArrayList<String>();
        userIds.add("Jason_zcw");
		Receiver r = new Receiver();
		r.getDepartmentId().addAll(departmentIds);
		r.getUserId().addAll(userIds);
		receivers.add(r);
		weChatPush.getReceiver().addAll(receivers);
		
		SendData sendData = new SendData();
		sendData.setMsgType("text");
		sendData.setContent("告警机房：深圳傲天科技IDC机房\n告警内容：2017-07-26 13:30:10下发了一个域名监测指令，指令ID：1878989，指令类容：www.aotain.com");
		weChatPush.setSendData(sendData); 
		return weChatPush; 
		*/
		return weChat; 
	} 

}

package com.aotain.zongfen.proxy.serviceproxy;

import com.alibaba.fastjson.JSONObject;
import com.aotain.zongfen.bean.EmailPush;
import com.aotain.zongfen.bean.SendMessageResult;
import com.aotain.zongfen.bean.ValidateMessageResult;
import com.aotain.zongfen.entity.IsmsMessage;
import com.aotain.zongfen.service.SendEmailService;
import com.aotain.zongfen.util.PushLog;
import com.aotain.zongfen.util.StringUtil;

/**
 * @Author ligh 
 */
public class EmailMessageServiceProxy extends BaseMessageServiceProxy implements IMessageServiceProxy {
	
	public final static int PUSHTYPE = 1; 
	
	private SendEmailService sendEmailService; // email接口 
	
	private EmailPush email = null; 
	
	public EmailMessageServiceProxy(IsmsMessage ismsMessage) { 
		super(ismsMessage); 
		this.sendEmailService = new SendEmailService();
		System.out.println(0);
	}
	
	@Override
	protected void innerValidateMessage() {
		if (!validateResult.isValid()) { 
			return ; 
		}
		// 校验email消息 
		try {
			JSONObject obj = JSONObject.parseObject(ismsMessage.getData()); 
			email = obj.toJavaObject(EmailPush.class); 
			// 长度不做任何校验，也没有校验的依据，只能是接口报错截获错误
			if (validateResult.isValid() && StringUtil.isBlank(email.getSubject())) { 
				PushLog.emailLog.info("校验不通过，邮件主题不能为空，" + email.toString());
				validateResult.setValid(false); 
				validateResult.setResultCode(ValidateMessageResult.FILE_CONTENT_ERROR);
				validateResult.setMsg("邮件主题不能为空");
			} 
			// 个数不做任何限制，也没有限制的依据，只能是接口报错截获错误
			if (validateResult.isValid() && (email.getReceiver() == null || email.getReceiver().getEmailReceiver().isEmpty())) { 
				PushLog.emailLog.info("校验不通过，邮件接受者不能为空，" + email.toString());
				validateResult.setValid(false); 
				validateResult.setResultCode(ValidateMessageResult.FILE_CONTENT_ERROR);
				validateResult.setMsg("邮件接受者不能为空");
			} 
			// 长度不做任何校验，也没有校验的依据，只能是接口报错截获错误
			if (validateResult.isValid() && StringUtil.isBlank(email.getSendData())) { 
				PushLog.emailLog.info("校验不通过，邮件发送信息不能为空，" + email.toString());
				validateResult.setValid(false); 
				validateResult.setResultCode(ValidateMessageResult.FILE_CONTENT_ERROR);
				validateResult.setMsg("邮件发送信息不能为空");
			} 
			if (validateResult.isValid() && StringUtil.isBlank(email.getTimeStamp())) { 
				PushLog.emailLog.info("校验不通过，邮件生成时间不能为空，" + email.toString());
				validateResult.setValid(false); 
				validateResult.setResultCode(ValidateMessageResult.FILE_CONTENT_ERROR);
				validateResult.setMsg("邮件生成时间不能为空");
			}
		} catch (Exception e) { // 转换失败
			PushLog.emailLog.error("校验不通过，无法识别email类型的push信息，" + email.toString());
			validateResult.setValid(false); 
			validateResult.setResultCode(ValidateMessageResult.FILE_CONTENT_ERROR);
			validateResult.setMsg("无法识别email类型的push信息");
		} 
		if (validateResult.isValid()) { 
			ismsMessage.setPushObj(email);
		}
	} 

	@Override
	protected void innerSendMessage() {
		try {
			sendEmailService.sendEmail("./mail/ismsInfoPush.vm", constructEmailPush());
			sendResult.setSuccess(true);
		} catch (Exception e) { 
			e.printStackTrace();
			PushLog.emailLog.error("推送失败，" + e.getMessage());
			sendResult.setSuccess(false); 
			sendResult.setMsg(e.getMessage());
			sendResult.setResultCode(SendMessageResult.FAIL);
		} 
	}
	
	/**
	 * 组装默认的参数
	 * @return
	 */
	private EmailPush constructEmailPush() { 
		/*
		EmailPush emailPush = new EmailPush(); 
		emailPush.setMailServer("mail.aotain.com");
		EmailPush.Sender sender = new EmailPush.Sender();
		sender.setEmailSender("test02@aotain.com");
		sender.setPassword("7ZpDjHmX");
		emailPush.setSender(sender);
		List<Receiver> receivers = new ArrayList<Receiver>(2);
		Receiver receiver = new Receiver();
		receiver.setEmailReceiver("zhangcw@aotain.com");
		receivers.add(receiver);
		emailPush.getReceiver().addAll(receivers);
		emailPush.setSubject("ISMS预警信息推送");
		emailPush.setSendData("ISMS预警推送测试"); 
		return emailPush; 
		*/
		// TODO:根据ismsMessage组装email数据 
		return email; 
	} 
	
}

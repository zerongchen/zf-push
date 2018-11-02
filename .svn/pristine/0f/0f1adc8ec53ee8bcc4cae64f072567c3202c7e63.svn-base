package com.aotain.zongfen.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.mail.internet.MimeMessage;

import org.apache.velocity.app.VelocityEngine;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.velocity.VelocityEngineUtils;

import com.alibaba.fastjson.JSON;
import com.aotain.zongfen.bean.EmailPush;
import com.aotain.zongfen.bean.SendData;
import com.aotain.zongfen.util.LocalConfig;
import com.aotain.zongfen.util.PushLog;
import com.aotain.zongfen.util.StringUtil;

@Service
public class SendEmailService {

	public void sendEmail(String template, EmailPush emailPush) {
		JavaMailSender sender = createJavaMailSender(emailPush);
		String text = null;
		if (template != null) {
			Map<String, Object> model = wrapperSendData(emailPush.getSendData());
			VelocityEngine velocityEngine = new VelocityEngine();
			text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, template, "UTF-8", model);
		}			
		send(sender, text, emailPush);
	}
	
	private Map<String, Object> wrapperSendData(String sendData) {
		Map<String, Object> model = new HashMap<String, Object>();
		if (!StringUtil.isEmptyString(sendData)) {
			try {
				SendData data = JSON.parseObject(sendData, SendData.class);
				model.put("province", (StringUtil.isEmptyString(data.getProvince()) ? "" : data.getProvince()));
				model.put("alarmTime", (StringUtil.isEmptyString(data.getAlarmTime()) ? "" : data.getAlarmTime()));
				model.put("alarmMessage", (StringUtil.isEmptyString(data.getAlarmMessage()) ? "" : data.getAlarmMessage()));
				model.put("alarmParameter", (StringUtil.isEmptyString(data.getAlarmParameter()) ? "" : data.getAlarmParameter()));
			} catch (Exception e) {
				PushLog.emailLog.info("无法识别email类型的push信息!", e);
			}
		} else {
			PushLog.emailLog.info("Java mail send data is null!");
		}
		return model;
	}

	/**
	 * 
	 * @param sender JavaMailSender实例
	 * @param emailPush
	 */
	private void send(JavaMailSender sender, String text, EmailPush emailPush) {
		if (sender != null) {
			try {
				MimeMessage msg = sender.createMimeMessage();
				MimeMessageHelper message = new MimeMessageHelper(msg, true, "UTF-8");
				message.setFrom(LocalConfig.getInstance().getEmailSenderUsername());
				message.setTo(emailPush.getReceiver().getEmailReceiver().toArray(new String[emailPush.getReceiver().getEmailReceiver().size()]));
				message.setSubject(emailPush.getSubject());
				if (text != null) {
					message.setText(text, true);
				} else {
					message.setText(emailPush.getSendData());
				}
				sender.send(msg);
			} catch (Exception e) {
				PushLog.emailLog.error("send eamil failed!", e);
			} 
		} else {
			PushLog.emailLog.info("Java mail sender is null!");
		}
	}

	/**
	 * 获取JavaMailSender实例
	 * @return
	 */
	private JavaMailSender createJavaMailSender(EmailPush emailPush) {
		try {
			JavaMailSenderImpl sender = new JavaMailSenderImpl();
			sender.setHost(LocalConfig.getInstance().getEmailMailServer());
			sender.setDefaultEncoding("UTF-8");
			sender.setUsername(LocalConfig.getInstance().getEmailSenderUsername());
			sender.setPassword(LocalConfig.getInstance().getEmailSenderPassword());

			Properties props = new Properties();
			props.setProperty("mail.smtp.auth", "true");
			props.setProperty("mail.transport.protocol", "smtp");
			props.setProperty("mail.smtp.timeout", LocalConfig.getInstance().getEmailSenderTimeout());
			/*props.setProperty("mail.smtp.starttls.enable", "true"); // 公司邮件服务器不支持这两个配置
			props.setProperty("mail.smtp.starttls.required", "true");*/
			sender.setJavaMailProperties(props);
			return sender;
		} catch (Exception e) {
			PushLog.emailLog.error("create java mail sender failed!", e);
		}
		return null;
	}
}

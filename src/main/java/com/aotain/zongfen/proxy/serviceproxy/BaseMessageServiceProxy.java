package com.aotain.zongfen.proxy.serviceproxy;

import java.io.UnsupportedEncodingException;

import com.aotain.zongfen.ack.IsmsPushAckHelper;
import com.aotain.zongfen.ack.PushAckHelper;
import com.aotain.zongfen.bean.IsmsPushAck;
import com.aotain.zongfen.bean.SendMessageResult;
import com.aotain.zongfen.bean.ValidateMessageResult;
import com.aotain.zongfen.entity.IsmsMessage;
import com.aotain.zongfen.util.PushLog;
import com.aotain.zongfen.util.PushSecurityTool;
import com.aotain.zongfen.util.PushSecurityTool.PushDecryptResult;
import com.aotain.zongfen.util.StringUtil;

/**
 * @Author ligh 
 */
public abstract class BaseMessageServiceProxy implements IMessageServiceProxy {
	
	protected IsmsMessage ismsMessage; 
	
	protected ValidateMessageResult validateResult; 
	
	protected SendMessageResult sendResult; 
	
	protected IsmsPushAck ack = new IsmsPushAck();

	public BaseMessageServiceProxy(IsmsMessage ismsMessage) {
		this.ismsMessage = ismsMessage; 
		this.validateResult = new ValidateMessageResult(); 
		this.sendResult = new SendMessageResult(); 
	} 
	
	public ValidateMessageResult validateMessage() { 
		this.commonValidateMessage(); 
		this.innerValidateMessage(); 
		return validateResult; 
	} 
	
	public SendMessageResult sendMessage() { 
		this.innerSendMessage(); 
		this.constructSendMessageIsmsPushAck();
		return sendResult; 
	} 
	
	public IsmsMessage getIsmsMessage() {
		return ismsMessage; 
	} 
	
	private void commonValidateMessage() {
		if (ismsMessage == null) { 
			PushLog.threadLog.info("校验不通过，参数不能全为空。");
			validateResult.setValid(false); 
			validateResult.setResultCode(ValidateMessageResult.ENCRYPT_ERROR);
			validateResult.setMsg("参数不能全为空"); 
		} 
		if (validateResult.isValid() && StringUtil.isBlank(ismsMessage.getRandVal())) { 
			PushLog.threadLog.info("校验不通过，randVal参数不能为空，" + ismsMessage.toString());
			validateResult.setValid(false); 
			validateResult.setResultCode(ValidateMessageResult.ENCRYPT_ERROR);
			validateResult.setMsg("randVal参数不能为空"); 
		} 
		if (validateResult.isValid() && StringUtil.isBlank(ismsMessage.getPwdHash())) { 
			PushLog.threadLog.info("校验不通过，pwdHash参数不能为空，" + ismsMessage.toString());
			validateResult.setValid(false); 
			validateResult.setResultCode(ValidateMessageResult.ENCRYPT_ERROR);
			validateResult.setMsg("pwdHash参数不能为空"); 
		} 
		if (validateResult.isValid() && StringUtil.isBlank(ismsMessage.getPush())) { 
			PushLog.threadLog.info("校验不通过，push参数不能为空，" + ismsMessage.toString());
			validateResult.setValid(false); 
			validateResult.setResultCode(ValidateMessageResult.ENCRYPT_ERROR);
			validateResult.setMsg("push参数不能为空"); 
		} 
		if (validateResult.isValid() && StringUtil.isBlank(ismsMessage.getPushHash())) { 
			PushLog.threadLog.info("校验不通过，pushHash参数不能为空，" + ismsMessage.toString());
			validateResult.setValid(false); 
			validateResult.setResultCode(ValidateMessageResult.ENCRYPT_ERROR);
			validateResult.setMsg("pushHash参数不能为空"); 
		} 
		if (validateResult.isValid() && ismsMessage.getEncryptAlgorithm() == null) { 
			PushLog.threadLog.info("校验不通过，encryptAlgorithm参数不能为空，" + ismsMessage.toString());
			validateResult.setValid(false); 
			validateResult.setResultCode(ValidateMessageResult.ENCRYPT_ERROR);
			validateResult.setMsg("encryptAlgorithm参数不能为空"); 
		} 
		if (validateResult.isValid() && ismsMessage.getHashAlgorithm() == null) { 
			PushLog.threadLog.info("校验不通过，hashAlgorithm参数不能为空，" + ismsMessage.toString());
			validateResult.setValid(false); 
			validateResult.setResultCode(ValidateMessageResult.ENCRYPT_ERROR);
			validateResult.setMsg("hashAlgorithm参数不能为空"); 
		} 
		if (validateResult.isValid() && ismsMessage.getCompressionFormat() == null) { 
			PushLog.threadLog.info("校验不通过，compressionFormat参数不能为空，" + ismsMessage.toString());
			validateResult.setValid(false); 
			validateResult.setResultCode(ValidateMessageResult.ENCRYPT_ERROR);
			validateResult.setMsg("compressionFormat参数不能为空"); 
		} 
		if (validateResult.isValid() && StringUtil.isBlank(ismsMessage.getPushVersion())) { 
			PushLog.threadLog.info("校验不通过，pushVersion参数不能为空，" + ismsMessage.toString());
			validateResult.setValid(false); 
			validateResult.setResultCode(ValidateMessageResult.ENCRYPT_ERROR);
			validateResult.setMsg("pushVersion参数不能为空"); 
		} 
		if (validateResult.isValid()) { 
			PushSecurityTool tool = new PushSecurityTool(); 
			PushDecryptResult drs = tool.decrypt(ismsMessage.getRandVal(), 
					ismsMessage.getPwdHash(), 
					ismsMessage.getPush(), 
					ismsMessage.getPushHash(), 
					ismsMessage.getEncryptAlgorithm(), 
					ismsMessage.getHashAlgorithm(), 
					ismsMessage.getCompressionFormat(),
					ismsMessage.getPushVersion()); 
			if (drs.isSuccess()) { 
				validateResult.setValid(true); 
				try {
					ismsMessage.setData(new String(drs.getData().getBytes(),"utf-8"));
				} catch (UnsupportedEncodingException e) {
					PushLog.threadLog.info("数据转换失败" + ismsMessage.toString());
					validateResult.setValid(false);
					validateResult.setResultCode(ValidateMessageResult.OTHER_ERROR);
					validateResult.setMsg(drs.getError()); 
				} 
			} else { 
				PushLog.threadLog.info("校验不通过，解码失败，" + ismsMessage.toString());
				validateResult.setValid(false);
				validateResult.setResultCode(ValidateMessageResult.ENCRYPT_ERROR);
				validateResult.setMsg(drs.getError()); 
			}
		}
	}
	
	protected abstract void innerValidateMessage(); 
	
	protected abstract void innerSendMessage(); 
	
	protected void constructSendMessageIsmsPushAck() { 
		if (sendResult.isSuccess()) { 
			sendResult.setAck(IsmsPushAckHelper.ImmsPushAck(PushAckHelper.SUCCESSPushAck(ismsMessage.getPushObj().getPushId(), ismsMessage.getPushType())));; 
		} else { 
			sendResult.setAck(IsmsPushAckHelper.ImmsPushAck(PushAckHelper.FAILPushAck(ismsMessage.getPushObj().getPushId(), ismsMessage.getPushType(), sendResult.getMsg())));
		}
	}

}

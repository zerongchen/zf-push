package com.aotain.zongfen.proxy.serviceproxy;

import com.aotain.zongfen.bean.SendMessageResult;
import com.aotain.zongfen.bean.ValidateMessageResult;
import com.aotain.zongfen.entity.IsmsMessage;

/**
 * @Author ligh 
 */
public class EmptyTypeMessageServiceProxy extends BaseMessageServiceProxy implements IMessageServiceProxy {
	
	public EmptyTypeMessageServiceProxy(IsmsMessage ismsMessage) {
		super(ismsMessage); 
	}

	@Override
	public ValidateMessageResult validateMessage() {
		this.innerValidateMessage(); 
		return validateResult; 
	}

	@Override
	public SendMessageResult sendMessage() {
		throw new RuntimeException(); 
	}

	@Override
	protected void innerValidateMessage() {
		validateResult.setValid(false);
		validateResult.setResultCode(ValidateMessageResult.OTHER_ERROR);
		validateResult.setMsg("未知的推送类型");
	}

	@Override
	protected void innerSendMessage() {
		throw new RuntimeException(); 
	}

	@Override
	protected void constructSendMessageIsmsPushAck() {
		throw new RuntimeException(); 
	}
	
}

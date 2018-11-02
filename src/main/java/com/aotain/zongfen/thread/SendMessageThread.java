package com.aotain.zongfen.thread;

import com.aotain.zongfen.ack.IsmsPushAckHelper;
import com.aotain.zongfen.ack.PushAckHelper;
import com.aotain.zongfen.ack.SendMessageUtil;
import com.aotain.zongfen.bean.IsmsPushAck;
import com.aotain.zongfen.bean.IsmsPushAck.PushAck;
import com.aotain.zongfen.bean.SendMessageResult;
import com.aotain.zongfen.entity.IsmsMessage;
import com.aotain.zongfen.proxy.serviceproxy.BaseMessageServiceProxy;
import com.aotain.zongfen.proxy.serviceproxy.IMessageServiceProxy;
import com.aotain.zongfen.util.PushLog;

/**
 * @Author ligh 
 */
public class SendMessageThread implements Runnable { 
	
	private BaseMessageServiceProxy proxy; 
	
	private IsmsMessage ismsMessage; 
	
	public SendMessageThread(IMessageServiceProxy proxy) {
		this.proxy = (BaseMessageServiceProxy) proxy; 
		this.ismsMessage = this.proxy.getIsmsMessage(); 
	}
	
	@Override
	public void run() { 
		IsmsPushAck ack = null; 
		try { 
			SendMessageResult sendResult = proxy.sendMessage(); 
			ack = sendResult.getAck(); 
		} catch (Exception e) { 
			e.printStackTrace(); 
			PushLog.threadLog.error(SendMessageThread.class.getName() + ".run():" + e.getMessage());
			PushAck pushAck = PushAckHelper.FAILPushAck(ismsMessage.getPushObj().getPushId(), ismsMessage.getPushType(), e.getMessage()); 
			pushAck.setPushId(proxy.getIsmsMessage().getPushObj().getPushId());
			ack = IsmsPushAckHelper.ImmsPushAck(pushAck); 
		} finally { 
			SendMessageUtil.ack(ismsMessage.getPushObj().getReturnAddress(), ack); 
		} 
	}

	public BaseMessageServiceProxy getProxy() {
		return proxy;
	}

	public IsmsMessage getIsmsMessage() {
		return ismsMessage;
	} 

}

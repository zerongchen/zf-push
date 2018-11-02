package com.aotain.zongfen.thread;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

import com.aotain.zongfen.ack.IsmsPushAckHelper;
import com.aotain.zongfen.ack.PushAckHelper;
import com.aotain.zongfen.ack.SendMessageUtil;
import com.aotain.zongfen.bean.IsmsPushAck;
import com.aotain.zongfen.bean.IsmsPushAck.PushAck;
import com.aotain.zongfen.bean.SendMessageResult;
import com.aotain.zongfen.entity.IsmsMessage;
import com.aotain.zongfen.util.PushLog;

/**
 * @Author ligh 
 */
public class SendMessageRejectedExecutionHandler implements RejectedExecutionHandler {

	
	@Override
	public void rejectedExecution(Runnable runnable, ThreadPoolExecutor executor) {
		SendMessageThread thread = (SendMessageThread) runnable; 
		IsmsMessage ismsMessage = thread.getIsmsMessage(); 
		PushLog.threadLog.info("线程池已满，" + ismsMessage.toString());
		String returnAddress = ismsMessage.getPushObj().getReturnAddress(); 
		PushAck pushAck = PushAckHelper.FAILPushAck(ismsMessage.getPushObj().getPushId(), SendMessageResult.FAIL, "线程池已满"); 
		IsmsPushAck ack = IsmsPushAckHelper.ImmsPushAck(pushAck); 
		SendMessageUtil.ack(returnAddress, ack); 
	}

}

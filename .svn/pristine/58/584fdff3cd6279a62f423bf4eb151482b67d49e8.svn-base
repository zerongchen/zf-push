package com.aotain.zongfen.ack;

import java.math.BigInteger;

import com.aotain.zongfen.bean.IsmsPushAck.PushAck;
import com.aotain.zongfen.bean.SendMessageResult;

/**
 * @Author ligh 
 */
public class PushAckHelper {

	public static PushAck SUCCESSPushAck(long pushId, Integer type) { 
		PushAck pushAck = new PushAck(); 
		pushAck.setPushId(pushId); 
		pushAck.setResultCode(new BigInteger("" + SendMessageResult.SUCCESS));
		pushAck.setType(new BigInteger("" + type)); 
		return pushAck; 
	} 
	
	public static PushAck FAILPushAck(long pushId, Integer type, String msgInfo) { 
		PushAck pushAck = new PushAck(); 
		pushAck.setPushId(pushId);
		pushAck.setResultCode(new BigInteger("" + SendMessageResult.FAIL));
		pushAck.setType(new BigInteger("" + type)); 
		pushAck.setMsgInfo(msgInfo);
		return pushAck; 
		
	}
	
}

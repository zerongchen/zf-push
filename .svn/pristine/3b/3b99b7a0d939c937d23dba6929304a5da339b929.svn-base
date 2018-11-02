package com.aotain.zongfen.ack;

import com.aotain.zongfen.bean.IsmsPushAck;
import com.aotain.zongfen.bean.IsmsPushAck.PushAck;

/**
 * @Author ligh 
 */
public class IsmsPushAckHelper {

	public static IsmsPushAck ImmsPushAck(PushAck pushAck) { 
		IsmsPushAck ismsPushAck = new IsmsPushAck(); 
		ismsPushAck.setAckId("" + System.nanoTime());;
		ismsPushAck.setTimeStamp("" + System.currentTimeMillis()); 
		ismsPushAck.getPushAck().add(pushAck); 
		return ismsPushAck; 
	} 
	
	
	
}

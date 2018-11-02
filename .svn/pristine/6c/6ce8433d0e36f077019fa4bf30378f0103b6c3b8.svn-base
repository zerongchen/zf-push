package com.aotain.zongfen.proxy;

import org.springframework.stereotype.Service;

import com.aotain.zongfen.entity.IsmsMessage;
import com.aotain.zongfen.proxy.serviceproxy.EmailMessageServiceProxy;
import com.aotain.zongfen.proxy.serviceproxy.EmptyTypeMessageServiceProxy;
import com.aotain.zongfen.proxy.serviceproxy.IMessageServiceProxy;
import com.aotain.zongfen.proxy.serviceproxy.WeChatMessageServiceProxy;

/**
 * @Author ligh 
 */
@Service
public class MessageServiceProxyFactory { 

	public static IMessageServiceProxy createMessageProxy(IsmsMessage ismsMessage) { 
		if (ismsMessage.getPushType() != null 
				&& ismsMessage.getPushType().intValue() == WeChatMessageServiceProxy.PUSHTYPE) { 
			return new WeChatMessageServiceProxy(ismsMessage); 
		} 
		if (ismsMessage.getPushType() != null 
				&& ismsMessage.getPushType().intValue() == EmailMessageServiceProxy.PUSHTYPE) { 
			return new EmailMessageServiceProxy(ismsMessage); 
		} 
		return new EmptyTypeMessageServiceProxy(ismsMessage); 
	}
	
}

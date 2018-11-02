package com.aotain.zongfen.controller;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.aotain.zongfen.bean.Return;
import com.aotain.zongfen.bean.ValidateMessageResult;
import com.aotain.zongfen.entity.IsmsMessage;
import com.aotain.zongfen.proxy.MessageServiceProxyFactory;
import com.aotain.zongfen.proxy.serviceproxy.IMessageServiceProxy;
import com.aotain.zongfen.thread.GetThreadPool;
import com.aotain.zongfen.thread.SendMessageThread;

@Controller
public class MessageResource {
	
	@RequestMapping(value= {"/zfpush"},method=RequestMethod.POST,produces="application/json;charset=UTF-8")
	@ResponseBody
	public Return zf_push(IsmsMessage ismsMessage) { 
		IMessageServiceProxy proxy = MessageServiceProxyFactory.createMessageProxy(ismsMessage); 
		ValidateMessageResult validateResult = proxy.validateMessage(); 
		if (validateResult.isValid()) { 
			// 添加进线程池中 
			ThreadPoolTaskExecutor poolTaskExecutor = GetThreadPool.getInstance(); 
			poolTaskExecutor.execute(new SendMessageThread(proxy)); 
		} 
		//ack返回
		return validateResult.getReturn(); 
	}
}

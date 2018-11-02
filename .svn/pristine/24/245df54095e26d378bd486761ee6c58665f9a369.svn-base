package com.aotain.zongfen.thread;

import java.util.ResourceBundle;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

public class GetThreadPool {

	private static ThreadPoolTaskExecutor threadPool;
	
	public static ThreadPoolTaskExecutor GetThreadPools() {
		ResourceBundle config = ResourceBundle.getBundle("application");
		ThreadPoolTaskExecutor poolTaskExecutor = new ThreadPoolTaskExecutor(); 
		poolTaskExecutor.setCorePoolSize(Integer.valueOf(config.getString("threadpool.size")));
		poolTaskExecutor.setMaxPoolSize(Integer.valueOf(config.getString("threadpool.max.size")));
		poolTaskExecutor.setQueueCapacity(Integer.valueOf(config.getString("threadpool.queue.capacity")));
		poolTaskExecutor.initialize();
		return poolTaskExecutor;
	}
	
	public synchronized static ThreadPoolTaskExecutor getInstance() {
		if(threadPool==null) {
			threadPool =  GetThreadPools();
		}
		return threadPool;
	}
}

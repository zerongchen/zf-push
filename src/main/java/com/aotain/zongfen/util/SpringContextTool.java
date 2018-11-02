package com.aotain.zongfen.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @Author ligh 
 */
public class SpringContextTool implements ApplicationContextAware {
	
	private static ApplicationContext context;
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		SpringContextTool.context = applicationContext;
	}
	
	public static Object getBean(String name){
		return context.getBean(name);
	}
 }
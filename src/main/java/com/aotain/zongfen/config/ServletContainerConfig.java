package com.aotain.zongfen.config;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.web.servlet.ErrorPage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.http.HttpStatus;

/**
 * 
* @ClassName: CustomServletContainer 
* @Description: Tomcat服务器的设置(这里用一句话描述这个类的作用) 
* @author DongBoye
* @date 2018年1月25日 下午2:31:33 
*
 */
//@Configuration
//@PropertySource(value ="classpath:config/server.properties")
public class ServletContainerConfig implements EmbeddedServletContainerCustomizer {
	
	@Value("${server.port}")
	private int port;
	
	@Value("${server.session.timeout}")
	private int timeout;
	
	@Value("${server.page.notfound}")
	private String notfound;
	
	
	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
	    return new PropertySourcesPlaceholderConfigurer();
	}

	@Override
	public void customize(ConfigurableEmbeddedServletContainer container) {
		container.setPort(port);
        container.addErrorPages(new ErrorPage(HttpStatus.NOT_FOUND, notfound));
        container.setSessionTimeout(timeout, TimeUnit.MINUTES);
	}

}

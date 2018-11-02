package com.aotain.zongfen.util;

public class SystemInitUtil {

	public SystemInitUtil() { 
		LocalConfigInit(); 
	}
	
	/**
	 * 初始化配置文件
	 */
	private void LocalConfigInit() {
		try {
			Class.forName(LocalConfig.class.getName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} 
	}

}

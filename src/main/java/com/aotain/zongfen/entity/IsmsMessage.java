package com.aotain.zongfen.entity;

import java.io.Serializable;

import com.aotain.zongfen.bean.BasePush;

/**
 * @Author ligh 
 */
public class IsmsMessage implements Serializable {
	
	/** 
	* @Fields serialVersionUID : TODO(用一句话描述这个变量表示什么) 
	*/  
	private static final long serialVersionUID = 1L;

	private String randVal; 

	private String pwdHash; 
	
	private String push; 
	
	private String pushHash; 
	
	private Integer pushType; 
	
	private Long pushSequence; 
	
	private Integer encryptAlgorithm; 
	
	private Integer hashAlgorithm; 
	
	private Integer compressionFormat; 
	
	private String pushVersion; 
	
	/**
	 * 解密后的数据字符串
	 */
	private String data; 
	
	/**
	 * 解密后经过解析的obj
	 */
	private BasePush pushObj; 

	public String getRandVal() {
		return randVal;
	}

	public void setRandVal(String randVal) {
		this.randVal = randVal;
	}

	public String getPwdHash() {
		return pwdHash;
	}

	public void setPwdHash(String pwdHash) {
		this.pwdHash = pwdHash;
	}

	public String getPush() {
		return push;
	}

	public void setPush(String push) {
		this.push = push;
	}

	public String getPushHash() {
		return pushHash;
	}

	public void setPushHash(String pushHash) {
		this.pushHash = pushHash;
	}

	public Integer getPushType() {
		return pushType;
	}

	public void setPushType(Integer pushType) {
		this.pushType = pushType;
	}

	public Long getPushSequence() {
		return pushSequence;
	}

	public void setPushSequence(Long pushSequence) {
		this.pushSequence = pushSequence;
	}

	public Integer getEncryptAlgorithm() {
		return encryptAlgorithm;
	}

	public void setEncryptAlgorithm(Integer encryptAlgorithm) {
		this.encryptAlgorithm = encryptAlgorithm;
	}

	public Integer getHashAlgorithm() {
		return hashAlgorithm;
	}

	public void setHashAlgorithm(Integer hashAlgorithm) {
		this.hashAlgorithm = hashAlgorithm;
	}

	public Integer getCompressionFormat() {
		return compressionFormat;
	}

	public void setCompressionFormat(Integer compressionFormat) {
		this.compressionFormat = compressionFormat;
	}

	public String getPushVersion() {
		return pushVersion;
	}

	public void setPushVersion(String pushVersion) {
		this.pushVersion = pushVersion;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public BasePush getPushObj() {
		return pushObj;
	}

	public void setPushObj(BasePush pushObj) {
		this.pushObj = pushObj;
	} 
	
	@Override
	public String toString() {
		return "IsmsMessage [randVal=" + randVal + ",pwdHash=" + pwdHash + ",push=" + push + ",pushHash=" + pushHash + ",pushType=" + pushType + ",pushSequence=" + pushSequence + ",encryptAlgorithm=" + encryptAlgorithm + ",hashAlgorithm=" + hashAlgorithm + ",compressionFormat=" + compressionFormat + ",pushVersion=" + pushVersion + "]";
	}

}

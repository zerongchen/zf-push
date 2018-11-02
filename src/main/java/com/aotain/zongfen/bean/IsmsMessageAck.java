package com.aotain.zongfen.bean;

import java.io.Serializable;

/**
 * @Author ligh 
 */
public class IsmsMessageAck implements Serializable {
	
	/** 
	* @Fields serialVersionUID : TODO(用一句话描述这个变量表示什么) 
	*/  
	private static final long serialVersionUID = 1L;

	private String randVal; 

	private String pwdHash; 
	
	private String result; 
	
	private String resultHash; 
	
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
	private IsmsPushAck ackObj; 

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

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getResultHash() {
		return resultHash;
	}

	public void setResultHash(String resultHash) {
		this.resultHash = resultHash;
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

	public IsmsPushAck getAckObj() {
		return ackObj;
	}

	public void setAckObj(IsmsPushAck ackObj) {
		this.ackObj = ackObj;
	}

}

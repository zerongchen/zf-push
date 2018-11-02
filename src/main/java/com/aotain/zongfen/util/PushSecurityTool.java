package com.aotain.zongfen.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ResourceBundle;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * 消息推送安全认证工具
 *
 * @author liuz@aotian.com
 * @date 2017年8月4日 上午10:23:26
 */
@Component
public class PushSecurityTool { 
	private String password; // PUSH认证密码,默认值
	private String aesKey; // aes秘钥
	private String aesIv; // aes初始化向量
	private String rzKey; // 消息认证秘钥
	private String randVal = Tools.getRandomString(); // 随机数

	private int encryptAlgorithm; // 0-无加密，1-AES加密
	private int hashAlgorithm; // 0-无HASH，1-MD5，2-SHA-1
	private int commpresssionFormat; // 0-无压缩，1-ZIP压缩
	private String pushVersion; // PUSH接口版本

	public PushSecurityTool() { 
		this.password = PushSecurityToolHelper.instance.password;
		this.aesKey = PushSecurityToolHelper.instance.aesKey;
		this.aesIv = PushSecurityToolHelper.instance.aesIv;
		this.rzKey = PushSecurityToolHelper.instance.rzKey;
		this.encryptAlgorithm = PushSecurityToolHelper.instance.encryptAlgorithm;
		this.hashAlgorithm = PushSecurityToolHelper.instance.hashAlgorithm;
		this.commpresssionFormat = PushSecurityToolHelper.instance.commpresssionFormat;
		this.pushVersion = PushSecurityToolHelper.instance.pushVersion;
	}

	/**
	 * 对发送内容进行压缩加密，并添加权限信息
	 * 
	 * @param dataStr
	 * @return
	 * @throws Exception
	 */
	public PushEncryptResult encrypt(String dataStr) throws Exception {
		if (dataStr == null || dataStr.isEmpty()) {
			throw new Exception("Empty input String");
		}

		PushEncryptResult rs = new PushEncryptResult();
		byte[] data = dataStr.getBytes("UTF-8");
		if (this.commpresssionFormat == PushSecurityTool.COMPRESSION_FORMAT_ZIP) {
			data = compress(dataStr); // 压缩
		}

		rs.pwdHash = this.password + this.randVal;
		if (this.hashAlgorithm == PushSecurityTool.HASH_ALGORITHM_MD5) {
			rs.pwdHash = Tools.Encrypt2(rs.pwdHash.getBytes());
		} else if (this.hashAlgorithm == PushSecurityTool.HASH_ALGORITHM_SHA) {
			SHA1 sha1 = new SHA1();
			String byteData = this.password + this.randVal;
			rs.pwdHash = sha1.getDigestOfbase64(byteData.getBytes());
		} else{ // 不指定hash算法是用base64处理
			rs.pwdHash = new BASE64Encoder().encode(rs.pwdHash.getBytes());
		}

		if (this.encryptAlgorithm == PushSecurityTool.ENCRYPT_ALGORITHM_AES) {
			rs.push = AES.Encrypt(data, this.aesKey, this.aesIv);
		} else {
			rs.push = new BASE64Encoder().encode(data);
		}
		byte[] resultBytes = Tools.joinBytes(data, this.rzKey.getBytes("UTF-8"));
		String resultHash = resultBytes.toString();
		if (this.hashAlgorithm == PushSecurityTool.HASH_ALGORITHM_MD5) {
			resultHash = Tools.Encrypt2(resultBytes);
		} else if (this.hashAlgorithm == PushSecurityTool.HASH_ALGORITHM_SHA) {
			SHA1 sha1 = new SHA1();
			resultHash = sha1.getDigestOfbase64(resultBytes);
		}
		rs.pushHash = resultHash;
		return rs;
	}

	/**
	 * 权限验证与数据解密
	 * 
	 * @param randVal 随机数
	 * @param pwdHash 用户口令HASH
	 * @param push push数据
	 * @param pushHash push数据HASH
	 * @param encryptAlgorithm 加密类型：0-无加密，1-AES加密
	 * @param hashAlgorithm hash类型：0-hash，1-MD5，2-SHA-1
	 * @param commpresssionFormat 压缩格式：0-无压缩，1-ZIP压缩
	 * @param pushVersion 接口版本
	 * @return
	 */
	public PushDecryptResult decrypt(String randVal, String pwdHash, String push, String pushHash, int encryptAlgorithm, int hashAlgorithm, int commpresssionFormat, String pushVersion) {
		PushDecryptResult rs = new PushDecryptResult();
		// 0. 接口版本验证
		if (!pushVersion.equals(this.pushVersion)) {
			rs.success = false;
			rs.error = "Client version is " + pushVersion + ",but server version is " + this.pushVersion;
			return rs;
		}
		// 1. pwdHash校验
		String pwdHash_ = this.password + randVal;
		if (hashAlgorithm == 0) {
			pwdHash_ = Tools.encodeBase64(pwdHash_);
		} else if (hashAlgorithm == 1) {
			pwdHash_ = Tools.Encrypt2(pwdHash_.getBytes());
		} else if (hashAlgorithm == 2) {
			SHA1 sha1 = new SHA1();
			pwdHash_ = sha1.getDigestOfbase64(pwdHash_.getBytes());
		} else {
			rs.success = false;
			rs.error = "hashAlgorithm错误, hashAlgorithm=" + hashAlgorithm;
			return rs;
		}

		if (!pwdHash_.equals(pwdHash)) {
			rs.success = false;
			rs.error = "密码错误,clientPwdHash=" + pwdHash + ",serverPwdHash=" + pwdHash_;
			return rs;
		}

		// 2. 加密类型验证
		if (encryptAlgorithm != 0 && encryptAlgorithm != 1) {
			rs.success = false;
			rs.error = "encryptAlgorithm错误,encryptAlgorithm=" + encryptAlgorithm;
			return rs;
		}

		// 3. 解密
		byte[] compressedData = null;
		if (encryptAlgorithm != 0) {
			// 使用了AES加密
			try {
				compressedData = AES.DecryptReturnByte(push, this.aesKey, this.aesIv);
			} catch (Exception e) {
				rs.success = false;
				rs.error = "AES解密失败:" + e.getMessage();
				return rs;
			}
		} else {
			try {
				compressedData = new BASE64Decoder().decodeBuffer(push);
			} catch (Exception e) {
				rs.success = false;
				rs.error = "BASE64解码失败:" + e.getMessage();
				return rs;
			}
		}

		if (compressedData == null) {
			rs.success = false;
			rs.error = "数据解密失败：解密结果为空";
			return rs;
		}

		// 4. 数据完整性验证
		String rzKey = this.rzKey;
		String pushHash_ = "";
		try {
			byte[] resultBytes = Tools.joinBytes(compressedData, rzKey.getBytes("UTF-8"));
			if (hashAlgorithm == 0) {
				pushHash_ = Tools.encodeBase64(compressedData.toString());
			} else if (hashAlgorithm == 1) {
				pushHash_ = Tools.Encrypt2(resultBytes);
			} else if (hashAlgorithm == 2) {
				SHA1 sha1 = new SHA1();
				pushHash_ = sha1.getDigestOfbase64(resultBytes);
			}

			// 只对有hash的数据进行hash校验
			if (hashAlgorithm != 0 && !pushHash.equals(pushHash_)) {
				rs.success = false;
				rs.error = "pushHash哈希值验证未通过,clientPushHash=" + pushHash + ",serverPushHash=" + pushHash_;
				return rs;
			}
		} catch (Exception e) {
			rs.success = false;
			rs.error = "pushHash哈希值验证未通过:" + e.getMessage();
			return rs;
		}

		// 5. 数据解压
		try {
			String msg = new String(compressedData, "UTF-8");
			if (commpresssionFormat == COMPRESSION_FORMAT_ZIP) {
				msg = unCompress(compressedData);
			}
			rs.data = msg;
		} catch (IOException e) {
			rs.success = false;
			rs.error = "数据解压缩失败：" + e.getMessage();
			return rs;
		}

		return rs;
	}

	/**
	 * 字符串压缩算法
	 * 
	 * @param str
	 * @return
	 * @throws IOException
	 */
	private static byte[] compress(String str) throws IOException {
		if (null == str || str.length() <= 0) {
			return null;
		}
		// 创建一个新的 byte 数组输出流
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		// 使用默认缓冲区大小创建新的输出流
		GZIPOutputStream gzip = new GZIPOutputStream(out);
		// 将 b.length 个字节写入此输出流
		gzip.write(str.getBytes("UTF-8"));
		gzip.close();
		// 使用指定的 charsetName，通过解码字节将缓冲区内容转换为字符串
		return out.toByteArray();
	}

	/**
	 * 字符串解压缩算法
	 * 
	 * @param str
	 * @return
	 * @throws IOException
	 */
	private static String unCompress(byte[] data) throws IOException {
		if (null == data || data.length <= 0) {
			return null;
		}
		// 创建一个新的 byte 数组输出流
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		// 创建一个 ByteArrayInputStream，使用 buf 作为其缓冲区数组
		ByteArrayInputStream in = new ByteArrayInputStream(data);
		// 使用默认缓冲区大小创建新的输入流
		GZIPInputStream gzip = new GZIPInputStream(in);
		byte[] buffer = new byte[256];
		int n = 0;
		while ((n = gzip.read(buffer)) >= 0) {// 将未压缩数据读入字节数组
			// 将指定 byte 数组中从偏移量 off 开始的 len 个字节写入此 byte数组输出流
			out.write(buffer, 0, n);
		}
		// 使用指定的 charsetName，通过解码字节将缓冲区内容转换为字符串
		return out.toString("UTF-8");
	}

	public static void main(String[] args) {
		try {
			PushSecurityTool tool = new PushSecurityTool();
			PushEncryptResult rs = tool.encrypt("哈哈哈哈");
			System.out.println(rs);
			PushDecryptResult drs = tool.decrypt(rs.getRandVal(), rs.getPwdHash(), rs.getPush(), rs.getPushHash(), rs.getEncryptAlgorithm(), rs.getHashAlgorithm(), rs.getCommpresssionFormat(),
					rs.getPushVersion());
			System.out.println(drs);
		} catch (Exception e) {
			e.printStackTrace();
		}
	} 
	
	public enum PushSecurityToolHelper { 
		
		instance(); 
		
		public String password = "LLX239"; // PUSH认证密码,默认值
		public String aesKey = "LVVKVXWF56496929"; // aes秘钥
		public String aesIv = "MSFBUWFS86537785"; // aes初始化向量
		public String rzKey = "LLX239"; // 消息认证秘钥

		public int encryptAlgorithm = 1; // 0-无加密，1-AES加密
		public int hashAlgorithm = 1; // 0-无HASH，1-MD5，2-SHA-1
		public int commpresssionFormat = 1; // 0-无压缩，1-ZIP压缩
		public String pushVersion = "v1.0"; // PUSH接口版本
		
		private Logger logger = LoggerFactory.getLogger(PushSecurityToolHelper.class); 
		
		private final static String PROPERTIES_PATH = "application";
		
		private PushSecurityToolHelper() {
			try {
				ResourceBundle config = ResourceBundle.getBundle(PROPERTIES_PATH); 
				initConfigs(config); 
			} catch (Exception e) {
				logger.error("安全认证工具参数初始化异常，部分参数将采用默认配置", e);
			}
		}
		
		private void initConfigs(ResourceBundle config) {
			this.password = (String) config.getString("password");
			this.aesKey = (String) config.getString("aes_key");
			this.aesIv = (String) config.getString("aes_iv");
			this.rzKey = (String) config.getString("rz_key");
			this.encryptAlgorithm = Integer.parseInt((String) config.getString("encrypt_algorithm"));
			this.hashAlgorithm = Integer.parseInt((String) config.getString("hash_algorithm"));
			this.commpresssionFormat = Integer.parseInt((String) config.getString("compression_format"));
			this.pushVersion = (String) config.getString("version");
		}
		
	}

	/**
	 * 加密后的数据
	 *
	 * @author liuz@aotian.com
	 * @date 2017年8月4日 上午11:26:49
	 */
	public class PushEncryptResult {
		private String randVal;
		private String pwdHash;
		private String push;
		private String pushHash;
		private int encryptAlgorithm;
		private int hashAlgorithm;
		private int commpresssionFormat;
		private String pushVersion;

		public PushEncryptResult() {
			this.randVal = PushSecurityTool.this.randVal;
			this.encryptAlgorithm = PushSecurityTool.this.encryptAlgorithm;
			this.hashAlgorithm = PushSecurityTool.this.hashAlgorithm;
			this.commpresssionFormat = PushSecurityTool.this.commpresssionFormat;
			this.pushVersion = PushSecurityTool.this.pushVersion;
		}

		public String getPushVersion() {
			return pushVersion;
		}

		@Override
		public String toString() {
			return "PushEncryptResult [randVal=" + randVal + ", pwdHash=" + pwdHash + ", push=" + push + ", pushHash=" + pushHash + ", encryptAlgorithm=" + encryptAlgorithm + ", hashAlgorithm="
					+ hashAlgorithm + ", commpresssionFormat=" + commpresssionFormat + ", pushVersion=" + pushVersion + "]";
		}

		public String getRandVal() {
			return randVal;
		}

		public String getPwdHash() {
			return pwdHash;
		}

		public String getPush() {
			return push;
		}

		public String getPushHash() {
			return pushHash;
		}

		public int getEncryptAlgorithm() {
			return encryptAlgorithm;
		}

		public int getHashAlgorithm() {
			return hashAlgorithm;
		}

		public int getCommpresssionFormat() {
			return commpresssionFormat;
		}

	}

	/**
	 * 解密结果数据
	 *
	 * @author liuz@aotian.com
	 * @date 2017年8月4日 下午2:12:08
	 */
	public class PushDecryptResult {
		private boolean success;
		private String error;
		private String data;

		public PushDecryptResult() {
			success = true;
		}

		public boolean isSuccess() {
			return success;
		}

		public String getError() {
			return error;
		}

		public String getData() {
			return data;
		}

		@Override
		public String toString() {
			return "PushDecryptResult [success=" + success + ", error=" + error + ", data=" + data + "]";
		}

	}

	// 对称加密算法-不加密
	private static final int ENCRYPT_ALGORITHM_NO = 0;
	// 对称加密算法-AES加密算法
	private static final int ENCRYPT_ALGORITHM_AES = 1;
	// 哈希算法-无哈希
	private static final int HASH_ALGORITHM_NO = 0;
	// 哈希算法-MD5
	private static final int HASH_ALGORITHM_MD5 = 1;
	// 哈希算法-SHA-1
	private static final int HASH_ALGORITHM_SHA = 2;
	// 压缩格式-无压缩
	private static final int COMPRESSION_FORMAT_NO = 0;
	// 压缩格式-zip压缩格式
	private static final int COMPRESSION_FORMAT_ZIP = 1;


	private static class Tools {
		/**
		 * 获取加密随机串
		 * 
		 * @return
		 */
		public static String getRandomString() {
			char c[] = new char[62];
			for (int i = 97, j = 0; i < 123; i++, j++) {
				c[j] = (char) i;
			}
			for (int o = 65, p = 26; o < 91; o++, p++) {
				c[p] = (char) o;
			}
			for (int m = 48, n = 52; m < 58; m++, n++) {
				c[n] = (char) m;
			}
			// 取随机产生的认证码(4位数字)
			String randomString = "";
			java.util.Random r = new java.util.Random();
			for (int i = 0; i < 20; i++) {
				int x = r.nextInt(62);
				randomString += String.valueOf(c[x]);
			}
			return randomString;
		}
		
		/**
		 * 数组合并
		 * 
		 * @param srcByte1
		 * @param srcByte2
		 * @return
		 */
		public static byte[] joinBytes(byte[] srcByte1, byte[] srcByte2) {
			int byte1Length = srcByte1.length;
			int byte2Length = srcByte2.length;
			byte[] retByte = new byte[byte1Length + byte2Length];
			System.arraycopy(srcByte1, 0, retByte, 0, byte1Length);
			System.arraycopy(srcByte2, 0, retByte, byte1Length, byte2Length);
			return retByte;
		}
		
		/**
		 * MD5+BASE64
		 * 
		 * @param source
		 * @return
		 * @throws NoSuchAlgorithmException
		 */
		public static String Encrypt2(byte[] source){
			return getBase64(getMd5(source));
		}

		/**
		 * BASE64
		 * 
		 * @param source
		 * @return
		 */
		public static String getBase64(String source) {
			return new String(org.apache.commons.codec.binary.Base64.encodeBase64(source.getBytes()));
		}

		/**
		 * MD5
		 * @param source
		 * @return
		 * @throws NoSuchAlgorithmException
		 */
		public static String getMd5(byte[] source) {
			String s = null;
			char hexDigits[] = { // 用来将字节转换成 16 进制表示的字符
			'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
			java.security.MessageDigest md;
			try {
				md = java.security.MessageDigest.getInstance("MD5");
				md.update(source);
				byte tmp[] = md.digest(); // MD5 的计算结果是一个 128 位的长整数，
				// 用字节表示就是 16 个字节
				char str[] = new char[16 * 2]; // 每个字节用 16 进制表示的话，使用两个字符，
				// 所以表示成 16 进制需要 32 个字符
				int k = 0; // 表示转换结果中对应的字符位置
				for (int i = 0; i < 16; i++) { // 从第一个字节开始，对 MD5 的每一个字节
					// 转换成 16 进制字符的转换
					byte byte0 = tmp[i]; // 取第 i 个字节
					str[k++] = hexDigits[byte0 >>> 4 & 0xf]; // 取字节中高 4 位的数字转换,
					// >>> 为逻辑右移，将符号位一起右移
					str[k++] = hexDigits[byte0 & 0xf]; // 取字节中低 4 位的数字转换
				}
					s = new String(str); // 换后的结果转换为字符串
				return s;
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
				return "";
			}
		}
		
		//base64编码
		public static String encodeBase64(String param){
			if(StringUtils.isNotBlank(param)){
				param = new BASE64Encoder().encodeBuffer(param.getBytes());
			}
			return param;
		}
	}
	
	/**
	 * SHA1加密算法
	 *
	 * @author liuz@aotian.com
	 * @date 2017年8月4日 下午3:10:25
	 */
	private static class SHA1 { 
	    private final int[] abcde = { 
	            0x67452301, 0xefcdab89, 0x98badcfe, 0x10325476, 0xc3d2e1f0 
	        }; 
	    // 摘要数据存储数组 
	    private int[] digestInt = new int[5]; 
	    // 计算过程中的临时数据存储数组 
	    private int[] tmpData = new int[80]; 
	    // 计算sha-1摘要 
	    private int process_input_bytes(byte[] bytedata) { 
	        // 初试化常量 
	        System.arraycopy(abcde, 0, digestInt, 0, abcde.length); 
	        // 格式化输入字节数组，补10及长度数据 
	        byte[] newbyte = byteArrayFormatData(bytedata); 
	        // 获取数据摘要计算的数据单元个数 
	        int MCount = newbyte.length / 64; 
	        // 循环对每个数据单元进行摘要计算 
	        for (int pos = 0; pos < MCount; pos++) { 
	            // 将每个单元的数据转换成16个整型数据，并保存到tmpData的前16个数组元素中 
	            for (int j = 0; j < 16; j++) { 
	                tmpData[j] = byteArrayToInt(newbyte, (pos * 64) + (j * 4)); 
	            } 
	            // 摘要计算函数 
	            encrypt(); 
	        } 
	        return 20; 
	    } 
	    // 格式化输入字节数组格式 
	    private byte[] byteArrayFormatData(byte[] bytedata) { 
	        // 补0数量 
	        int zeros = 0; 
	        // 补位后总位数 
	        int size = 0; 
	        // 原始数据长度 
	        int n = bytedata.length; 
	        // 模64后的剩余位数 
	        int m = n % 64; 
	        // 计算添加0的个数以及添加10后的总长度 
	        if (m < 56) { 
	            zeros = 55 - m; 
	            size = n - m + 64; 
	        } else if (m == 56) { 
	            zeros = 63; 
	            size = n + 8 + 64; 
	        } else { 
	            zeros = 63 - m + 56; 
	            size = (n + 64) - m + 64; 
	        } 
	        // 补位后生成的新数组内容 
	        byte[] newbyte = new byte[size]; 
	        // 复制数组的前面部分 
	        System.arraycopy(bytedata, 0, newbyte, 0, n); 
	        // 获得数组Append数据元素的位置 
	        int l = n; 
	        // 补1操作 
	        newbyte[l++] = (byte) 0x80; 
	        // 补0操作 
	        for (int i = 0; i < zeros; i++) { 
	            newbyte[l++] = (byte) 0x00; 
	        } 
	        // 计算数据长度，补数据长度位共8字节，长整型 
	        long N = (long) n * 8; 
	        byte h8 = (byte) (N & 0xFF); 
	        byte h7 = (byte) ((N >> 8) & 0xFF); 
	        byte h6 = (byte) ((N >> 16) & 0xFF); 
	        byte h5 = (byte) ((N >> 24) & 0xFF); 
	        byte h4 = (byte) ((N >> 32) & 0xFF); 
	        byte h3 = (byte) ((N >> 40) & 0xFF); 
	        byte h2 = (byte) ((N >> 48) & 0xFF); 
	        byte h1 = (byte) (N >> 56); 
	        newbyte[l++] = h1; 
	        newbyte[l++] = h2; 
	        newbyte[l++] = h3; 
	        newbyte[l++] = h4; 
	        newbyte[l++] = h5; 
	        newbyte[l++] = h6; 
	        newbyte[l++] = h7; 
	        newbyte[l++] = h8; 
	        return newbyte; 
	    } 
	    private int f1(int x, int y, int z) { 
	        return (x & y) | (~x & z); 
	    } 
	    private int f2(int x, int y, int z) { 
	        return x ^ y ^ z; 
	    } 
	    private int f3(int x, int y, int z) { 
	        return (x & y) | (x & z) | (y & z); 
	    } 
	    private int f4(int x, int y) { 
	        return (x << y) | x >>> (32 - y); 
	    } 
	    // 单元摘要计算函数 
	    private void encrypt() { 
	        for (int i = 16; i <= 79; i++) { 
	            tmpData[i] = f4(tmpData[i - 3] ^ tmpData[i - 8] ^ tmpData[i - 14] ^ 
	                    tmpData[i - 16], 1); 
	        } 
	        int[] tmpabcde = new int[5]; 
	        for (int i1 = 0; i1 < tmpabcde.length; i1++) { 
	            tmpabcde[i1] = digestInt[i1]; 
	        } 
	        for (int j = 0; j <= 19; j++) { 
	            int tmp = f4(tmpabcde[0], 5) + 
	                f1(tmpabcde[1], tmpabcde[2], tmpabcde[3]) + tmpabcde[4] + 
	                tmpData[j] + 0x5a827999; 
	            tmpabcde[4] = tmpabcde[3]; 
	            tmpabcde[3] = tmpabcde[2]; 
	            tmpabcde[2] = f4(tmpabcde[1], 30); 
	            tmpabcde[1] = tmpabcde[0]; 
	            tmpabcde[0] = tmp; 
	        } 
	        for (int k = 20; k <= 39; k++) { 
	            int tmp = f4(tmpabcde[0], 5) + 
	                f2(tmpabcde[1], tmpabcde[2], tmpabcde[3]) + tmpabcde[4] + 
	                tmpData[k] + 0x6ed9eba1; 
	            tmpabcde[4] = tmpabcde[3]; 
	            tmpabcde[3] = tmpabcde[2]; 
	            tmpabcde[2] = f4(tmpabcde[1], 30); 
	            tmpabcde[1] = tmpabcde[0]; 
	            tmpabcde[0] = tmp; 
	        } 
	        for (int l = 40; l <= 59; l++) { 
	            int tmp = f4(tmpabcde[0], 5) + 
	                f3(tmpabcde[1], tmpabcde[2], tmpabcde[3]) + tmpabcde[4] + 
	                tmpData[l] + 0x8f1bbcdc; 
	            tmpabcde[4] = tmpabcde[3]; 
	            tmpabcde[3] = tmpabcde[2]; 
	            tmpabcde[2] = f4(tmpabcde[1], 30); 
	            tmpabcde[1] = tmpabcde[0]; 
	            tmpabcde[0] = tmp; 
	        } 
	        for (int m = 60; m <= 79; m++) { 
	            int tmp = f4(tmpabcde[0], 5) + 
	                f2(tmpabcde[1], tmpabcde[2], tmpabcde[3]) + tmpabcde[4] + 
	                tmpData[m] + 0xca62c1d6; 
	            tmpabcde[4] = tmpabcde[3]; 
	            tmpabcde[3] = tmpabcde[2]; 
	            tmpabcde[2] = f4(tmpabcde[1], 30); 
	            tmpabcde[1] = tmpabcde[0]; 
	            tmpabcde[0] = tmp; 
	        } 
	        for (int i2 = 0; i2 < tmpabcde.length; i2++) { 
	            digestInt[i2] = digestInt[i2] + tmpabcde[i2]; 
	        } 
	        for (int n = 0; n < tmpData.length; n++) { 
	            tmpData[n] = 0; 
	        } 
	    } 
	    // 4字节数组转换为整数 
	    private int byteArrayToInt(byte[] bytedata, int i) { 
	        return ((bytedata[i] & 0xff) << 24) | ((bytedata[i + 1] & 0xff) << 16) | 
	        ((bytedata[i + 2] & 0xff) << 8) | (bytedata[i + 3] & 0xff); 
	    } 
	    // 整数转换为4字节数组 
	    private void intToByteArray(int intValue, byte[] byteData, int i) { 
	        byteData[i] = (byte) (intValue >>> 24); 
	        byteData[i + 1] = (byte) (intValue >>> 16); 
	        byteData[i + 2] = (byte) (intValue >>> 8); 
	        byteData[i + 3] = (byte) intValue; 
	    } 
	    // 将字节转换为十六进制字符串 
	    private static String byteToHexString(byte ib) { 
	        char[] Digit = { 
	                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 
	                'D', 'E', 'F' 
	            }; 
	        char[] ob = new char[2]; 
	        ob[0] = Digit[(ib >>> 4) & 0X0F]; 
	        ob[1] = Digit[ib & 0X0F]; 
	        String s = new String(ob); 
	        return s; 
	    } 
	    // 将字节数组转换为十六进制字符串 
	    private static String byteArrayToHexString(byte[] bytearray) { 
	        String strDigest = ""; 
	        for (int i = 0; i < bytearray.length; i++) { 
	            strDigest += byteToHexString(bytearray[i]); 
	        } 
	        return strDigest; 
	    } 
	    // 计算sha-1摘要，返回相应的字节数组 
	    public byte[] getDigestOfBytes(byte[] byteData) { 
	        process_input_bytes(byteData); 
	        byte[] digest = new byte[20]; 
	        for (int i = 0; i < digestInt.length; i++) { 
	            intToByteArray(digestInt[i], digest, i * 4); 
	        } 
	        return digest; 
	    } 
	    // 计算sha-1摘要，返回相应的十六进制字符串 
	    public String getDigestOfString(byte[] byteData) { 
	        return byteArrayToHexString(getDigestOfBytes(byteData)); 
	    } 
	    
	 // 计算sha-1摘要，返回相应的base64字符串 
	    public String getDigestOfbase64(byte[] byteData) { 
	        return new BASE64Encoder().encodeBuffer(getDigestOfBytes(byteData)).replace("\r\n", "").replace("\n", ""); 
	    }
	}
	
	/**
	 * AES加密算法
	 *
	 * @author liuz@aotian.com
	 * @date 2017年8月4日 下午4:05:07
	 */
	private static class AES {
	    public static String Encrypt(String sSrc, String sKey,String sIv) throws Exception {
	        if (sKey == null) {
	            return null;
	        }
	        if (sKey.length() != 16) {
	            return null;
	        }
	        byte[] raw = sKey.getBytes();
	        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
	        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
	        IvParameterSpec iv = new IvParameterSpec(sIv.getBytes());
	        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
	        byte[] encrypted = cipher.doFinal(sSrc.getBytes());

	        return new BASE64Encoder().encode(encrypted);
	    }
	    
	    public static String Encrypt(byte[] src, String sKey,String sIv) throws Exception {
	        if (sKey == null) {
	            return null;
	        }
	        if (sKey.length() != 16) {
	            return null;
	        }
	        byte[] raw = sKey.getBytes();
	        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
	        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
	        IvParameterSpec iv = new IvParameterSpec(sIv.getBytes());
	        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
	        byte[] encrypted = cipher.doFinal(src);

	        return new BASE64Encoder().encode(encrypted);
	    }

	    public static String Decrypt(String sSrc, String sKey,String sIv) throws Exception {
	        try {
	            if (sKey == null) {
	                return null;
	            }
	            if (sKey.length() != 16) {
	                return null;
	            }
	            byte[] raw = sKey.getBytes("ASCII");
	            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
	            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
	            IvParameterSpec iv = new IvParameterSpec(sIv.getBytes());
	            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
	            byte[] encrypted1 = new BASE64Decoder().decodeBuffer(sSrc);
	            try {
	                byte[] original = cipher.doFinal(encrypted1);
	                String originalString = new String(original);
	                return originalString;
	            } catch (Exception e) {
	                System.out.println(e.toString());
	                return null;
	            }
	        } catch (Exception ex) {
	            System.out.println(ex.toString());
	            return null;
	        }
	    }
	    
	    public static byte[] DecryptReturnByte(String sSrc, String sKey,String sIv) throws Exception {
	        try {
	            if (sKey == null) {
	                return null;
	            }
	            if (sKey.length() != 16) {
	                return null;
	            }
	            byte[] raw = sKey.getBytes("ASCII");
	            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
	            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
	            IvParameterSpec iv = new IvParameterSpec(sIv.getBytes());
	            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
	            byte[] encrypted1 = new BASE64Decoder().decodeBuffer(sSrc);
	            try {
	                byte[] original = cipher.doFinal(encrypted1);
	                return original;
	            } catch (Exception e) {
	                System.out.println(e.toString());
	                return null;
	            }
	        } catch (Exception ex) {
	            System.out.println(ex.toString());
	            return null;
	        }
	    }
	    
	    public static String DecryptAESAndMD5(String sSrc, String sKey,String sIv) throws Exception {
	        try {
	            if (sKey == null) {
	                System.out.print("KeyΪ��null");
	                return null;
	            }
	            if (sKey.length() != 16) {
	                return null;
	            }
	            byte[] raw = sKey.getBytes("ASCII");
	            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
	            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
	            IvParameterSpec iv = new IvParameterSpec(sIv.getBytes());
	            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
	            byte[] encrypted1 = new BASE64Decoder().decodeBuffer(sSrc);
	            try {
	                byte[] original = cipher.doFinal(encrypted1);
	                MessageDigest md = MessageDigest.getInstance("MD5"); 
	    			md.update(original); 
	    			byte b[] = md.digest(); 
	    			return new BASE64Encoder().encodeBuffer(b).replace("\r\n", "").replace("\n", ""); 
	            } catch (Exception e) {
	                System.out.println(e.toString());
	                return null;
	            }
	        } catch (Exception ex) {
	            System.out.println(ex.toString());
	            return null;
	        }
	    }
	}
}

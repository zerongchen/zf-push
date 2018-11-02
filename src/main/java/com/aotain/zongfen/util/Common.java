package com.aotain.zongfen.util;

import java.io.UnsupportedEncodingException;

/**
 * 公用类
 * 
 * @author Jason.CW.Cheung
 * @date 2017年7月21日 上午9:31:50
 * @version 1.0
 */
public final class Common {
	private static final char chars[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E',
			'F' };
	public static String CHARSET = "UnicodeBigUnmarked"; // 使用UCS2编码

	public static String byte2hex(byte b) {
		char hex[] = new char[2];
		hex[0] = chars[(new Byte(b).intValue() & 0xf0) >> 4];
		hex[1] = chars[new Byte(b).intValue() & 0xf];
		return new String(hex);
	}

	public static String bytes2hex(byte[] b) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			sb.append(byte2hex(b[i]));
			sb.append(" ");
		}
		return sb.toString();
	}

	/**
	 * 将字符串转化成特定长度的byte[],如果value的长度小于idx,则右补零。比如
	 * getText(5,"1"),结果为{49,0,0,0,0}; 如果value的长度大于idx,则截取掉一部分。比如
	 * getText(2,"11111"),结果为{49,49};
	 * 
	 * @param idx 转化后byte[]的长度
	 * @param value 要转化的字符串
	 * @return byte[]
	 */
	public static byte[] getText(int idx, String value) {
		byte[] b1 = new byte[idx];
		int i = 0;
		if (value != null || !"".equals(value)) {
			byte[] b2 = value.getBytes();
			while (i < b2.length && i < idx) {
				b1[i] = b2[i];
				i++;
			}
		}
		while (i < b1.length) {
			b1[i] = 0;
			i++;
		}
		return b1;
	}

	/**
	 * 将字符串转化成特定长度的byte[],如果value的长度小于idx,则右补零。比如
	 * getText(5,"1"),结果为{49,0,0,0,0}; 如果value的长度大于idx,则截取掉一部分。比如
	 * getText(2,"11111"),结果为{49,49};
	 * 
	 * @param idx 转化后byte[]的长度
	 * @param value 要转化的字符串
	 * @return byte[]
	 */
	public static byte[] getTextContent(int idx, String value) {
		byte[] b1 = new byte[idx];
		int i = 0;
		if (value != null || !"".equals(value)) {
			byte[] b2;
			try {
				b2 = value.getBytes(CHARSET);
				System.out.println(b2.length + "  " + bytes2hex(b2));
			} catch (UnsupportedEncodingException e) {
				b2 = value.getBytes();
			}
			while (i < b2.length && i < idx) {
				b1[i] = b2[i];
				i++;
			}
		}
		while (i < b1.length) {
			b1[i] = 0;
			i++;
		}
		return b1;
	}

	private static int longMessageId = 0;

	/**
	 * 长消息ID
	 * 
	 * @return int
	 */
	public synchronized static byte getLongMessageId() {
		longMessageId++;
		if (longMessageId > 0xff)
			longMessageId = 1;
		return (byte) (longMessageId & 0xff);
	}

	/**
	 * <pre>对内容进行编码，并加上长短信头
	 * 1、  字节一：包头长度，固定填写0x05；
	 * 2、  字节二：包头类型标识，固定填写0x00，表示长短信；
	 * 3、  字节三：子包长度，固定填写0x03，表示后面三个字节的长度；
	 * 4、  字节四到字节六：包内容：
	 * a）  字节四：长消息参考号，每个SP给每个用户发送的每条参考号都应该不同，可以从0开始，每次加1，最大255，便于同一个终端对同一个SP的消息的不同的长短信进行识别；
	 * b）  字节五：本条长消息的的总消息数，从1到255，一般取值应该大于2；
	 * c）  字节六：本条消息在长消息中的位置或序号，从1到255，第一条为1，第二条为2，最后一条等于第四字节的值。
	 * </pre>
	 * 
	 * @param idx
	 * @param value
	 * @param index
	 * @param count
	 * @return
	 */
	public static byte[] getTextContent(int idx, String value, byte index, byte count,byte msgId) {
		byte[] b1 = new byte[idx];
		int i = 0;
		int length = idx;
		if (index > 0) {
			// 补充头部
			b1[i++] = 0x05;
			b1[i++] = 0x00;
			b1[i++] = 0x03;
			b1[i++] = msgId;
			b1[i++] = count;
			/*if (index == count) { // 最后一条等于第四字节的值。(经分析存在问题，直接使用index的值)
				b1[i++] = msgId;
			} else {*/
			b1[i++] = index;
			/*}*/
		}
		if (value != null || !"".equals(value)) {
			byte[] b2;
			try {
				b2 = value.getBytes(CHARSET);
			} catch (UnsupportedEncodingException e) {
				b2 = value.getBytes();
			}
			int k = 0;
			while (k < b2.length) {
				b1[i++] = b2[k++];
			}
		}
		// 补零
		while (i < length) {
			b1[i] = 0;
			i++;
		}
		return b1;
	}

	/**
	 * 8位的byte[]数组转换成long型
	 * 
	 * @param mybytes
	 * @return long
	 */
	public static long bytes8ToLong(byte mybytes[]) {
		long tmp = (0xff & mybytes[0]) << 56 | (0xff & mybytes[1]) << 48 | (0xff & mybytes[2]) << 40
				| (0xff & mybytes[3]) << 32 | (0xff & mybytes[4]) << 24 | (0xff & mybytes[5]) << 16
				| (0xff & mybytes[6]) << 8 | 0xff & mybytes[7];
		return tmp;
	}

	/**
	 * 4位的byte[]数组转换成long型
	 * 
	 * @param mybytes
	 * @return long
	 */
	public static long bytes4ToLong(byte mybytes[]) {
		long tmp = (0xff & mybytes[0]) << 24 | (0xff & mybytes[1]) << 16 | (0xff & mybytes[2]) << 8
				| (0xff & mybytes[3]);
		return tmp;
	}

	public static long UNbytes4ToLong(byte[] mybytes) {
		long tmp = (mybytes[0]) << 24 | (mybytes[1]) << 16 | (mybytes[2]) << 8 | (mybytes[3]);
		return tmp;
	}

	/**
	 * long类型转化成8个字节
	 * 
	 * @param i 要转化的长整形
	 * @return byte[]
	 */
	public static byte[] longToBytes8(long i) {
		byte mybytes[] = new byte[8];
		mybytes[7] = (byte) (int) ((long) 255 & i);
		mybytes[6] = (byte) (int) (((long) 65280 & i) >> 8);
		mybytes[5] = (byte) (int) (((long) 0xff0000 & i) >> 16);
		mybytes[4] = (byte) (int) (((long) 0xff000000 & i) >> 24);
		int high = (int) (i >> 32);
		mybytes[3] = (byte) (0xff & high);
		mybytes[2] = (byte) ((0xff00 & high) >> 8);
		mybytes[1] = (byte) ((0xff0000 & high) >> 16);
		mybytes[0] = (byte) ((0xff000000 & high) >> 24);
		return mybytes;
	}

	/**
	 * int转化成4个字节的数组
	 * 
	 * @param i 要转化的整形变量
	 * @return byte[]
	 */
	public static byte[] intToBytes4(int i) {
		byte mybytes[] = new byte[4];
		mybytes[3] = (byte) (0xff & i);
		mybytes[2] = (byte) ((0xff00 & i) >> 8);
		mybytes[1] = (byte) ((0xff0000 & i) >> 16);
		mybytes[0] = (byte) ((0xff000000 & i) >> 24);
		return mybytes;
	}

	/**
	 * int转化成4个字节的数组
	 * 
	 * @param i 要转化的整形变量
	 * @return byte[]
	 */
	public static byte[] longToBytes4(long i) {
		byte mybytes[] = new byte[4];
		mybytes[3] = (byte) (0xff & i);
		mybytes[2] = (byte) ((0xff00 & i) >> 8);
		mybytes[1] = (byte) ((0xff0000 & i) >> 16);
		mybytes[0] = (byte) ((0xff000000 & i) >> 24);
		return mybytes;
	}

	/**
	 * byte数组转化成int类型
	 * 
	 * @param mybytes 要转化的
	 * @return int
	 */
	public static int bytes4ToInt(byte mybytes[]) {
		int tmp = (0xff & mybytes[0]) << 24 | (0xff & mybytes[1]) << 16 | (0xff & mybytes[2]) << 8 | 0xff & mybytes[3];
		return tmp;
	}

	/**
	 * 2个字节的byte数组转化成short类型
	 *
	 * @param mybytes 要转化的长度为2的byte数组
	 * @return int
	 */
	public static short bytes2Short(byte mybytes[]) {
		short tmp = (short) ((0xff & mybytes[0]) << 8 | 0xff & mybytes[1]);
		return tmp;
	}

	public static byte[] short2Byte(short i) {
		byte mybytes[] = new byte[2];
		mybytes[1] = (byte) (0xff & i);
		mybytes[0] = (byte) ((0xff00 & i) >> 8);
		return mybytes;
	}

	public static String getHexStr(byte[] bs) {
		String retStr = "";
		for (int i = 0; i < bs.length; i++) {
			if (Integer.toHexString((int) bs[i]).length() > 1) {
				retStr += Integer.toHexString((int) bs[i]).substring(Integer.toHexString((int) bs[i]).length() - 2);
			} else {
				retStr += "0"
						+ Integer.toHexString((int) bs[i]).substring(Integer.toHexString((int) bs[i]).length() - 1);
			}
		}
		return retStr;
	}

	public static byte[] getByte(String byteStr) {
		if (byteStr.length() % 2 != 0) {
			byteStr = "0" + byteStr;
		}
		byte[] retByte = new byte[byteStr.length() / 2];

		for (int i = 0; i < byteStr.length() / 2; i++) {
			byte[] b = new byte[1];
			b[0] = toByte(Integer.parseInt(byteStr.substring(2 * i, 2 * i + 2), 16))[3];
			retByte[i] = toByte(Integer.parseInt(byteStr.substring(2 * i, 2 * i + 2), 16))[3];
		}
		return retByte;
	}

	public static byte[] toByte(int i) {
		byte mybytes[] = new byte[4];
		mybytes[3] = (byte) (0xff & i);
		mybytes[2] = (byte) ((0xff00 & i) >> 8);
		mybytes[1] = (byte) ((0xff0000 & i) >> 16);
		mybytes[0] = (byte) ((0xff000000 & i) >> 24);
		return mybytes;
	}

	public static String getStr(byte[] bRefArr) {
		int length = 0;
		for (length = 0; length < bRefArr.length; length++) {
			if (bRefArr[length] == 0)
				break;
		}
		byte[] temp = new byte[length];
		for (int i = 0; i < length; i++) {
			temp[i] = bRefArr[i];
		}
		String tempStr = "";
		try {
			tempStr = new String(temp, 0, temp.length, "GBK");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return tempStr;
	}

	public static void main(String[] arg) {
		byte[] a = "AEwBAAAHAAEAIAEiAAEAAAAEAAAAAgEAADQBAAEANAMAAgAENAUAAQA0BAAJQ3NxICBJcyA2NAIADDAwMDAwMDAwMDAzMgADAAJjuA=="
				.getBytes();
		String s = getHexStr(a);
		System.out.println(s);
		byte[] b = getByte(s);
		System.out.println(getHexStr(b));
		System.out.println(new String(b));
		System.out.println(new String(a));
		
		byte t = (byte) 0x81;
		System.out.println(byte2Short(t));

	}

	public static short byte2Short(byte b) {
		if(b > 0){
			return (short)b;
		}
		return (short) ((short)(b & 0x7f) | 0x80);  
	}
	
}
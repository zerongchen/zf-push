package com.aotain.zongfen.service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.aotain.zongfen.bean.WeChatPush;
import com.aotain.zongfen.bean.WeChatPush.Receiver;
import com.aotain.zongfen.entity.Department;
import com.aotain.zongfen.entity.DepartmentUsers;
import com.aotain.zongfen.entity.WeChatMessageBody;
import com.aotain.zongfen.entity.WeChatMessageText;
import com.aotain.zongfen.entity.WeChatReponse;
import com.aotain.zongfen.exception.WeChatException;
import com.aotain.zongfen.util.HttpUtils;
import com.aotain.zongfen.util.LocalConfig;
import com.aotain.zongfen.util.PushLog;
import com.aotain.zongfen.util.StringUtil;

@Service
public class WeChatService {
	
	/**
	 * wrapper  the request url according to the uri and method
	 * @param uri
	 * @param method
	 * @return
	 * @throws WeChatException
	 */
	private String wrapperUrl(String uri, String method) throws WeChatException {
		String url = "";
		if (StringUtil.isEmptyString(uri)) {
			throw new WeChatException("The request uri is null!");
		}
		if (!uri.contains("/")) {
			url = uri + "/" + method;
		} else {
			url = uri + method;
		}
		return url;
	}
	
	/**
	 * to get the token information
	 * @param weChatPush
	 * @return
	 * @throws WeChatException
	 */
	private String getToken(WeChatPush weChatPush) throws WeChatException {
		String uri = LocalConfig.getInstance().getWeChatUri();
		String method = LocalConfig.getInstance().getGetTokenMethod();
		String corpid = LocalConfig.getInstance().getCorpId();
		String corpsecret = LocalConfig.getInstance().getAppSecert();
		String url = wrapperUrl(uri, method) + "?corpid=" + corpid + "&corpsecret=" + corpsecret; 
		PushLog.weChatLog.info("get token url is:" + url);
		String json = HttpUtils.get(url);
		WeChatReponse response = JSON.parseObject(json, WeChatReponse.class);
		PushLog.weChatLog.info("The access token is:" + response.getAccess_token());
		return response.getAccess_token();
	}
	
	/**
	 * to get the departments information
	 * @param weChatPush
	 * @return
	 * @throws WeChatException
	 */
	public List<Department> getDepartments(WeChatPush weChatPush) throws WeChatException {
		List<Department> departments = new ArrayList<Department>();
		String accessToken = getToken(weChatPush);
		String uri = LocalConfig.getInstance().getWeChatUri();
		String method = LocalConfig.getInstance().getListDepartment();
		Receiver receiver = weChatPush.getReceiver();
		if (receiver != null) {
			List<BigInteger> departmentIds = receiver.getDepartmentId();
			if (departmentIds != null && departmentIds.size() > 0) {
				for (BigInteger departmentId : departmentIds) {
					String url = wrapperUrl(uri, method) + "?access_token=" + accessToken + "&id=" + departmentId; 
					PushLog.weChatLog.info("get department url is:" + url);
					String json = HttpUtils.get(url);
					WeChatReponse response = JSON.parseObject(json, WeChatReponse.class);
					departments.addAll(response.getDepartment());
				}
			} else {
				throw new WeChatException("The department id  null!");
			}
		} else {
			throw new WeChatException("The weChat receiver is null!");
		}
		return departments;
	}
	
	/**
	 * to get the users of the departmeents
	 * @param weChatPush
	 * @return
	 * @throws WeChatException
	 */
	public List<DepartmentUsers> getDepartmentUsers(WeChatPush weChatPush) throws WeChatException {
		List<DepartmentUsers> departmentUsers = new ArrayList<DepartmentUsers>();
		String accessToken = getToken(weChatPush);
		String uri = LocalConfig.getInstance().getWeChatUri();
		String method = LocalConfig.getInstance().getListDepartmentSimpleUser();
		Receiver receiver = weChatPush.getReceiver();
		if (receiver != null) {
			List<BigInteger> departmentIds = receiver.getDepartmentId();
			if (departmentIds != null && departmentIds.size() > 0) {
				for (BigInteger departmentId : departmentIds) {
					String url = wrapperUrl(uri, method) + "?access_token=" + accessToken + "&department_id=" + departmentId + "&fetch_child=1"; 
					PushLog.weChatLog.info("get department users url is:" + url);
					String json = HttpUtils.get(url);
					WeChatReponse response = JSON.parseObject(json, WeChatReponse.class);
					departmentUsers.addAll(response.getUserlist());
				}
			} else {
				throw new WeChatException("The department id  null!");
			}
		} else {
			throw new WeChatException("The weChat receiver is null!");
		}
		return departmentUsers;
	}
	
	/**
	 * send a message to the terminal user
	 * @param weChatPush
	 * @return
	 * @throws WeChatException
	 */
	public boolean sendMessage(WeChatPush weChatPush) throws WeChatException {
		boolean result = false;
		String accessToken = getToken(weChatPush);
		String uri = LocalConfig.getInstance().getWeChatUri();
		String method = LocalConfig.getInstance().getSendMessageMethod();
		WeChatMessageBody body = null;
		Receiver receiver = weChatPush.getReceiver();
		if (receiver != null) {
			List<BigInteger> departmentIds = receiver.getDepartmentId();
			List<String> userIds = receiver.getUserId();
			boolean departmentFlag = (departmentIds != null && departmentIds.size() > 0);
			boolean userIdFlag = (userIds != null && userIds.size() > 0);
			if (departmentFlag && userIdFlag) {
				body = wrapperMessageBody(weChatPush, departmentIds, userIds);
			} else if (departmentFlag) {
				body = wrapperMessageBody(weChatPush, departmentIds, null);
			} else if (userIdFlag) {
				body = wrapperMessageBody(weChatPush, null, userIds);
			} else {
				throw new WeChatException("The department id and the user cannot be completely empty!");
			}
			PushLog.weChatLog.debug("The WeChatMessageBody json string is:" + JSON.toJSONString(body));
			String url = wrapperUrl(uri, method) + "?access_token=" + accessToken; 
			PushLog.weChatLog.info("send message url is:" + url);
			String json = HttpUtils.post(url, JSON.toJSONString(body));
			WeChatReponse response = JSON.parseObject(json, WeChatReponse.class);
			int errorCode = response.getErrcode();
			String errorMessage = response.getErrmsg();
			PushLog.weChatLog.info("errorCode:" + errorCode + ",errorMessage:" + errorMessage);
			if (errorCode == 0) {
				result = true;
			}
		} else {
			throw new WeChatException("The weChat receiver is null!");
		}
		return result;
	}

	/**
	 * wrapper the message body information
	 * @param weChatPush
	 * @param departmentIds
	 * @param userIds
	 * @return
	 */
	private WeChatMessageBody wrapperMessageBody(WeChatPush weChatPush, List<BigInteger> departmentIds, List<String> userIds) {
		WeChatMessageBody body = new WeChatMessageBody();
		try {
			body.setAgentid(new BigInteger(LocalConfig.getInstance().getAgentId() + ""));
			body.setMsgtype(weChatPush.getSendData().getMsgType());
			WeChatMessageText text = new WeChatMessageText();
			text.setContent(weChatPush.getSendData().getContent());
			body.setText(text);
			body.setSafe(0);
			StringBuffer toPartys = new StringBuffer();
			StringBuffer toUsers = new StringBuffer();
			boolean departmentFlag = (departmentIds != null && departmentIds.size() > 0);
			boolean userIdFlag = (userIds != null && userIds.size() > 0);
			if (departmentFlag && userIdFlag) {
				for (BigInteger departmentId : departmentIds) {
					toPartys.append(departmentId).append("|");
				}
				for (String userId : userIds) {
					toUsers.append(userId).append("|");
				}
				toPartys.replace(toPartys.lastIndexOf("|"), toPartys.length(), "");
				toUsers.replace(toUsers.lastIndexOf("|"), toUsers.length(), "");
				body.setToparty(toPartys.toString());
				body.setTouser(toUsers.toString());
			} else if (departmentFlag) {
				for (BigInteger departmentId : departmentIds) {
					toPartys.append(departmentId).append("|");
				}
				toPartys.replace(toPartys.lastIndexOf("|"), toPartys.length(), "");
				body.setToparty(toPartys.toString());
			} else if (userIdFlag) {
				for (String userId : userIds) {
					toUsers.append(userId).append("|");
				}
				toUsers.replace(toUsers.lastIndexOf("|"), toUsers.length(), "");
				body.setTouser(toUsers.toString());
			}
			return body;
		} catch (Exception e) {
			PushLog.weChatLog.info("Wrapper message body failed!", e);
		}
		return null;
	}

}

package com.aotain.zongfen.entity;

import java.util.ArrayList;
import java.util.List;

public class WeChatReponse extends BaseResponse {

	private static final long serialVersionUID = 2235686125002634130L;

	/**
	 * access_token值
	 */
	private String access_token;
	/**
	 * access_token值的有效时间，单位秒
	 */
	private Long expires_in;
	
	/**
	 * 企业部门信息
	 */
	private List<Department> department = new ArrayList<Department>();
	
	/**
	 * 部门用户信息
	 */
	private List<DepartmentUsers> userlist = new ArrayList<DepartmentUsers>();

	public String getAccess_token() {
		return access_token;
	}

	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}

	public Long getExpires_in() {
		return expires_in;
	}

	public void setExpires_in(Long expires_in) {
		this.expires_in = expires_in;
	}

	public List<Department> getDepartment() {
		return department;
	}

	public void setDepartments(List<Department> department) {
		this.department = department;
	}

	public List<DepartmentUsers> getUserlist() {
		return userlist;
	}

	public void setUserlist(List<DepartmentUsers> userlist) {
		this.userlist = userlist;
	}

	public void setDepartment(List<Department> department) {
		this.department = department;
	}

}

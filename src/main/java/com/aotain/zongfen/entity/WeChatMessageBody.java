package com.aotain.zongfen.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigInteger;

@Setter
@Getter
public class WeChatMessageBody implements Serializable {

	private static final long serialVersionUID = -4496830333643410196L;

	private String touser;
	private String toparty;
	private String totag;
	private String msgtype;
	private BigInteger agentid;
	private WeChatMessageText text;
	private int safe;

}

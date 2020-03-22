package com.copsec.monitor.web.beans;

import com.copsec.monitor.web.config.Resources;
import lombok.Data;

import java.util.UUID;

@Data
public class UserInfoBean {

	private String userId = UUID.randomUUID().toString();//用户ID
	private String userName;//用户名
	private String password;//密码
	private String manufacturerInfo;//厂商信息
	private String mobile;//联系电话
	private String productionName;//产品名称

	@Override
	public String toString(){

		StringBuilder builder = new StringBuilder();
		builder.append(this.userId+ Resources.SPLITER);
		builder.append(this.userName+Resources.SPLITER);
		builder.append(this.password+Resources.SPLITER);
		builder.append(this.manufacturerInfo+Resources.SPLITER);
		builder.append(this.mobile+Resources.SPLITER);
		builder.append(this.productionName);
		return builder.toString();
	}
}

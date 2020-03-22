package com.copsec.monitor.web.beans.snmp;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * oid 和结构化数据之间的对应关系
 * 例如：
 * 1.3.6.1.2.1.1.5.0 （sysName） 在mongodb数据库中对应systemName
 *
 */
@Data
public class OidMapBean {

	private String oid;

	private String fieldName;

	private int type;

	private boolean status;

	/*public OidMapBean(String oid,String fieldName,int type){

		this.oid= oid;
		this.fieldName = fieldName;
		this.type = type;
	}*/
}

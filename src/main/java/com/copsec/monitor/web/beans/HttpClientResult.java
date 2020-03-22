package com.copsec.monitor.web.beans;

import lombok.Data;

@Data
public class HttpClientResult {

	private int code;

	private String content;

	public HttpClientResult(int code,String content){
		this.code = code;
		this.content = content;
	}

	public HttpClientResult(int code){

		this.code = code;
		this.content = "";
	}
}

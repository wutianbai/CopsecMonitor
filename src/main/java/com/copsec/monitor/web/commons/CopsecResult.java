package com.copsec.monitor.web.commons;

import lombok.Data;


@Data
public class CopsecResult {

	private static final String SUCCESS = "success";

	private static final String FAILED = "failed";

	public static final int SUCCESS_CODE = 200;

	public static final int FALIED_CODE = 0;

	private String message;
	private Object data;
	private String status;
	private int code;

	public CopsecResult(){}

	public static CopsecResult success(){
		return success(SUCCESS,null);
	}

	public static CopsecResult success(String message,Object data){
		CopsecResult result = new CopsecResult();
		result.setMessage(message);
		result.setData(data);
		result.setCode(SUCCESS_CODE);
		result.setStatus(SUCCESS);
		return result;
	}

	public static CopsecResult success(Object data){
		return success(SUCCESS,data);
	}

	public static CopsecResult success(String massage){
		return success(massage,null);
	}

	public static CopsecResult failed(String message,Object data){
		CopsecResult result = new CopsecResult();
		result.setMessage(message);
		result.setData(data);
		result.setStatus(FAILED);
		result.setCode(FALIED_CODE);
		return result;
	}

	public static CopsecResult failed(){
		return failed(FAILED,null);
	}

	public static CopsecResult failed(Object data){
		return failed(FAILED,data);
	}

	public static CopsecResult failed(String message){
		return failed(message,null);
	}
}


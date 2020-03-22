package com.copsec.monitor.web.utils;

import java.util.Map;
import java.util.function.Function;

import javax.annotation.Nonnull;

import com.copsec.monitor.web.beans.remote.RemoteDeviceBean;
import com.copsec.monitor.web.beans.remote.RemoteUriBean;
import com.copsec.monitor.web.commons.CopsecResult;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.cookie.Cookie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public class ExecuteRemoteMethodUtils {

	private static final Logger logger = LoggerFactory.getLogger(ExecuteRemoteMethodUtils.class);

	public static CopsecResult execute(@NonNull RemoteDeviceBean device,@Nonnull  RemoteUriBean uriInfo,
			Map<String,String> params,Cookie cookie,Function<CloseableHttpResponse,CopsecResult> function){

		String url = device.getDeviceProtocol() + "://"+device.getDeviceIp()
				+":"+device.getDevicePort() + uriInfo.getUrl();

		if(logger.isDebugEnabled()){

			logger.debug("execute remote method {} ",url);
		}
		if(uriInfo.getUriMethod().equals("GET")){

			return HttpClientUtils.getMethod(url,params,cookie,function);

		}else if(uriInfo.getUriMethod().equals("POST")){


			return HttpClientUtils.postMethod(url,params,cookie,function);
		}else{

			return CopsecResult.failed("远程方法调用类型不支持");
		}

	}
}

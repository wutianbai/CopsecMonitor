package com.copsec.monitor.web.service.serverService;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Future;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.copsec.monitor.web.beans.UserBean;
import com.copsec.monitor.web.beans.remote.RemoteDeviceBean;
import com.copsec.monitor.web.beans.remote.RemoteOperateBean;
import com.copsec.monitor.web.beans.remote.RemoteSessionBean;
import com.copsec.monitor.web.beans.remote.RemoteUriBean;
import com.copsec.monitor.web.commons.CopsecResult;
import com.copsec.monitor.web.config.SystemConfig;
import com.copsec.monitor.web.pools.RemoteDevicePools;
import com.copsec.monitor.web.pools.RemoteUriPools;
import com.copsec.monitor.web.utils.ExecuteRemoteMethodUtils;
import com.copsec.monitor.web.utils.HttpClientUtils;
import com.copsec.monitor.web.utils.logUtils.LogUtils;
import com.google.common.collect.Maps;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
public class KeboService {

	private static final Logger logger = LoggerFactory.getLogger(KeboService.class);

	@Autowired
	private SystemConfig config;

	public CopsecResult executeRemoteMethod(UserBean userInfo,String ip,RemoteOperateBean operateBean,HashMap<String,String> params ){

		Optional<RemoteDeviceBean> deviceOptional = RemoteDevicePools.getInstances().getDeviceById(operateBean.getDeviceId());
		if(deviceOptional.isPresent()){

			Optional<RemoteUriBean> urlOptional = RemoteUriPools.getInstances().
					get(deviceOptional.get().getDeviceType(),operateBean.getMethodType());
			if(urlOptional.isPresent()){

				List<RemoteSessionBean> sessions = userInfo.getCookieMaps().get(deviceOptional.get().getDeviceId());
				if(ObjectUtils.isEmpty(sessions)){

					return CopsecResult.failed("远程会话不存在或无权限");
				}
				CopsecResult executeResult = null;
				for(RemoteSessionBean session:sessions){

					executeResult = ExecuteRemoteMethodUtils.
						execute(deviceOptional.get(),urlOptional.get(),params,session.getCookie(),response -> {

							if(!ObjectUtils.isEmpty(response)){

								try {
									String content = EntityUtils.toString(response.getEntity(),HttpClientUtils.ENCODING);
									JSONObject jsonObject = JSON.parseObject(content);
									return CopsecResult.success(jsonObject);
								}
								catch (IOException e) {
									logger.error(e.getMessage(),e);

									LogUtils.sendFailLog(userInfo.getId(),ip,"执行远程操作-数据解析异常",config.getLogHost(),config.getLogPort(),config.getLogCollection(),"执行远程方法命令");

									return CopsecResult.success("数据解析异常");
								}
							}
							return CopsecResult.failed("远程方法执行失败，未知错误");
						});
					if(!ObjectUtils.isEmpty(executeResult) && executeResult.getCode() == CopsecResult.SUCCESS_CODE){

						break;
					}
				}
				return executeResult;
			}else{

				LogUtils.sendFailLog(userInfo.getId(),ip,"执行远程操作-远程方法信息不存在",config.getLogHost(),config.getLogPort(),config.getLogCollection(),"执行远程方法命令");

				return CopsecResult.failed("远程方法信息不存在");
			}
		}else{

			LogUtils.sendFailLog(userInfo.getId(),ip,"执行远程操作-远程设备信息不存在",config.getLogHost(),config.getLogPort(),config.getLogCollection(),"执行远程方法命令");

			return CopsecResult.failed("远程设备信息不存在");
		}
	}

	public CopsecResult executeRemoteMethodWidthParams(UserBean userInfo,RemoteOperateBean params){

		return null;
	}
}

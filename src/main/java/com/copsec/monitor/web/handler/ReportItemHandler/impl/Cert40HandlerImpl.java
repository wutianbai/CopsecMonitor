package com.copsec.monitor.web.handler.ReportItemHandler.impl;

import javax.annotation.PostConstruct;

import com.copsec.railway.rms.configurations.CopsecConfigurations;
import com.copsec.railway.rms.enums.MonitorItemEnum;
import com.copsec.railway.rms.fileUtils.FileReaderUtils;
import com.copsec.railway.rms.sigontanPools.CertDBPathPools;
import com.copsec.railway.rms.sigontanPools.MonitorHandlerPools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
public class Cert40HandlerImpl extends BaseCertHandler {

	private static final Logger logger = LoggerFactory.getLogger(Cert40HandlerImpl.class);

	@Autowired
	private CopsecConfigurations config;
	/**
	 * 从alise文件夹拷贝证书到指定证书存储库下
	 * @param instanceName
	 * @return
	 */
	@Override
	public String newCertDBPath(String instanceName){

		String path = CertDBPathPools.getInstances().getPath(instanceName);
		if(ObjectUtils.isEmpty(path)){
			/**
			 * 拷贝db文件到指定目录，方法返回证书临时目录
			 */
			path = FileReaderUtils.copyCertFiles(config.getDeviceId(),config.getWebProxy40CertPath(),instanceName);
			if(logger.isDebugEnabled()){

				logger.debug("copy instance cert -> {} to -> {} success",instanceName,path);
			}
		}
		return path;
	}

	@PostConstruct
	public void init(){

		MonitorHandlerPools.getInstance().registerHandler(MonitorItemEnum.CERT40,this);
	}
}

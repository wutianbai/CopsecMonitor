package com.copsec.monitor.web.service.serviceImpl;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import com.copsec.monitor.web.beans.flume.FlumeAgent;
import com.copsec.monitor.web.beans.flume.FlumeBean;
import com.copsec.monitor.web.beans.flume.FlumePropBean;
import com.copsec.monitor.web.beans.flume.FlumeProperty;
import com.copsec.monitor.web.beans.flume.Property4Edit;
import com.copsec.monitor.web.commons.CopsecResult;
import com.copsec.monitor.web.config.Resources;
import com.copsec.monitor.web.config.SystemConfig;
import com.copsec.monitor.web.exception.CopsecException;
import com.copsec.monitor.web.fileReaders.FlumeAliasReader;
import com.copsec.monitor.web.fileReaders.FlumeBeanReader;
import com.copsec.monitor.web.fileReaders.FlumeConfigReader;
import com.copsec.monitor.web.flume.parse.FlumePropertyParseUtils;
import com.copsec.monitor.web.flume.pools.FlumeAgentPool;
import com.copsec.monitor.web.flume.pools.FlumeBeanPool;
import com.copsec.monitor.web.flume.pools.FlumePropertyPool;
import com.copsec.monitor.web.flume.threads.FlumeRunner;
import com.copsec.monitor.web.flume.utils.FlumeAliaseMaps;
import com.copsec.monitor.web.flume.utils.FlumeCommandUtils;
import com.copsec.monitor.web.flume.utils.FlumePropertyUtils;
import com.copsec.monitor.web.service.FlumeConfigService;
import com.copsec.monitor.web.utils.CopsecFileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
public class FlumeConfigServiceImpl implements FlumeConfigService {

	private static final Logger logger = LoggerFactory.getLogger(FlumeConfigServiceImpl.class);

	@Autowired
	private FlumeBeanReader flumeBeanReader;

	@Autowired
	private FlumeConfigReader fcReader;
	@Autowired
	private SystemConfig config;

	@Autowired
	private FlumeAliasReader aliasReader;

	@Override
	public CopsecResult addFlumeConfig(FlumeBean flumeBean) {

		if(FlumeBeanPool.getInstances().exist(flumeBean)){

			if(logger.isDebugEnabled()){

				logger.warn("add flume config failed with {} ",flumeBean.getAgentId());
			}
			return CopsecResult.failed("agentId已存在请重新输入");
		}
		flumeBean.setFileName(UUID.randomUUID().toString().replace("-",""));
		flumeBean.setStatus("stop");
		FlumeBeanPool.getInstances().add(flumeBean);

		FlumeAgent flumeAgent = new FlumeAgent();
		flumeAgent.setAgentId(flumeBean.getAgentId());

		FlumeAgentPool.getInstances().add(flumeAgent);
		try {
			flumeBeanReader.writeDate(FlumeBeanPool.getInstances().getAll(),
					config.getBasePath()+config.getFlumeConfigPath());
		}catch(CopsecException e){

			FlumeBeanPool.getInstances().delete(flumeBean.getAgentId());

			FlumeAgentPool.getInstances().deleteFlumeAgent(flumeBean.getAgentId());

			logger.error(e.getMessage(),e);

			return CopsecResult.failed("创建配置失败",e.getMessage());
		}
		return CopsecResult.success("配置添加成功");
	}

	@Override
	public CopsecResult deleteFlumeConfig(String agentId) {

		FlumeBean flumeConfig = FlumeBeanPool.getInstances().getFlumeConfig(agentId);
		if(ObjectUtils.isEmpty(flumeConfig)){

			return CopsecResult.success("配置删除成功");
		}
		//运行状态，先将flume停止
		if(flumeConfig.getStatus().equals("start")){

			try {
				FlumeCommandUtils.stopFlumeAgent(flumeConfig.getRunId());
			}
			catch (Exception e) {

				logger.error(e.getMessage(),e);

			}
		}
		FlumeAgent agent = FlumeAgentPool.getInstances().get(agentId);

		FlumeBean bean = FlumeBeanPool.getInstances().getFlumeConfig(agentId);
		//从配置缓存中删除
		FlumeAgentPool.getInstances().deleteFlumeAgent(agentId);
		FlumeBeanPool.getInstances().delete(agentId);
		try {
			flumeBeanReader.writeDate(FlumeBeanPool.getInstances().getAll(),config.getBasePath() + config.getFlumeConfigPath());

			CopsecFileUtils.delete(config.getBasePath() + config.getConfigFlume() + bean.getFileName());
			CopsecFileUtils.delete(config.getBasePath() + config.getConfigFlume() + bean.getFileName()+ ".bak");
		}catch(Throwable e){
			FlumeAgentPool.getInstances().add(agent);

			FlumeBeanPool.getInstances().add(bean);
			logger.error(e.getMessage(),e);

			return CopsecResult.failed("删除配置信息失败",e.getMessage());
		}
		return CopsecResult.success("配置删除成功");
	}

	@Override
	public CopsecResult getFlumePropsById(String agentId,String typeId,String prefix) {

		FlumeAgent flumeConfig = FlumeAgentPool.getInstances().get(agentId);
		if(ObjectUtils.isEmpty(flumeConfig)){

			return CopsecResult.failed("未获取到配置相关信息!");
		}
		FlumePropBean propBean = new FlumePropBean();
		Map<String,List<FlumeProperty>> propMap = null;
		switch(prefix){

			case Resources.FLUME_SOURCE:
				propMap = flumeConfig.getSourcesMap();
				break;
			case Resources.FLUME_CHANNEL:
				propMap = flumeConfig.getChannelMap();
				break;
			case Resources.FLUME_SINKS:
				propMap = flumeConfig.getSinkMap();
				break;
		}
		if(!ObjectUtils.isEmpty(propMap) && propMap.containsKey(typeId)){ //存在配置读取配置信息

			propBean.setProps(propMap.get(typeId));
		}else{//不存在配置则将该类型对应的配置信息全部列出

			propBean.setProps(FlumePropertyPool.getInstances().get(prefix));
		}
		propBean.setTypes(FlumePropertyPool.getInstances().getTypeList(prefix));
		propBean.setTypeId(typeId);
		return CopsecResult.success(propBean);
	}

	@Override
	public CopsecResult startFlumeAgent(String agentId) {

		if(logger.isDebugEnabled()){

			logger.debug("start flume agent with id {}",agentId);
		}
		if(ObjectUtils.isEmpty(agentId)){

			return CopsecResult.failed("系统错误");
		}
		FlumeBean fb = FlumeBeanPool.getInstances().getFlumeConfig(agentId);
		if(ObjectUtils.isEmpty(fb)){

			return CopsecResult.failed("未找到该服务对应配置文件");
		}
		FlumeAgent fa = FlumeAgentPool.getInstances().get(agentId);
		if(fa.getSinkMap().keySet().size() == 0 || fa.getSourcesMap().keySet().size() == 0 || fa.getChannelMap().keySet().size() == 0){

			return CopsecResult.failed("配置信息不完整，请配置完成后再启动服务");
		}
		if(fb.getStatus().equalsIgnoreCase("start")){

			return CopsecResult.failed("服务已启动，请勿重复启动");
		}
		try {

			FlumeRunner runner = new FlumeRunner(fa.getAgentId(),
					config.getBasePath()+config.getFlumeEnvPath(),
					config.getBasePath()+ config.getConfigFlume()+ fb.getFileName(),
					config.getFlumeStartUpShell());
			Thread t = new Thread(runner,fa.getAgentId() + "-thread");
			t.start();
			logger.debug("flume config {} is start",fa.getAgentId());

			Thread.sleep(2000);

			String pid = FlumeCommandUtils.getPid(config.getBasePath()+ config.getConfigFlume()+ fb.getFileName());

			fb.setRunId(pid);
			fb.setStatus("start");
			FlumeBeanPool.getInstances().update(fb);

			flumeBeanReader.writeDate(FlumeBeanPool.getInstances().getAll(),
					config.getBasePath()+config.getFlumeConfigPath());
		}
		catch (Exception e) {

			logger.error(e.getMessage(),e);
			return CopsecResult.failed("服务启动失败");
		}

		return CopsecResult.success("服务启动成功");
	}

	@Override
	public CopsecResult stopFlumeAgent(String agentId) {

		if(logger.isDebugEnabled()){

			logger.debug("stop flume agent with agentId {}",agentId);
		}
		if(ObjectUtils.isEmpty(agentId)){

			return CopsecResult.failed("系统错误");
		}
		FlumeBean fb = FlumeBeanPool.getInstances().getFlumeConfig(agentId);
		if(ObjectUtils.isEmpty(fb)){

			return CopsecResult.failed("未找到该服务对应配置文件");
		}
		if(fb.getStatus().equalsIgnoreCase("stop")){

			return CopsecResult.failed("服务已停止，请勿重复操作");
		}
		try {

			fb.setStatus("stop");
			FlumeCommandUtils.stopFlumeAgent(fb.getRunId());
			fb.setRunId("0");
			FlumeBeanPool.getInstances().update(fb);

			flumeBeanReader.writeDate(FlumeBeanPool.getInstances().getAll(),
					config.getBasePath()+config.getFlumeConfigPath());
		}
		catch (Exception e) {

			logger.error(e.getMessage(),e);
			return CopsecResult.failed("停止服务失败");
		}
		return CopsecResult.success("停止服务成功");
	}

	@Override
	public CopsecResult getAllFlumeConfig() {

		try {
			aliasReader.getData(config.getBasePath() + config.getFlumeAliasePath());

			FlumeBeanPool.getInstances().getAll().stream().forEach(bean -> {

				String filePath = config.getBasePath() + config.getConfigFlume() + bean.getFileName();
				try {
					fcReader.parseFlumeConf(filePath,bean);
				}
				catch (CopsecException e) {

					logger.error(e.getMessage(),e);
				}
			});

		}catch (CopsecException e) {

			logger.error(e.getMessage(),e);
		}

		return CopsecResult.success(FlumeBeanPool.getInstances().getAll());
	}

	@Override
	public CopsecResult updateFlumeConfig(Property4Edit properties) {

		if(logger.isDebugEnabled()){

			logger.debug("update flume config by properties {}",Objects.toString(properties));
		}
		FlumeBean fb = FlumeBeanPool.getInstances().getFlumeConfig(properties.getAgentId());

		FlumePropertyUtils.addChannels(properties,fb);

		CopsecResult res =  FlumePropertyParseUtils.updateFlumeAgentProp(properties);

		if(res.getCode() == CopsecResult.SUCCESS_CODE){

			if(logger.isDebugEnabled())
			{

				logger.debug("create channel properties");
			}
			Property4Edit cProp = FlumePropertyUtils.getChannelProp(fb);

			FlumePropertyParseUtils.updateFlumeAgentProp(cProp);

			Property4Edit sinkProp = FlumePropertyUtils.getSinkProps(fb,config);

			FlumePropertyParseUtils.updateFlumeAgentProp(sinkProp);

			List<String> propList = FlumePropertyParseUtils.parseFlumeProperty(properties.getAgentId());

			String path = config.getBasePath() + config.getConfigFlume() + fb.getFileName();
			try {
				fcReader.writeDate(propList,path);
			}
			catch (CopsecException e) {

				logger.error(e.getMessage(),e);
			}

		}
		return res;
	}

	@Override
	public CopsecResult preView(String agentId) {

		if(logger.isDebugEnabled()){

			logger.debug("pre view config with agentId {}",agentId);
		}

		if(ObjectUtils.isEmpty(agentId)){

			return CopsecResult.failed("配置信息不存在");
		}
		List<String> propList = FlumePropertyParseUtils.parseFlumeProperty(agentId);
		if(!ObjectUtils.isEmpty(propList)){

			StringBuffer buffer = new StringBuffer();
			for(String line : propList){

				buffer.append(line + "\r\n");
			}
			return CopsecResult.success(buffer.toString());
		}
		logger.warn("parse flume config properties error");
		return CopsecResult.failed("配置信息不存在");
	}
}

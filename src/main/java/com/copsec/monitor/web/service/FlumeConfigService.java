package com.copsec.monitor.web.service;

import com.copsec.monitor.web.beans.flume.FlumeBean;
import com.copsec.monitor.web.beans.flume.Property4Edit;
import com.copsec.monitor.web.commons.CopsecResult;

public interface FlumeConfigService {

	/**
	 * 添加flume配置信息
	 * @param flumeBean
	 * @return
	 */
	CopsecResult addFlumeConfig(FlumeBean flumeBean);

	/**
	 * 删除flume配置信息
	 * @return
	 */
	CopsecResult deleteFlumeConfig(String agentId);

	/**
	 * 获取配置信息使用agentId
	 * @param agentId
	 * @return
	 */
	CopsecResult getFlumePropsById(String agentId,String typeId,String prefix);

	/**
	 * 启动flume
	 * @param agentId
	 * @return
	 */
	CopsecResult startFlumeAgent(String agentId);

	/**
	 * 启动flume
	 * @param agentId
	 * @return
	 */
	CopsecResult stopFlumeAgent(String agentId);

	/**
	 * 获取所有flume配置信息
	 * @return
	 */
	CopsecResult getAllFlumeConfig();

	/**
	 * 更新Flume 配置属性
	 * @param properties
	 * @return
	 */
	CopsecResult updateFlumeConfig(Property4Edit properties);

	/**
	 * 查看当前配置
	 * @param agentId
	 * @return
	 */
	CopsecResult preView(String agentId);
}

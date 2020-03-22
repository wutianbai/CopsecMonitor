package com.copsec.monitor.web.fileReaders;

import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.copsec.monitor.web.beans.network.RouterBean;
import com.copsec.monitor.web.config.Resources;
import com.copsec.monitor.web.exception.CopsecException;
import com.copsec.monitor.web.fileReaders.fileReaderEnum.NetworkType;
import com.copsec.monitor.web.pools.CommonPools;

import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
public class RouterBeanReader extends  BaseFileReader<RouterBean>{

	@Override
	public void getData(String filePath, NetworkType type) throws CopsecException {
		List<String> list = super.readContent(filePath);
		JSONArray array = new JSONArray();
		if(!ObjectUtils.isEmpty(list)){

			for(String item:list){

				if(!ObjectUtils.isEmpty(item)){

					RouterBean bean = new RouterBean();
					if(item.split(Resources.SPLITER).length == 5){
						String[] datas = item.split(Resources.SPLITER);
						bean.setUuid(datas[0]);
						bean.setIp(datas[1]);
						bean.setSubnet(datas[2]);
						bean.setGateway(datas[3]);
						bean.setInterfaceName(datas[4]);
						array.add(bean);
					}else if(item.split(Resources.SPLITER).length == 4){

						String[] datas = item.split(Resources.SPLITER);
						bean.setUuid(datas[0]);
						bean.setIp(datas[1]);
						bean.setSubnet(datas[2]);
						bean.setGateway(datas[3]);
						array.add(bean);
					}



				}
			}
			CommonPools.getInstances().update(type,array.toJSONString());
		}
	}
}

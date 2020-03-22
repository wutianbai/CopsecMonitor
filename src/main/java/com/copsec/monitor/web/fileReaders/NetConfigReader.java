package com.copsec.monitor.web.fileReaders;

import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.copsec.monitor.web.beans.network.NetConfigBean;
import com.copsec.monitor.web.config.Resources;
import com.copsec.monitor.web.exception.CopsecException;
import com.copsec.monitor.web.fileReaders.fileReaderEnum.NetworkType;
import com.copsec.monitor.web.pools.CommonPools;

import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
public class NetConfigReader extends BaseFileReader<NetConfigBean>{

	@Override
	public void getData(String filePath, NetworkType type) throws CopsecException {
		List<String> list = super.readContent(filePath);
		JSONArray array = new JSONArray();
		if(!ObjectUtils.isEmpty(list)){

			for(String item:list){

				if(!ObjectUtils.isEmpty(item) ){

					NetConfigBean bean = new NetConfigBean();
					if(item.split(Resources.SPLITER).length == 5){

						String[] datas = item.split(Resources.SPLITER);
						bean.setUuid(datas[0]);
						bean.setEthName(datas[1]);
						bean.setIp(datas[2]);
						bean.setSubnet(datas[3]);
						bean.setGateway(datas[4]);
						array.add(bean);
					}else if(item.split(Resources.SPLITER).length == 4){

						String[] datas = item.split(Resources.SPLITER);
						bean.setUuid(datas[0]);
						bean.setEthName(datas[1]);
						bean.setIp(datas[2]);
						bean.setSubnet(datas[3]);
						bean.setGateway("");
						array.add(bean);
					}

				}
			}
			CommonPools.getInstances().update(type,array.toJSONString());
		}
	}
}

package com.copsec.monitor.web.fileReaders;

import java.util.List;
import java.util.Map;

import com.copsec.monitor.web.beans.snmp.OidMapBean;
import com.copsec.monitor.web.config.Resources;
import com.copsec.monitor.web.exception.CopsecException;
import com.copsec.monitor.web.fileReaders.fileReaderEnum.NetworkType;
import com.copsec.monitor.web.pools.SnmpMibPools;
import com.google.common.collect.Maps;
import org.snmp4j.PDU;

import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
public class SnmpMibReader extends  BaseFileReader<OidMapBean>{

	@Override
	public void getData(String filePath, NetworkType type) throws CopsecException {

		List<String> lines = super.readContent(filePath);
		Map<String ,OidMapBean> oidMaps = Maps.newConcurrentMap();
		lines.stream().filter(d -> !ObjectUtils.isEmpty(d) && d.split(Resources.SPLITER).length==3).forEach(line -> {

			String[] attrs = line.split(Resources.SPLITER);

			OidMapBean bean = new OidMapBean();

			bean.setFieldName(attrs[0]);
			bean.setStatus(attrs[1].equalsIgnoreCase("enable"));
			bean.setOid(attrs[2]);
			bean.setType(PDU.GET);
			oidMaps.putIfAbsent(bean.getFieldName(),bean);

		});
		SnmpMibPools.getInstances().add(type,oidMaps);
	}

}

package com.copsec.monitor.web.fileReaders;

import java.io.File;
import java.util.List;

import com.copsec.monitor.web.beans.flume.FlumeProperty;
import com.copsec.monitor.web.config.Resources;
import com.copsec.monitor.web.exception.CopsecException;
import com.copsec.monitor.web.flume.pools.FlumePropertyPool;
import com.google.common.collect.Lists;

import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

/**
 * 读取flume 各项配置对应的原始属性信息
 */
@Component
public class FlumePropertyReader extends BaseFileReader<FlumeProperty> {

	@Override
	public void getData(String filePath) throws CopsecException {

		List<String> list = super.readContent(filePath);

		File  file = new File(filePath);

		if(!ObjectUtils.isEmpty(list)){
			List<FlumeProperty> propsList = Lists.newArrayList();

			list.stream().
					filter(d -> !ObjectUtils.isEmpty(d) && d.split(Resources.SPLITER).length == 3)
					.forEach(line -> {

						String[] attrs = line.split(Resources.SPLITER);
						FlumeProperty prop = new FlumeProperty();
						prop.setKey(attrs[0]);
						prop.setDefaultV(attrs[1]);
						prop.setMust(Boolean.valueOf(attrs[2]));
						propsList.add(prop);
					});
			FlumePropertyPool.getInstances().add(file.getName().substring(0,file.getName().indexOf(".")),propsList);
		}
	}
}

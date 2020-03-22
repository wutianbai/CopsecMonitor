package com.copsec.monitor.web.fileReaders;

import java.util.List;

import com.copsec.monitor.web.beans.flume.AliasMapBean;
import com.copsec.monitor.web.config.Resources;
import com.copsec.monitor.web.exception.CopsecException;
import com.copsec.monitor.web.flume.pools.FlumeAliasPool;

import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
public class FlumeAliasReader extends BaseFileReader<AliasMapBean> {

	@Override
	public void getData(String filePath) throws CopsecException {

		List<String> lists = super.readContent(filePath);

		lists.stream().
				filter(d -> !ObjectUtils.isEmpty(d) && d.split(Resources.SPLITER).length==3)
				.forEach(item -> {

					String[] attrs = item.split(Resources.SPLITER);
					AliasMapBean bean = new AliasMapBean();
					bean.setAlias(attrs[0]);
					bean.setFileName(attrs[1]);
					bean.setType(attrs[2]);

					FlumeAliasPool.getInstances().add(bean);
				});
	}
}

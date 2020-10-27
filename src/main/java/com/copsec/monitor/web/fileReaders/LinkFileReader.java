package com.copsec.monitor.web.fileReaders;

import com.alibaba.fastjson.JSON;
import com.copsec.monitor.web.beans.node.Link;
import com.copsec.monitor.web.exception.CopsecException;
import com.copsec.monitor.web.pools.LinkPools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Component
public class LinkFileReader extends BaseFileReader<Link> {

    private static final Logger logger = LoggerFactory.getLogger(LinkFileReader.class);

    @Override
    public void getData(String filePath) throws CopsecException {
        List<String> list = super.readContent(filePath);
        if (!ObjectUtils.isEmpty(list) && list.size() > 0) {
            list.stream().filter(d -> !ObjectUtils.isEmpty(d)).forEach(item -> {
                Link link = JSON.parseObject(item.trim(), Link.class);
                LinkPools.getInstance().add(link);
            });
        }
    }

	@Override
	public void getDataByInfos(String info) {

		Link link = JSON.parseObject(info.trim(), Link.class);
		LinkPools.getInstance().update(link);
	}
}

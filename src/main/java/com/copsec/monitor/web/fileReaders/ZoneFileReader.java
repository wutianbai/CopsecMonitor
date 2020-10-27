package com.copsec.monitor.web.fileReaders;

import com.alibaba.fastjson.JSON;
import com.copsec.monitor.web.beans.node.Data;
import com.copsec.monitor.web.beans.node.Device;
import com.copsec.monitor.web.beans.node.Position;
import com.copsec.monitor.web.config.Resources;
import com.copsec.monitor.web.exception.CopsecException;
import com.copsec.monitor.web.pools.ZonePools;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Component
public class ZoneFileReader extends BaseFileReader<Device> {
    @Override
    public void getData(String filePath) throws CopsecException {
        List<String> list = super.readContent(filePath);
        if (!ObjectUtils.isEmpty(list) && list.size() > 0) {
            list.stream().
                    filter(d -> !ObjectUtils.isEmpty(d) && d.trim().split(Resources.SPLITER, -1).length == 2)
                    .forEach(item -> {
                        String[] datalist = item.trim().split(Resources.SPLITER, -1);
                        Device zone = new Device();
                        zone.setData(JSON.parseObject(datalist[0], Data.class));
                        zone.setPosition(JSON.parseObject(datalist[1], Position.class));
                        ZonePools.getInstance().add(zone);
                    });
        }
    }

	@Override
	public void getDataByInfos(String info) {

		String[] datalist = info.trim().split(Resources.SPLITER, -1);
		Device zone = new Device();
		zone.setData(JSON.parseObject(datalist[0], Data.class));
		zone.setPosition(JSON.parseObject(datalist[1], Position.class));
		ZonePools.getInstance().update(zone);
	}
}

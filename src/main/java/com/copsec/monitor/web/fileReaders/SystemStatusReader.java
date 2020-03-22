package com.copsec.monitor.web.fileReaders;

import java.util.*;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.copsec.monitor.web.beans.monitor.CpuStateBean;
import com.copsec.monitor.web.beans.monitor.DBSyncTaskChildStateBean;
import com.copsec.monitor.web.beans.monitor.DBSyncTaskStateBean;
import com.copsec.monitor.web.beans.monitor.EthChildStateBean;
import com.copsec.monitor.web.beans.monitor.EthStateBean;
import com.copsec.monitor.web.beans.monitor.FileSyncTaskChildStateBean;
import com.copsec.monitor.web.beans.monitor.FileSyncTaskStateBean;
import com.copsec.monitor.web.beans.monitor.HardDiskChildStateBean;
import com.copsec.monitor.web.beans.monitor.HardDiskStateBean;
import com.copsec.monitor.web.beans.monitor.MemeryStateBean;
import com.copsec.monitor.web.beans.monitor.SystemStatusBean;
import com.copsec.monitor.web.config.Resources;
import com.copsec.monitor.web.exception.CopsecException;
import com.copsec.monitor.web.fileReaders.fileReaderEnum.NetworkType;
import com.copsec.monitor.web.pools.CommonPools;
import com.copsec.monitor.web.utils.FormatUtils;

import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
public class SystemStatusReader extends BaseFileReader<SystemStatusBean>{

	@Override
	public void getData(String filePath, NetworkType type) throws CopsecException {

		List<String> list = super.readContent(filePath);
		if(!ObjectUtils.isEmpty(list)){

			JSONObject object = (JSONObject) JSON.parseObject(list.get(0));
			SystemStatusBean system = JSON.parseObject(list.get(0),SystemStatusBean.class);

			if(!ObjectUtils.isEmpty(system)){

				//更新cpu信息
				CpuStateBean cpu = system.getCpuStatus();
				cpu.setCpuUseRate(cpu.getCpuUseRate()+Resources.PERCENTAGE);
				system.setCpuStatus(cpu);
				//更新disk信息
				HardDiskStateBean disk = system.getHardDisk();
				disk.setTotalUseRate(disk.getTotalUseRate()+Resources.PERCENTAGE);
				disk.setTotal(FormatUtils.getFormatSizeByte(Long.valueOf(disk.getTotal()) * 1024));
				disk.setTotalUsed(FormatUtils.getFormatSizeByte(Long.parseLong(disk.getTotalUsed())*1024));
				HashMap<String ,HardDiskChildStateBean> map = disk.getPartitionMap();
				List<HardDiskChildStateBean> diskList = new ArrayList<>();
				Iterator<Map.Entry<String,HardDiskChildStateBean>> iterator = map.entrySet().iterator();
				while(iterator.hasNext()){

					HardDiskChildStateBean item =  iterator.next().getValue();
					item.setUseRate(item.getUseRate()+"%");
					item.setTotal(FormatUtils.getFormatSizeByte(Long.parseLong(item.getTotal())*1024));
					item.setUsed(FormatUtils.getFormatSizeByte(Long.parseLong(item.getUsed())*1024));
					diskList.add(item);
				}
				disk.setList(diskList);
				disk.setPartitionMap(null);

				//更新memery 信息
				MemeryStateBean memery = system.getMemeryStatus();
				memery.setUseRate(memery.getUseRate()+"%");
				memery.setTotal(FormatUtils.getFormatSizeByteForCeil(Long.parseLong(memery.getTotal())*1024));
				memery.setFree(FormatUtils.getFormatSizeByte(Long.parseLong(memery.getFree())*1024));

				//更新网口信息
				java.text.DecimalFormat df=new java.text.DecimalFormat("#.00");
				EthStateBean eth = system.getEthStatus();
				eth.setTotalReceive(FormatUtils.getFormatSizeByte(Long.parseLong(eth.getTotalReceive()))+"("+eth.getTotalReceive()+")");
				eth.setTotalSend(FormatUtils.getFormatSizeByte(Long.parseLong(eth.getTotalSend()))+"("+eth.getTotalSend()+")");
				eth.setReceiveSpeed(FormatUtils.getFormatSpeedBS(Double.parseDouble(eth.getReceiveSpeed()))+"("+df.format(Double.parseDouble(eth.getReceiveSpeed()))+")");
				eth.setSendSpeed(FormatUtils.getFormatSpeedBS(Double.parseDouble(eth.getSendSpeed()))+"("+df.format(Double.parseDouble(eth.getSendSpeed()))+")");
				HashMap<String,EthChildStateBean> emap = eth.getEthMap();
				Iterator<Map.Entry<String,EthChildStateBean>> iterator1 = emap.entrySet().iterator();
				List<EthChildStateBean> ethChildStateBeanList = new ArrayList<>();
				while(iterator1.hasNext()){

					EthChildStateBean childEth = iterator1.next().getValue();
					childEth.setTotalReceive(FormatUtils.getFormatSizeByte(Long.parseLong(childEth.getTotalReceive()))+"("+childEth.getTotalReceive()+")");
					childEth.setTotalSend(FormatUtils.getFormatSizeByte(Long.parseLong(childEth.getTotalSend()))+"("+childEth.getTotalSend()+")");
					childEth.setReceiveSpeed(FormatUtils.getFormatSpeedBS(Double.parseDouble(childEth.getReceiveSpeed()))+"("+df.format(Double.parseDouble(childEth.getReceiveSpeed()))+")");
					childEth.setSendSpeed(FormatUtils.getFormatSpeedBS(Double.parseDouble(childEth.getSendSpeed()))+"("+df.format(Double.parseDouble(childEth.getSendSpeed()))+")");
					ethChildStateBeanList.add(childEth);
				}
				eth.setEthMap(null);
				eth.setList(ethChildStateBeanList);

				//更新文件同步任务信息
				FileSyncTaskStateBean fileSync = system.getFileSyncTaskStaus();
				if(!ObjectUtils.isEmpty(fileSync)){

					List<FileSyncTaskChildStateBean> list1 = new ArrayList<>();
					fileSync.setDownFileSizeCount(FormatUtils.getFormatSizeByte(Long.parseLong(fileSync.getDownFileSizeCount()))+"("+fileSync.getDownFileSizeCount()+")");
					fileSync.setUpFileSizeCount(FormatUtils.getFormatSizeByte(Long.parseLong(fileSync.getUpFileSizeCount()))+"("+fileSync.getUpFileSizeCount()+")");

					HashMap<String,FileSyncTaskChildStateBean> map2 = fileSync.getFileSyncTaskMap();
					Iterator<Map.Entry<String,FileSyncTaskChildStateBean>> iterator3 = map2.entrySet().iterator();
					while(iterator3.hasNext()){

						FileSyncTaskChildStateBean childFileSync = iterator3.next().getValue();
						childFileSync.setDownFileSizeCount(FormatUtils.getFormatSizeByte(Long.parseLong(childFileSync.getDownFileSizeCount()))+"("+childFileSync.getDownFileSizeCount()+")");
						childFileSync.setUpFileSizeCount(FormatUtils.getFormatSizeByte(Long.parseLong(childFileSync.getUpFileSizeCount()))+"("+childFileSync.getUpFileSizeCount()+")");
						list1.add(childFileSync);
					}
					fileSync.setFileSyncTaskMap(null);
					fileSync.setList(list1);
				}

				DBSyncTaskStateBean dbSync= system.getDbSyncTaskStatus();
				if(!ObjectUtils.isEmpty(dbSync)){
					List<DBSyncTaskChildStateBean> list2 = new ArrayList<>();
					HashMap<String,DBSyncTaskChildStateBean> map4 = dbSync.getDbSyncTaskMap();
					Iterator<Map.Entry<String,DBSyncTaskChildStateBean>> iterator4 = map4.entrySet().iterator();
					while(iterator4.hasNext()){

						DBSyncTaskChildStateBean childDBSync = iterator4.next().getValue();
						String getherDesc ="总："+childDBSync.getGatherCount()+" 增加："+childDBSync.getGatherInsertCount()+" 删除："+childDBSync.getGatherDeleteCount()+" 修改："+childDBSync.getGatherUpdateCount();
						childDBSync.setGatherDesc(getherDesc);

						String storageDesc = "总："+childDBSync.getStorageCount()+" 增加："+childDBSync.getStorageInsertCount()+" 删除："+childDBSync.getStorageDeleteCount()+" 修改："+childDBSync.getStorageUpdateCount();
						childDBSync.setStorageDesc(storageDesc);
						list2.add(childDBSync);
					}
					dbSync.setDbSyncTaskMap(null);
					dbSync.setList(list2);
				}

				CommonPools.getInstances().add(type,JSON.toJSONString(system));
			}
		}
	}
}

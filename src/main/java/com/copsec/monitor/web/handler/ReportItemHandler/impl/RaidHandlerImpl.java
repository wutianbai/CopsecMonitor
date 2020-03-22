package com.copsec.monitor.web.handler.ReportItemHandler.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import javax.annotation.PostConstruct;

import com.copsec.railway.rms.beans.MonitorItem;
import com.copsec.railway.rms.beans.ReportItem;
import com.copsec.railway.rms.beans.vmInfo.ControllerInfo;
import com.copsec.railway.rms.beans.vmInfo.DiskInfo;
import com.copsec.railway.rms.beans.vmInfo.DomainInfo;
import com.copsec.railway.rms.beans.vmInfo.VmInfoBean;
import com.copsec.railway.rms.beans.vmInfo.VolumeInfo;
import com.copsec.railway.rms.common.CopsecResult;
import com.copsec.railway.rms.configurations.CommandsResources;
import com.copsec.railway.rms.configurations.CopsecConfigurations;
import com.copsec.railway.rms.configurations.StatisResources;
import com.copsec.railway.rms.enums.MonitorItemEnum;
import com.copsec.railway.rms.enums.MonitorTypeEnum;
import com.copsec.railway.rms.handler.MonitorHandler;
import com.copsec.railway.rms.processorUtils.ProcessorUtils;
import com.copsec.railway.rms.reflectionUtils.CopsecReflectionUtils;
import com.copsec.railway.rms.sigontanPools.MonitorHandlerPools;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RaidHandlerImpl implements MonitorHandler {

	private static final Logger logger = LoggerFactory.getLogger(RaidHandlerImpl.class);

	@Autowired
	private CopsecConfigurations config;

	@Override
	public ReportItem handler(MonitorItem monitorItem) {

		ReportItem reportItem = new ReportItem();
		reportItem.setMonitorId(monitorItem.getMonitorId());
		reportItem.setMonitorType(monitorItem.getMonitorType());
		reportItem.setMonitorItemType(monitorItem.getMonitorItemType());
		reportItem.setItem(monitorItem.getItem());

		if(config.getSystemType().equalsIgnoreCase(StatisResources.system_type_solaris)){

			VmInfoBean vmInfoBean = new VmInfoBean();

			CopsecResult ldmResult = ProcessorUtils.executeCommand(CommandsResources.ldm_cmd,str -> {

				List<String> strs = Arrays.asList(str.split(StatisResources.line_spliter));

				List<DomainInfo> domainInfos = Lists.newArrayList();
				strs.stream().filter(d -> {

					if(d.startsWith("DOMAIN")){
						return true;
					}
					return false;
				}).forEach(s -> {

					DomainInfo domainInfo = new DomainInfo();
					List<String> attrs = Arrays.asList(s.split("\\|"));
					attrs.stream().filter(d -> {

						if(d.split("=").length == 2){
							return true;
						}
						return false;
					}).forEach(attr -> {

						String[] keyValus = attr.split("=");
						String key = "set"+ (new StringBuilder()).append(Character.toUpperCase(keyValus[0].charAt(0)))
								.append(keyValus[0].substring(1)).toString();
						CopsecReflectionUtils.setInvoke(domainInfo,key,keyValus[1]);
					});
					domainInfos.add(domainInfo);
				});

				return CopsecResult.success(domainInfos);
			});

			if(ldmResult.getCode() == CopsecResult.SUCCESS_CODE){

				vmInfoBean.setDomainInfos((List<DomainInfo>)ldmResult.getData());
			}

			CopsecResult raidConfigResult = ProcessorUtils.executeCommand(CommandsResources.raidconfig_cmd, string -> {

				List<String> strs = Arrays.asList(string.split(StatisResources.line_spliter));
				Vector<String> v = new Vector<>();
				strs.stream().forEach(s -> {

					v.add(s);
				});
				return CopsecResult.success(v);
 			});

			if(raidConfigResult.getCode() == CopsecResult.SUCCESS_CODE){

				getRaidconfig((Vector<String>)raidConfigResult.getData(),0,vmInfoBean);
			}

			reportItem.setResult(vmInfoBean);
			reportItem.setStatus(StatisResources.status_normal);
		}

		return reportItem;
	}

	private static void getRaidconfig(Vector<String> raidvector,
			int index,VmInfoBean vmInfoBean) {

		List<ControllerInfo> controllerInfos = vmInfoBean.getControllerInfos();

		List<VolumeInfo> volumeInfos = vmInfoBean.getVolumeInfos();

		List<DiskInfo> diskInfos = vmInfoBean.getDiskInfos();

		try {
			String line = raidvector.get(index);
			String tmp;
			if (line.startsWith("CONTROLLER")) {

				tmp = line.substring(line.indexOf(" ")+1);
				for (int i = index + 4; i < raidvector.size(); i++) {
					line = raidvector.get(i);

					if (line.startsWith("RAID Volumes") || line.startsWith("DISKS")
							|| line.startsWith("CONTROLLER")) {
						getRaidconfig(raidvector, i, vmInfoBean);
						break;
					}
					String[] params = line.split("[ ]+"); //regex: at least onespace

					int len = params.length;
					ControllerInfo controllerInfo = new ControllerInfo();
					if (len == 5) {
						controllerInfo.setManufacturer(params[0]+tmp);
						controllerInfo.setModel(params[1]);
						controllerInfo.setVersion(params[2]);
						controllerInfo.setVolumes(params[3]);
						controllerInfo.setDisks(params[4]);
					} else if (len > 5) {
						// there is space inside The first domain
						String manufacturer = "";
						for (int j = 0; j < len - 4; j++) {
							manufacturer =manufacturer+ params[j] + " ";
						}
						controllerInfo.setManufacturer(manufacturer.trim()+tmp);
						controllerInfo.setModel(params[len - 4]);
						controllerInfo.setVersion(params[len - 3]);
						controllerInfo.setVolumes(params[len - 2]);
						controllerInfo.setDisks(params[len - 1]);
					}
					controllerInfos.add(controllerInfo);
					vmInfoBean.setControllerInfos(controllerInfos);
				}
			} else if (line.startsWith("RAID Volumes")) {
				for (int i = index + 4; i < raidvector.size(); i++) {
					line = raidvector.get(i);
					if (line.startsWith("RAID Volumes") || line.startsWith("DISKS")
							|| line.startsWith("CONTROLLER")) {
						getRaidconfig(raidvector, i, vmInfoBean);
						break;
					}

					String[] params = line.split("[ ]+");// regex: at least one space

					VolumeInfo volumeInfo = new VolumeInfo();

					if (params.length == 7) {

						volumeInfo.setId(params[0]);
						volumeInfo.setName(params[1]);
						volumeInfo.setDevice(params[2]);
						volumeInfo.setStatus(params[3]);
						volumeInfo.setNumDisks(params[4]);
						volumeInfo.setLevel(params[5]);
						volumeInfo.setStatus(params[6]);

						volumeInfos.add(volumeInfo);
						vmInfoBean.setVolumeInfos(volumeInfos);
					}
				}

			} else if (line.startsWith("DISKS")) {

				for (int i = index + 4; i < raidvector.size(); i++) {
					line = raidvector.get(i);

					if (line.startsWith("RAID Volumes") || line.startsWith("DISKS")
							|| line.startsWith("CONTROLLER")) {
						getRaidconfig(raidvector, i, vmInfoBean);
						break;
					}

					String[] params = line.split("[ ]+");// regex: at least one space
					DiskInfo diskInfo = new DiskInfo();
					if (params.length == 9) {

						diskInfo.setId( params[0]);
						diskInfo.setChassis(params[1]);
						diskInfo.setSlot(params[2]);
						diskInfo.setRaidId(params[3]);
						diskInfo.setStatus(params[4]);
						diskInfo.setType(params[5]);
						diskInfo.setMedia(params[6]);
						diskInfo.setSpare(params[7]);
						diskInfo.setSize(params[8]);

						diskInfos.add(diskInfo);
						vmInfoBean.setDiskInfos(diskInfos);
					}
				}
			}
		} catch (Exception e) {

			logger.error(e.getMessage(),e);
		}
	}

	@PostConstruct
	public void inti(){

		MonitorHandlerPools.getInstance().registerHandler(MonitorItemEnum.RAID,this);
	}
}

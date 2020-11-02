package com.copsec.monitor.web.runner;

import com.copsec.monitor.web.fileReaders.DeviceFileReader;
import com.copsec.monitor.web.fileReaders.MonitorGroupReader;
import com.copsec.monitor.web.fileReaders.MonitorItemReader;
import com.copsec.monitor.web.fileReaders.MonitorTaskReader;
import com.copsec.monitor.web.fileReaders.UserFileReader;
import com.copsec.monitor.web.fileReaders.UserInfoReader;
import com.copsec.monitor.web.fileReaders.WarningItemReader;
import com.copsec.monitor.web.pools.DevicePools;
import com.copsec.monitor.web.pools.MonitorGroupPools;
import com.copsec.monitor.web.pools.MonitorItemPools;
import com.copsec.monitor.web.pools.MonitorTaskPools;
import com.copsec.monitor.web.pools.UserInfoPools;
import com.copsec.monitor.web.pools.UserPools;
import com.copsec.monitor.web.pools.WarningEventPools;
import com.copsec.monitor.web.repository.DeviceRepository;
import com.copsec.monitor.web.repository.MonitorGroupRepository;
import com.copsec.monitor.web.repository.MonitorItemRepository;
import com.copsec.monitor.web.repository.MonitorTaskRepository;
import com.copsec.monitor.web.repository.UserInfoEntityRepository;
import com.copsec.monitor.web.repository.UserRepository;
import com.copsec.monitor.web.repository.WarningEventRepository;
import com.copsec.monitor.web.repository.WarningItemRepository;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class WarningEventScheduler {

	@Autowired
	private WarningEventRepository warningEventRepository;
	@Autowired
	private DeviceRepository deviceRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private UserInfoEntityRepository userInfoEntityRepository;
	@Autowired
	private MonitorItemRepository monitorItemRepository;
	@Autowired
	private MonitorTaskRepository monitorTaskRepository;
	@Autowired
	private MonitorGroupRepository monitorGroupRepository;
	@Autowired
	private WarningItemRepository warningItemRepository;

	@Autowired
	private DeviceFileReader deviceReader;
	@Autowired
	private UserFileReader userReader;
	@Autowired
	private UserInfoReader userInfoReader;
	@Autowired
	private MonitorItemReader monitorItemReader;
	@Autowired
	private MonitorGroupReader monitorGroupReader;
	@Autowired
	private MonitorTaskReader monitorTaskReader;
	@Autowired
	private WarningItemReader warningItemReader;

	@Scheduled(fixedRate = 1000 * 60 * 30)
	public void checkWarning(){

		WarningEventPools.getInstances().getAll()
				.entrySet().stream().forEach(entry -> warningEventRepository.save(entry.getValue()));

		WarningEventPools.getInstances().clean();
	}

	@Scheduled(fixedRate = 1000 * 5)
	public void updateCache(){

		userRepository.findAll().stream().forEach(u -> userReader.getDataByInfos(u.getUserInfo()));
		if(deviceRepository.findAll().size() == 0){

			DevicePools.getInstance().clean();
		}
		deviceRepository.findAll().stream().forEach(d -> deviceReader.getDataByInfos(d.getDeviceInfo()));

		if(userRepository.findAll().size()== 0){

			UserPools.getInstances().clean();
		}
		userRepository.findAll().stream().forEach(u -> userReader.getDataByInfos(u.getUserInfo()));
		if(userInfoEntityRepository.findAll().size() == 0){

			UserInfoPools.getInstances().clean();
		}
		userInfoEntityRepository.findAll().stream().forEach(u -> userInfoReader.getDataByInfos(u.getUserInfo()));
		if(monitorGroupRepository.findAll().size() == 0){

			MonitorGroupPools.getInstances().clean();
		}
		monitorGroupRepository.findAll().stream().forEach(m -> monitorGroupReader.getDataByInfos(m.getMonitorGroupInfo()));
		if(monitorItemRepository.findAll().size() == 0){

			MonitorItemPools.getInstances().clean();
		}
		monitorItemRepository.findAll().stream().forEach((m -> monitorItemReader.getDataByInfos(m.getMonitorItemInfo())));
		if(monitorTaskRepository.findAll().size() == 0){

			MonitorTaskPools.getInstances().clean();
		}
		monitorTaskRepository.findAll().stream().forEach(m -> monitorTaskReader.getDataByInfos(m.getMonitorTaskInfo()));
		if(warningItemRepository.findAll().size() == 0){

			WarningEventPools.getInstances().clean();
		}
		warningItemRepository.findAll().stream().forEach(m -> warningItemReader.getDataByInfos(m.getWarningItemInfo()));
	}
}

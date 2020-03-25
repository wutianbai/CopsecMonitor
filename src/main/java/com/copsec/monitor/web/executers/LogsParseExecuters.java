package com.copsec.monitor.web.executers;

import com.copsec.monitor.web.beans.syslogParseBeans.SyslogMessageBean;
import com.copsec.monitor.web.entity.AuditSyslogMessage;
import com.copsec.monitor.web.entity.SyslogMessage;
import com.copsec.monitor.web.pools.LogSettingPools;
import com.copsec.monitor.web.pools.SyslogMessagePools;
import com.copsec.monitor.web.repository.AuditSyslogMessageRepository;
import com.copsec.monitor.web.repository.DeviceMessageRepository;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * 解析syslogMessage
 */
@Component
public class LogsParseExecuters {

    private static final Logger logger = LoggerFactory.getLogger(LogsParseExecuters.class);

    @Autowired
    private DeviceMessageRepository dMRepository;

    @Autowired
    private AuditSyslogMessageRepository auditSyslogMessageRepository;

    private static final String sperator = " ";

    @Scheduled(fixedRate = 2000)
    public void readLogs() {

        List<SyslogMessage> lists = dMRepository.findByIdAfter();
        lists.stream().forEach(item -> {

            /**
             * 解析数据存入缓存
             */
            SyslogMessageBean messageBean = new SyslogMessageBean();
            messageBean.setDeviceId(item.getHost());
            messageBean.setUpdateTime(item.getReportTime());
            List<String> properties = Lists.newArrayList();
            for (String str : item.getMessage().split(sperator)) {

                if (str.split("=").length == 2) {
                    properties.add(str);
                } else {
                    String v = properties.get(properties.size() - 1);
                    properties.remove(v);
                    String t = v + " " + str;
                    properties.add(t);
                }
            }
            messageBean.setProperties(properties);

            saveAuditMessage(item, messageBean);

            SyslogMessagePools.getInstance().add(messageBean);
            dMRepository.deleteDeviceMessage(item);
        });
    }

    private void saveAuditMessage(SyslogMessage syslogMessage, SyslogMessageBean syslogMessageBean) {
        Optional<String> logType = syslogMessageBean.getProperties().stream().filter(d -> {
            if (d.startsWith("type")) {
                return true;
            }
            return false;
        }).findFirst();
        if (logType.isPresent()) {
            if (LogSettingPools.getInstances().getAllEnableLogTypes().size() != 0 &&
                    LogSettingPools.getInstances().getAllEnableLogTypes().contains(logType.get())) {

                /**
                 * 日志类型过滤
                 */
                AuditSyslogMessage auditMessageEntity = new AuditSyslogMessage();
                BeanUtils.copyProperties(syslogMessage, auditMessageEntity);
                auditSyslogMessageRepository.save(auditMessageEntity);
            } else {
                AuditSyslogMessage auditMessageEntity = new AuditSyslogMessage();
                BeanUtils.copyProperties(syslogMessage, auditMessageEntity);
                auditSyslogMessageRepository.save(auditMessageEntity);
            }
        }
    }

	/*private FileSynLogBean generatePreFileHistoryStatus(SyslogMessage deviceMessage,List<String> lists){

		FileSynLogBean status = new FileSynLogBean();
		status.setDeviceId(deviceMessage.getHost());
		status.setUpdateTime(deviceMessage.getReportTime());
		lists.stream().forEach(item -> {

			if(item.split("=").length == 2){

				String[] attrs = item.split("=");
				switch(attrs[0]){

					case "taskName":{

						status.setTaskName(attrs[1]);
						break;
					}
					case "logType":{

						status.setGather(attrs[1].equalsIgnoreCase("文件采集"));
						break;
					}
					case "objectName":{

						status.setFileName(attrs[1]);
						break;
					}
					case "desc":{


						if((attrs[1].startsWith("开始下载文件") || attrs[1].startsWith("开始上传文件")) && !attrs[1].contains("次") ){
							long size = FormatUtils.getValue(attrs[1],2,true);
							status.setFileSize(size);
							status.setStart(true);
							status.setCanSave(true);
						}else if(attrs[1].startsWith("下载文件完成") || attrs[1].startsWith("文件上传完成")){
							long time = FormatUtils.getValue(attrs[1],1,false);
							status.setTime(time);
							status.setFinish(true);
							status.setCanSave(true);
						}else if(attrs[1].startsWith("文件上传异常")){ //storage

							long size = FormatUtils.getValue(attrs[1],6,true);
							status.setFileSize(size);
							status.setCanSave(true);
							status.setError(true);

						}else if(attrs[1].startsWith("下载文件失败")){ //gather

							long size = FormatUtils.getValue(attrs[1],3,true);
							status.setFileSize(size);
							status.setCanSave(true);
							status.setError(true);

						}else{

							status.setCanSave(false);
						}
						break;
					}
				}
			}
		});
		return status;
	}*/

	/*private void updatePreFileSynHistory(FileSynLogBean bean){

		if(!bean.getTaskName().equals("N/A")){

			FileSyncStatusBean syncStatus = new FileSyncStatusBean();
			syncStatus.setTaskName(bean.getTaskName());
			syncStatus.setUpdateTime(bean.getUpdateTime());
			syncStatus.setStatus(true);
			FileSyncStatusPools.getInstances().update(syncStatus);
		}

		if(!bean.isCanSave()){

			return ;
		}
		PreFileSynHistoryStatus preStatus = new PreFileSynHistoryStatus();
		preStatus.setFileName(bean.getFileName());
		preStatus.setTaskName(bean.getTaskName());
		preStatus.setDeviceId(bean.getDeviceId());
		preStatus.setUpdateTime(bean.getUpdateTime());
		preStatus.setError(bean.isError());
		if(bean.isGather()){

			if(bean.isError()){

				preStatus.setGatherSizeSum(bean.getFileSize());
			}else if(bean.isStart()){

				preStatus.setGatherSizeSum(bean.getFileSize());
			}else if(bean.isFinish()){

				preStatus.setGatherTime(bean.getTime());
			}

		}else{

			if(bean.isError()){

				preStatus.setStorageSizeSum(bean.getFileSize());
			}else if(bean.isStart()){

				preStatus.setStorageSizeSum(bean.getFileSize());
			}else if(bean.isFinish()){

				preStatus.setStorageTime(bean.getTime());
			}

		}
		pfsRepository.updatePreFileSyncHisotryStatus(preStatus);
	}*/


	/*@Scheduled(fixedRate = 2000)
	public void updateFileSyncHistoryStatus(){

		List<PreFileSynHistoryStatus> list = pfsRepository.findAll(PageRequest.of(0,10000)).getContent();

		list.stream().forEach(item ->{

			FileSyncHistoryStatus status = new FileSyncHistoryStatus();
			status.setDeviceId(item.getDeviceId());
			status.setTaskName(item.getTaskName());
			if(item.getGatherTime() > 0 ){

				status.setGatherCount(1);
				status.setGatherTime(item.getGatherTime());
			}else if(item.getStorageTime() > 0){

				status.setStorageCount(1);
				status.setStorageTime(item.getStorageTime());
			}
			status.setGatherSizeSum(item.getGatherSizeSum());
			status.setStorageSizeSum(item.getStorageSizeSum());
			status.setUpdateTime(item.getUpdateTime());
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(item.getUpdateTime());
			status.setYear(calendar.get(Calendar.YEAR));
			status.setMonth((calendar.get(Calendar.MONTH)+1));
			status.setDay(calendar.get(Calendar.DAY_OF_MONTH));
			status.setHour(calendar.get(Calendar.HOUR_OF_DAY));

			if(item.isError()){


				fileStatusRepository.updateFileSyncHistoryStatusWithError(status);
			}else{

				fileStatusRepository.updateFileSyncHistoryStatus(status);
			}

			pfsRepository.delete(item);
		});
	}*/
}

package com.copsec.monitor.web.controller;

import com.copsec.monitor.web.beans.LogConditionBean;
import com.copsec.monitor.web.beans.UserBean;
import com.copsec.monitor.web.beans.statistics.ConditionBean;
import com.copsec.monitor.web.commons.CopsecResult;
import com.copsec.monitor.web.pools.DevicePools;
import com.copsec.monitor.web.service.StatisticsService;
import com.copsec.monitor.web.service.SystemService;
import com.copsec.monitor.web.utils.FormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Calendar;
import java.util.List;

@Controller
@RequestMapping("/statistics")
@SessionAttributes(value={"userInfo"})
public class StatisticsController {

	private static final Logger logger = LoggerFactory.getLogger(StatisticsController.class);
	@Autowired
	private StatisticsService statisticsService;

	@Autowired
	private SystemService systemService;

	@GetMapping(value={"/{pageId}"})
	public String toPage(@SessionAttribute UserBean userInfo,@PathVariable("pageId")String pageId){

		return "statistics/"+pageId;
	}

	@GetMapping("/cpu/get")
	@ResponseBody
	public CopsecResult cpuStatisticsInfo(@SessionAttribute UserBean userInfo){

		if(logger.isDebugEnabled()){

			logger.debug("get cpu info");
		}
		ConditionBean conditionBean = new ConditionBean();

		return statisticsService.getCpuStatisticsInfoBy1(conditionBean);
	}
	@PostMapping("/cpu/info")
	@ResponseBody
	public CopsecResult getCpuInfoByCondition(@SessionAttribute UserBean userInfo,ConditionBean condition){

		if(logger.isDebugEnabled()){

			logger.debug("get cpu info by condition {}",condition);
		}

		return statisticsService.getCpuStatisticsInfoByDayTime(condition);
	}

	@PostMapping(value="/net/info")
	@ResponseBody
	public CopsecResult getNetInfoByCondition(@SessionAttribute UserBean userInfo,@RequestBody ConditionBean condition){

		if(logger.isDebugEnabled()){

			logger.debug("get net info by condition {}",condition);
		}
		condition = checkCondition(condition);

		return statisticsService.getNetData(condition,condition.isSum());
	}

	@PostMapping("/protocol/device")
	@ResponseBody
	public CopsecResult getProtocoData4Connection(@SessionAttribute UserBean userInfo,ConditionBean condition){

		if(logger.isDebugEnabled()){

			logger.debug("get protocol status info by condition {}",condition);
		}
		condition = checkCondition(condition);
		condition.setByDevice("deviceId");
		if(condition.isConnection()){

			condition.setReference("handleConnectCount");
		}else{

			condition.setReference("handleNetDataSize");
		}

		return statisticsService.getProtocol(condition);
	}

	@PostMapping("/protocol/task")
	@ResponseBody
	public CopsecResult getProtocoData4Net(@SessionAttribute UserBean userInfo,ConditionBean condition){

		if(logger.isDebugEnabled()){

			logger.debug("get protocol status info by condition {}",condition);
		}
		condition = checkCondition(condition);
		condition.setByDevice("taskName");
		if(condition.isConnection()){

			condition.setReference("handleConnectCount");

		}else{

			condition.setReference("handleNetDataSize");
		}

		return statisticsService.getProtocol(condition);
	}

	@PostMapping("/db/device")
	@ResponseBody
	public CopsecResult getDbDevice(@SessionAttribute UserBean userInfo,ConditionBean condition){

		if(logger.isDebugEnabled()){

			logger.debug("get db status info by condition {}",condition);
		}
		condition = checkCondition(condition);
		condition.setByDevice("deviceId");
		if(condition.isGather()){

			condition.setReference("gatherCount");

		}else{

			condition.setReference("storageCount");
		}

		return statisticsService.getDbData(condition);
	}

	@PostMapping("/db/task")
	@ResponseBody
	public CopsecResult getDbTask(@SessionAttribute UserBean userInfo,ConditionBean condition){

		if(logger.isDebugEnabled()){

			logger.debug("get db status info by condition {}",condition);
		}
		condition = checkCondition(condition);
		condition.setByDevice("taskName");
		if(condition.isGather()){

			condition.setReference("gatherCount");

		}else{

			condition.setReference("storageCount");
		}

		return statisticsService.getDbData(condition);
	}

	@PostMapping("/file/device")
	@ResponseBody
	public CopsecResult getFile4Device(@SessionAttribute UserBean userInfo,ConditionBean condition){

		condition = checkCondition(condition);
		condition.setByDevice("deviceId");

		return statisticsService.getFileData(condition);
	}

	@PostMapping("/file/task")
	@ResponseBody
	public CopsecResult getFile4Task(@SessionAttribute UserBean userInfo,ConditionBean condition){

		condition = checkCondition(condition);
		condition.setByDevice("taskName");

		return statisticsService.getFileData(condition);
	}

	@PostMapping("/total/net")
	@ResponseBody
	public CopsecResult getTotal4NetData(@SessionAttribute UserBean userInfo,ConditionBean condition){



		condition = checkCondition(condition);

		return statisticsService.getTotal4Net(condition);
	}

	@PostMapping("/total/db")
	@ResponseBody
	public CopsecResult getTotal4DbData(@SessionAttribute UserBean userInfo,ConditionBean condition){

		condition = checkCondition(condition);
		if(condition.isGather()){

			condition.setReference("gatherCount");

		}else{

			condition.setReference("storageCount");
		}
		return statisticsService.getTotal4Db(condition);
	}

	@PostMapping("/total/file")
	@ResponseBody
	public CopsecResult getTotal4FileData(@SessionAttribute UserBean userInfo,ConditionBean condition){

		condition = checkCondition(condition);
		return statisticsService.getTotal4File(condition);
	}

	@PostMapping("/total/protocol")
	@ResponseBody
	public CopsecResult getTotal4ProtocolData(@SessionAttribute UserBean userInfo,ConditionBean condition){

		condition = checkCondition(condition);
		if(condition.isConnection()){

			condition.setReference("handleConnectCount");

		}else{

			condition.setReference("handleNetDataSize");
		}
		return statisticsService.getTotal4Protocol(condition);
	}

	private ConditionBean checkCondition(ConditionBean condition){

		if(condition.getType().equals("day")){

			if(ObjectUtils.isEmpty(condition.getStart()) && ObjectUtils.isEmpty(condition.getEnd())){

				condition.setStart(FormatUtils.getDateBefore("yyyy-MM-dd HH:mm:ss",Calendar.DAY_OF_YEAR,-5));
				condition.setEnd(FormatUtils.getDateByPattern("yyyy-MM-dd HH:mm:ss"));
			}
		}else if(condition.getType().equals("month")){

			if(ObjectUtils.isEmpty(condition.getStart()) &&  ObjectUtils.isEmpty(condition.getEnd())){

				condition.setStart(FormatUtils.getDateBefore("yyyy-MM",Calendar.MONTH,-5));
				condition.setEnd(FormatUtils.getDateByPattern("yyyy-MM"));
			}
		}else if(condition.getType().equals("year")){

			if(ObjectUtils.isEmpty(condition.getStart()) && ObjectUtils.isEmpty(condition.getEnd())){

				condition.setStart(FormatUtils.getDateBefore("yyyy",Calendar.YEAR,-5));
				condition.setEnd(FormatUtils.getDateByPattern("yyyy"));
			}
		}else if(condition.getType().equals("hour")){

			condition.setStart(FormatUtils.getDateByPattern("yyyy-MM-dd"));
			condition.setEnd(FormatUtils.getDateByPattern("yyyy-MM-dd"));
		}

		return condition;
	}


	@PostMapping("/total/day")
	@ResponseBody
	public CopsecResult getTotalOfDay(@SessionAttribute UserBean userInfo,ConditionBean condition){

		condition.setStart(FormatUtils.getDateByPattern("yyyy-MM-dd"));
		condition.setEnd(FormatUtils.getDateByPattern("yyyy-MM-dd"));
		return statisticsService.countTotal(condition);
	}

	@PostMapping("/today/files")
	@ResponseBody
	public CopsecResult getTodayFilesData(@SessionAttribute UserBean userInfo,ConditionBean condition){

		condition.setStart((FormatUtils.getDateByPattern("yyyy-MM-dd")));
		condition.setEnd((FormatUtils.getDateByPattern("yyyy-MM-dd")));
		return statisticsService.countFileSynHistoryStatus(condition);
	}

	@GetMapping(value = "/device/get")
	@ResponseBody
	public CopsecResult getAllDeviceInfo(){
		return CopsecResult.success(DevicePools.getInstance().getAllDevice());
	}

	@PostMapping(value = "/gatherPie/data")
	@ResponseBody
	public CopsecResult getGatherPieData(ConditionBean conditionBean){

		/*conditionBean.setStart(FormatUtils.getDateByPattern("yyyy-MM-dd"));
		conditionBean.setEnd(FormatUtils.getDateByPattern("yyyy-MM-dd"));*/
		conditionBean.setStart("2019-12-18");
		conditionBean.setEnd("2019-12-18");
		return statisticsService.getPrecentCountOfTask(conditionBean);
	}

	@PostMapping(value = "/storagePie/data")
	@ResponseBody
	public CopsecResult getStoragePieData(ConditionBean conditionBean){

		/*conditionBean.setStart(FormatUtils.getDateByPattern("yyyy-MM-dd"));
		conditionBean.setEnd(FormatUtils.getDateByPattern("yyyy-MM-dd"));*/
		conditionBean.setStart("2019-12-18");
		conditionBean.setEnd("2019-12-18");

		return statisticsService.getPrecentSumOfTask(conditionBean);
	}

	@PostMapping(value = "/gatherValue/data")
	@ResponseBody
	public CopsecResult getGatherValueData(@SessionAttribute UserBean userInfo,ConditionBean conditionBean){

		/*conditionBean.setStart(FormatUtils.getDateByPattern("yyyy-MM-dd"));
		conditionBean.setEnd(FormatUtils.getDateByPattern("yyyy-MM-dd"));*/
		conditionBean.setStart("2019-12-18");
		conditionBean.setEnd("2019-12-18");

		return statisticsService.getPrecentCountValues(conditionBean);
	}

	@PostMapping( value = "/storageValue/data")
	@ResponseBody
	public CopsecResult getStorageValueData(@SessionAttribute UserBean userInfo,ConditionBean conditionBean){

		/*conditionBean.setStart(FormatUtils.getDateByPattern("yyyy-MM-dd"));
		conditionBean.setEnd(FormatUtils.getDateByPattern("yyyy-MM-dd"));*/
		conditionBean.setStart("2019-12-18");
		conditionBean.setEnd("2019-12-18");
		return statisticsService.getPrecentSumValues(conditionBean);
	}

	@PostMapping(value = "/currentNet/data")
	@ResponseBody
	public CopsecResult getCurrentNetData(@SessionAttribute UserBean userInfo,ConditionBean conditionBean){
		conditionBean.setStart(FormatUtils.getDateByPattern("yyyy-MM-dd"));
		conditionBean.setEnd(FormatUtils.getDateByPattern("yyyy-MM-dd"));

		return statisticsService.getCurrentNetData(conditionBean);
	}

	@PostMapping(value = "/currentFileSum/data")
	@ResponseBody
	public CopsecResult getTotalCountToday(@SessionAttribute UserBean userInfo,ConditionBean conditionBean){

		/*conditionBean.setStart(FormatUtils.getDateByPattern("yyyy-MM-dd"));
		conditionBean.setEnd(FormatUtils.getDateByPattern("yyyy-MM-dd"));*/
		conditionBean.setStart("2019-12-18");
		conditionBean.setEnd("2019-12-18");

		return CopsecResult.success(statisticsService.getFileCountToday(conditionBean));
	}


	@PostMapping(value= "/alarm/search")
	@ResponseBody
	public CopsecResult getTaskStatusWithPageable(@SessionAttribute UserBean userInfo,LogConditionBean condition){

		return CopsecResult.success(statisticsService.getAlarmFileSyncTaskNames());
	}

	@PostMapping(value = "/alarm/devices")
	@ResponseBody
	public CopsecResult getAlarmDeviceInfo(@SessionAttribute UserBean userInfo,@RequestBody List<String> taskNames){

		return statisticsService.getDeviceIdsByTaskNams(taskNames);
	}

	@GetMapping(value="/resources/cpu")
	@ResponseBody
	public CopsecResult getResourcesUseOfCpu(@SessionAttribute UserBean userInfo ){

		return statisticsService.getResourcesUse(true);
	}

	@GetMapping(value="/resources/memory")
	@ResponseBody
	public CopsecResult getResourcesUseOfMemory(@SessionAttribute UserBean userInfo ){

		return statisticsService.getResourcesUse(false);
	}

	@GetMapping(value="/dbTask/detail/{time}")
	@ResponseBody
	public CopsecResult getDBTaskDetail(@SessionAttribute UserBean userInfo,@PathVariable("time") String time){

		ConditionBean condition = new ConditionBean();
		condition.setStart((FormatUtils.getDateByPattern("yyyy-MM-dd")));
		condition.setEnd((FormatUtils.getDateByPattern("yyyy-MM-dd")));
		return statisticsService.getDBSyncTaskDetails(condition);
	}

	@GetMapping("/db/statistics/total/{time}")
	@ResponseBody
	public CopsecResult getDBStatisticsDataTotalForToday(@SessionAttribute UserBean userInfo,@PathVariable("time") String time){

		ConditionBean conditionBean = new ConditionBean();
		/*conditionBean.setStart(FormatUtils.getDateByPattern("yyyy-MM-dd"));
		conditionBean.setEnd(FormatUtils.getDateByPattern("yyyy-MM-dd"));*/
		conditionBean.setStart("2019-11-05");
		conditionBean.setEnd("2019-11-05");

		return CopsecResult.success(statisticsService.getDBDataTotalForToday(conditionBean));
	}

	@PostMapping("/db/statistics/percent")
	@ResponseBody
	public CopsecResult getDBStatisticsDataPercentForToday(@SessionAttribute UserBean userInfo,ConditionBean conditionBean){

		/*conditionBean.setStart(FormatUtils.getDateByPattern("yyyy-MM-dd"));
		conditionBean.setEnd(FormatUtils.getDateByPattern("yyyy-MM-dd"));*/
		conditionBean.setStart("2019-11-05");
		conditionBean.setEnd("2019-11-05");

		return CopsecResult.success(statisticsService.getDBDataForToday(conditionBean));
	}

	@PostMapping("/db/statistics/pie")
	@ResponseBody
	public CopsecResult getDBStatisticsPieDateForToday(@SessionAttribute UserBean userInfo,ConditionBean conditionBean){

		/*conditionBean.setStart(FormatUtils.getDateByPattern("yyyy-MM-dd"));
		conditionBean.setEnd(FormatUtils.getDateByPattern("yyyy-MM-dd"));*/
		conditionBean.setStart("2019-11-05");
		conditionBean.setEnd("2019-11-05");

		return statisticsService.getDBTaskPieDateForToday(conditionBean);

	}
}

package com.copsec.monitor;


import java.util.Calendar;
import java.util.Date;

import com.copsec.monitor.web.beans.statistics.ConditionBean;
import com.copsec.monitor.web.entity.FileSyncHistoryStatus;
import com.copsec.monitor.web.repository.AuditSyslogMessageRepository;
import com.copsec.monitor.web.repository.FileStatusRepository;
import com.copsec.monitor.web.service.StatisticsService;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MonitorApplicationTests {


	@Autowired
	private StatisticsService service;
	@Test
	public void testSnmp(){

		ConditionBean conditionBean = new ConditionBean();
		conditionBean.setStart("2019-11-05");
		conditionBean.setEnd("2019-11-05");
		System.err.println(service.getDBDataForToday(conditionBean));

		System.err.println(service.getDBDataTotalForToday(conditionBean));
	}

}

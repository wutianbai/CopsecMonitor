package com.copsec.monitor.web.beans.statistics;

import java.util.List;

import com.google.common.collect.Lists;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

@Getter @Setter @Data
public class ConditionBean {
	/**
	 * type : day month year
	 */
	private String type = "day";

	private boolean sum = true;

	/**
	 * 分组查询键值
	 */
	private String byDevice = "deviceId";
	private boolean connection = true;
	private boolean gather = true;
	private boolean count = true;

	/**
	 * 统计数据key
	 */
	private String title;

	/**
	 * 分组字段 handleConnectCount or handleNetDataSize or gatherCount or storageCount
	 */
	private String reference;

	/**
	 * start time
	 */
	private String start;
	/**
	 * end time
	 */
	private String end;

	/**
	 * net / file / db /protocol
	 */
	private String statistiscalType;

	private Sort.Direction direction;

	private int sortDirection;

	private List<String> deviceIds = Lists.newArrayList();
}

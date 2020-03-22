package com.copsec.monitor.web.beans.statistics;

import java.util.Comparator;
import java.util.Date;

import lombok.Data;

@Data
public class FileStatisticResultBean implements Comparator<FileStatisticResultBean>{

	private String taskName;
	private String days;

	private long gatherCount=0L;
	private long storageCount=0L;
	private long gatherSizeSum=0L;
	private long storageSizeSum=0L;
	private long gatherTime=0L;
	private long storageTime=0L;

	private double dayOfSpeed=0L;
	private double totalOfSpeed=0L;
	private long dataSizeTotal=0L;
	private long dataSizeToday=0L;

	private String gatherSpeedToday;
	private String storageSpeedToday;
	private String dataSizeTodayStr;
	private String gatherSumToday;
	private String storageSumToday;
	private long gatherCountToday=0L;
	private long storageCountToday=0L;

	private String gatherSpeedTotal;
	private String storageSpeedTotal;
	private String dataSizeTotalStr;
	private String gatherSumTotal;
	private String storageSumTotal;
	private long gatherCountTotal=0L;
	private long storageCountTotal=0L;

	private Date oldTime;
	private Date lastTime;

	private String precentGatherCount;
	private String precentStorageCount;
	private String backGroudColor;

	@Override
	public int compare(FileStatisticResultBean o1, FileStatisticResultBean o2) {

		long sum1 = o1.getGatherSizeSum() + o1.getStorageSizeSum();
		long sum2 = o2.getGatherSizeSum() + o2.getStorageSizeSum();
		if(sum1 <= sum2){

			return 1;
		}else {

			return -1;
		}
	}
}

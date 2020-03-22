package com.copsec.monitor.web.entity;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import lombok.Data;
import org.bson.types.ObjectId;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "dbSyncHistoryStatus")
public class DBSyncHistoryStatus {

	@Id
	private ObjectId id;

	private String deviceId;
	private int year;
	private int month;
	private int day;
	private int hour;

	private String taskName;

	private long gatherCount;
	private long gatherInsertCount;
	private long gatherDeleteCount;
	private long gatherUpdateCount;

	private long storageCount;
	private long storageInsertCount;
	private long storageDeleteCount;
	private long storageUpdateCount;

	private Date updateTime;

	public String getDate(String type) {
		StringBuffer sbf = new StringBuffer(this.year + "");
		switch(type){

			case "hour":
				return this.hour + "";
			case "day":
				if (this.month > 0) {

					if (this.month < 10) {
						sbf.append("-" + "0" + this.month);
					} else {
						sbf.append("-" + this.month);
					}
				}
				if (this.day >0) {

					if (this.day < 10) {
						sbf.append("-" + "0" + this.day);
					} else {
						sbf.append("-" + this.day);
					}
				}
				return sbf.toString();
			case "month":
				if (this.month > 0) {

					if (this.month < 10) {
						sbf.append("-" + "0" + this.month);
					} else {
						sbf.append("-" + this.month);
					}
				}
				return sbf.toString();
			case "year":
				return sbf.toString();
		}
		return sbf.toString();
	}
}

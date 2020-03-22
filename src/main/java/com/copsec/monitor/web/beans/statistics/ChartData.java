package com.copsec.monitor.web.beans.statistics;

import java.util.List;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class ChartData {

	private List<String> labels;
	private List<DatasetBean> datasets;
}

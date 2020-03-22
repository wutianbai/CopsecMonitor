package com.copsec.monitor.web.beans.statistics;

import java.util.List;

import com.google.common.collect.Lists;
import lombok.Data;

@Data
public class PieData {

	private List<String> labels = Lists.newArrayList();

	private List<PieDataSets> datasets = Lists.newArrayList();
}

package com.copsec.monitor.web.beans.statistics;

import java.util.List;

import com.google.common.collect.Lists;
import lombok.Data;

@Data
public class PieDataSets {

	private List<Double> data = Lists.newArrayList();

	private List<String> backgroundColor = Lists.newArrayList();
}

package com.copsec.monitor.web.beans.statistics;

import java.util.List;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class DatasetBean<T> {

	private String backgroundColor;
	private String borderColor;
	private String label;
	private List<T> data;
	private boolean fill = false;
}

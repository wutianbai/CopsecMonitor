package com.copsec.monitor.web.beans.node;

import java.io.Serializable;

import lombok.Data;

@Data
public class PositionBeans implements Serializable {

	private int x;
	private int y;
	private String id;
}

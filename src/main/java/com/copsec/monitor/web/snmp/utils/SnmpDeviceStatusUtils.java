package com.copsec.monitor.web.snmp.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;


import javax.validation.constraints.Max;

import com.copsec.monitor.web.beans.snmp.InterfaceBean;
import com.copsec.monitor.web.snmp.resources.SnmpFieldResources;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.UCDecoder;

import org.springframework.util.ObjectUtils;

public class SnmpDeviceStatusUtils {

	private static final Logger logger= LoggerFactory.getLogger(SnmpDeviceStatusUtils.class);

	private static final BigDecimal MAX_32 = new BigDecimal(0xffffffffL);

	private static final BigDecimal MAX_64 = new BigDecimal(Long.MAX_VALUE).multiply(new BigDecimal(2));

	/**
	 * 32 位计数
	 * @param preValue 记录之前的字节数
	 * @param current 当前记录的字节数
	 * @param times 时间间隔
	 * @param speed 当前接口带宽
	 * @return
	 */
	public static long getInterfaceSpeedBy32(Long preValue,Long current,Long times ,Long speed){

		BigDecimal p = new BigDecimal(preValue);
		BigDecimal c = new BigDecimal(current);
		if(c.compareTo(p) == -1 ){

			c = c.add(MAX_32);
		}
		BigDecimal x = c.subtract(p).divide(new BigDecimal(((times/1000))),10, BigDecimal.ROUND_HALF_DOWN);
 		return x.longValue();
	}
	/**
	 * 64 位计数
	 * @param preValue 记录之前的字节数
	 * @param current 当前记录的字节数
	 * @param times 时间间隔
	 * @param speed 当前接口带宽
	 * @return
	 */
	public static long getInterfaceSpeedBy64(Long preValue,Long current,Long times ,Long speed){

		BigDecimal p = new BigDecimal(preValue);
		BigDecimal c = new BigDecimal(current);
		if(c.compareTo(p) == -1 ){

			c.add(MAX_64);
		}
		BigDecimal re = c.subtract(p).divide(new BigDecimal(((times/1000))),10, BigDecimal.ROUND_HALF_DOWN);
		return re.longValue();
	}

	public static double diskPercentage(Map<String,String> oidStatusMap){

		if(ObjectUtils.isEmpty(oidStatusMap.get(SnmpFieldResources.MEMORY_TOTAL)) ||
				ObjectUtils.isEmpty(oidStatusMap.get(SnmpFieldResources.MEMORY_FREE)) ||
				ObjectUtils.isEmpty(oidStatusMap.get(SnmpFieldResources.MEMORY_BUFFER)) ||
				ObjectUtils.isEmpty(oidStatusMap.get(SnmpFieldResources.MEMORY_CHCHEED))){

			logger.error("errors occur when get memory use rate one of memory value is empty or null");
			return 0.00;
		}

		DecimalFormat decimalFormat = new DecimalFormat("#0.0000");

		long total = Long.valueOf(oidStatusMap.get(SnmpFieldResources.MEMORY_TOTAL));

		long free = Long.valueOf(oidStatusMap.get(SnmpFieldResources.MEMORY_FREE));

		long buffer = Long.valueOf(oidStatusMap.get(SnmpFieldResources.MEMORY_BUFFER));

		long cached = Long.valueOf(oidStatusMap.get(SnmpFieldResources.MEMORY_CHCHEED));

		BigDecimal t = new BigDecimal(total);
		BigDecimal used = new BigDecimal(total-free-buffer-cached);

		return used.divide(t,10, BigDecimal.ROUND_HALF_DOWN).multiply(new BigDecimal(100)).doubleValue();
	}

	/**
	 * 根据oidStatusMap，获取该设备对应的device
	 * @param oidStatusMap
	 */
	public static List<InterfaceBean> getDeviceInterface(Map<String,String> oidStatusMap){

		List<InterfaceBean> list = Lists.newArrayList();

		String countValue = oidStatusMap.get(SnmpFieldResources.INTERFACE_COUNT);
		if(ObjectUtils.isEmpty(countValue)){

			logger.error("interface count value is null ");
			return list;
		}
		int ifCount = Integer.valueOf(oidStatusMap.get(SnmpFieldResources.INTERFACE_COUNT));

		for(int i=1;i<=ifCount;i++){

			InterfaceBean it = new InterfaceBean();
			it.setId(i);
			String in = oidStatusMap.get(SnmpFieldResources.INTERFACE_IN+"."+i);
			if(ObjectUtils.isEmpty(in)){

				it.setUse64(true);
				in = oidStatusMap.get(SnmpFieldResources.INTERFACE_IN_64+"."+i);

			}
			if(ObjectUtils.isEmpty(in)){

				it.setIfInOctets(0L);
			}else{

				it.setIfInOctets(Long.valueOf(in));
			}

			String out = oidStatusMap.get(SnmpFieldResources.INTERFACE_OUT+"."+i);
			if(ObjectUtils.isEmpty(out)){

				out = oidStatusMap.get(SnmpFieldResources.INTERFACE_OUT_64+"."+i);
			}
			if(ObjectUtils.isEmpty(out)){

				it.setIfOutOctets(0L);
			}else{

				it.setIfOutOctets(Long.valueOf(out));
			}
			String status = oidStatusMap.get(SnmpFieldResources.INTERFACE_STATUS+"."+i);
			if(ObjectUtils.isEmpty(status)){

				it.setStatus("up");
			}else{

				it.setStatus(Integer.valueOf(status)==2?"down":"up");
			}
			String name = oidStatusMap.get(SnmpFieldResources.INTERFACE_NAME+"."+i);
			it.setName(name);
			String speed = oidStatusMap.get(SnmpFieldResources.INTERFACE_SPEED+"."+i);
			if(ObjectUtils.isEmpty(speed)){

				continue;
			}
			it.setSpeed(Long.valueOf(speed));
			list.add(it);
		}

		return list;

	}

	public static String getMessage(List<InterfaceBean> current,List<InterfaceBean> pre){

		String result = "";

		for (InterfaceBean item:current){

			InterfaceBean _preIf = getInterface(item.getId(),pre);
			if(!ObjectUtils.isEmpty(_preIf)){

				if(!item.getStatus().equals(_preIf.getStatus()) && item.getStatus().equals("down") ){

					result += item.getName() + "-down ;";
				}
			}
		}
		if(result.length() == 0 ){

			result = "网络接口正常";
		}
		return result;
	}

	public static double getAllSpeed(List<InterfaceBean> pre,List<InterfaceBean> current,long time){

		double totalSpeed = 0.00;

		if(ObjectUtils.isEmpty(pre)){

			return 0.00;
		}

		for(InterfaceBean preV :pre){

			InterfaceBean currentV = getInterface(preV.getId(),current);
			if(currentV.getSpeed() == 0 || ObjectUtils.isEmpty(currentV)){

				continue;
			}
			long preIn = 0,preOut = 0;
			if(!ObjectUtils.isEmpty(currentV)){

				preIn = currentV.getIfInOctets();
				preOut = currentV.getIfOutOctets();
			}
			long resultIn = 0,resultOut = 0;
			if(preV.isUse64()){

				resultIn = getInterfaceSpeedBy64(preV.getIfInOctets(),preIn,time,currentV.getSpeed());
				resultOut =  getInterfaceSpeedBy64(preV.getIfOutOctets(),preOut,time,currentV.getSpeed());
			}else{

				resultIn = getInterfaceSpeedBy32(preV.getIfInOctets(),preIn,time,currentV.getSpeed());
				resultOut =  getInterfaceSpeedBy32(preV.getIfOutOctets(),preOut,time,currentV.getSpeed());
			}
			totalSpeed = totalSpeed + resultIn + resultOut;
		}
		return Double.valueOf(totalSpeed);
	}

	public static long getTotal(List<InterfaceBean> pre,List<InterfaceBean> current,long oldTotal){

		oldTotal = 0L;
		for(InterfaceBean c :current){

			if(ObjectUtils.isEmpty(pre)){

				return 0L;
			}

			InterfaceBean prev = getInterface(c.getId(),pre);

			long pIn =0,pOut=0 ;
			if(!ObjectUtils.isEmpty(prev)){

				pIn = prev.getIfInOctets();
				pOut = prev.getIfOutOctets();
			}
			long _in = 0L,_out = 0L;
			if(!c.isUse64()){

				if(c.getIfInOctets() < pIn ){

					_in = MAX_32.min(new BigDecimal(pIn)).add(new BigDecimal(c.getIfInOctets())).longValue();
				}else{

					_in = c.getIfInOctets() - pIn;
				}

				if(c.getIfOutOctets() < pOut){

					_out = MAX_32.min(new BigDecimal(pOut)).add(new BigDecimal(c.getIfOutOctets())).longValue();
				}else{

					_out = c.getIfOutOctets() - pOut;
				}
			}else{

				if(c.getIfInOctets() < pIn ){

					_in = MAX_64.min(new BigDecimal(pIn)).add(new BigDecimal(c.getIfInOctets())).longValue();
				}else{

					_in = c.getIfInOctets() - pIn;
				}

				if(c.getIfOutOctets() < pOut){

					_out = MAX_64.min(new BigDecimal(pOut)).add(new BigDecimal(c.getIfOutOctets())).longValue();
				}else{

					_out = c.getIfOutOctets() - pOut;
				}
			}
			oldTotal += _in + _out;
		}
		return oldTotal;
	}

	private static InterfaceBean getInterface(int id,List<InterfaceBean> current){

		Iterator<InterfaceBean> iterator = current.iterator();
		while(iterator.hasNext()){

			InterfaceBean bean = iterator.next();
			if(bean.getId() == id){

				return bean;
			}
		}
		return null;
	}
}

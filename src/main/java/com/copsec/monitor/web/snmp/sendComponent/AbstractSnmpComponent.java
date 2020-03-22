package com.copsec.monitor.web.snmp.sendComponent;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Vector;

import com.copsec.monitor.web.beans.snmp.InterfaceBean;
import com.copsec.monitor.web.beans.snmp.OidMapBean;
import com.copsec.monitor.web.beans.snmp.SnmpConfigBean;
import com.copsec.monitor.web.config.SystemConfig;
import com.copsec.monitor.web.snmp.mibs.LinuxMibs;
import com.copsec.monitor.web.snmp.resources.SnmpFieldResources;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.omg.CORBA.Object;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snmp4j.MessageDispatcher;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.Variable;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.DefaultPDUFactory;
import org.snmp4j.util.TableEvent;
import org.snmp4j.util.TableUtils;

import org.springframework.util.ObjectUtils;

/**
 * 定义通过snmp协议获取消息的操作工具类
 */
public abstract class AbstractSnmpComponent {

	private static final Logger logger = LoggerFactory.getLogger(AbstractSnmpComponent.class);
	private Snmp snmp;
	/**
	 *  初始化snmp
	 */
	public void initSnmp() throws IOException {

		MessageDispatcher md = generatMessageDispatcher();
		this.snmp = new Snmp(md,new DefaultUdpTransportMapping());
		this.snmp.listen();
	}

	protected abstract  MessageDispatcher generatMessageDispatcher();

	/**
	 * 创建target，根据config配置
	 * @return
	 */
	protected abstract Target createTarget();

	/**
	 * 根据指定oid创建pdu命令
	 * @param oid
	 * @return
	 */
	protected abstract PDU createPDU(String oid,int type);

	/**
	 * 获取服务器响应，解析响应并返回该字段对应的具体数值
	 * @param oids
	 * @return
	 */
	public void getResponse(List<OidMapBean> oids,Map<String,String> map) throws Exception{

		for(OidMapBean item : oids){

			Target t = createTarget();
			PDU requestPdu = createPDU(item.getOid(),item.getType());

			ResponseEvent event= this.snmp.send(requestPdu,t);
			if(!ObjectUtils.isEmpty(event)){

				PDU responsePdu = event.getResponse();

				if(responsePdu.isResponsePdu()){

					parseResponse(item.getOid(),item.getFieldName(),responsePdu,map);
				}
			}
		}

	}

	/**
	 * 将value值写入map，与field对应
	 * @param oid
	 * @param messageFiled
	 * @param response
	 * @param map
	 */
	protected void parseResponse(String oid,String messageFiled,PDU response,Map<String,String> map){

		Vector<? extends VariableBinding> vbs = response.getVariableBindings();
		vbs.stream().forEach(item -> {

			VariableBinding v = item;

			map.put(messageFiled,v.getVariable().toString());
		});
	}

	/**
	 * 获取网络接口数量
	 * @param oid
	 * @return
	 * @throws Exception
	 */
	protected int getInterfaceCount(String oid) throws Exception{

		Target target = createTarget();

		PDU pdu = createPDU(oid,PDU.GET);

		ResponseEvent event = this.snmp.send(pdu,target);

		PDU responsePdu = event.getResponse();

		Variable variable = responsePdu.getVariable(new OID(oid));

		return variable.toInt();
	}


	public void close(){

		try {
			this.snmp.close();
		}
		catch (IOException e) {

			logger.error(e.getMessage(),e);
		}
	}

	public void getInterfaceTable(OID[] oids,Map<String ,String> map){

		Target t = createTarget();

		TableUtils tableUtils = new TableUtils(this.snmp,new DefaultPDUFactory());
		List<TableEvent> events = tableUtils.getTable(t,oids,null,null);

		for (TableEvent event : events) {
			if(!event.isError()) {

				for(VariableBinding vb: event.getColumns()) {

					String oid = vb.getOid().toString();
					String index = event.getIndex().toString().substring(event.getIndex().toString().lastIndexOf("."));
					if(oid.startsWith(LinuxMibs.INTERFACE_IN_32)){

						map.putIfAbsent(SnmpFieldResources.INTERFACE_IN+index,vb.toValueString());
					}else if(oid.startsWith(LinuxMibs.INTERFACE_OUT_32)){

						map.putIfAbsent(SnmpFieldResources.INTERFACE_OUT+index,vb.toValueString());
					}else if(oid.startsWith(LinuxMibs.INTERFACE_SPEED)){

						map.putIfAbsent(SnmpFieldResources.INTERFACE_SPEED+index,vb.toValueString());
					}else if(oid.startsWith(LinuxMibs.INTERFACE_IN_64)){

						map.putIfAbsent(SnmpFieldResources.INTERFACE_IN_64+index,vb.toValueString());
					}else if( oid.startsWith(LinuxMibs.INTERFACE_OUT_64)){

						map.putIfAbsent(SnmpFieldResources.INTERFACE_OUT_64+index,vb.toValueString());
					}else if(oid.startsWith(LinuxMibs.INTERFACE_STATUS)){

						map.putIfAbsent(SnmpFieldResources.INTERFACE_STATUS+index,vb.toValueString());
					}else if(oid.startsWith(LinuxMibs.INTERFACE_NAME)){

						map.putIfAbsent(SnmpFieldResources.INTERFACE_NAME+index,vb.toValueString());
					}
				}
			}else{

				logger.error(event.getErrorMessage());
			}
		}
	}
}

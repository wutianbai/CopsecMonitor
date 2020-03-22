package com.copsec.monitor.web.snmp.sendComponent;

import com.copsec.monitor.web.beans.snmp.SnmpConfigBean;
import com.copsec.monitor.web.config.SystemConfig;
import org.snmp4j.CommunityTarget;
import org.snmp4j.MessageDispatcher;
import org.snmp4j.MessageDispatcherImpl;
import org.snmp4j.PDU;
import org.snmp4j.Target;
import org.snmp4j.mp.MPv1;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.VariableBinding;

/**
 * v1 版本协议
 */
public class SnmpComponent4V1 extends AbstractSnmpComponent {

	private SnmpConfigBean config;
	private SystemConfig systemConfig;
	public SnmpComponent4V1(SnmpConfigBean config,SystemConfig systemConfig){

		this.config = config;
		this.systemConfig = systemConfig;
	}
	@Override
	protected MessageDispatcher generatMessageDispatcher() {

		MessageDispatcher md = new MessageDispatcherImpl();
		md.addMessageProcessingModel(new MPv1());
		return md;
	}

	@Override
	protected Target createTarget() {

		CommunityTarget target = new CommunityTarget(new UdpAddress(this.config.getIp()+"/"+this.config.getPort())
				,new OctetString(this.config.getCommunity()));
		target.setRetries(this.systemConfig.getSnmpRetryTimes());
		target.setTimeout(this.systemConfig.getSnmpTimeout());
		return target;
	}

	@Override
	protected PDU createPDU(String oid,int type) {

		PDU pdu = new PDU();
		pdu.setType(type);
		pdu.add(new VariableBinding(new OID(oid)));

		return pdu;
	}
}

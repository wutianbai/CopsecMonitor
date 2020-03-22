package com.copsec.monitor.web.snmp.sendComponent;

import com.copsec.monitor.web.beans.snmp.SnmpConfigBean;
import com.copsec.monitor.web.config.SystemConfig;
import org.snmp4j.CommunityTarget;
import org.snmp4j.MessageDispatcher;
import org.snmp4j.MessageDispatcherImpl;
import org.snmp4j.PDU;
import org.snmp4j.PDUv1;
import org.snmp4j.Target;
import org.snmp4j.mp.MPv2c;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.VariableBinding;

public class SnmpComponent4V2 extends AbstractSnmpComponent{


	private SnmpConfigBean config;
	private SystemConfig systemConfig;

	public SnmpComponent4V2(SnmpConfigBean config,SystemConfig systemConfig){

			this.config = config;
			this.systemConfig = systemConfig;
	}

	@Override
	protected MessageDispatcher generatMessageDispatcher() {

		MessageDispatcher md = new MessageDispatcherImpl();
		md.addMessageProcessingModel(new MPv2c());
		return md;
	}

	@Override
	protected Target createTarget() {

		CommunityTarget target = new CommunityTarget();
		target.setVersion(SnmpConstants.version2c);
		target.setCommunity(new OctetString(this.config.getCommunity()));
		target.setAddress(new UdpAddress(this.config.getIp()+"/"+this.config.getPort()));
		target.setRetries(this.systemConfig.getSnmpRetryTimes());
		target.setTimeout(this.systemConfig.getSnmpTimeout());
		return target;
	}

	@Override
	protected PDU createPDU(String oid,int type) {

		PDU requestPdu = new PDUv1();
		requestPdu.setType(type);
		requestPdu.add(new VariableBinding(new OID(oid)));
		return requestPdu;
	}
}

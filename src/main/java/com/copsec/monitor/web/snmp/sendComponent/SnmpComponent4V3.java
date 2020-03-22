package com.copsec.monitor.web.snmp.sendComponent;

import com.copsec.monitor.web.beans.snmp.SnmpConfigBean;
import com.copsec.monitor.web.config.SystemConfig;
import org.snmp4j.MessageDispatcher;
import org.snmp4j.MessageDispatcherImpl;
import org.snmp4j.PDU;
import org.snmp4j.ScopedPDU;
import org.snmp4j.Target;
import org.snmp4j.UserTarget;
import org.snmp4j.mp.MPv3;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.security.SecurityLevel;
import org.snmp4j.security.SecurityProtocols;
import org.snmp4j.security.USM;
import org.snmp4j.security.UsmUser;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.VariableBinding;

public class SnmpComponent4V3 extends AbstractSnmpComponent {

	private SnmpConfigBean config;
	private SystemConfig systemConfig;

	public SnmpComponent4V3(SnmpConfigBean config,SystemConfig systemConfig){

		this.config = config;
		this.systemConfig = systemConfig;
	}

	@Override
	protected MessageDispatcher generatMessageDispatcher() {

		MessageDispatcher md = new MessageDispatcherImpl();

		OctetString localEnginedId = new OctetString(MPv3.createLocalEngineID());
		USM usm = new USM(SecurityProtocols.getInstance().addDefaultProtocols(),
				localEnginedId,0);
		UsmUser user = new UsmUser(new OctetString(this.config.getUserName()),new OID(this.config.getAuthenticationProtocol()),
				new OctetString(this.config.getAuthPassphrase()),new OID(this.config.getPrivatyProtocol()),new OctetString(this.config.getPrivacyPassphrase()));

		usm.addUser(user);
		md.addMessageProcessingModel(new MPv3(usm));
		return md;
	}

	@Override
	protected Target createTarget() {

		Target target = new UserTarget();
		target.setSecurityLevel(SecurityLevel.AUTH_PRIV);
		target.setSecurityName(new OctetString(this.config.getUserName()));
		target.setVersion(SnmpConstants.version3);
		target.setAddress(new UdpAddress(this.config.getIp()+"/"+this.config.getPort()));
		target.setRetries(this.systemConfig.getSnmpRetryTimes());
		target.setTimeout(this.systemConfig.getSnmpTimeout());
		return target;
	}

	@Override
	protected PDU createPDU(String oid,int type) {

		ScopedPDU requestPdu = new ScopedPDU();
		requestPdu.setType(type);
		requestPdu.add(new VariableBinding(new OID(oid)));
		return requestPdu;
	}
}

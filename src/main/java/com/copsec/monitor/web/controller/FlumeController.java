package com.copsec.monitor.web.controller;

import java.util.Objects;

import com.copsec.monitor.web.beans.UserBean;
import com.copsec.monitor.web.beans.flume.FlumeBean;
import com.copsec.monitor.web.beans.flume.FlumePropBean;
import com.copsec.monitor.web.beans.flume.Property4Edit;
import com.copsec.monitor.web.commons.CopsecResult;
import com.copsec.monitor.web.flume.pools.FlumePropertyPool;
import com.copsec.monitor.web.service.FlumeConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;

@Controller
@RequestMapping(value = "/inspector")
@SessionAttributes(value={"userInfo"})
public class FlumeController {

	private static final Logger logger = LoggerFactory.getLogger(FlumeController.class);
	@Autowired
	private FlumeConfigService service;

	@GetMapping(value={"/{pageId}"})
	public String toPage(@SessionAttribute UserBean userInfo,@PathVariable("pageId")String pageId){

		return "nodes/"+pageId;
	}

	@GetMapping(value = "/config/get")
	@ResponseBody
	public CopsecResult getAllFlumeConfig(@SessionAttribute UserBean userInfo){

		return service.getAllFlumeConfig();
	}

	@PostMapping(value = "/config/add")
	@ResponseBody
	public CopsecResult addFlumeConfig(@SessionAttribute UserBean userInfo,FlumeBean config){

		if(logger.isDebugEnabled()){

			logger.debug("add flume configs {}",Objects.toString(config));
		}

		return service.addFlumeConfig(config);
	}

	@GetMapping(value = "/config/delete/{agentId}")
	@ResponseBody
	public CopsecResult deleteFlumeConfig(@SessionAttribute UserBean userInfo,@PathVariable("agentId") String agentId){

		if(logger.isDebugEnabled()){

			logger.debug("delete flume config by agentId {}",agentId);
		}
		return service.deleteFlumeConfig(agentId);
	}

	@PostMapping(value = "/config/edit")
	@ResponseBody
	public CopsecResult getProps(@SessionAttribute UserBean userInfo,String agentId,String typeId,String prefix){

		if(logger.isDebugEnabled()){

			logger.debug("edit agent {} width typeId {}",agentId,typeId);
		}

		return service.getFlumePropsById(agentId,typeId,prefix);
	}

	@GetMapping(value="/config/props/{propId}")
	@ResponseBody
	public CopsecResult getPropsById(@SessionAttribute UserBean userInfo,@PathVariable("propId") String propId){

		if(logger.isDebugEnabled()){

			logger.debug("get props by id {} ",propId);
		}

		return CopsecResult.success(FlumePropertyPool.getInstances().get(propId));
	}

	@PostMapping(value="/config/update")
	@ResponseBody
	public CopsecResult updateFlumeConfig(@SessionAttribute UserBean userInfo,@RequestBody Property4Edit property4Edit){

		if(logger.isDebugEnabled()){

			logger.debug("update flume agent {} width typeId {}",property4Edit.getAgentId(),property4Edit.getPropList());
		}

		return service.updateFlumeConfig(property4Edit);
	}

	@GetMapping(value={"/view/{agentId}"})
	@ResponseBody
	public CopsecResult getPreViewConfig(@SessionAttribute UserBean userInfo,@PathVariable("agentId") String agentId){

		if(logger.isDebugEnabled()){

			logger.debug("reading flume config by agentId {}",agentId);
		}

		return service.preView(agentId);
	}

	@GetMapping(value={"/opt/start/{agentId}"})
	@ResponseBody
	public CopsecResult startFlumeAgent(@SessionAttribute UserBean userInfo,@PathVariable("agentId") String agentId){

		if(logger.isDebugEnabled()){

			logger.debug("start flume agent");
		}

		return service.startFlumeAgent(agentId);
	}

	@GetMapping(value={"/opt/stop/{agentId}"})
	@ResponseBody
	public CopsecResult stopFlumeAgent(@SessionAttribute UserBean userInfo,@PathVariable("agentId") String agentId){

		if(logger.isDebugEnabled()){

			logger.debug("stop flume agent");
		}

		return service.stopFlumeAgent(agentId);
	}
}

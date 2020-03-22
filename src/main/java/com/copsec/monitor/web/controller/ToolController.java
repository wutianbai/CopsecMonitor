package com.copsec.monitor.web.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import com.copsec.monitor.web.beans.UserBean;
import com.copsec.monitor.web.commons.CopsecResult;
import com.copsec.monitor.web.utils.SocketUtils;
import com.copsec.monitor.web.utils.commandUtils.CommandProcessUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;

@Controller
@RequestMapping("/tool")
@SessionAttributes(value={"userInfo"})
public class ToolController {

	private static final Logger logger = LoggerFactory.getLogger(ToolController.class);

	@GetMapping(value={"/{pageId}"})
	public String toPage(@SessionAttribute UserBean userInfo,@PathVariable("pageId")String pageId){

		return "tool/"+pageId;
	}

	@PostMapping("/ping/4")
	@ResponseBody
	public CopsecResult testIp4(@SessionAttribute UserBean userInfo,HttpServletRequest request,String ip){
		String message = "";
		try {
			message = CommandProcessUtils.getUtil().pingTesting(ip);
		}
		catch (IOException e) {

			logger.error(e.getMessage(),e);
			return CopsecResult.failed("测试失败，系统异常",e.getMessage());
		}
		return CopsecResult.success(message);
	}
	@PostMapping("/ping/6")
	@ResponseBody
	public CopsecResult testIp6(@SessionAttribute UserBean userInfo,HttpServletRequest request,String ip){
		String message = "";
		try {
			message = CommandProcessUtils.getUtil().pingTesting(ip);
		}
		catch (IOException e) {

			logger.error(e.getMessage(),e);
			return CopsecResult.failed("测试失败，系统异常",e.getMessage());
		}
		return CopsecResult.success(message);
	}

	@PostMapping("/router")
	@ResponseBody
	public CopsecResult routeTest(@SessionAttribute UserBean userInfo,HttpServletRequest request,String ip){
		String message = "";
		try {
			message = CommandProcessUtils.getUtil().routeTracking(ip);
		}
		catch (IOException e) {

			logger.error(e.getMessage(),e);
			return CopsecResult.failed("测试失败，系统异常",e.getMessage());
		}
		return CopsecResult.success(message);
	}

	@PostMapping("/service/4")
	@ResponseBody
	public CopsecResult serviceTest4(@SessionAttribute UserBean userInfo,HttpServletRequest request,String ip,String port){

		String message = "";
		try {
			message = SocketUtils.getUtil().ipv4ServiceTesting(ip,port);
		}
		catch (IOException e) {
			logger.error(e.getMessage(),e);
			return CopsecResult.failed("测试失败，系统异常",e.getMessage());
		}
		return CopsecResult.success(message);
	}

	@PostMapping("/service/6")
	@ResponseBody
	public CopsecResult serviceTest6(@SessionAttribute UserBean userInfo,HttpServletRequest request,String ip,String port){

		String message = "";
		try {
			message = SocketUtils.getUtil().ipv4ServiceTesting(ip,port);
		}
		catch (IOException e) {
			logger.error(e.getMessage(),e);
			return CopsecResult.failed("测试失败，系统异常",e.getMessage());
		}
		return CopsecResult.success(message);
	}
}

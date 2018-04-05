package com.qbao.sdk.server.controller;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.qbao.sdk.server.service.TestService;

@Controller
@RequestMapping("/test")
public class TestController extends BaseController {
	
	@Resource
	private TestService testService; 
	
	@RequestMapping("/testJson")
	@ResponseBody
	public String testJson(String username) {
		
		testService.add(username);
		return "success";
	}
	
	@RequestMapping("/testPage")
	public String testJson() {
		
		return "index.page";
	}
	
}
